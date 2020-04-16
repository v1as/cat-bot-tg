package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.EmojiConst.SHOP_BAG;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.WAY_TO_SHOP;
import static ru.v1as.tg.cat.messages.ButtonsMessageHandler.GO_TO_THE_CITY;
import static ru.v1as.tg.cat.tg.KeyboardUtils.replyKeyboardMarkup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.ChatParamResource;

@Component
@RequiredArgsConstructor
public class FindWayToTheShop extends AbstractCuriosCatPhase {

    private final ChatParamResource paramResource;

    @Override
    protected void open() {
        final CuriosCatContext ctx = getPhaseContext();
        final TgUser user = ctx.getUser();
        final Long chatId = ctx.getPublicChatId();
        final Integer userId = user.getId();
        if (!paramResource.paramBool(chatId, userId, WAY_TO_SHOP)) {
            paramResource.param(chatId, userId, WAY_TO_SHOP, true);
            message(
                    ctx.getPublicChat(),
                    String.format(
                            "Игрок %s нашел путь к магазину." + SHOP_BAG,
                            user.getUsernameOrFullName()));
            sender.execute(
                    new SendMessage(user.getChatId(), "Вы нашли путь к магазину! " + SHOP_BAG)
                            .setReplyMarkup(replyKeyboardMarkup(GO_TO_THE_CITY)));
        } else {
            message("Ничего нового вы тут не нашли.");
        }

        catchUpCatAndClose(CAT1);
    }
}
