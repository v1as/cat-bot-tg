package ru.v1as.tg.cat.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
        assertTrue(assertMessage, containText(value));
        return this;
    }

    public AssertCallback findCallback(String text) {
        final InlineKeyboardMarkup replyMarkup =
                (InlineKeyboardMarkup) sendMessage.getReplyMarkup();
        return buttonsStream(replyMarkup)
                .filter(b -> b.getText().contains(text))
                .map(b -> new AssertCallback(testInvoker, message, b))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No such callback: " + text));
    }

    public List<AssertCallback> getCallbacks() {
        final InlineKeyboardMarkup replyMarkup =
                (InlineKeyboardMarkup) sendMessage.getReplyMarkup();
        return buttonsStream(replyMarkup)
                .map(b -> new AssertCallback(testInvoker, message, b))
                .collect(Collectors.toList());
    }

    public boolean hasCallbacks() {
        final InlineKeyboardMarkup replyMarkup =
                (InlineKeyboardMarkup) sendMessage.getReplyMarkup();
        return buttonsStream(replyMarkup).findFirst().isPresent();
    }

    private Stream<InlineKeyboardButton> buttonsStream(InlineKeyboardMarkup replyMarkup) {
        return Optional.ofNullable(replyMarkup)
                .map(InlineKeyboardMarkup::getKeyboard)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .flatMap(Collection::stream);
    }

    public boolean containText(String value) {
        return sendMessage.getText().toLowerCase().contains(value.toLowerCase());
    }
}
