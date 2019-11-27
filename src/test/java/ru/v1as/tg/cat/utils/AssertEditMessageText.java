package ru.v1as.tg.cat.utils;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@RequiredArgsConstructor
public class AssertEditMessageText {
    private final EditMessageText editMessageText;

    public AssertEditMessageText assertText(String value) {
        Assert.assertEquals(value, editMessageText.getText());
        return this;
    }
}
