package ru.v1as.tg.cat.model;

import lombok.Data;
import lombok.extern.java.Log;

@Data
@Log
public class ChatData {

    protected final TgChat chat;
    protected final Long chatId;
    protected final boolean isPrivate;
    protected String name;

    public ChatData(TgChat chat, boolean isPrivate) {
        this.chat = chat;
        this.chatId = chat.getId();
        this.isPrivate = isPrivate;
        log.info("Chat data created: " + this);
    }

    public void update(TgChat chat) {
        this.name = chat.getTitle();
    }

    public TgChat getChat() {
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

    public boolean isPublic() {
        return !isPrivate;
    }
}
