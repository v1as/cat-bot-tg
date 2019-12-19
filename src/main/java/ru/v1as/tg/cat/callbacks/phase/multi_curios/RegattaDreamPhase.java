package ru.v1as.tg.cat.callbacks.phase.multi_curios;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.MultiUserPhaseContext;

@Component
public class RegattaDreamPhase extends AbstractMultiUserPhase<MultiUserPhaseContext> {

    @Override
    protected void open() {
        publicPoll("Регата!").send();
    }
}
