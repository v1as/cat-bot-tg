package ru.v1as.tg.cat.model;

import lombok.Data;
import lombok.extern.java.Log;
import org.telegram.telegrambots.meta.api.objects.Chat;

@Data
@Log
public class ChatData {

    protected final Chat chat;
    protected final Long chatId;
    protected final boolean isPrivate;
    protected String name;

    public ChatData(Chat chat, boolean isPrivate) {
        this.chat = chat;
        this.chatId = chat.getId();
        this.isPrivate = isPrivate;
        log.info("Chat data created: " + this);
    }

    public void update(Chat chat) {
        this.name = chat.getTitle();
    }

    public Chat getChat() {
        return chat;
    }

    @Override
    public String toString() {
        return "ChatData{"
                + "chatId="
                + chatId
                + ", isPrivate="
                + isPrivate
                + ", name='"
                + name
                + '\''
                + '}';
    }
}
