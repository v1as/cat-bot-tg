package ru.v1as.tg.cat.callbacks.phase.poll.interceptor;

import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

public class PhaseContextChoiceAroundInterceptor<T extends PhaseContext>
        extends AbstractChoiceAroundInterceptor {

    private final ThreadLocal<T> phaseContextThreadLocal;
    private final T phaseContext;

    public PhaseContextChoiceAroundInterceptor(ThreadLocal<T> phaseContextThreadLocal) {
        this.phaseContextThreadLocal = phaseContextThreadLocal;
        this.phaseContext = phaseContextThreadLocal.get();
    }

    @Override
    protected void after(ChooseContext ctx) {
        phaseContextThreadLocal.remove();
    }

    @Override
    protected void before(ChooseContext ctx) {
        phaseContextThreadLocal.set(phaseContext);
    }
}
