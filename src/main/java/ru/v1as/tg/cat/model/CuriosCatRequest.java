package ru.v1as.tg.cat.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CuriosCatRequest extends TgRequestPoll<String> {

    public CuriosCatRequest(Long chatId) {
        super(chatId);
    }
}
