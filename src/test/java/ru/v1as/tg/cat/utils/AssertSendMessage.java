package ru.v1as.tg.cat.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class AssertSendMessage extends AbstractAssertMessage {

    private final SendMessage sendMessage;
    private final Message message;

    public AssertSendMessage(SendMessage sendMessage, Message message) {
        this.sendMessage = sendMessage;
        this.message = message;
    }

    public AssertSendMessage assertText(String text) {
        assertEquals(text, sendMessage.getText());
        return this;
    }

    public void assertReplyTo(Integer pollMessageId) {
        assertEquals(pollMessageId, sendMessage.getReplyToMessageId());
    }

    public AssertSendMessage assertContainText(String value) {
        final String assertMessage =
                String.format(
                        "Expect contain value '%s' but was '%s'", value, sendMessage.getText());
        assertTrue(assertMessage, containText(value));
        return this;
    }

    public boolean containText(String value) {
        return sendMessage.getText().toLowerCase().contains(value.toLowerCase());
    }

    @Override
    protected InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return (InlineKeyboardMarkup) sendMessage.getReplyMarkup();
    }

    @Override
    public Integer getMessageId() {
        return message.getMessageId();
    }

}
