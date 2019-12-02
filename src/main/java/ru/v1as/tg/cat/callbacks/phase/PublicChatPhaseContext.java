package ru.v1as.tg.cat.callbacks.phase;

import lombok.Getter;
import ru.v1as.tg.cat.model.TgChat;

@Getter
public class PublicChatPhaseContext extends PhaseContext {

    private final TgChat publicChat;

    public PublicChatPhaseContext(TgChat chat, TgChat publicChat) {
        super(chat);
        this.publicChat = publicChat;
    }

    public Long getPublicChatId() {
        return publicChat.getId();
    }
}
