package com.chat.server.cmd;

public enum Command {
    NICK, LIST, JOIN, EXIT, INVALID, MESSAGE;

    private String argument;

    public String getArgument() {
        return argument;
    }

    public Command setArgument(String argument) {
        this.argument = argument;
        return this;
    }
}
