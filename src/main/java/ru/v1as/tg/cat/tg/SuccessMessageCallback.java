package ru.v1as.tg.cat.tg;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@RequiredArgsConstructor
public class SuccessMessageCallback extends LogSentCallback<Message> {

    private final Consumer<Message> consumer;

    @Override
    public void onResult(BotApiMethod<Message> method, Message response) {
        super.onResult(method, response);
        consumer.accept(response);
    }
}
