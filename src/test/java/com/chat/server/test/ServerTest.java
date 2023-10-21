package com.chat.server.test;

import com.chat.server.ChatServerBackground;
import com.chat.server.util.Util;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTest {

    private ExecutorService executor = Executors.newCachedThreadPool();
    private int port = 9999;
    private String seed = "1";

    @Before
    public void setUp() {
        executor.submit(new ChatServerBackground(port, seed));
        Util.waitPort(port);
    }

    @Test
    public void firstConnectionTest() throws IOException {
        String input = Util.readFromResource("/first-connection.in");
        String expected = Util.readFromResource("/first-connection.ok");

        Socket clientSocket = new Socket("0.0.0.0", port);

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        outToServer.writeUTF(input);

        String actual = Util.read(clientSocket.getInputStream());

        Assert.assertTrue(actual.contains(expected));
    }

    @After
    public void tearDown() {
        executor.shutdown();
    }
}
