package ru.v1as.tg.cat.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class DbData<T extends ChatData> {

    private final Map<Long, T> chats = new HashMap<>();
    private final Function<TgChat, T> chatDataFactory;

    public T getChatData(TgChat chat) {
        return chats.computeIfAbsent(chat.getId(), id -> chatDataFactory.apply(chat));
    }

    public Collection<T> getChats() {
        return chats.values();
    }
}
