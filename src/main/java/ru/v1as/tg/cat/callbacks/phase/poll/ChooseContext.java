package ru.v1as.tg.cat.callbacks.phase.poll;

import lombok.Value;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Value
public class ChooseContext {
    TgChat chat;
    TgUser user;
    CallbackQuery callbackQuery;
}
