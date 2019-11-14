package ru.v1as.tg.cat.commands;

import static com.google.common.collect.ImmutableList.of;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static ru.v1as.tg.cat.commands.TgCommandRequest.parse;

import org.junit.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;

public class TgCommandRequestTest {
    @Test
    public void parseSimpleCommand() {
        assertEquals(
                new TgCommandRequest(null, "command1", null, emptyList()),
                parse(getMessage("/command1")));

        assertEquals(
                new TgCommandRequest(null, "c_2", null, emptyList()), parse(getMessage("/c_2")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotParseNotCommand() {
        parse(getMessage("text"));
    }

    @Test
    public void parseCommandWithBotName() {
        assertEquals(
                new TgCommandRequest(null, "command", "name", emptyList()),
                parse(getMessage("/command@name")));
    }

    @Test
    public void parseCommandWithArgs() {
        assertEquals(
                new TgCommandRequest(null, "command", null, of("1", "2", "3")),
                parse(getMessage("/command 1 2 3")));
    }

    public Message getMessage(String text) {
        final Message mock = Mockito.mock(Message.class);
        when(mock.getText()).thenReturn(text);
        return mock;
    }
}
