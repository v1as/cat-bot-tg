package ru.v1as.tg.cat.utils;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.TgTestInvoker;

@RequiredArgsConstructor
public class AssertMessage {
    private final TgTestInvoker testInvoker;
    private final Message message;

    AssertCallback findCallback(String text) {
        final InlineKeyboardMarkup replyMarkup = message.getReplyMarkup();
        return replyMarkup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .filter(b -> b.getText().contains(text))
                .map(b -> new AssertCallback(testInvoker, message, b))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No such callback: " + text));
    }

}
