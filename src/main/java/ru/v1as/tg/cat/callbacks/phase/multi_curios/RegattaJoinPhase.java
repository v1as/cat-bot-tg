package ru.v1as.tg.cat.callbacks.phase.multi_curios;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.MultiUserPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.UserDreamJoinPhase.UserDreamJoinPhaseConnect;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.UserDreamJoinPhase.UserDreamJoinPhaseContext;

@Component
@RequiredArgsConstructor
public class RegattaJoinPhase extends AbstractCuriosCatPhase {

    private final UserDreamJoinPhase dreamJoinPhase;
    private final RegattaDreamPhase regattaDreamPhase;

    @Override
    protected void open() {
        final CuriosCatContext ctx = getPhaseContext();
        final UserDreamJoinPhaseConnect<MultiUserPhaseContext> connect =
                new UserDreamJoinPhaseConnect<>(regattaDreamPhase, 3, this::buildContext);
        close();
        dreamJoinPhase.open(new UserDreamJoinPhaseContext(ctx.getChat(), ctx.getPublicChat(), ctx.getUser(), connect));
    }

    private MultiUserPhaseContext buildContext(UserDreamJoinPhaseContext context) {
        return new MultiUserPhaseContext(
                context.getChat(), context.getPublicChat(), context.getOwner());
    }
}
