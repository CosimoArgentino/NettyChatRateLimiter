package com.chat.server.test;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChatServerHandlerTest {

    private HashMap<String, List<String>> rooms = new HashMap();

    @Before
    public void setUp() throws Exception {
        String nick1 = "nick1";
        String nick2 = "nick2";
        String room1 = "room1";
        rooms.put(room1, Arrays.asList(nick1, nick2));
    }

    @Test
    public void roomCountTest() throws IOException {
       // run the server, join some room and assert the count
    }
/*
    private ExecutorService executor = Executors.newCachedThreadPool();
    private int port = 9999;
    private String seed = "1";
    private HashMap<String, List<String>> rooms = new HashMap<>();

   */
}
