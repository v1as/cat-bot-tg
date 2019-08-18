package ru.v1as.tg.cat;

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
class ChatData {

    final Long chatId;
    final boolean isPrivate;
    final Map<Integer, UserData> users = new HashMap<>();
    final Map<Integer, CatRequest> catRequests = new HashMap<>();
    String name;

    ChatData(Chat chat, boolean isPrivate) {
        this.chatId = chat.getId();
        this.isPrivate = isPrivate;
        log.info("Chat data created: " + this);
    }

    void update(Chat chat) {
        this.name = chat.getTitle();
    }

    CatRequest getCatRequest(CallbackQuery callbackQuery) {
        return catRequests.get(callbackQuery.getMessage().getMessageId());
    }

    UserData getUserData(Integer id) {
        return users.get(id);
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
