package com.chat.server.test;

import com.chat.server.i18n.Messages;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

public class MessagesTest {

    private Locale enLocale = new Locale("en", "EN");
    private Locale esLocale = new Locale("es", "ES");
    private Locale ptLocale = new Locale("pt", "BR");

    @Test
    public void helloENTest() {
        String expected = "Hello Humberto Dias";
        String actual = Messages.get(enLocale, Messages.HELLO, "Humberto Dias");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void helloESTest() {
        String expected = "Hola Humberto Dias";
        String actual = Messages.get(esLocale, Messages.HELLO, "Humberto Dias");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void helloPT_BRTest() {
        String expected = "Olá Humberto Dias";
        String actual = Messages.get(ptLocale, Messages.HELLO, "Humberto Dias");
        Assert.assertEquals(expected, actual);
    }

}
