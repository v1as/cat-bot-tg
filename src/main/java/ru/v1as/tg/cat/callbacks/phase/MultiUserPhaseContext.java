package ru.v1as.tg.cat.callbacks.phase;

import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Getter
public class MultiUserPhaseContext extends PublicChatPhaseContext {

    private final Set<TgUser> users = new LinkedHashSet<>();
    private final TgUser owner;

    public MultiUserPhaseContext(TgChat chat, TgChat publicChat, TgUser owner) {
        super(chat, publicChat);
        this.owner = owner;
    }

    public void addUser(TgUser user) {
        users.add(user);
    }
}
