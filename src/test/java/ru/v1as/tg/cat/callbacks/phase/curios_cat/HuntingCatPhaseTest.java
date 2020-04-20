package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase.CuriosCatContext;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.RegattaDreamPhaseTest.CuriosConfiguration;
import ru.v1as.tg.cat.model.TgUserWrapper;
import ru.v1as.tg.cat.service.clock.TestBotClock;
import ru.v1as.tg.cat.tg.TestUserChat;

import java.util.concurrent.TimeUnit;

import static ru.v1as.tg.cat.model.TgChatWrapper.wrap;

@Import(CuriosConfiguration.class)
public class HuntingCatPhaseTest extends AbstractCatBotTest {

    @Autowired private HuntingCatPhase phase;
    @Autowired private TestBotClock clock;

    @Test
    public void no_waiting_no_cat_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("В этот раз Кот промчался");
        chat.getSendMessage().assertContainText("После некоторого времени блуждания");
        chat.getSendMessage().assertContainText("Кот чрезвычайно занят, он затаился");
        chat.getSendMessage().assertContainText("Скорее всего, кот убежит");
        chat.getSendMessage().assertContainText("не стоит спешить");

        chat.getSendMessageToSend()
                .assertText("Что дальше?")
                .findCallbackToSend("Подойти поближе")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Неосторожное приближение");
        chat.getSendMessage().assertContainText("Кот убежал");

