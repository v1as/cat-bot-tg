package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.WAY_TO_SHOP;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.service.ChatParamResource;

public class FindWayToTheShopTest extends AbstractCuriosCatPhaseTest {

    @Autowired private FindWayToTheShop phase;
    @Autowired private ChatParamResource paramResource;

    @Test
    public void find_shop() {
        assertFalse(paramResource.paramBool(inPublic.getId(), bob.getUserId(), WAY_TO_SHOP));

        phase.open(getStartCtx(bob));

        bob.inPublic().getSendMessage().assertContainText("Игрок @bob нашел путь к магазину.");

        bob.inPublic().getSendMessage().assertContainText("Любопытный кот убегает к @bob ");

        bob.inPrivate()
                .getSendMessageToSend()
                .assertContainText("Вы нашли путь к магазину! ")
                .findButtonToSend("Пойти в город")
                .send();

        bob.inPrivate()
                .getSendMessageToSend()
                .assertText("Куда пойдём?")
                .assertButton("Магазин")
                .assertButton("Назад");

        assertTrue(paramResource.paramBool(inPublic.getId(), bob.getUserId(), WAY_TO_SHOP));
    }
}
