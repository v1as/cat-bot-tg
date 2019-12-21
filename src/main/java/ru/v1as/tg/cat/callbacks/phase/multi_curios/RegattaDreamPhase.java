package ru.v1as.tg.cat.callbacks.phase.multi_curios;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.MultiUserPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.RegattaDreamPhase.RegattaDreamPhaseContext;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Component
public class RegattaDreamPhase extends AbstractMultiUserPhase<RegattaDreamPhaseContext> {

    @Override
    protected void open() {
        message("Игра начинается!");
        everyoneMessages(
                "Вы идёте по пирсу, гордо чеканя шаг.",
                "Море сегодня игриво, но до шторма еще далеко.",
                "Теплый ветер ласково играет с вашими волосами.");
        everyoneMessage(
                me -> "Вас окружает ваша проверенная временем команда " + toString(getOthers(me)));
        everyoneMessages(
                "На краю пирса вас ждёт, нетерпеливо подпрыгивающая на волнах, спортивная яхта");


    }

    public static class RegattaDreamPhaseContext extends MultiUserPhaseContext {

        private TgUser onTail;
        private TgUser onGrotto;
        private TgUser onStaysail;

        public RegattaDreamPhaseContext(TgChat chat, TgChat publicChat, TgUser owner) {
            super(chat, publicChat, owner);
        }
    }
}
