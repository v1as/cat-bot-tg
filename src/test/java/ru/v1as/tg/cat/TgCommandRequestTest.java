package ru.v1as.tg.cat;

import static com.google.common.collect.ImmutableList.of;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static ru.v1as.tg.cat.TgCommandRequest.parse;

import org.junit.Test;

public class TgCommandRequestTest {

    @Test
    public void parseSimpleCommand() {
        assertEquals(new TgCommandRequest("command1", null, emptyList()), parse("/command1"));

        assertEquals(new TgCommandRequest("c_2", null, emptyList()), parse("/c_2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotParseNotCommand() {
        parse("text");
    }

    @Test
    public void parseCommandWithBotName() {
        assertEquals(new TgCommandRequest("command", "name", emptyList()), parse("/command@name"));
    }

    @Test
    public void parseCommandWithArgs() {
        assertEquals(
                new TgCommandRequest("command", null, of("1", "2", "3")), parse("/command 1 2 3"));
    }

}