        inPublic.getSendMessage().assertText("Любопытный кот сбегает от игрока @zakh");
    }

    @Test
    public void no_waiting_one_cat_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("В этот раз Кот промчался");
        chat.getSendMessage().assertContainText("После некоторого времени блуждания");
        chat.getSendMessage().assertContainText("Кот чрезвычайно занят, он затаился");
        chat.getSendMessage().assertContainText("Скорее всего, кот убежит");
        chat.getSendMessage().assertContainText("не стоит спешить");

        chat.getSendMessageToSend()
                .assertText("Что дальше?")
                .findCallbackToSend("Кот!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("От вашего восклицания");
        chat.getSendMessage().assertContainText("Вы посчитали кота");
        chat.getSendMessage().assertContainText("Как долго вы будете помнить");

        inPublic.getSendMessage().assertContainText("Любопытный кот убегает к @zakh");
    }

    @Test
    public void short_waiting_two_cat_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("В этот раз Кот промчался");
        chat.getSendMessage().assertContainText("После некоторого времени блуждания");
        chat.getSendMessage().assertContainText("Кот чрезвычайно занят, он затаился");
        chat.getSendMessage().assertContainText("Скорее всего, кот убежит");
        chat.getSendMessage().assertContainText("не стоит спешить");

        chat.getSendMessage().assertText("Что дальше?");
        clock.skip(4, TimeUnit.SECONDS);
        chat.getDeleteMessage().assertTextContains("Что дальше?");

        chat.getSendMessage().assertText("Ожидание привело к изменениям.");
        chat.getSendMessage().assertContainText("Кот выбрал жертву");

        chat.getSendMessageToSend()
                .assertText("Пора выбрать следующее действие?")
                .findCallbackToSend("Кот!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Кот вас услышал и остановился");
        chat.getSendMessage().assertContainText("Внезапно вы замечаете");
        chat.getSendMessage().assertText("Появился ещё один кот-охотник.");

        chat.getSendMessageToSend()
                .assertText("Что дальше?")
                .findCallbackToSend("Ещё один кот!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("От вашего восклицания");
        chat.getSendMessage().assertContainText("Вы посчитали обоих котов");

        inPublic.getSendMessage().assertContainText("Два кота засчитано игроку @zakh");
    }

    @Test
    public void short_waiting_one_cat_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("В этот раз Кот промчался");
        chat.getSendMessage().assertContainText("После некоторого времени блуждания");
        chat.getSendMessage().assertContainText("Кот чрезвычайно занят, он затаился");
        chat.getSendMessage().assertContainText("Скорее всего, кот убежит");
        chat.getSendMessage().assertContainText("не стоит спешить");

        chat.getSendMessage().assertText("Что дальше?");
        clock.skip(4, TimeUnit.SECONDS);
        chat.getDeleteMessage().assertTextContains("Что дальше?");

        chat.getSendMessage().assertText("Ожидание привело к изменениям.");
        chat.getSendMessage().assertContainText("Кот выбрал жертву");

        chat.getSendMessageToSend()
                .assertText("Пора выбрать следующее действие?")
                .findCallbackToSend("Кот!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Кот вас услышал и остановился");
        chat.getSendMessage().assertContainText("Внезапно вы замечаете");
        chat.getSendMessage().assertText("Появился ещё один кот-охотник.");

        chat.getSendMessageToSend()
                .assertText("Что дальше?")
                .findCallbackToSend("Наблюдать издалека")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Второй кот");
        chat.getSendMessage().assertContainText("Он бросается");
        chat.getSendMessage().assertContainText("Ну, хотя бы одного");

        inPublic.getSendMessage().assertContainText("Любопытный кот убегает к @zakh");
    }

    @Test
    public void long_waiting_three_cat_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("В этот раз Кот промчался");
        chat.getSendMessage().assertContainText("После некоторого времени блуждания");
        chat.getSendMessage().assertContainText("Кот чрезвычайно занят, он затаился");
        chat.getSendMessage().assertContainText("Скорее всего, кот убежит");
        chat.getSendMessage().assertContainText("не стоит спешить");

        chat.getSendMessage().assertText("Что дальше?");
        clock.skip(4, TimeUnit.SECONDS);
        chat.getDeleteMessage().assertTextContains("Что дальше?");

        chat.getSendMessage().assertText("Ожидание привело к изменениям.");
        chat.getSendMessage().assertContainText("Кот выбрал жертву");
        chat.getSendMessage().assertText("Пора выбрать следующее действие?");

        clock.skip(7, TimeUnit.SECONDS);
        chat.getDeleteMessage().assertTextContains("Пора выбрать следующее действие?");

        chat.getSendMessage().assertText("Что-то изменилось.");

        chat.getSendMessageToSend()
                .assertText("Что же дальше?")
                .findCallbackToSend("Кот!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Кот вас услышал и остановился");
        chat.getSendMessage().assertContainText("Внезапно вы замечаете");
        chat.getSendMessage().assertContainText("Но всех опередил");
        chat.getSendMessage().assertContainText("Птицы разлетелись");
        chat.getSendMessage().assertContainText("Конечно, вы сосчитали");

        inPublic.getSendMessage().assertContainText("Целых три кота засчитано игроку @zakh");
    }

    @Test
    public void long_waiting_no_cat_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("В этот раз Кот промчался");
        chat.getSendMessage().assertContainText("После некоторого времени блуждания");
        chat.getSendMessage().assertContainText("Кот чрезвычайно занят, он затаился");
        chat.getSendMessage().assertContainText("Скорее всего, кот убежит");
        chat.getSendMessage().assertContainText("не стоит спешить");

        chat.getSendMessage().assertText("Что дальше?");
        clock.skip(4, TimeUnit.SECONDS);
        chat.getDeleteMessage().assertTextContains("Что дальше?");

        chat.getSendMessage().assertText("Ожидание привело к изменениям.");
        chat.getSendMessage().assertContainText("Кот выбрал жертву");
        chat.getSendMessage().assertText("Пора выбрать следующее действие?");

        clock.skip(7, TimeUnit.SECONDS);
        chat.getDeleteMessage().assertTextContains("Пора выбрать следующее действие?");

        chat.getSendMessage().assertText("Что-то изменилось.");

        chat.getSendMessageToSend()
                .assertText("Что же дальше?")
                .findCallbackToSend("Подойти поближе")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Неосторожное приближение");
        chat.getSendMessage().assertContainText("Кот убежал");

        inPublic.getSendMessage().assertContainText("Любопытный кот сбегает от игрока @zakh");
    }

    private CuriosCatContext getStartCtx() {
        final CuriosCatContext phaseContext =
                new CuriosCatContext(
                        wrap(zakh.getPrivateChat().getChat()),
                        wrap(inPublic.getChat()),
                        TgUserWrapper.wrap(zakh.getUser()),
                        bot.inPublic().sendTextMessage("Starting!"));
        clearMethodsQueue();
        return phaseContext;
    }

    @Configuration
    @ComponentScan("ru.v1as.tg.cat.callbacks.phase.curios_cat")
    public static class CuriosConfiguration {}
}
