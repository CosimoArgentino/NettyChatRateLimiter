package com.chat.server.channel;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.time.Instant;

public class ChannelAttribute {

    private final static AttributeKey<String> nickAttr = AttributeKey.newInstance("nickname");
    private final static AttributeKey<String> roomAttr = AttributeKey.newInstance("room");
    private final static AttributeKey<Integer> currentCountAttr = AttributeKey.newInstance("currentCount");
    private final static AttributeKey<Integer> previousCountAttr = AttributeKey.newInstance("previousCount");
    private final static AttributeKey<Instant> windowStartAttr = AttributeKey.newInstance("windowStart");
    private final int maxMessagesPerMinute;
    private final long windowSizeInMillis;

    public ChannelAttribute(int maxMessagesPerMinute) {
        this.maxMessagesPerMinute = maxMessagesPerMinute;
        this.windowSizeInMillis = 60_000; // 1 minute in milliseconds, hardcoded
    }

    public String room(Channel ch) {
        return ch.attr(roomAttr).get();
    }

    public void bindRoom(Channel ch, String room) {
        ch.attr(roomAttr).set(room);
    }

    // Assign a nickname to the channel.
    public void bindNickname(Channel c, String nickname) {
        c.attr(nickAttr).set(nickname);
    }

    // Gets the nickname assigned to the channel.
    public String nickname(Channel c) {
        return c.attr(nickAttr).get();
    }

    //set current and previous window counter and the start of current window
    private void bindCountsAndWindowStart(Channel ch, int currentCount, int previousCount, Instant windowStart) {
        ch.attr(currentCountAttr).set(currentCount);
        ch.attr(previousCountAttr).set(previousCount);
        ch.attr(windowStartAttr).set(windowStart);
    }

    private void incrementCurrentCount(Channel ch) {
        int currentCount = currentCount(ch);
        bindCountsAndWindowStart(ch, currentCount + 1, previousCount(ch), windowStart(ch));
    }

    public int currentCount(Channel ch) {
        Integer currentCount = ch.attr(currentCountAttr).get();
        return currentCount == null ? 0 : currentCount;
    }

    public boolean isSubsequence(String s, String t) {
        int pointer = 0;
        int subLength = t.length() -1;
        for(int i = 0; i < s.length(); i++){
            Character tmp = s.charAt(i);
            Character sub = t.charAt(pointer);
            if(tmp == sub) {
                if(pointer == subLength) {
                    return true;
                }
                pointer++;
            }
        }
        return false;
    }

    public int previousCount(Channel ch) {
        Integer previousCount = ch.attr(previousCountAttr).get();
        return previousCount == null ? 0 : previousCount;
    }
    public Instant windowStart(Channel ch) {
        return ch.attr(windowStartAttr).get();
    }

    //increment counter for each message
    public void incrementMessageCounter(Channel ch) {
        final Instant windowStart = windowStart(ch);
        final Instant now = Instant.now();

        //first message, just set everything to 0 and to instant.now as window
        if(windowStart == null){
            bindCountsAndWindowStart(ch, 0, 0, now);
        }else{
            long currentWindowSize = getWindowSizeInMillis(now, windowStart);
            if(hasPassedTheSetWindowSize(currentWindowSize)) {
                //if more than a minute has passed since the last message, set a new
                //window, the currentCount to 0 and the previousCount with the currentCount
                bindCountsAndWindowStart(ch, 0, currentCount(ch), now);
            }
        }
        //increment the currentCounter
        incrementCurrentCount(ch);
    }

    //readability can be improved

    //this new version of the rate limiter use a sliding window counter algorithm.
    //Due to the high volume of messages a chat system can process, using the sliding window log algorithm
    //to save the timestamp even if the message is rejected can produce large memory usage.
    public boolean hasExceededTheRateLimitPerMinute(Channel ch) {
        Instant now = Instant.now();
        Instant windowStart = windowStart(ch);

        if (windowStart == null) {
            //first message
            return false;
        }

        long currentWindowSize = getWindowSizeInMillis(now, windowStart);
        if (hasPassedTheSetWindowSize(currentWindowSize)) {
            //the previous message is older than 1 minute
            return false;
        }

        int currentCount = currentCount(ch);
        int previousCount = previousCount(ch);

        return isLimitReachedOnCurrentWindow(currentWindowSize, currentCount, previousCount);
    }


    //Messages in current window + requests in the previous window * overlap percentage of the rolling window and previous window
    private boolean isLimitReachedOnCurrentWindow(long currentWindowSize, int currentCount, int previousCount) {
        double overlap = (windowSizeInMillis - currentWindowSize) / (double) windowSizeInMillis;
        double perceivedLoad = currentCount + previousCount * overlap;

        return perceivedLoad >= maxMessagesPerMinute;
    }

    private boolean hasPassedTheSetWindowSize(long elapsedMillis) {
        return elapsedMillis >= windowSizeInMillis;
    }

    private static long getWindowSizeInMillis(Instant instant, Instant windowStart) {
        return instant.toEpochMilli() - windowStart.toEpochMilli();
    }
}
