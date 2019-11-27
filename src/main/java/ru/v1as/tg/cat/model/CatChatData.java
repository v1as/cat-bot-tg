package ru.v1as.tg.cat.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class CatChatData extends ChatData {

    final Map<Integer, CatRequest> msgIdToCatRequests = new HashMap<>();

    public CatChatData(TgChat chat) {
        super(chat, chat.isUserChat());
    }

    public CatRequest getCatRequest(Integer messageId) {
        return msgIdToCatRequests.get(messageId);
    }

    public void registerCatRequest(CatRequest request, Integer messageId) {
        msgIdToCatRequests.put(messageId, request);
    }

    public Collection<CatRequest> getCatRequests() {
        return msgIdToCatRequests.values();
    }
}
