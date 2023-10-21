package com.chat.server;

import com.chat.server.util.Env;

public class Main {
    public static void main(String... args) {
        int port = Env.getPort();
        ChatServer.build(port).start();
    }
}