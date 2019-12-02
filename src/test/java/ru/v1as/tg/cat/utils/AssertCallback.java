package ru.v1as.tg.cat.utils;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.v1as.tg.cat.TgTestInvoker;

@RequiredArgsConstructor
public class AssertCallback {

    private final TgTestInvoker testInvoker;
    private final Integer messageId;
    private final InlineKeyboardButton button;

    public void send() {
        testInvoker.sendCallback(messageId, button.getCallbackData());
    }

    public AssertCallback assertCallbackData(String data) {
        Assert.assertEquals(data, button.getCallbackData());
        return this;
    }

    public AssertCallback assertText(String text) {
        Assert.assertEquals(text, button.getText());
        return this;
    }
}
