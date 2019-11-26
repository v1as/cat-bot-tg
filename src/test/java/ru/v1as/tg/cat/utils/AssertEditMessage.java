package ru.v1as.tg.cat.utils;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@RequiredArgsConstructor
public class AssertEditMessage {
    private final EditMessageText editMessageText;

    public AssertEditMessage assertContainText(String text) {
        Assert.assertTrue(editMessageText.getText().toLowerCase().contains(text.toLowerCase()));
        return this;
    }
}
