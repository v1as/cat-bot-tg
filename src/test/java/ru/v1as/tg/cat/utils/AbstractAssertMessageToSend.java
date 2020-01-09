package ru.v1as.tg.cat.utils;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.TgTestInvoker;

@RequiredArgsConstructor
public abstract class AbstractAssertMessageToSend extends AbstractAssertMessage {

    private final TgTestInvoker testInvoker;

    public AssertCallbackToSend findCallbackToSend(String text) {
        final InlineKeyboardMarkup replyMarkup = getInlineKeyboardMarkup();
        return buttonsStream(replyMarkup)
                .filter(b -> b.getText().contains(text))
                .map(b -> new AssertCallbackToSend(testInvoker, getMessageId(), b))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No such callback: " + text));
    }

    protected abstract InlineKeyboardMarkup getInlineKeyboardMarkup();

    protected abstract Integer getMessageId();

    public List<AssertCallbackToSend> getCallbacksToSend() {
        final InlineKeyboardMarkup replyMarkup = getInlineKeyboardMarkup();
        return buttonsStream(replyMarkup)
                .map(b -> new AssertCallbackToSend(testInvoker, getMessageId(), b))
                .collect(Collectors.toList());
    }

    public AssertCallbackToSend firstCallbacksToSend() {
        return getCallbacksToSend().get(0);
    }
}
