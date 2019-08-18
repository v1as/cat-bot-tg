package ru.v1as.tg.cat;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

@RequiredArgsConstructor
public class CatPollCallback implements SentCallback<Message> {

    private final DbData data;
    private final CatRequest catRequest;

    @Override
    public void onResult(BotApiMethod<Message> method, Message response) {
        catRequest.setVoteMessage(response);
        data.register(catRequest, response);
    }

    @Override
    public void onError(BotApiMethod<Message> method, TelegramApiRequestException apiException) {}

    @Override
    public void onException(BotApiMethod<Message> method, Exception exception) {}
}
