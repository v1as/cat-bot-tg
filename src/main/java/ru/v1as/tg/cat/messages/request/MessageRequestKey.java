package ru.v1as.tg.cat.messages.request;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

@EqualsAndHashCode
@RequiredArgsConstructor
public class MessageRequestKey {
    private final Long chatId;
    private final Integer userId;

    public MessageRequestKey(MessageRequest request) {
        this(request.chatId(), request.userId());
    }

    public MessageRequestKey(Message msg) {
        this(msg.getChat().getId(), msg.getFrom().getId());
    }
}
