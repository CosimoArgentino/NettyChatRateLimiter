package com.chat.server.test;

import com.chat.server.provider.NicknameProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NicknameProviderTest {

    private NicknameProvider nicknameProvider;
    private Long seed = 1L;

    @Before
    public void setUp() {
        nicknameProvider = new NicknameProvider(seed);
    }

    @Test
    public void nickNameSeedTest() {
        String firstNickname = nicknameProvider.reserve();
        String expected = "Catwoman";
        Assert.assertEquals(expected, firstNickname);
    }

}
