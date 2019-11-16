package ru.v1as.tg.cat;

import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import ru.v1as.tg.cat.tg.TgSender;
import ru.v1as.tg.cat.tg.TgUpdateProcessor;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgBot extends TelegramLongPollingBot implements TgSender {

    private final TgUpdateProcessor updateProcessor;

    @Value("${tg.bot.username}")
    private String botUsername;

    @Value("${tg.bot.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        updateProcessor.onUpdateReceived(update);
    }

    @SneakyThrows
    @Override
    public <
                    T extends Serializable,
                    Method extends BotApiMethod<T>,
                    Callback extends SentCallback<T>>
            void executeAsync(Method method, Callback callback) {
        log.debug(method.toString());
        super.executeAsync(method, callback);
    }

    @Override
    @SneakyThrows
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        log.debug(method.toString());
        return super.execute(method);
    }

    @Override
    @SneakyThrows
    public Message executeDoc(SendDocument sendDocument) {
        log.debug(sendDocument.toString());
        return super.execute(sendDocument);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
