package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase.CuriosCatContext;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.RegattaDreamPhaseTest.CuriosConfiguration;
import ru.v1as.tg.cat.tg.TestUserChat;

import java.time.LocalDateTime;

@Import(CuriosConfiguration.class)
public class DayAndNightPhaseTest extends AbstractCuriosCatPhaseTest {

    @Autowired private DayAndNightPhase phase;

    @Test
    public void day_shoo_way() {
        final CuriosCatContext phaseContext = getStartCtx(zakh);
        phase.now = LocalDateTime.of(2020, 1,1,5,0);
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("Пока светло");

        chat.getSendMessageToSend()
                .assertContainText("Вперёд за котом?")
                .findCallbackToSend("Вперёд!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Следуя за котом");
        chat.getSendMessage().assertContainText("Вы неспеша движетесь");
        chat.getSendMessage().assertContainText("Кот прибавил шаг");
        chat.getSendMessage().assertContainText("Куст достаточно густой");
        chat.getSendMessage().assertContainText("Спустя мгновение");
        chat.getSendMessage().assertContainText("Вы видите его напряжённую спину");
        chat.getSendMessage().assertContainText("Из куста выходит");
        chat.getSendMessage().assertContainText("Коты шипят друг на друга");
        chat.getSendMessage().assertContainText("Вы стоите поодаль");

        chat.getSendMessageToSend()
                .assertContainText("Стоит ли вмешаться?")
                .findCallbackToSend("Распугать котов")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Ваш окрик разгоняет котов");

        inPublic.getSendMessage().assertContainText("Любопытный Кот сбегает от игрока @zakh");
    }

    @Test
    public void day_avoid_way() {
        final CuriosCatContext phaseContext = getStartCtx(zakh);
        phase.now = LocalDateTime.of(2020, 1,1,5,0);
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("Пока светло");

        chat.getSendMessageToSend()
                .assertContainText("Вперёд за котом?")
                .findCallbackToSend("Вперёд!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Следуя за котом");
        chat.getSendMessage().assertContainText("Вы неспеша движетесь");
        chat.getSendMessage().assertContainText("Кот прибавил шаг");
        chat.getSendMessage().assertContainText("Куст достаточно густой");
        chat.getSendMessage().assertContainText("Спустя мгновение");
        chat.getSendMessage().assertContainText("Вы видите его напряжённую спину");
        chat.getSendMessage().assertContainText("Из куста выходит");
        chat.getSendMessage().assertContainText("Коты шипят друг на друга");
        chat.getSendMessage().assertContainText("Вы стоите поодаль");

        chat.getSendMessageToSend()
                .assertContainText("Стоит ли вмешаться?")
                .findCallbackToSend("Обойти")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Вы очень осторожно обходите");

        inPublic.getSendMessage().assertContainText("Два кота засчитано игроку @zakh");
    }

    @Test
    public void night_avoid_way() {
        final CuriosCatContext phaseContext = getStartCtx(zakh);
        phase.now = LocalDateTime.of(2020, 1,1,23,0);
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("Вы решили прогуляться");

        chat.getSendMessageToSend()
                .assertContainText("Вперёд за котом?")
                .findCallbackToSend("Вперёд!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Идя по освещённым улицам");
        chat.getSendMessage().assertContainText("Вы помните, что эта дорога");
        chat.getSendMessage().assertContainText("Похоже, в парке сломалось освещение");
        chat.getSendMessage().assertContainText("Кот шмыгает куда-то");
        chat.getSendMessage().assertContainText("Где же кот?");
        chat.getSendMessage().assertContainText("А, нет, вот он");
        chat.getSendMessage().assertContainText("Кажется, он пятится");
        chat.getSendMessage().assertContainText("Вслед за ним появляется");
        chat.getSendMessage().assertContainText("Слышны завывания и шипение");
        chat.getSendMessage().assertContainText("Вы стоите поодаль");

        chat.getSendMessageToSend()
                .assertContainText("Что дальше?")
                .findCallbackToSend("Обойти")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Вы осторожно обходите");

        inPublic.getSendMessage().assertContainText("Любопытный Кот сбегает от игрока @zakh");
    }

    @Test
    public void night_count_way() {
        final CuriosCatContext phaseContext = getStartCtx(zakh);
        phase.now = LocalDateTime.of(2020, 1,1,23,0);
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("Вы решили прогуляться");

        chat.getSendMessageToSend()
                .assertContainText("Вперёд за котом?")
                .findCallbackToSend("Вперёд!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Идя по освещённым улицам");
        chat.getSendMessage().assertContainText("Вы помните, что эта дорога");
        chat.getSendMessage().assertContainText("Похоже, в парке сломалось освещение");
        chat.getSendMessage().assertContainText("Кот шмыгает куда-то");
        chat.getSendMessage().assertContainText("Где же кот?");
        chat.getSendMessage().assertContainText("А, нет, вот он");
        chat.getSendMessage().assertContainText("Кажется, он пятится");
        chat.getSendMessage().assertContainText("Вслед за ним появляется");
        chat.getSendMessage().assertContainText("Слышны завывания и шипение");
        chat.getSendMessage().assertContainText("Вы стоите поодаль");

        chat.getSendMessageToSend()
                .assertContainText("Что дальше?")
                .findCallbackToSend("Попробовать сосчитать")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Вы пытаетесь сосчитать");

        inPublic.getSendMessage().assertContainText("Любопытный Кот убегает к @zakh");
    }

    @Configuration
    @ComponentScan("ru.v1as.tg.cat.callbacks.phase.curios_cat")
    public static class CuriosConfiguration {}
}
