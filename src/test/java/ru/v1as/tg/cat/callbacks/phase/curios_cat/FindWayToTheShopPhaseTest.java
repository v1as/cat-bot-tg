package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.WAY_TO_SHOP;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.service.ChatParamResource;
import ru.v1as.tg.cat.tg.TestUserChat;

public class FindWayToTheShopPhaseTest extends AbstractCuriosCatPhaseTest {

    @Autowired private FindWayToTheShopPhase phase;
    @Autowired private ChatParamResource paramResource;

    @Test
    public void find_shop() {
        assertFalse(paramResource.paramBool(inPublic.getId(), bob.getUserId(), WAY_TO_SHOP));
        phase.open(getStartCtx(bob));
        final TestUserChat bobChat = bob.inPrivate();

        bobChat.getSendMessage().assertContainText("Задумвиво бредя по улочкам");
        bobChat.getSendMessage().assertContainText("Интуиция беснуется,");
        bobChat.getSendMessage().assertContainText("Вам вдруг захотелось");

        bobChat.getSendMessageToSend()
                .assertText("Как поступим?")
                .findCallbackToSend("Пойти дальше")
                .send();
        bobChat.getEditMessage();

        bobChat.getSendMessage().assertContainText("Вы бредёте дальше");
        bobChat.getSendMessage().assertContainText("Ваш взгляд падает");
        bobChat.getSendMessage().assertContainText("Кому могло прийти");
        bobChat.getSendMessage().assertContainText("За стеклом вы");
        bobChat.getSendMessage().assertContainText("Тут и разноцветные");

        bobChat.getSendMessageToSend()
                .assertText("Зайдём в магазин?")
                .findCallbackToSend("Зайти")
                .send();
        bobChat.getEditMessage();

        bob.inPublic().getSendMessage().assertContainText("Игрок @bob нашел путь к магазину ");


        bobChat.getSendMessageToSend()
                .assertContainText("Вы нашли путь к магазину! ")
                .findButtonToSend("Пойти в город")
                .send();

        bobChat.getSendMessage().assertContainText("Выглянув через витрину");
        bob.inPublic().getSendMessage().assertContainText("Любопытный кот убегает к @bob ");

        bobChat.getSendMessageToSend()
                .assertText("Куда пойдём?")
                .assertButton("Магазин")
                .assertButton("Назад");

        assertTrue(paramResource.paramBool(inPublic.getId(), bob.getUserId(), WAY_TO_SHOP));
    }
}
