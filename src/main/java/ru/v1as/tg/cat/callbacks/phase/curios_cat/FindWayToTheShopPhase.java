package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.EmojiConst.SHOP_BAG;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.WAY_TO_SHOP;
import static ru.v1as.tg.cat.messages.ButtonsMessageHandler.GO_TO_THE_CITY;
import static ru.v1as.tg.cat.tg.KeyboardUtils.replyKeyboardMarkup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.ChatParamResource;

@Component
@RequiredArgsConstructor
public class FindWayToTheShopPhase extends AbstractCuriosCatPhase {

    private final ChatParamResource paramResource;

    @Override
    public boolean filter(TgUser user, TgChat chat) {
        return !paramResource.paramBool(chat.getId(), user.getId(), WAY_TO_SHOP);
    }

    @Override
    protected void open() {
        messages(
                "Задумвиво бредя по улочкам, вы внезапно останавливаетесь, сами не понимая почему.",
                "Интуиция беснуется, вытаскивая ваше сознание из задумчивости.",
                "Вам вдруг захотелось потереть выступающий из стены кирпич.");
        poll("Как поступим?")
                .choice("Потереть кипрпич", this::brick)
                .choice("Пойти дальше", this::goFurther)
                .send();
    }

    private void brick(ChooseContext chooseContext) {
        messages(
                "Поддаваяст своему наитию, вы медленно поднимаете руку и трёте кирпич.",
                "Вы замираете и ждете секунду...",
                "Другую...",
                "И ничего не происходит.",
                "Странно было ожидать, что что-то случится.");
        goFurther(chooseContext);
    }

    private void goFurther(ChooseContext chooseContext) {
        messages(
                "Вы бредёте дальше, но теперь внимательно озираясь по сторонам.",
                "Ваш взгляд падает на яркую витрину.",
                "Кому могло прийти в голову ставить магазинчик в таком месте?",
                "За стеклом вы наблюдаете множество интересных вещиц.",
                "Тут и разноцветные алхимические колбы, и затейливо жужжащие устройства.");
        poll("Зайдём в магазин?")
                .choice("Зайти", this::findShop)
                .choice("Пройти мимо", this::notFindShop)
                .send();
    }

    private void notFindShop(ChooseContext chooseContext) {
        messages(
                "Проходя мимо магазинчика вы рассматриваете брущатку.",
                "Сегодня вы гуляли не одни - пушистый хвост обмахивает ваши ноги.");
        catchUpCatAndClose(CAT1);
    }

    private void findShop(ChooseContext chooseContext) {
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
            message("Выглянув через витрину, вы замечаете как Кот следует по своим делам.");
        } else {
            message("Ничего нового вы тут не нашли.");
        }

        catchUpCatAndClose(CAT1);
    }
}
