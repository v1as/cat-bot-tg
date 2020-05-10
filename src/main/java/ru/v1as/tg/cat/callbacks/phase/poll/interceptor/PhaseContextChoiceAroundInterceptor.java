package ru.v1as.tg.cat.callbacks.phase.poll.interceptor;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.v1as.tg.cat.tg.MdcTgContext.fromPhaseContext;

import java.util.function.Consumer;
import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.tg.MdcTgContext;

public class PhaseContextChoiceAroundInterceptor<T extends PhaseContext>
        extends AbstractChoiceAroundInterceptor {

    private final ThreadLocal<T> phaseContextThreadLocal;
    private final T phaseContext;
    private final ThreadLocal<Boolean> threadLocalWasSet;

    public PhaseContextChoiceAroundInterceptor(ThreadLocal<T> phaseContextThreadLocal) {
        this.phaseContextThreadLocal = phaseContextThreadLocal;
        this.phaseContext = checkNotNull(phaseContextThreadLocal.get());
        this.threadLocalWasSet = new ThreadLocal<>();
        this.threadLocalWasSet.set(false);
    }

    @Override
    protected void after(ChooseContext ctx) {
        if (threadLocalWasSet.get() != null && threadLocalWasSet.get()) {
            phaseContextThreadLocal.remove();
            threadLocalWasSet.set(false);
        }
    }

    @Override
    protected void before(ChooseContext ctx) {
        if (phaseContextThreadLocal.get() == null) {
            phaseContextThreadLocal.set(phaseContext);
            threadLocalWasSet.set(true);
        }
    }

    @Override
    public void onAround(ChooseContext ctx, Consumer<ChooseContext> method) {
        try (final MdcTgContext ignored = fromPhaseContext(phaseContext)) {
            method.accept(ctx);
        }
    }
}
