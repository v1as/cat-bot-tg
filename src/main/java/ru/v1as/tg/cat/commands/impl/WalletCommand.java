package ru.v1as.tg.cat.commands.impl;

import static org.springframework.util.StringUtils.isEmpty;
import static ru.v1as.tg.cat.EmojiConst.MONEY_BAG;
import static ru.v1as.tg.cat.service.init.ResourceService.MONEY;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.ResourceEventDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.resource.ResourceEvent;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Component
public class WalletCommand extends AbstractCommand {

    private final ResourceEventDao resourceEventDao;
    private final ChatDao chatDao;

    public WalletCommand(ResourceEventDao resourceEventDao, ChatDao chatDao) {
        super(cfg().commandName("wallet"));
        this.resourceEventDao = resourceEventDao;
        this.chatDao = chatDao;
    }

    @Override
    public void process(TgCommandRequest command, TgChat chat, TgUser user) {
        final List<ChatEntity> chats = chatDao.findByUsersId(user.getId());
        final Map<ChatEntity, BigDecimal> chatToMoney = new HashMap<>();
        for (ChatEntity chatEntity : chats) {
            final BigDecimal money =
                    resourceEventDao
                            .findByChatIdAndUserIdAndResourceId(
                                    chatEntity.getId(), user.getId(), MONEY.getId())
                            .stream()
                            .map(ResourceEvent::getDelta)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (money.compareTo(BigDecimal.ZERO) > 0) {
                chatToMoney.put(chatEntity, money);
            }
        }
        final String message;
        if (chatToMoney.size() == 0) {
            message = "0 " + MONEY_BAG;
        } else if (chatToMoney.size() == 1) {
            message = chatToMoney.values().iterator().next().toString() + " " + MONEY_BAG;
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
