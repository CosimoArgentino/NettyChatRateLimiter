package com.chat.server;

import com.chat.server.cmd.Command;
import com.chat.server.cmd.CommandCodec;
import com.chat.server.channel.ChannelAttribute;
import com.chat.server.channel.HistoricMessages;
import com.chat.server.i18n.Messages;
import com.chat.server.provider.NicknameProvider;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ChatServerHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger LOGGER = Logger.getLogger( ChatServerHandler.class.getName() );

    private static final ChannelGroup channels = new DefaultChannelGroup(
            GlobalEventExecutor.INSTANCE);
    private static final NicknameProvider nicknameProvider = new NicknameProvider();
    private static final ChannelAttribute attr = new ChannelAttribute(30);
    private static final HistoricMessages messages = new HistoricMessages(5);

    public void handlerAdded(ChannelHandlerContext ctx) {
        if (ctx.channel().isActive()) {
            helo(ctx.channel());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        helo(ctx.channel());
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();
        String msg = "[" + ch.remoteAddress() + "] offline";
        msg(ch, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        LOGGER.log(Level.WARNING,t.toString(), t);
        ctx.writeAndFlush(t.getMessage());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) {
        switch (cmd) {
            case NICK:
                changeNickname(ctx, cmd);
                break;
            case LIST:
                list(ctx);
                break;
            case JOIN:
                join(ctx, cmd);
                break;
            case EXIT:
                exit(ctx);
                break;
            case INVALID:
                invalidCommand(ctx, cmd);
                break;
            default:
                sendMessage(ctx, cmd);
        }
    }

    private void invalidCommand(ChannelHandlerContext ctx, Command cmd) {
        String msg = Messages.get(Messages.INVALID_COMMAND, cmd.getArgument());
        msg(ctx.channel(), msg);
    }

    private void msgRoom(Channel ch, String room, String nick, String text) {
        attr.incrementMessageCounter(ch);
        String msg = nick + ": " + text;
        channels.stream().filter(ignoreNick(nick)).filter(sameRoom(room)).forEach(msg(msg));
        messages.addMsgToActivityLogs(room, msg);
    }

    private Consumer<Channel> msg(String msg) {
        return c -> msg(c, msg);
    }

    private ChannelFuture msg(Channel c, String msg) {
        return c.writeAndFlush(msg + CommandCodec.LF);
    }

    // ChatFilter
    private Predicate<Channel> sameRoom(String room) {
        return c -> Objects.equals(attr.room(c), room);
    }

    private Predicate<Channel> ignoreNick(String nick) {
        return c -> !Objects.equals(attr.nickname(c), nick);
    }
    // ChatFilter

    private void welcomeWithConnectedUsers(Channel c, String room) {
        long countUsers = channels.stream().filter(sameRoom(room)).count();
        String msg = Messages.get(Messages.WELCOME_TO_ROOM, room, countUsers);
        msg(c, msg);
    }

    private void showLastActivityMessages(Channel ch, String room) {
        LinkedList<String> log = messages.getLastActivityLogs().get(room);
        if (log != null) {
            log.forEach(msg -> msg(ch, msg));
        }
    }

    // 1.1 First connection
    // Upon first connection the server replies with a welcome message :)
    private void helo(Channel ch) {
        String nick = attr.nickname(ch);
        // already done?
        if (nick != null)
            return;
        nick = nicknameProvider.reserve();
        if (nick == null) {
            msg(ch, Messages.get(Messages.SORRY_NO_MORE_NAMES_FOR_YOU))
                    .addListener(ChannelFutureListener.CLOSE);
        } else {
            attr.bindNickname(ch, nick);
            channels.add(ch);
            msg(ch, Messages.get(Messages.HELLO, nick));
        }
    }

    // 1.2 Nickname change
    // Command /nick <new name> shall be used to change connected user nickname.
    // If a name is already being used, an appropriate error message should be displayed.
    private void changeNickname(ChannelHandlerContext ctx, Command cmd) {
        Channel ch = ctx.channel();
        String newNick = cmd.getArgument();
        String prev = attr.nickname(ch);
        if (!newNick.equals(prev) && nicknameProvider.available(newNick)) {
            nicknameProvider.release(prev).reserve(newNick);
            attr.bindNickname(ch, newNick);
            msg(ch, Messages.get(Messages.HELLO, newNick));
        } else {
            msg(ch, Messages.get(Messages.COULD_NOT_CHANGE));
        }
    }

    // 1.3 List rooms
    // Command /list would output all current bindRoom names in the server.
    private void list(ChannelHandlerContext ctx) {
        Set<String> rooms = channels.stream().map(attr::room).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Channel ch = ctx.channel();
        rooms.forEach(room -> msg(ch, room));
    }

    // 1.4 Join room
    // Command /join <room name> is used to join an already
    // existing room, or create one as needed.
    private void join(ChannelHandlerContext ctx, Command cmd) {
        Channel ch = ctx.channel();

        String room = cmd.getArgument();
        attr.bindRoom(ch, room);

        // The room should reply with a welcome message,
        // telling how many users are connected in that moment.
        welcomeWithConnectedUsers(ch, room);

        // When joining, a user receives the historic
        // of last 5 previous messages, in order
        showLastActivityMessages(ch, room);

        // Rooms are automatically destroyed when the last connected user leaves.
        destroyRoomAfterLastUserLeaves();
    }

    private void destroyRoomAfterLastUserLeaves() {
        // unnecessary. Because, the channel is removed, together with attributes
    }

    // 1.5 Sending & receiving messages
    // Once a user has joined a bindRoom, she can freely type to send messages to
    // the users in the chat bindRoom, and will receive what others send.
    // To avoid spam, there is a message rate limit of max 30 messages per minute.
    // Messages that exceed the ratio should be automatically dropped
    private void sendMessage(ChannelHandlerContext ctx, Command cmd) {
        Channel ch = ctx.channel();
        String nick = attr.nickname(ch);
        String room = attr.room(ch);

        if (room == null || nick == null) {
            msg(ch, Messages.get(Messages.YOU_CANNOT_SEND_MESSAGES_OUT_OF_ROOM));
        } else if (attr.hasExceededTheRateLimitPerMinute(ch)) {
            msg(ch, Messages.get(Messages.LIMIT_OF_MESSAGES_PER_MINUTE_EXCEED, attr.currentCount(ch)));
        } else {
            msgRoom(ch, room, nick, cmd.getArgument());
        }
    }

    // 1.6 Exit
    // Command /exit will disconnect the player from the server.
    // Upon disconnection, the server shall broadcast an information message to
    // other members of the same chat bindRoom
    private void exit(ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();
        channels.remove(ch);

        String nick = attr.nickname(ch);
        String msg = Messages.get(Messages.LEAVING, nick);

        String room = attr.room(ch);
        msgRoom(ch, room, nick, msg);

        msg(ch, msg).addListener(ChannelFutureListener.CLOSE);
        ch.disconnect();

        nicknameProvider.release(nick);
    }

}
