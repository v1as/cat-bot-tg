package ru.v1as.tg.cat.callbacks.phase.multi_curios;

import static java.time.Duration.ofSeconds;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.MultiUserPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.RegattaDreamPhase.RegattaDreamPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Component
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
            startRegatta();
        }
    }

    private void nextSit(TgUser user) {
        final TgInlinePoll poll = poll("Какую роль на яхте вы предпочтёте?", user);
        poll.timeout(
                new PollTimeoutConfiguration(ofSeconds(15))
                        .removeMsg(true)
                        .onTimeout(() -> onTail(user)));
        final RegattaDreamPhaseContext ctx = getPhaseContext();
        if (ctx.onTail == null) {
            poll.choice("На руль", c -> this.onTail(user));
        }
        if (ctx.onGrotto == null) {
            poll.choice("На грот", c -> this.onGrotto(user));
        }
        if (ctx.onStaysail == null) {
            poll.choice("На стаксель", c -> this.onStaysail(user));
        }
        if (poll.getChoices().size() > 1) {
            messages(
                    getOthers(user),
                    "Ждем пока " + user.getUsernameOrFullName() + " выберёт свою роль.");
        } else {
            if (ctx.onTail == null) {
                this.onTail(user);
                return;
            }
            if (ctx.onGrotto == null) {
                this.onGrotto(user);
                return;
            }
            if (ctx.onStaysail == null) {
                this.onStaysail(user);
            }
        }
    }

    private void onTail(TgUser user) {
        message(user.getUsernameOrFullName() + " сегодня рулевой.");
        messages(user, "Вы удобно устраиваетесь на хвосте.", "Сегодня вы будете рулить яхтой.");
        getPhaseContext().onTail = user;
        nextSit();
    }

    private void startRegatta() {
        message("И так, всё заняли свои места, гонка начинается!");
    }

    private void onGrotto(TgUser user) {
        message(user.getUsernameOrFullName() + " сегодня на гроте.");
        messages(
                user,
                "Вы размещаетесь на борту, немного тесновато, но это не так важно.",
                "Над головой трепещет белоснежный парус - грот.");
        getPhaseContext().onGrotto = user;
        nextSit();
    }

    private void onStaysail(TgUser user) {
        message(user.getUsernameOrFullName() + " сегодня на стакселе.");
        messages(
                user,
                "Вы размещаетесь на носу, ",
                "Ветер встречает вас нежным поцелуем.",
                "Команда позади вас вселяет в вас уверенность.");
        getPhaseContext().onStaysail = user;
        nextSit();
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
