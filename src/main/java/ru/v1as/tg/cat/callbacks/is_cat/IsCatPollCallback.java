package ru.v1as.tg.cat.callbacks.is_cat;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.tg.LogSentCallback;

@RequiredArgsConstructor
public class IsCatPollCallback extends LogSentCallback<Message> {

    private final CatChatData data;
    private final CatRequest request;

    @Override
    public void onResult(BotApiMethod<Message> method, Message response) {
        request.setMessageId(response.getMessageId());
        data.registerCatRequest(request, response.getMessageId());
    }
}
