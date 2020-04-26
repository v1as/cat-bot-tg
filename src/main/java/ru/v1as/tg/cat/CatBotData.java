package ru.v1as.tg.cat;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.DbData;

@Component
@Getter
public class CatBotData extends DbData<CatChatData> {

    final Map<Integer, Integer> usersPhaseCounter = new HashMap<>();

    public CatBotData() {
        super(CatChatData::new);
    }

    public void clear() {
        getChats().clear();
    }

    public void incrementPhase(Integer userId) {
        usersPhaseCounter.put(userId, usersPhaseCounter.getOrDefault(userId, 0) + 1);
    }

    public void decrementPhase(Integer userId) {
        usersPhaseCounter.put(userId, usersPhaseCounter.getOrDefault(userId, 0) - 1);
    }

    public boolean inPhase(Integer userId) {
        return usersPhaseCounter.getOrDefault(userId, 0) > 0;
    }
}
