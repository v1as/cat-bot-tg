package ru.v1as.tg.cat.messages;

import static java.lang.String.format;
import static ru.v1as.tg.cat.EmojiConst.FISH;
import static ru.v1as.tg.cat.EmojiConst.POTION;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.CONCENTRATION_POTION;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.MONEY;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.WAY_TO_SHOP;
import static ru.v1as.tg.cat.service.ChatParam.CAT_BITE_LEVEL;

import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.ChatParamResource;
import ru.v1as.tg.cat.service.TgInlinePollFactory;
import ru.v1as.tg.cat.tg.TgSender;

@Component
@Transactional
@RequiredArgsConstructor
public class ShopService {

    public static final int CAT_BITE_PRICE = 10;
    public static final int CONCENTRATION_POTION_PRICE = 20;
    private final ChatDao chatDao;
    private final UserDao userDao;
    private final TgSender sender;
    private final ChatParamResource chatParam;
    private final TgInlinePollFactory pollFactory;

    public void buyCatBite(Message message, TgChat tgChat, TgUser user) {
        buySomethingInChat(tgChat, user, (ChatEntity chat) -> buyCatBiteInChat(chat, user));
    }

    private void buyCatBiteInChat(ChatEntity chat, TgUser user) {
        if (checkShopWay(chat, user)) {
            return;
        }
        final UserEntity userEntity = userDao.findById(user.getId()).get();
        final int money = chatParam.paramInt(chat, userEntity, MONEY);
        if (money < CAT_BITE_PRICE) {
            sender.message(user, "У вас недостаточно денег.");
            return;
        }
        if (!chatParam.increment(chat, userEntity, CAT_BITE_LEVEL, 1).isEmpty()) {
            chatParam.increment(chat, userEntity, MONEY, -1 * CAT_BITE_PRICE);
            sender.execute(new SendMessage(user.getChatId(), "Вы купили приманку " + FISH));
            sender.message(chat, "Куплена приманка для Любопытного Кота " + FISH);
        } else {
            sender.message(user, "Максимальное количество приманок куплено, попробуйте завтра");
        }
    }

    public void buyConcentrationPotion(Message message, TgChat tgChat, TgUser user) {
        buySomethingInChat(tgChat, user, (ChatEntity chat) -> buyLookPotionInChat(chat, user));
    }

    private void buyLookPotionInChat(ChatEntity chat, TgUser user) {
        if (checkShopWay(chat, user)) {
            return;
        }
        final UserEntity userEntity = userDao.findById(user.getId()).get();
        final int money = chatParam.paramInt(chat, userEntity, MONEY);
        if (money < CONCENTRATION_POTION_PRICE) {
            sender.message(user, "У вас недостаточно денег.");
            return;
        }
        if (!chatParam.param(chat, userEntity, CONCENTRATION_POTION, "true").isEmpty()) {
            chatParam.increment(chat, userEntity, MONEY, -1 * CONCENTRATION_POTION_PRICE);
            sender.execute(
                    new SendMessage(user.getChatId(), "Вы купили зелье концентрации " + POTION));
            sender.message(
                    chat,
                    format(
                            "Игрок %s купил зелье концентрации %s",
                            user.getUsernameOrFullName(), POTION));
        } else {
            sender.message(user, "Вы не можете купить зелье, пока его действие не прекратится");
        }
    }

    private boolean checkShopWay(ChatEntity chat, TgUser user) {
        if (!chatParam.paramBool(chat.getId(), user.getId(), WAY_TO_SHOP)) {
            sender.message(user, "У вас нет доступа к магазину");
            return true;
        }
        return false;
    }

    private void buySomethingInChat(TgChat tgChat, TgUser user, Consumer<ChatEntity> chatChooser) {
        final List<ChatEntity> chats = chatDao.findByUsersId(user.getId());
        if (chats.isEmpty()) {
            sender.message(tgChat, "Вы не играете ни в одном чате");
        } else if (chats.size() == 1) {
            chatChooser.accept(chats.get(0));
        } else {
            final TgInlinePoll poll = pollFactory.poll(tgChat.getId(), "Выберите чат");
            chats.forEach(chat -> poll.choice(chat.getTitle(), ctx -> chatChooser.accept(chat)));
            poll.send();
        }
    }
}
