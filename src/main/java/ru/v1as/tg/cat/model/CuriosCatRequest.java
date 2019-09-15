package ru.v1as.tg.cat.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CuriosCatRequest extends TgRequestPoll<String> {

    public CuriosCatRequest(ChatData chat) {
        super(chat);
    }

    @Override
    public void finish(String result) {
        super.finish(result);
        log.info("Curios cat request was finished {}", this);
    }

    @Override
    public void cancel() {
        super.cancel();
        log.info("Curios cat request was canceled {}", this);
    }

    @Override
    public String toString() {
        return "CuriosCatRequest{"
                + "chat="
                + chat
                + ", created="
                + created
                + ", finished="
                + finished
                + ", canceled="
                + canceled
                + ", voteMessage="
                + (voteMessage != null ? voteMessage.getMessageId().toString() : "")
                + '}';
    }
}
