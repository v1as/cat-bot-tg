package ru.v1as.tg.cat.callback;

import java.util.HashMap;
import java.util.Map;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

public class TgEnumCallbackProcessor {

    private Map<String, TgCallbackEnumParser<? extends Enum>> parsers = new HashMap<>();
    private Map<String, EnumCallBackHandler<? extends Enum>> handlers = new HashMap<>();

    public TgEnumCallbackProcessor register(
            TgCallbackEnumParser<? extends Enum> parser,
            EnumCallBackHandler<? extends Enum> handler) {
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
        TgCallbackEnumParser parser = parsers.get(prefix);
        EnumCallBackHandler<Enum> handler = (EnumCallBackHandler<Enum>) handlers.get(prefix);
        Enum parse = parser.parse(data);
        handler.handle(parse, chat, user, callback);
    }
}
