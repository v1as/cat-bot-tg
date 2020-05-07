package ru.v1as.tg.cat.messages.request;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public abstract class SimpleRequestCommandHandler extends RequestWithTimeoutCommandHandler<Object> {

    @Override
    final protected boolean handleRequest(Message message, TgChat chat, TgUser user, Object data) {
        return this.handleRequest(message, chat, user);
    }

    protected abstract boolean handleRequest(Message message, TgChat chat, TgUser user);

    public MessageWaitingRequest<Object> addRequest(Message msg) {
        return super.addRequest(msg, null);
    }
}
