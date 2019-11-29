package ru.v1as.tg.cat.model;

import lombok.experimental.Delegate;
import org.telegram.telegrambots.meta.api.objects.Chat;

public class TgChatWrapper implements TgChat {

    @Delegate private final Chat chat;

    private TgChatWrapper(Chat chat) {
        this.chat = chat;
    }

    public static TgChat wrap(Chat chat) {
        return new TgChatWrapper(chat);
    }

    @Override
    public String toString() {
        return String.format("Chat(%s:%s)", getTitle(), getId());
    }
}
