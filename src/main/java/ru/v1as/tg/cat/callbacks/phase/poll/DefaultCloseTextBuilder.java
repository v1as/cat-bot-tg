package ru.v1as.tg.cat.callbacks.phase.poll;

import ru.v1as.tg.cat.Const;
import ru.v1as.tg.cat.callbacks.phase.poll.CloseOnTextBuilder;

public class DefaultCloseTextBuilder implements CloseOnTextBuilder {

    @Override
    public String build(String description, String choose) {
        return description + Const.LINE + "[" + choose + "]";
    }

}
