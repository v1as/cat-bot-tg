package ru.v1as.tg.cat.utils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.v1as.tg.cat.TgTestInvoker;

@RequiredArgsConstructor
public abstract class AbstractAssertMessage {

    private final TgTestInvoker testInvoker;

    public AssertCallback findCallback(String text) {
        final InlineKeyboardMarkup replyMarkup = getInlineKeyboardMarkup();
        return buttonsStream(replyMarkup)
                .filter(b -> b.getText().contains(text))
                .map(b -> new AssertCallback(testInvoker, getMessageId(), b))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No such callback: " + text));
    }

    protected abstract InlineKeyboardMarkup getInlineKeyboardMarkup();

    protected abstract Integer getMessageId();

    public List<AssertCallback> getCallbacks() {
        final InlineKeyboardMarkup replyMarkup = getInlineKeyboardMarkup();
        return buttonsStream(replyMarkup)
                .map(b -> new AssertCallback(testInvoker, getMessageId(), b))
                .collect(Collectors.toList());
    }

    public boolean hasCallbacks() {
        return buttonsStream(getInlineKeyboardMarkup()).findFirst().isPresent();
    }

    private Stream<InlineKeyboardButton> buttonsStream(InlineKeyboardMarkup replyMarkup) {
        return Optional.ofNullable(replyMarkup)
                .map(InlineKeyboardMarkup::getKeyboard)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .flatMap(Collection::stream);
    }
}
