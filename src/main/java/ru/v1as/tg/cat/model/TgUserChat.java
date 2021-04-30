package ru.v1as.tg.cat.model;

public class TgUserChat implements TgChat {

    private final TgUser user;

    public TgUserChat(TgUser user) {
        this.user = user;
    }

    @Override
    public Long getId() {
        return user.getChatId();
    }

    @Override
    public String getTitle() {
        return user.toString();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Boolean isUserChat() {
        return true;
    }
}
