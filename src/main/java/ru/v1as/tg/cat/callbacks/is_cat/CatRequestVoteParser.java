package ru.v1as.tg.cat.callbacks.is_cat;

import ru.v1as.tg.cat.callbacks.TgCallbackParser;

public class CatRequestVoteParser implements TgCallbackParser<CatRequestVote> {

    @Override
    public String getPrefix() {
        return CatRequestVote.PREFIX;
    }

    @Override
    public CatRequestVote parse(String value) {
        return CatRequestVote.parse(value);
    }
}
