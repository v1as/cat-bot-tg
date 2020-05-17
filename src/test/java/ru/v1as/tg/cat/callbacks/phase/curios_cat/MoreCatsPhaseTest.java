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
import ru.v1as.tg.cat.tg.TestUserChat;

import static ru.v1as.tg.cat.model.TgChatWrapper.wrap;

@Import(CuriosConfiguration.class)
public class MoreCatsPhaseTest extends AbstractCatBotTest {

    @Autowired private MoreCatsPhase phase;
    private static final String noCatText = "Любопытный кот сбегает от игрока @zakh";
    private static final String oneCatText = "Любопытный кот убегает к @zakh";

    @Test
    public void curious_cat_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("Любопытный Кот");
        chat.getSendMessage().assertContainText("Побродив за котом");
        chat.getSendMessage().assertContainText("Там часто встречается");
        chat.getSendMessage().assertContainText("Но путь туда неблизкий");

        chat.getSendMessageToSend()
                .assertText("Стоит ли удлинять прогулку?")
                .findCallbackToSend("Мне и Любопытного хватит")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Любопытный Кот рад");
        chat.getSendMessage().assertContainText("Прогулявшись с ним");

        inPublic.getSendMessage().assertContainText(oneCatText);
    }

    @Test
    public void no_cat_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("Любопытный Кот");
        chat.getSendMessage().assertContainText("Побродив за котом");
        chat.getSendMessage().assertContainText("Там часто встречается");
        chat.getSendMessage().assertContainText("Но путь туда неблизкий");

        chat.getSendMessageToSend()
                .assertText("Стоит ли удлинять прогулку?")
                .findCallbackToSend("Вперёд за котами!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Покинув Кота");
        chat.getSendMessage().assertContainText("Довольно долго проблуждав");
        chat.getSendMessage().assertContainText("Устроившись на скамейке");
        chat.getSendMessage().assertContainText("Ещё немного отдыха");

        chat.getSendMessage().assertContainText("Тем временем небо хмурится");
        chat.getSendMessage().assertContainText("При такой погоде");

        chat.getSendMessageToSend()
                .assertText("Как много котов вы ожидаете встретить в этом дворе?")
                .findCallbackToSend("Ни одного, к сожалению")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Внезапно вздрогнув");
        chat.getSendMessage().assertContainText("Что ж, пора продолжить путь");

        // рандом по умолчанию вернёт 0

        chat.getSendMessage().assertContainText("Наконец-то вы добрались");
        chat.getSendMessage().assertContainText("Не терпится узнать");

        chat.getSendMessageToSend()
                .assertText("Как быстро вы входите во двор?")
                .findCallbackToSend("Неспешно")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Вы никого не нашли");

        inPublic.getSendMessage().assertContainText(noCatText);
    }

    @Test
    public void one_cat_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("Любопытный Кот");
        chat.getSendMessage().assertContainText("Побродив за котом");
        chat.getSendMessage().assertContainText("Там часто встречается");
        chat.getSendMessage().assertContainText("Но путь туда неблизкий");

        chat.getSendMessageToSend()
                .assertText("Стоит ли удлинять прогулку?")
                .findCallbackToSend("Вперёд за котами!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Покинув Кота");
        chat.getSendMessage().assertContainText("Довольно долго проблуждав");
        chat.getSendMessage().assertContainText("Устроившись на скамейке");
        chat.getSendMessage().assertContainText("Ещё немного отдыха");

        chat.getSendMessage().assertContainText("Тем временем небо хмурится");
        chat.getSendMessage().assertContainText("При такой погоде");

        chat.getSendMessageToSend()
                .assertText("Как много котов вы ожидаете встретить в этом дворе?")
                .findCallbackToSend("Один-то точно будет")
                .send();
        chat.getEditMessage();

        chat.getSendMessageToSend()
                .assertText("Вдруг там больше котов, чем 1?")
                .findCallbackToSend("Вряд ли больше, чем 1")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Внезапно вздрогнув");
        chat.getSendMessage().assertContainText("Что ж, пора продолжить путь");

        // рандом по умолчанию вернёт 0

        chat.getSendMessage().assertContainText("Наконец-то вы добрались");
        chat.getSendMessage().assertContainText("Не терпится узнать");

        chat.getSendMessageToSend()
                .assertText("Как быстро вы входите во двор?")
                .findCallbackToSend("Неспешно")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Вы никого не нашли");

        inPublic.getSendMessage().assertContainText(noCatText);
    }

    @Test
    public void two_cats_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("Любопытный Кот");
        chat.getSendMessage().assertContainText("Побродив за котом");
        chat.getSendMessage().assertContainText("Там часто встречается");
        chat.getSendMessage().assertContainText("Но путь туда неблизкий");

        chat.getSendMessageToSend()
                .assertText("Стоит ли удлинять прогулку?")
                .findCallbackToSend("Вперёд за котами!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Покинув Кота");
        chat.getSendMessage().assertContainText("Довольно долго проблуждав");
        chat.getSendMessage().assertContainText("Устроившись на скамейке");
        chat.getSendMessage().assertContainText("Ещё немного отдыха");

        chat.getSendMessage().assertContainText("Тем временем небо хмурится");
        chat.getSendMessage().assertContainText("При такой погоде");

        chat.getSendMessageToSend()
                .assertText("Как много котов вы ожидаете встретить в этом дворе?")
                .findCallbackToSend("Один-то точно будет")
                .send();
        chat.getEditMessage();

        chat.getSendMessageToSend()
                .assertText("Вдруг там больше котов, чем 1?")
                .findCallbackToSend("Больше!")
                .send();
        chat.getEditMessage();

        chat.getSendMessageToSend()
                .assertText("Вдруг там больше котов, чем 2?")
                .findCallbackToSend("Вряд ли больше, чем 2")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Внезапно вздрогнув");
        chat.getSendMessage().assertContainText("Что ж, пора продолжить путь");

        // рандом по умолчанию вернёт 0

        chat.getSendMessage().assertContainText("Наконец-то вы добрались");
        chat.getSendMessage().assertContainText("Не терпится узнать");

        chat.getSendMessageToSend()
                .assertText("Как быстро вы входите во двор?")
                .findCallbackToSend("Неспешно")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Вы никого не нашли");

        inPublic.getSendMessage().assertContainText(noCatText);
    }

    @Test
    public void three_cats_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("Любопытный Кот");
        chat.getSendMessage().assertContainText("Побродив за котом");
        chat.getSendMessage().assertContainText("Там часто встречается");
        chat.getSendMessage().assertContainText("Но путь туда неблизкий");

        chat.getSendMessageToSend()
                .assertText("Стоит ли удлинять прогулку?")
                .findCallbackToSend("Вперёд за котами!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Покинув Кота");
        chat.getSendMessage().assertContainText("Довольно долго проблуждав");
        chat.getSendMessage().assertContainText("Устроившись на скамейке");
        chat.getSendMessage().assertContainText("Ещё немного отдыха");

        chat.getSendMessage().assertContainText("Тем временем небо хмурится");
        chat.getSendMessage().assertContainText("При такой погоде");

        chat.getSendMessageToSend()
                .assertText("Как много котов вы ожидаете встретить в этом дворе?")
                .findCallbackToSend("Один-то точно будет")
                .send();
        chat.getEditMessage();

        chat.getSendMessageToSend()
                .assertText("Вдруг там больше котов, чем 1?")
                .findCallbackToSend("Больше!")
                .send();
        chat.getEditMessage();

        chat.getSendMessageToSend()
                .assertText("Вдруг там больше котов, чем 2?")
                .findCallbackToSend("Больше!!")
                .send();
        chat.getEditMessage();

        chat.getSendMessageToSend()
                .assertText("Вдруг там больше котов, чем 3?")
                .findCallbackToSend("Вряд ли больше, чем 3")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Внезапно вздрогнув");
        chat.getSendMessage().assertContainText("Что ж, пора продолжить путь");

        // рандом по умолчанию вернёт 0

        chat.getSendMessage().assertContainText("Наконец-то вы добрались");
        chat.getSendMessage().assertContainText("Не терпится узнать");

        chat.getSendMessageToSend()
                .assertText("Как быстро вы входите во двор?")
                .findCallbackToSend("Неспешно")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Вы никого не нашли");

        inPublic.getSendMessage().assertContainText(noCatText);
    }

    @Test
    public void more_cats_way() {
        final CuriosCatContext phaseContext = getStartCtx();
        phase.open(phaseContext);
        final TestUserChat chat = zakh.inPrivate();

        chat.getSendMessage().assertContainText("Любопытный Кот");
        chat.getSendMessage().assertContainText("Побродив за котом");
        chat.getSendMessage().assertContainText("Там часто встречается");
        chat.getSendMessage().assertContainText("Но путь туда неблизкий");

        chat.getSendMessageToSend()
                .assertText("Стоит ли удлинять прогулку?")
                .findCallbackToSend("Вперёд за котами!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Покинув Кота");
        chat.getSendMessage().assertContainText("Довольно долго проблуждав");
        chat.getSendMessage().assertContainText("Устроившись на скамейке");
        chat.getSendMessage().assertContainText("Ещё немного отдыха");

        chat.getSendMessage().assertContainText("Тем временем небо хмурится");
        chat.getSendMessage().assertContainText("При такой погоде");

        chat.getSendMessageToSend()
                .assertText("Как много котов вы ожидаете встретить в этом дворе?")
                .findCallbackToSend("Один-то точно будет")
                .send();
        chat.getEditMessage();

        chat.getSendMessageToSend()
                .assertText("Вдруг там больше котов, чем 1?")
                .findCallbackToSend("Больше!")
                .send();
        chat.getEditMessage();

        chat.getSendMessageToSend()
                .assertText("Вдруг там больше котов, чем 2?")
                .findCallbackToSend("Больше!!")
                .send();
        chat.getEditMessage();

        chat.getSendMessageToSend()
                .assertText("Вдруг там больше котов, чем 3?")
                .findCallbackToSend("Больше!!!")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Внезапно вздрогнув");
        chat.getSendMessage().assertContainText("Что ж, пора продолжить путь");

        // рандом по умолчанию вернёт 0

        chat.getSendMessage().assertContainText("Наконец-то вы добрались");
        chat.getSendMessage().assertContainText("Не терпится узнать");

        chat.getSendMessageToSend()
                .assertText("Как быстро вы входите во двор?")
                .findCallbackToSend("Неспешно")
                .send();
        chat.getEditMessage();

        chat.getSendMessage().assertContainText("Вы никого не нашли");

        inPublic.getSendMessage().assertContainText(noCatText);
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