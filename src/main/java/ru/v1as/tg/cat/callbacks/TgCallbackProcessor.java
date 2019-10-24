package ru.v1as.tg.cat.callbacks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@Component
public class TgCallbackProcessor {

    private Map<String, TgCallBackHandler> prefixToHandler = new HashMap<>();

    public TgCallbackProcessor(List<TgCallBackHandler> handlers) {
        handlers.forEach(this::register);
    }

    public void register(TgCallBackHandler handler) {
        String parserPrefix = handler.getPrefix();
        for (String prefix : prefixToHandler.keySet()) {
            if (prefix.startsWith(parserPrefix) || parserPrefix.startsWith(prefix)) {
                throw new IllegalArgumentException("This parser prefix already in use");
            }
        }
        prefixToHandler.put(parserPrefix, handler);
        log.info(
                "Callback handler '{}' registered with prefix '{}'",
                handler.getClass().getSimpleName(),
                handler.getPrefix());
    }

    public void drop(TgCallBackHandler handler) {
        prefixToHandler.remove(handler.getPrefix());
    }

    @SuppressWarnings("unchecked")
    public void process(CallbackQuery callback, Chat chat, User user) {
        String data = callback.getData();
        log.debug("Callback '{}' received from user '{}' in chat '{}'", data, user, chat);
        String prefix =
                prefixToHandler.keySet().stream()
                        .filter(data::startsWith)
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Callback is not supported: " + data));
        TgCallBackHandler handler = prefixToHandler.get(prefix);
        handler.handle(handler.parse(data), chat, user, callback);
    }
}
