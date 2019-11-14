package ru.v1as.tg.cat;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TgUpdateBeforeHandler {

    void register(Update update);
}
