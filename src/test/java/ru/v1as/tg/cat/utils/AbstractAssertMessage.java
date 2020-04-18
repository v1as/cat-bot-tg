package ru.v1as.tg.cat.utils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

public abstract class AbstractAssertMessage {

    public AssertCallback findCallback(String text) {
        final InlineKeyboardMarkup replyMarkup = getInlineKeyboardMarkup();
        return inlineButtonsStream(replyMarkup)
                .filter(b -> b.getText().contains(text))
                .map(b -> new AssertCallback(getMessageId(), b))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No such callback: " + text));
    }

    protected abstract InlineKeyboardMarkup getInlineKeyboardMarkup();

    protected abstract Integer getMessageId();

    public List<AssertCallback> getCallbacks() {
        final InlineKeyboardMarkup replyMarkup = getInlineKeyboardMarkup();
        return inlineButtonsStream(replyMarkup)
                .map(b -> new AssertCallback(getMessageId(), b))
                .collect(Collectors.toList());
    }

    public boolean hasCallbacks() {
        return inlineButtonsStream(getInlineKeyboardMarkup()).findFirst().isPresent();
    }

    protected Stream<InlineKeyboardButton> inlineButtonsStream(InlineKeyboardMarkup replyMarkup) {
        return Optional.ofNullable(replyMarkup)
                .map(InlineKeyboardMarkup::getKeyboard)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .flatMap(Collection::stream);
    }

    protected Stream<KeyboardButton> buttonsStream(ReplyKeyboardMarkup replyMarkup) {
        return Optional.ofNullable(replyMarkup)
                .map(ReplyKeyboardMarkup::getKeyboard)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .flatMap(Collection::stream);
    }
}
