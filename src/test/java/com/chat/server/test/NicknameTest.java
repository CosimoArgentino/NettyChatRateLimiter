package com.chat.server.test;

import com.chat.server.provider.NicknameProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NicknameTest {

    private NicknameProvider nickNameProvider;

    @Before
    public void setUp() {
        nickNameProvider = new NicknameProvider(1L);
    }

    @Test
    public void nameTest() {
        String nick = nickNameProvider.reserve();
        Assert.assertNotNull(nick);
    }

    @Test
    public void nameAvaiableTest() {
        String nick = nickNameProvider.reserve();
        boolean actual = nickNameProvider.available(nick);
        Assert.assertEquals(false, actual);
    }

    @Test
    public void seedTest() {
        String nick = nickNameProvider.reserve();
        String expected = "Catwoman";
        Assert.assertEquals(expected, nick);
    }

}
