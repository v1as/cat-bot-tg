package ru.v1as.tg.cat.callbacks.phase.multi_curios.regata;

import lombok.RequiredArgsConstructor;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.UserDreamJoinPhase;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.UserDreamJoinPhase.UserDreamJoinPhaseConnect;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.UserDreamJoinPhase.UserDreamJoinPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.regata.RegattaDreamPhase.RegattaDreamPhaseContext;

// @Component
@RequiredArgsConstructor
public class RegattaJoinPhase extends AbstractCuriosCatPhase {

    private final UserDreamJoinPhase dreamJoinPhase;
    private final RegattaDreamPhase regattaDreamPhase;

    @Override
    protected void open() {
        final CuriosCatContext ctx = getPhaseContext();
        final UserDreamJoinPhaseConnect<RegattaDreamPhaseContext> connect =
                new UserDreamJoinPhaseConnect<>(regattaDreamPhase, 3, this::buildContext);
        close();
        dreamJoinPhase.open(
                new UserDreamJoinPhaseContext(
                        ctx.getChat(), ctx.getPublicChat(), ctx.getUser(), connect));
    }

    private RegattaDreamPhaseContext buildContext(UserDreamJoinPhaseContext ctx) {
        return new RegattaDreamPhaseContext(
                ctx.getChat(), ctx.getPublicChat(), ctx.getOwner(), ctx.getGuests());
    }
}
