package ru.v1as.tg.cat.callbacks.phase;

import lombok.Getter;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public class PersonalPublicChatPhaseContext extends PublicChatPhaseContext {

    @Getter private final TgUser user;

    public PersonalPublicChatPhaseContext(TgChat chat, TgUser user, TgChat publicChat) {
        super(chat, publicChat);
        this.user = user;
    }
}
