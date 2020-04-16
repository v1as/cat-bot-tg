package ru.v1as.tg.cat.utils;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.v1as.tg.cat.TgTestInvoker;

@RequiredArgsConstructor
public abstract class AbstractAssertMessageToSend extends AbstractAssertMessage {

    private final TgTestInvoker testInvoker;

    public AssertCallbackToSend findCallbackToSend(String text) {
        final InlineKeyboardMarkup replyMarkup = getInlineKeyboardMarkup();
        return inlineButtonsStream(replyMarkup)
                .filter(b -> b.getText().contains(text))
                .map(b -> new AssertCallbackToSend(testInvoker, getMessageId(), b))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No such callback: " + text));
    }

    public AssertButtonToSend findButtonToSend(String text) {
        final ReplyKeyboardMarkup replyMarkup = getReplyKeyboardMarkup();
        return buttonsStream(replyMarkup)
                .filter(b -> b.getText().contains(text))
                .map(b -> new AssertButtonToSend(testInvoker, getMessageId(), b))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No such button: " + text));
    }

    protected abstract InlineKeyboardMarkup getInlineKeyboardMarkup();

    protected abstract ReplyKeyboardMarkup getReplyKeyboardMarkup();

    protected abstract Integer getMessageId();

    public List<AssertCallbackToSend> getCallbacksToSend() {
        final InlineKeyboardMarkup replyMarkup = getInlineKeyboardMarkup();
        return inlineButtonsStream(replyMarkup)
                .map(b -> new AssertCallbackToSend(testInvoker, getMessageId(), b))
                .collect(Collectors.toList());
    }

    public AssertCallbackToSend firstCallbacksToSend() {
        return getCallbacksToSend().get(0);
    }
}
