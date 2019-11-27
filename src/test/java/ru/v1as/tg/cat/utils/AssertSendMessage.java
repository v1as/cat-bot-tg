package ru.v1as.tg.cat.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.TgTestInvoker;

@RequiredArgsConstructor
public class AssertSendMessage {
    private final TgTestInvoker testInvoker;
    private final SendMessage sendMessage;
    private final Message message;

    public AssertSendMessage assertText(String text) {
        assertEquals(text, sendMessage.getText());
        return this;
    }

    public AssertSendMessage assertContainText(String value) {
        final String assertMessage =
                String.format(
                        "Expect contain value '%s' but was '%s'", value, sendMessage.getText());
        assertTrue(assertMessage, sendMessage.getText().contains(value));
        return this;
    }

    public AssertCallback findCallback(String text) {
        final InlineKeyboardMarkup replyMarkup =
                (InlineKeyboardMarkup) sendMessage.getReplyMarkup();
        return replyMarkup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .filter(b -> b.getText().contains(text))
                .map(b -> new AssertCallback(testInvoker, message, b))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No such callback: " + text));
    }
}
