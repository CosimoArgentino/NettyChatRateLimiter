package com.chat.server;

public class ChatServerBackground implements Runnable {

    private ChatServer chatServer;

    public ChatServerBackground(int port, String seed) {
        System.setProperty("SEED", seed);
        this.chatServer = new ChatServer(port);
    }

    @Override
    public void run() {
        this.chatServer.start();
    }
}
