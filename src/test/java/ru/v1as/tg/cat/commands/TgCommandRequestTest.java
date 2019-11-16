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
        Message msg = getMessage("/command1");
        assertEquals(new TgCommandRequest(msg, "command1", null, emptyList()), parse(msg));

        msg = getMessage("/c_2");
        assertEquals(new TgCommandRequest(msg, "c_2", null, emptyList()), parse(msg));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotParseNotCommand() {
        parse(getMessage("text"));
    }

    @Test
    public void parseCommandWithBotName() {
        final Message msg = getMessage("/command@name");
        assertEquals(new TgCommandRequest(msg, "command", "name", emptyList()), parse(msg));
    }

    @Test
    public void parseCommandWithArgs() {
        final Message msg = getMessage("/command 1 2 3");
        assertEquals(new TgCommandRequest(msg, "command", null, of("1", "2", "3")), parse(msg));
    }

    public Message getMessage(String text) {
        final Message mock = Mockito.mock(Message.class);
        when(mock.getText()).thenReturn(text);
        return mock;
    }
}
