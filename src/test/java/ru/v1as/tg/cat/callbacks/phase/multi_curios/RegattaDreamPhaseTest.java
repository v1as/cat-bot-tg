package ru.v1as.tg.cat.callbacks.phase.multi_curios;

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
import ru.v1as.tg.cat.utils.AssertCallback;

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
        switchToPublicChat();

        phase.open(getTgChat());
        final AssertCallback followCatCb =
                popSendMessage()
                        .assertContainText("Любопытный Кот гуляет рядом")
                        .findCallback("Пойти за котом");

        switchFirstUserChat();
        followCatCb.sendStart();

        switchToPublicChat();
        popDeleteMessage().assertTextContains("Любопытный Кот гуляет рядом");

        final AssertCallback joinCallback =
                popSendMessage()
                        .assertContainText("приглашает всех в свой сон")
                        .findCallback("Присоединиться");

        switchToSecondUser();
        joinCallback.sendStart();
        popSendMessage().assertContainText("Вы ожидаете дрёму");

        switchToPublicChat();
        popEditMessage().assertContainText("@User1");

        switchToThirdUser();
        joinCallback.sendStart();
        popSendMessage().assertContainText("Вы ожидаете дрёму");

        switchToPublicChat();
        popEditMessage().assertContainText("@User2");

        switchToFourthUser();
        joinCallback.sendStart();
        popSendMessage().assertContainText("Вы ожидаете дрёму");

        switchToPublicChat();
        popDeleteMessage().assertTextContains("приглашает всех");

        popSendMessage().assertText("Игра начинается!");

        switchFirstUserChat();
        popSendMessage().assertText("Вы идёте по пирсу, уверенно чеканя шаг.");
        popSendMessage().assertContainText("Море сегодня игриво");
        popSendMessage().assertContainText("Теплый ветер");
        popSendMessage()
                .assertText(
                        "Вас окружает ваша проверенная временем команда @User1, @User2, @User3");
        popSendMessage().assertContainText("На краю пирса вас ждёт");

        switchSecondUserChat();
        popSendMessage().assertText("Вы идёте по пирсу, уверенно чеканя шаг.");
        popSendMessage().assertContainText("Море сегодня игриво");
        popSendMessage().assertContainText("Теплый ветер");
        popSendMessage()
                .assertText(
                        "Вас окружает ваша проверенная временем команда @User0, @User2, @User3");
        popSendMessage().assertContainText("На краю пирса вас ждёт");

        switchThirdUserChat();
        popSendMessage().assertText("Вы идёте по пирсу, уверенно чеканя шаг.");
        popSendMessage().assertContainText("Море сегодня игриво");
        popSendMessage().assertContainText("Теплый ветер");
        popSendMessage()
                .assertText(
                        "Вас окружает ваша проверенная временем команда @User0, @User1, @User2");
        popSendMessage().assertContainText("На краю пирса вас ждёт");

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
