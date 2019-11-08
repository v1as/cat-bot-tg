package ru.v1as.tg.cat.callbacks.phase.poll.interceptor;

import java.util.function.Consumer;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

public class NoopChoiceAroundInterceptor implements ChoiceAroundInterceptor {

    @Override
    public void around(ChooseContext ctx, Consumer<ChooseContext> method) {
        method.accept(ctx);
    }

}
