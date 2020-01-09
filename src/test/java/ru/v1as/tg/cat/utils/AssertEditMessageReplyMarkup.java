package ru.v1as.tg.cat.utils;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class AssertEditMessageReplyMarkup extends AbstractAssertMessage {

    private EditMessageReplyMarkup edit;

    public AssertEditMessageReplyMarkup(EditMessageReplyMarkup edit) {
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
