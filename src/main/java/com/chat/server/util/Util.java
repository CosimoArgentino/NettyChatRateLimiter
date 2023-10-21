package com.chat.server.util;

import com.chat.server.cmd.CommandCodec;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public final class Util {

    private Util() {
    }

    public static boolean isNullOrEmpty(String s){
        return Objects.isNull(s) || s.isEmpty();
    }
    public static boolean available(final int port) {
        try (Socket ignored = new Socket("0.0.0.0", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

    public static void waitPort(final int port) {
        final int counter = 0;
        while (available(port) && counter < 100) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static String read(final InputStream inputStream) {
        final StringBuilder sb = new StringBuilder();
        final Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext())
            sb.append(scanner.nextLine() + CommandCodec.LF);
        return sb.toString();
    }

    public static String readFromResource(final String resourceName) {
        final InputStream resourceAsStream = Util.class.getResourceAsStream(resourceName);
        return read(resourceAsStream);
    }

}
