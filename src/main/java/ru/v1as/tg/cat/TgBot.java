package ru.v1as.tg.cat;

import static java.time.LocalDateTime.now;
import static ru.v1as.tg.cat.utils.TgDetailsException.tgException;
import static ru.v1as.tg.cat.utils.ThrowableFunctionalInterfacesWrapper.wrapExceptions;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import ru.v1as.tg.cat.service.clock.BotClock;
import ru.v1as.tg.cat.tg.TgSender;
import ru.v1as.tg.cat.tg.TgUpdateProcessor;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgBot extends TelegramLongPollingBot implements TgSender {

    private static final int MIN_READING_TIMEOUT_MS = 1500;
    private static final int SYMBOL_TIMEOUT_MS = 70;

    private final TgUpdateProcessor updateProcessor;
    private final BotClock clock;
    private final Map<Long, LocalDateTime> readingFinishedMap = new HashMap<>();

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
        if (method instanceof SendMessage) {
            SendMessage sendMessage = (SendMessage) method;
            clock.schedule(
                    wrapExceptions(
                            () -> {
                                log.debug(method.toString());
                                super.executeAsync(method, new ProxyCallback<>(callback));
                            }),
                    getAndIncreaseReadingDelay(sendMessage));

        } else {
            log.debug(method.toString());
            super.executeAsync(method, new ProxyCallback<>(callback));
        }
    }

    private Duration getAndIncreaseReadingDelay(SendMessage message) {
        final long chatId = Long.parseLong(message.getChatId());
        final int ms =
                Math.max(MIN_READING_TIMEOUT_MS, message.getText().length() * SYMBOL_TIMEOUT_MS);
        final LocalDateTime readingFinished =
                readingFinishedMap.computeIfAbsent(chatId, id -> now());
        long delay = Duration.between(now(), readingFinished).toMillis();
        readingFinishedMap.put(
                chatId, (delay < 0 ? now() : readingFinished).plus(Duration.ofMillis(ms)));
        return Duration.ofMillis(delay < 0 ? 0 : delay);
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
