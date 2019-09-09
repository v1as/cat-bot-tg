package ru.v1as.tg.cat.callback.is_cat;

import ru.v1as.tg.cat.callback.TgCallbackEnumParser;

public class CatRequestVoteParser implements TgCallbackEnumParser<CatRequestVote> {

    @Override
    public String getPrefix() {
        return CatRequestVote.PREFIX;
    }

    @Override
    public CatRequestVote parse(String value) {
        return CatRequestVote.parse(value);
    }

}
