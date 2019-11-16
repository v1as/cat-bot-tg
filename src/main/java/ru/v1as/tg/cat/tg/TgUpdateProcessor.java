package ru.v1as.tg.cat.tg;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TgUpdateProcessor {

    void onUpdateReceived(Update update);
}
