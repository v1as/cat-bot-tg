package ru.v1as.tg.cat.callbacks.curios;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CuriosCatRequest;
import ru.v1as.tg.cat.tg.LogSentCallback;

@RequiredArgsConstructor
public class CuriosCatPolLCallback extends LogSentCallback<Message> {

    private final CatChatData data;
    private final CuriosCatRequest request;

    @Override
    public void onResult(BotApiMethod<Message> method, Message resp) {
        request.setMessageId(resp.getMessageId());
        data.registerCuriosCatRequest(request);
    }
}
