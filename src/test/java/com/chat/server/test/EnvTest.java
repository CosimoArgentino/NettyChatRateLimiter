package com.chat.server.test;

import com.chat.server.util.Env;
import org.junit.Assert;
import org.junit.Test;

public class EnvTest {

    @Test
    public void portTest() {
        Integer expected = Env.getPort();
        Assert.assertNotNull(expected);
    }

    @Test
    public void seedTest() {
        Long expected = 1L;
        System.setProperty("SEED", expected.toString());
        Assert.assertEquals(expected, Env.getSeed());
    }

}
