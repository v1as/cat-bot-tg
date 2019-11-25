package ru.v1as.tg.cat.utils;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.v1as.tg.cat.TgTestInvoker;

@RequiredArgsConstructor
public class AssertCallback {

    private final TgTestInvoker testInvoker;
    private final Message message;
    private final InlineKeyboardButton button;

    public void send() {
        testInvoker.sendCallback(message.getMessageId(), button.getCallbackData());
    }
}
