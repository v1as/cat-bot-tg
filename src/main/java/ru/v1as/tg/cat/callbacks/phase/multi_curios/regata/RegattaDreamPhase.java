package ru.v1as.tg.cat.callbacks.phase.multi_curios.regata;

import static ru.v1as.tg.cat.callbacks.phase.multi_curios.regata.BoatPosition.ON_GROTTO;
import static ru.v1as.tg.cat.callbacks.phase.multi_curios.regata.BoatPosition.ON_STAY_SAIL;
import static ru.v1as.tg.cat.callbacks.phase.multi_curios.regata.BoatPosition.ON_TAIL;
import static ru.v1as.tg.cat.utils.CollectionUtils.first;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.v1as.tg.cat.callbacks.phase.MultiUserPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.AbstractMultiUserPhase;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.MarkerOrderChoice;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.regata.RegattaDreamPhase.RegattaDreamPhaseContext;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

// @Component
public class RegattaDreamPhase extends AbstractMultiUserPhase<RegattaDreamPhaseContext> {

    @Override
    protected void open() {
        message("Игра начинается!");
        everyoneMessages(
                "Вы идёте по пирсу, уверенно чеканя шаг.",
                "Море сегодня игриво, но до шторма еще далеко.",
                "Теплый ветер ласково играет с вашими волосами.");
        everyoneMessage(
                me -> "Вас окружает ваша проверенная временем команда " + toString(getOthers(me)));
        everyoneMessages(
                "На краю пирса вас ждёт, нетерпеливо подпрыгивающая на волнах, спортивная яхта");
        final RegattaDreamPhaseContext ctx = getPhaseContext();
        final TgUser owner = ctx.getOwner();
        nextSit(owner);
    }

    private void nextSit() {
        final RegattaDreamPhaseContext ctx = getPhaseContext();
        final Set<TgUser> waiting =
                Stream.of(ctx.onTail, ctx.onGrotto, ctx.onStaysail)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
        final Optional<TgUser> next =
                getUsers().stream().filter(u -> !waiting.contains(u)).findFirst();
        if (next.isPresent()) {
            nextSit(next.get());
        } else {

        }
    }

    private void nextSit(TgUser user) {
        MarkerOrderChoice<BoatPosition> position =
                new MarkerOrderChoice<>(BoatPosition.class, getPhaseContext())
                        .description("Какую роль на яхте вы предпочтёте?")
                        .choice(ON_TAIL, "На руль")
                        .choice(ON_GROTTO, "На грот")
                        .choice(ON_STAY_SAIL, "На стаксель")
                        .userOrderStart(
                                u ->
                                        message(
                                                "Ждем пока "
                                                        + u.getUsernameOrFullName()
                                                        + " выберёт свою роль."))
                        .userOrderFinish(
                                (u, c) -> {
                                    message(u.getUsernameOrFullName() + c.getPublicMessages());
                                    messages(u, c.getUserMessages());
                                })
                        .onTimeout(
                                (u, cs) -> {
                                    message(
                                            u.getUsernameOrFullName()
                                                    + " не успел, но это ничего - судьба сделала выбор за него.");
                                    return first(cs);
                                })
                        .onFinish(this::startRegatta)
                        .done(this::poll);
        final RegattaDreamPhaseContext ctx = getPhaseContext();
    }

    private void startRegatta() {
        message("И так, всё заняли свои места, гонка начинается!");
    }

    public static class RegattaDreamPhaseContext extends MultiUserPhaseContext {

        private TgUser onTail;
        private TgUser onGrotto;
        private TgUser onStaysail;

        public RegattaDreamPhaseContext(
                TgChat chat, TgChat publicChat, TgUser owner, Set<TgUser> guests) {
            super(chat, publicChat, owner, guests);
        }
    }

}
