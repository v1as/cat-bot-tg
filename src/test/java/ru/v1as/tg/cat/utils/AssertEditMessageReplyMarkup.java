package ru.v1as.tg.cat.utils;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.TgBotTest;

public class AssertEditMessageReplyMarkup extends AbstractAssertMessage {

    private EditMessageReplyMarkup edit;

    public AssertEditMessageReplyMarkup(TgBotTest testInvoker, EditMessageReplyMarkup edit) {
        super(testInvoker);
        this.edit = edit;
    }

    @Override
    protected InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return edit.getReplyMarkup();
    }

    @Override
    protected Integer getMessageId() {
        return edit.getMessageId();
    }
}
