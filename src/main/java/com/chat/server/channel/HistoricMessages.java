package com.chat.server.channel;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HistoricMessages {

    private final int maxOfMessages;
    private final ConcurrentMap<String, LinkedList<String>> lastActivityLogs = new ConcurrentHashMap<>();

    public HistoricMessages(int maxOfMessages) {
        this.maxOfMessages = maxOfMessages;
    }

    public synchronized void addMsgToActivityLogs(String room, String msg) {
        if (room != null) {
            // Add activity to last channel activity logs
            LinkedList<String> activity = lastActivityLogs.getOrDefault(room, new LinkedList<>());
            lastActivityLogs.put(room, activity);

            activity.add(msg);
            while (activity.size() > maxOfMessages)
                activity.remove(0);
        }
    }

    public ConcurrentMap<String, LinkedList<String>> getLastActivityLogs() {
        return lastActivityLogs;
    }
}
