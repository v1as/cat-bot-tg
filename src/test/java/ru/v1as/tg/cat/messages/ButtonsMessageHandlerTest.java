package ru.v1as.tg.cat.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.MONEY;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.WAY_TO_SHOP;
import static ru.v1as.tg.cat.messages.ButtonsMessageHandler.GO_TO_THE_CITY;
import static ru.v1as.tg.cat.service.ChatParam.CAT_BITE_LEVEL;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.service.ChatParamResource;
import ru.v1as.tg.cat.tg.TestUserChat;

public class ButtonsMessageHandlerTest extends AbstractCatBotTest {

    @Autowired ChatParamResource paramResource;

    @Test
    public void has_no_shop_access_test() {
        buyCatBite();
        bob.inPrivate().getSendMessageToSend().assertText("У вас нет доступа к магазину");
    }

    @Test
    public void not_enough_money_test() {

        paramResource.param(inPublic.getId(), bob.getUserId(), WAY_TO_SHOP, true);

        buyCatBite();

        bob.inPrivate().getSendMessageToSend().assertText("У вас недостаточно денег.");
    }

    @Test
    public void buy_cat_bite_test() {

        paramResource.param(inPublic.getId(), bob.getUserId(), WAY_TO_SHOP, true);
        paramResource.param(inPublic.getId(), bob.getUserId(), MONEY, 60);

        for (int i = 1; i <= 5; i++) {
            buyCatBite();

            bob.inPrivate().getSendMessageToSend().assertContainText("Вы купили приманку");
            bob.inPublic()
                    .getSendMessageToSend()
                    .assertContainText("Куплена приманка для Любопытного Кота");
            assertEquals(i, paramResource.paramInt(inPublic.getId(), CAT_BITE_LEVEL));
        }
        buyCatBite();
        bob.inPrivate()
                .getSendMessageToSend()
                .assertText("Максимальное количество приманок куплено, попробуйте завтра");

        assertEquals(5, paramResource.paramInt(inPublic.getId(), CAT_BITE_LEVEL));
        assertEquals(10, paramResource.paramInt(inPublic.getId(), bob.getUserId(), MONEY));
    }

    private TestUserChat buyCatBite() {
        final TestUserChat chat = bob.inPrivate();
        chat.sendTextMessage(GO_TO_THE_CITY);
        chat.getSendMessageToSend().assertText("Куда пойдём?").findButtonToSend("Магазин").send();
        chat.getSendMessageToSend()
                .assertText("Что купим?")
                .findButtonToSend("Кошачье угощение")
                .send();
        return chat;
    }
}
