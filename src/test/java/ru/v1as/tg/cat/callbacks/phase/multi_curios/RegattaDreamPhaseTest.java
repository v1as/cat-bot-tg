package ru.v1as.tg.cat.callbacks.phase.multi_curios;

import static ru.v1as.tg.cat.model.TgChatWrapper.wrap;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.callbacks.phase.impl.JoinCatFollowPhase;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.RegattaDreamPhaseTest.CuriosConfiguration;
import ru.v1as.tg.cat.model.random.RandomItem;
import ru.v1as.tg.cat.model.random.RandomRequest;
import ru.v1as.tg.cat.service.random.TestRandomChoice;

@Import(CuriosConfiguration.class)
public class RegattaDreamPhaseTest extends AbstractCatBotTest {

    @Autowired private TestRandomChoice<AbstractCuriosCatPhase> randomChoice;
    @Autowired private JoinCatFollowPhase phase;

    @Before
    public void before() {
        super.before();
        randomChoice.setChooser(this::choose);
    }

    @Test
    public void testJoinRegataPhase() {
        phase.open(wrap(public0.getChat()));

        bob.inPublic()
                .getSendMessageToSend()
                .assertContainText("Любопытный Кот гуляет рядом")
                .findCallbackToSend("Пойти за котом")
                .sendStart();

        public0.getDeleteMessage().assertTextContains("Любопытный Кот гуляет рядом");

        mary.inPublic()
                .findSendMessageToSend("приглашает всех в свой сон")
                .findCallbackToSend("Присоединиться")
                .sendStart();
        mary.inPrivate().getSendMessage().assertContainText("Вы ожидаете дрёму");

        public0.getEditMessage().assertContainText("@User1");

        jho.inPublic()
                .findSendMessageToSend("приглашает всех в свой сон")
                .findCallbackToSend("Присоединиться")
                .sendStart();
        jho.inPrivate().getSendMessage().assertContainText("Вы ожидаете дрёму");

        public0.getEditMessage().assertContainText("@User2");

        zakh.inPublic()
                .getSendMessageToSend()
                .assertContainText("приглашает всех в свой сон")
                .findCallbackToSend("Присоединиться")
                .sendStart();
        zakh.inPrivate().getSendMessage().assertContainText("Вы ожидаете дрёму");

        public0.getDeleteMessage().assertTextContains("приглашает всех");
        public0.getSendMessage().assertText("Игра начинается!");

        bob.inPrivate().getSendMessage().assertText("Вы идёте по пирсу, уверенно чеканя шаг.");
        bob.inPrivate().getSendMessage().assertContainText("Море сегодня игриво");
        bob.inPrivate().getSendMessage().assertContainText("Теплый ветер");
        bob.inPrivate()
                .getSendMessage()
                .assertText(
                        "Вас окружает ваша проверенная временем команда @User2, @User3, @User4");
        bob.inPrivate().getSendMessage().assertContainText("На краю пирса вас ждёт");

        mary.inPrivate().getSendMessage().assertText("Вы идёте по пирсу, уверенно чеканя шаг.");
        mary.inPrivate().getSendMessage().assertContainText("Море сегодня игриво");
        mary.inPrivate().getSendMessage().assertContainText("Теплый ветер");
        mary.inPrivate()
                .getSendMessage()
                .assertText(
                        "Вас окружает ваша проверенная временем команда @User1, @User3, @User4");
        mary.inPrivate().getSendMessage().assertContainText("На краю пирса вас ждёт");

        jho.inPrivate().getSendMessage().assertText("Вы идёте по пирсу, уверенно чеканя шаг.");
        jho.inPrivate().getSendMessage().assertContainText("Море сегодня игриво");
        jho.inPrivate().getSendMessage().assertContainText("Теплый ветер");
        jho.inPrivate()
                .getSendMessage()
                .assertText(
                        "Вас окружает ваша проверенная временем команда @User1, @User2, @User4");
        jho.inPrivate().getSendMessage().assertContainText("На краю пирса вас ждёт");

        clearMethodsQueue();
    }

    private AbstractCuriosCatPhase choose(RandomRequest<AbstractCuriosCatPhase> randomRequest) {
        return randomRequest.getItems().stream()
                .map(RandomItem::getValue)
                .filter(q -> q.getName().equals("RegattaJoinPhase"))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @Configuration
    @ComponentScan("ru.v1as.tg.cat.callbacks.phase.curios_cat")
    public static class CuriosConfiguration {}
}
