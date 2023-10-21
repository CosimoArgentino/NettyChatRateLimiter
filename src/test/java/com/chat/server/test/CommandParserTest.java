package com.chat.server.test;

import com.chat.server.cmd.Command;
import com.chat.server.cmd.CommandParser;
import org.junit.Assert;
import org.junit.Test;

public class CommandParserTest {

    @Test
    public void invalidCommandTest() {
        String input = "/invalid";
        Command cmd = CommandParser.parse(input);
        Assert.assertEquals(Command.INVALID, cmd);
    }

    @Test
    public void validCommandTest() {
        String input = "/join dc";
        Command cmd = CommandParser.parse(input);
        Assert.assertEquals(Command.JOIN, cmd);
        Assert.assertEquals("dc", cmd.getArgument());
    }

}
