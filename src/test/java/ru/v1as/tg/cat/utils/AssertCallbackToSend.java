package ru.v1as.tg.cat.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.v1as.tg.cat.TgTestInvoker;

public class AssertCallbackToSend extends AssertCallback<AssertCallbackToSend> {

    private final TgTestInvoker testInvoker;

    public AssertCallbackToSend(
            TgTestInvoker testInvoker, Integer messageId, InlineKeyboardButton button) {
        super(messageId, button);
        this.testInvoker = testInvoker;
    }

    public AssertCallbackToSend send() {
        testInvoker.sendCallback(messageId, button.getCallbackData());
        return this;
    }

    public AssertCallbackToSend sendStart() {
        final String url = button.getUrl();
        assertNotNull(url);
        final int startCommand = url.indexOf(START_PREFIX);
        assertTrue(startCommand > 0);
        final String startArgument = url.substring(startCommand + START_PREFIX.length());
        testInvoker.sendCommand("/start " + startArgument);
        return self();
    }

    @Override
    public AssertCallbackToSend self() {
        return this;
    }
}
