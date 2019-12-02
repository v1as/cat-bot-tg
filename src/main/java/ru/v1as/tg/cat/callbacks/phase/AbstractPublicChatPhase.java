package ru.v1as.tg.cat.callbacks.phase;

import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;

public abstract class AbstractPublicChatPhase<T extends PublicChatPhaseContext>
        extends AbstractPhase<T> {

    public TgInlinePoll publicPoll(String text) {
        return poll(text, getPhaseContext().getChatId());
    }
}
