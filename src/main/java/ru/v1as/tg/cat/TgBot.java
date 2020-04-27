package ru.v1as.tg.cat;

import static ru.v1as.tg.cat.utils.TgDetailsException.tgException;

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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
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
        super.executeAsync(method, new ProxyCallback<>(callback));
    }

    @Override
    @SneakyThrows
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        log.debug(method.toString());
        final T result;
        try {
            result = super.execute(method);
        } catch (TelegramApiException e) {
            throw tgException(e);
        }
        return result;
    }

    @Override
    @SneakyThrows
    public Message executeDoc(SendDocument sendDocument) {
        log.debug(sendDocument.toString());
        final Message execute;
        try {
            execute = super.execute(sendDocument);
        } catch (TelegramApiException e) {
            throw tgException(e);
        }
        return execute;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private static class ProxyCallback<T extends Serializable> implements SentCallback<T> {

        private final SentCallback<T> callback;

        public <Callback extends SentCallback<T>> ProxyCallback(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void onResult(BotApiMethod<T> botApiMethod, T serializable) {
            callback.onResult(botApiMethod, serializable);
        }

        @Override
        public void onError(BotApiMethod<T> botApiMethod, TelegramApiRequestException e) {
            callback.onError(botApiMethod, tgException(e));
        }

        @Override
        public void onException(BotApiMethod<T> botApiMethod, Exception e) {
            callback.onException(botApiMethod, tgException(e));
        }
    }
}
