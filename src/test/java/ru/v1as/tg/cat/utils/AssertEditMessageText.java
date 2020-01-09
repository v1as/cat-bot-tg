package ru.v1as.tg.cat.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@RequiredArgsConstructor
public class AssertEditMessageText extends AbstractAssertMessage {
    private final EditMessageText editMessageText;

    public AssertEditMessageText assertContainText(String text) {
        assertTrue(editMessageText.getText().toLowerCase().contains(text.toLowerCase()));
        return this;
    }

    public AssertEditMessageText assertText(String text) {
        assertEquals(editMessageText.getText(), text);
        return this;
    }

    @Override
    protected InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return editMessageText.getReplyMarkup();
    }

    @Override
    protected Integer getMessageId() {
        return editMessageText.getMessageId();
    }
}
