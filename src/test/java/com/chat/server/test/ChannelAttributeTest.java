package com.chat.server.test;

import com.chat.server.channel.ChannelAttribute;
import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChannelAttributeTest {

    public boolean isSubsequence(String t, String s) {
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

    public int[] solution(int N){
        Set<Integer> set = new HashSet<>();
        int i = 0;
        int [] returnArray = new int[N];
        int counter = 0;
        Random rand = new Random();
        while(set.size() != N-2){
            i++;
            int randomNumber = rand.nextInt();
            set.add(randomNumber);
            if(set.size() == i){
                returnArray[i] = randomNumber;
                counter += randomNumber;
            }else{
                i--;
            }
        }
        returnArray[++i] = -(counter);
        return returnArray;
    }

    @Test
    public void testTestTest(){
        int[] x = solution(5);
        int counter = 0;
        for(int i = 0; i < x.length; i++){
            counter+=x[i];
        }
        System.out.println(counter);
    }

    @Test
    public void testTest(){
        isSubsequence("abc", "ahbgdc");
    }
    @Test
    public void testChannelAttribute_bindNickName_nickFound() {
        ChannelAttribute channelAttribute = new ChannelAttribute(5);
        Channel channel = new EmbeddedChannel();
        String nickname = "testNickname";
        channelAttribute.bindNickname(channel, nickname);

        Assert.assertEquals(nickname, channelAttribute.nickname(channel));
    }

    @Test
    public void testChannelAttribute_bindRoom_roomFound() {
        ChannelAttribute channelAttribute = new ChannelAttribute(5);
        Channel channel = new EmbeddedChannel();
        String room = "testRoom";
        channelAttribute.bindRoom(channel, room);

        Assert.assertEquals(room, channelAttribute.room(channel));
    }

    @Test
    public void testChannelAttribute_incrementMessageCounter_messageIncremented() {
        ChannelAttribute channelAttribute = new ChannelAttribute(5);
        Channel channel = new EmbeddedChannel();
        channelAttribute.incrementMessageCounter(channel);

        Assert.assertEquals(1, channelAttribute.currentCount(channel));
    }

    @Test
    public void testChannelAttribute_rateLimiterExceeded_exceedAfterThreshold() {
        ChannelAttribute channelAttribute = new ChannelAttribute(5);
        Channel channel = new EmbeddedChannel();
        // Test when count is below the limit
        for (int i = 0; i < 4; i++) {
            channelAttribute.incrementMessageCounter(channel);
        }

        Assert.assertFalse(channelAttribute.hasExceededTheRateLimitPerMinute(channel));

        // Test when count exceeds the limit
        channelAttribute.incrementMessageCounter(channel);

        Assert.assertTrue(channelAttribute.hasExceededTheRateLimitPerMinute(channel));
    }
}
