package ru.v1as.tg.cat.utils;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import ru.v1as.tg.cat.TgTestInvoker;

@RequiredArgsConstructor
public class AssertButtonToSend {
    private final TgTestInvoker testInvoker;
    protected final Integer messageId;
    protected final KeyboardButton button;

    public AssertButtonToSend assertText(String text) {
        Assert.assertEquals(text, button.getText());
        return this;
    }

    public void send() {
        testInvoker.sendTextMessage(button.getText());
    }
}
