package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.messages.ButtonsMessageHandler.GO_TO_THE_CITY;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase.CuriosCatContext;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.RegattaDreamPhaseTest.CuriosConfiguration;
import ru.v1as.tg.cat.tg.TestUserChat;

@Import(CuriosConfiguration.class)
public class LongJourneyPhaseTest extends AbstractCuriosCatPhaseTest {

    public static final String TEXT =
            "Вы так себе и идёте дальше, улыбаясь и мурлыча что-то незатейливое под нос.";

    @Autowired private LongJourneyPhase phase;

    @Test
    public void no_cat_way() {
        final CuriosCatContext phaseContext = getStartCtx(zakh);
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();
        chat.getSendMessage().assertText("Сегодня выдался на удивление приятный денёк.");
        chat.getSendMessage().containText("Вы с котом медленно прогуливаетесь по городу.");
        chat.getSendMessageToSend()
                .assertText("Что будем делать?")
                .findCallbackToSend("Нагоним кота")
                .send();
        chat.getEditMessage();
        chat.getSendMessage().assertText("Кот, недовольно мяукая, убегает.");
        inPublic.getSendMessage().assertText("Любопытный Кот сбегает от игрока @zakh");
    }

    @Test
    public void test_shop_in_quest() {
        final CuriosCatContext phaseContext = getStartCtx(zakh);
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();
        chat.getSendMessage().assertText("Сегодня выдался на удивление приятный денёк.");
        chat.getSendMessage().containText("Вы с котом медленно прогуливаетесь по городу.");
        chat.getSendMessage().assertText("Что будем делать?");

        chat.sendTextMessage(GO_TO_THE_CITY);
    }

    @Test
    public void long_journey_way() {
        final CuriosCatContext phaseContext = getStartCtx(zakh);
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();
        chat.getSendMessage().assertText("Сегодня выдался на удивление приятный денёк.");
        chat.getSendMessage().containText("Вы с котом медленно прогуливаетесь по городу.");
        for (int i = 0; i < 5; i++) {
            chat.getSendMessageToSend()
                    .assertText("Что будем делать?")
                    .findCallbackToSend("Гуля")
                    .send();
            chat.getEditMessage();
            chat.getSendMessage().assertText(TEXT);
        }
        chat.getSendMessageToSend()
                .assertText("Что будем делать?")
                .findCallbackToSend("Нагоним кота")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertText("Кот удивлённо смотрит на вас.");
        chat.getSendMessage()
                .assertText(
                        "Похоже, он совсем забыл о вашей компании во время этой дивной прогулки.");
        chat.getSendMessage().assertText("Пользуюясь его замешательством, вы воскликнули 'Кот!'");
        inPublic.getSendMessage().assertContainText("Любопытный кот убегает к @zakh");
    }

    @Configuration
    @ComponentScan("ru.v1as.tg.cat.callbacks.phase.curios_cat")
    public static class CuriosConfiguration {}
}
