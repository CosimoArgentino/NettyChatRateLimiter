package com.chat.server.cmd;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class CommandCodec extends MessageToMessageCodec<String, Command> {

    public static final String LF = "\n";

    @Override
    protected void encode(ChannelHandlerContext ctx, Command cmd, List<Object> out) {
        out.add(cmd + LF);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, String line, List<Object> out) {
        out.add(CommandParser.parse(line));
    }
}
