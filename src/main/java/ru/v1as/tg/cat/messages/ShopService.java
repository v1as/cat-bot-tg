package ru.v1as.tg.cat.messages;

import static ru.v1as.tg.cat.EmojiConst.FISH;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.MONEY;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.WAY_TO_SHOP;
import static ru.v1as.tg.cat.service.ChatParam.CAT_BITE_LEVEL;

import java.util.List;
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
@RequiredArgsConstructor
public class ShopService {

    private final ChatDao chatDao;
    private final UserDao userDao;
    private final TgSender sender;
    private final ChatParamResource chatParam;
    private final TgInlinePollFactory pollFactory;

    public void buyCatBite(Message message, TgChat tgChat, TgUser user) {
        final List<ChatEntity> chats = chatDao.findByUsersId(user.getId());
        if (chats.isEmpty()) {
            sender.message(tgChat, "Вы не играете ни в одном чате");
        } else if (chats.size() == 1) {
            buyCatBiteInChat(message, chats.get(0), user);
        } else {
            final TgInlinePoll poll =
                    pollFactory.poll(tgChat.getId(), "В какой чат вы хотите купить приманку?");
            chats.forEach(
                    chat ->
                            poll.choice(
                                    chat.getTitle(), ctx -> buyCatBiteInChat(message, chat, user)));
            poll.send();
        }
    }

    @Transactional
    void buyCatBiteInChat(Message message, ChatEntity chat, TgUser user) {
        if (!chatParam.paramBool(chat.getId(), user.getId(), WAY_TO_SHOP)) {
            sender.message(user, "У вас нет доступа к магазину");
            return;
        }
        final UserEntity userEntity = userDao.findById(user.getId()).get();
        final int money = chatParam.paramInt(chat, userEntity, MONEY);
        if (money < 10) {
            sender.message(user, "У вас недостаточно денег.");
            return;
        }
        if (!chatParam.increment(chat, userEntity, CAT_BITE_LEVEL, 1).isEmpty()) {
            chatParam.increment(chat, userEntity, MONEY, -10);
            sender.execute(new SendMessage(user.getChatId(), "Вы купили приманку " + FISH));
            sender.message(chat, "Куплена приманка для Любопытного Кота " + FISH);
        } else {
            sender.message(user, "Максимальное количество приманок куплено, попробуйте завтра");
        }
    }
}
