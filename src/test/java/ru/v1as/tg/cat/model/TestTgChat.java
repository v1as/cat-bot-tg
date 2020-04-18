package ru.v1as.tg.cat.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestTgChat implements TgChat {

    private final boolean isUserChat;
    private final long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public Boolean isUserChat() {
        return isUserChat;
    }
}
