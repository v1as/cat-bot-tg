package ru.v1as.tg.cat.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.v1as.tg.cat.TgTestInvoker;

@RequiredArgsConstructor
public class AssertCallback {

    public static final String START_PREFIX = "start=";
    private final TgTestInvoker testInvoker;
    private final Integer messageId;
    private final InlineKeyboardButton button;

    public AssertCallback send() {
        testInvoker.sendCallback(messageId, button.getCallbackData());
        return this;
    }

    public AssertCallback assertCallbackData(String data) {
        Assert.assertEquals(data, button.getCallbackData());
        return this;
    }

    public AssertCallback assertText(String text) {
        Assert.assertEquals(text, button.getText());
        return this;
    }

    public AssertCallback sendStart() {
        final String url = button.getUrl();
        assertNotNull(url);
        final int startCommand = url.indexOf(START_PREFIX);
        assertTrue(startCommand > 0);
        final String startArgument = url.substring(startCommand + START_PREFIX.length());
        testInvoker.sendCommand("/start " + startArgument);
        return this;
    }
}
