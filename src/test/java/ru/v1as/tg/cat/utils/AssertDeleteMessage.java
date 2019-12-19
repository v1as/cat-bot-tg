package ru.v1as.tg.cat.utils;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@RequiredArgsConstructor
public class AssertDeleteMessage {
    private final DeleteMessage deleteMessage;
    private final Message message;
    private final Boolean deleted;

    public AssertDeleteMessage assertTextContains(String text) {
        Assert.assertTrue(message.getText().contains(text));
        return this;
    }
}
