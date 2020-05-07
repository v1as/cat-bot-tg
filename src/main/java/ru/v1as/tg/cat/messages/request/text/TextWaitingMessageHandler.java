package ru.v1as.tg.cat.messages.request.text;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.messages.request.RequestWithTimeoutCommandHandler;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Component
@RequiredArgsConstructor
public class TextWaitingMessageHandler
        extends RequestWithTimeoutCommandHandler<TextWaitingRequest> {

    @Override
    public MessageWaitingRequest<TextWaitingRequest> addRequest(
            Message msg, TextWaitingRequest data) {
        return super.addRequest(msg, data);
    }

    @Override
    protected boolean handleRequest(
            Message message, TgChat chat, TgUser user, TextWaitingRequest data) {
        return data.;
    }
}
