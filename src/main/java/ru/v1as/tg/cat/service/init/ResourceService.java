package ru.v1as.tg.cat.service.init;

import static java.util.Optional.ofNullable;
import static ru.v1as.tg.cat.EmojiConst.MONEY_BAG;
import static ru.v1as.tg.cat.jpa.entities.resource.ResourceType.COUNTABLE;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.jpa.dao.ChatUserParamValueDao;
import ru.v1as.tg.cat.jpa.dao.ResourceEventDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.resource.ResourceEntity;
import ru.v1as.tg.cat.jpa.entities.resource.ResourceEvent;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParam;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParamValue;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

@Component
@RequiredArgsConstructor
public class ResourceService {

    private static final ResourceEntity MONEY =
            new ResourceEntity(1L, "Деньги", COUNTABLE, MONEY_BAG);
    private final ResourceEventDao resourceEventDao;
    private final ChatUserParamValueDao paramValueDao;

    //    @PostConstruct todo
    public void init() {
        Table<Long, Integer, Integer> moneys = HashBasedTable.create();
        List<ResourceEvent> txs = resourceEventDao.findByResourceId(MONEY.getId());
        Map<Long, ChatEntity> chats = new HashMap<>();
        Map<Integer, UserEntity> users = new HashMap<>();
        for (ResourceEvent tx : txs) {
            final Integer userId = tx.getUser().getId();
            final Long chatId = tx.getChat().getId();
            users.put(userId, tx.getUser());
            chats.put(chatId, tx.getChat());
            final Integer money = ofNullable(moneys.get(chatId, userId)).orElse(0);
            moneys.put(chatId, userId, money + tx.getDelta().intValue());
        }
        List<ChatUserParamValue> moneyParams = new ArrayList<>();
        for (Cell<Long, Integer, Integer> money : moneys.cellSet()) {
            moneyParams.add(
                    new ChatUserParamValue(
                            chats.get(money.getRowKey()),
                            users.get(money.getColumnKey()),
                            ChatUserParam.MONEY,
                            money.getValue()));
        }
        paramValueDao.saveAll(moneyParams);
    }
}
