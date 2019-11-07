package ru.v1as.tg.cat.callbacks.phase.poll.interceptor;

import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.service.clock.BotClock;

public class TimeoutPhaseContextChoiceAroundInterceptor<T extends PhaseContext>
        extends PhaseContextChoiceAroundInterceptor<T> {

    private final BotClock botClock;
    private final int milliseconds;

    public TimeoutPhaseContextChoiceAroundInterceptor(
            ThreadLocal<T> phaseContextThreadLocal, BotClock botClock, int milliseconds) {
        super(phaseContextThreadLocal);
        this.botClock = botClock;
        this.milliseconds = milliseconds;
    }

    @Override
    protected void before(ChooseContext ctx) {
        this.botClock.wait(milliseconds);
        super.before(ctx);
    }
}
