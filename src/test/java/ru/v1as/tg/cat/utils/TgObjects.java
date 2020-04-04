package ru.v1as.tg.cat.utils;

import static org.mockito.Mockito.when;

import lombok.experimental.UtilityClass;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;

@UtilityClass
public class TgObjects {

    public static Message message(String text) {
        final Message mock = Mockito.mock(Message.class);
        when(mock.getText()).thenReturn(text);
        return mock;
    }
}
