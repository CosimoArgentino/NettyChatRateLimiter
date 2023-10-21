package com.chat.server.cmd;

import com.chat.server.util.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chat.server.cmd.Command.INVALID;

public final class CommandParser {

    private CommandParser() {
    }

    private static final Pattern pattern = Pattern
            .compile("/(?<cmd>nick|list|join|exit)\\s*(?<arg>.*)", Pattern.CASE_INSENSITIVE);

    public static Command parse(String line) {
        if (line.startsWith("/")) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String cmd = matcher.group("cmd").toUpperCase();
                String arg = matcher.group("arg");
                Command command = Command.valueOf(cmd).setArgument(arg);
                switch (command){
                    case NICK:
                    case JOIN:
                        if(Util.isNullOrEmpty(arg))
                            command = INVALID;
                }
                return command;
            }
            return Command.INVALID.setArgument(line);
        }
        return Command.MESSAGE.setArgument(line);
    }

}
