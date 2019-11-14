package ru.v1as.tg.cat.callbacks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Slf4j
@Component
public class TgCallbackProcessor {

    private DefaultTgCallbackHandler defaultCallbackHandler;

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
                handler.getClass(),
                handler.getPrefix());
    }

    public void drop(TgCallBackHandler handler) {
        prefixToHandler.remove(handler.getPrefix());
    }

    @SuppressWarnings("unchecked")
    public void process(CallbackQuery callback, TgChat chat, TgUser user) {
        String data = callback.getData();
        log.debug("Callback '{}' received from user '{}' in chat '{}'", data, user, chat);

        Optional<TgCallBackHandler> callbackHandler =
                prefixToHandler.keySet().stream()
                        .filter(data::startsWith)
                        .findFirst()
                        .map(prefixToHandler::get);
        if (callbackHandler.isPresent()) {
            TgCallBackHandler handler = callbackHandler.get();
            log.debug("Callback found: {}", handler.getClass().getName());
            handler.handle(handler.parse(data), chat, user, callback);
        } else if (defaultCallbackHandler != null) {
            log.debug(
                    "Callback not found, using default: {}",
                    defaultCallbackHandler.getClass().getName());
            defaultCallbackHandler.handle(chat, user, callback);
        } else {
            throw new IllegalStateException("Callback is not supported: " + data);
        }
    }

    @Autowired
    public void setDefaultCallbackHandler(DefaultTgCallbackHandler defaultCallbackHandler) {
        this.defaultCallbackHandler = defaultCallbackHandler;
    }
}
