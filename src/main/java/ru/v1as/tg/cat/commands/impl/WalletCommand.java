package ru.v1as.tg.cat.commands.impl;

import static org.springframework.util.StringUtils.isEmpty;
import static ru.v1as.tg.cat.EmojiConst.MONEY_BAG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParam;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.ChatParamResource;

@Component
public class WalletCommand extends AbstractCommand {

    private final ChatParamResource paramResource;
    private final ChatDao chatDao;
    private final UserDao userDao;

    public WalletCommand(ChatParamResource paramResource, ChatDao chatDao, UserDao userDao) {
        super(cfg().commandName("wallet"));
        this.paramResource = paramResource;
        this.chatDao = chatDao;
        this.userDao = userDao;
    }

    @Override
    public void process(TgCommandRequest command, TgChat chat, TgUser user) {
        final List<ChatEntity> chats = chatDao.findByUsersId(user.getId());
        final UserEntity userEntity = userDao.findById(user.getId()).get();
        final Map<ChatEntity, String> chatToMoney = new HashMap<>();

        for (ChatEntity chatEntity : chats) {
            final String money = paramResource.param(chatEntity, userEntity, ChatUserParam.MONEY);
            chatToMoney.put(chatEntity, money);
        }
        final String message;
        if (chatToMoney.size() == 0) {
            message = "0 " + MONEY_BAG;
        } else if (chatToMoney.size() == 1) {
            message = chatToMoney.values().iterator().next() + " " + MONEY_BAG;
        } else {
            message =
                    chatToMoney.entrySet().stream()
                            .map(
                                    e ->
                                            String.format(
                                                    "%s - %s %s",
                                                    e.getKey().getTitle(), e.getValue(), MONEY_BAG))
                            .collect(Collectors.joining("\n"));
        }
        if (!isEmpty(message)) {
            sender.message(chat, message);
        }
    }

    @Override
    public String getCommandDescription() {
        return "Сколько золотишка у вас в кармане";
    }
}
