package ru.v1as.tg.cat.callbacks.phase.poll.interceptor;

import java.util.function.Consumer;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

public abstract class AbstractChoiceAroundInterceptor implements ChoiceAroundInterceptor {

    private final Logger log =
            org.slf4j.LoggerFactory.getLogger(AbstractChoiceAroundInterceptor.class);

    @Override
    @SneakyThrows
    public final void around(ChooseContext ctx, Consumer<ChooseContext> method) {
        try {
            before(ctx);
            onAround(ctx, method);
        } catch (Exception ex) {
            onError(ctx, ex);
        } finally {
            after(ctx);
        }
    }

    protected void onAround(ChooseContext ctx, Consumer<ChooseContext> method) {
        method.accept(ctx);
    }

    protected void onError(ChooseContext ctx, Exception ex) throws Exception {
        log.error("Poll choice implementation error", ex);
        throw ex;
    }

    protected abstract void after(ChooseContext ctx);

    protected abstract void before(ChooseContext ctx);
}
