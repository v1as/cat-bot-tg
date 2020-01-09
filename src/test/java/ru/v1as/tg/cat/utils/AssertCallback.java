package ru.v1as.tg.cat.utils;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@RequiredArgsConstructor
public class AssertCallback<T extends AssertCallback<?>> {

    public static final String START_PREFIX = "start=";
    protected final Integer messageId;
    protected final InlineKeyboardButton button;

    public T assertCallbackData(String data) {
        Assert.assertEquals(data, button.getCallbackData());
        return self();
    }

    public T assertText(String text) {
        Assert.assertEquals(text, button.getText());
        return self();
    }

    @SuppressWarnings("unchecked")
    public T self() {
        return (T) this;
    }
}
