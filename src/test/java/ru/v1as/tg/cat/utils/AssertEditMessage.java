package ru.v1as.tg.cat.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.TgTestInvoker;

public class AssertEditMessage extends AbstractAssertMessage {
    private final EditMessageText editMessageText;

    public AssertEditMessage(TgTestInvoker testInvoker, EditMessageText editMessageText) {
        super(testInvoker);
        this.editMessageText = editMessageText;
    }

    public AssertEditMessage assertContainText(String text) {
        assertTrue(editMessageText.getText().toLowerCase().contains(text.toLowerCase()));
        return this;
    }

    public AssertEditMessage assertText(String text) {
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
