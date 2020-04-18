package ru.v1as.tg.cat.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.v1as.tg.cat.TgTestInvoker;

public class AssertSendMessageToSend extends AbstractAssertMessageToSend {

    private final SendMessage sendMessage;
    private final Message message;

    public AssertSendMessageToSend(
            TgTestInvoker testInvoker, SendMessage sendMessage, Message message) {
        super(testInvoker);
        this.sendMessage = sendMessage;
        this.message = message;
    }

    public AssertSendMessageToSend assertText(String text) {
        assertEquals(text, sendMessage.getText());
        return this;
    }

    public AssertSendMessageToSend assertContainText(String value) {
        final String assertMessage =
                String.format(
                        "Expect contain value '%s' but was '%s'", value, sendMessage.getText());
        assertTrue(assertMessage, containText(value));
        return this;
    }

    public boolean containText(String value) {
        return sendMessage.getText().toLowerCase().contains(value.toLowerCase());
    }

    public AssertSendMessageToSend assertButton(String text) {
        final ReplyKeyboardMarkup replyMarkup = getReplyKeyboardMarkup();
        buttonsStream(replyMarkup)
                .filter(b -> b.getText().contains(text))
                .findAny()
                .orElseThrow(() -> new AssertionError("No such button: " + text));
        return this;
    }

    @Override
    protected InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return (InlineKeyboardMarkup) sendMessage.getReplyMarkup();
    }

    @Override
    protected ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        return (ReplyKeyboardMarkup) sendMessage.getReplyMarkup();
    }

    @Override
    protected Integer getMessageId() {
        return message.getMessageId();
    }
}
