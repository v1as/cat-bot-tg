package ru.v1as.tg.cat.callbacks.phase.poll;

import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

public class SimplePoll extends AbstractPoll<SimplePoll> {

    public SimplePoll(UnsafeAbsSender sender, TgCallbackProcessor callbackProcessor) {
        super(sender, callbackProcessor);
    }

    @Override
    protected SimplePoll self() {
        return this;
    }
}
