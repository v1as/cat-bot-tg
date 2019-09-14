package ru.v1as.tg.cat.model;

import static lombok.AccessLevel.PRIVATE;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;

@Data
@Log
@FieldDefaults(level = PRIVATE)
public class ChatData {

    final Long chatId;
    final boolean isPrivate;
    final Map<Integer, UserData> users = new HashMap<>();
    final Map<Integer, CatRequest> catRequests = new HashMap<>();
    String name;

    public ChatData(Chat chat, boolean isPrivate) {
        this.chatId = chat.getId();
        this.isPrivate = isPrivate;
        log.info("Chat data created: " + this);
    }

    public void update(Chat chat) {
        this.name = chat.getTitle();
    }

    public CatRequest getCatRequest(CallbackQuery callbackQuery) {
        return catRequests.get(callbackQuery.getMessage().getMessageId());
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
