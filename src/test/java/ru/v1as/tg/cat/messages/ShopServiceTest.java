package ru.v1as.tg.cat.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.CONCENTRATION_POTION;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.MONEY;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.WAY_TO_SHOP;
import static ru.v1as.tg.cat.messages.ButtonsMessageHandler.GO_TO_THE_CITY;
import static ru.v1as.tg.cat.service.ChatParam.CAT_BITE_LEVEL;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.service.ChatParamResource;
import ru.v1as.tg.cat.tg.TestUserChat;

public class ShopServiceTest extends AbstractCatBotTest {

    @Autowired ChatParamResource paramResource;

    @Test
    public void has_no_shop_access_test() {
        buyCatBite();
        mary.inPrivate().getSendMessageToSend().assertText("У вас нет доступа к магазину");
    }

    @Test
    public void not_enough_money_test() {

        paramResource.param(inPublic.getId(), mary.getUserId(), WAY_TO_SHOP, true);

        buyCatBite();

        mary.inPrivate().getSendMessageToSend().assertText("У вас недостаточно денег.");
    }

    @Test
    public void buy_cat_bite_test() {

        paramResource.param(inPublic.getId(), mary.getUserId(), WAY_TO_SHOP, true);
        paramResource.param(inPublic.getId(), mary.getUserId(), MONEY, 60);

        for (int i = 1; i <= 5; i++) {
            buyCatBite();

            mary.inPrivate().getSendMessageToSend().assertContainText("Вы купили приманку");
            mary.inPublic()
                    .getSendMessageToSend()
                    .assertContainText("Игрок @mary купил приманку для Любопытного Кота");
            assertEquals(i, paramResource.paramInt(inPublic.getId(), CAT_BITE_LEVEL));
        }
        buyCatBite();
        mary.inPrivate()
                .getSendMessageToSend()
                .assertText("Максимальное количество приманок куплено, попробуйте завтра");

        assertEquals(5, paramResource.paramInt(inPublic.getId(), CAT_BITE_LEVEL));
        assertEquals(10, paramResource.paramInt(inPublic.getId(), mary.getUserId(), MONEY));
    }

    private TestUserChat buyCatBite() {
        final TestUserChat chat = mary.inPrivate();
        chat.sendTextMessage(GO_TO_THE_CITY);
        chat.getSendMessageToSend().assertText("Куда пойдём?").findButtonToSend("Магазин").send();
        chat.getSendMessageToSend()
                .assertContainText("Что купим?")
                .findButtonToSend("Кошачье угощение")
                .send();
        return chat;
    }

    @Test
    public void buy_concentration_potion() {
        assertFalse(
                paramResource.paramBool(inPublic.getId(), mary.getUserId(), CONCENTRATION_POTION));

        paramResource.param(inPublic.getId(), mary.getUserId(), WAY_TO_SHOP, true);
        paramResource.param(inPublic.getId(), mary.getUserId(), MONEY, 60);

        final TestUserChat chat = mary.inPrivate();
        chat.sendTextMessage(GO_TO_THE_CITY);
        chat.getSendMessageToSend().assertText("Куда пойдём?").findButtonToSend("Магазин").send();
        chat.getSendMessageToSend()
                .assertContainText("Что купим?")
                .findButtonToSend("Зелье концентрации")
                .send();

        chat.getSendMessage().assertContainText("Вы купили зелье концентрации");
        inPublic.getSendMessage().assertContainText("Игрок @mary купил зелье концентрации");

        assertTrue(
                paramResource.paramBool(inPublic.getId(), mary.getUserId(), CONCENTRATION_POTION));
        assertEquals(30, paramResource.paramInt(inPublic.getId(), mary.getUserId(), MONEY));
    }
}
