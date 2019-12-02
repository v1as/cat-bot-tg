package ru.v1as.tg.cat.callbacks.phase.poll;

import static ru.v1as.tg.cat.EmojiConst.SMALL_ORANGE_DIAMOND;

import ru.v1as.tg.cat.service.BotConfiguration;

public class UpdateWithChoiceTextBuilder implements CloseOnTextBuilder {

    @Override
    public String build(String description, String choose) {
        return description + BotConfiguration.LINE + SMALL_ORANGE_DIAMOND + "[" + choose + "]";
    }
}
