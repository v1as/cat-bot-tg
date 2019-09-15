package ru.v1as.tg.cat.callbacks;

import java.util.HashMap;
import java.util.Map;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

public class TgCallbackProcessor {

    private Map<String, TgCallbackParser> parsers = new HashMap<>();
    private Map<String, TgCallBackHandler> handlers = new HashMap<>();

    public TgCallbackProcessor register(TgCallbackParser parser, TgCallBackHandler handler) {
        String parserPrefix = parser.getPrefix();
        for (String prefix : parsers.keySet()) {
            if (prefix.startsWith(parserPrefix) || parserPrefix.startsWith(prefix)) {
                throw new IllegalArgumentException("This parser prefix already in use");
            }
        }
        parsers.put(parserPrefix, parser);
        handlers.put(parserPrefix, handler);
        return this;
    }

    @SuppressWarnings("unchecked")
    public void process(CallbackQuery callback, Chat chat, User user) {
        String data = callback.getData();
        String prefix =
                parsers.keySet().stream()
                        .filter(data::startsWith)
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Callback is not supported: " + data));
        TgCallbackParser parser = parsers.get(prefix);
        TgCallBackHandler handler = handlers.get(prefix);
        handler.handle(parser.parse(data), chat, user, callback);
    }
}
