package ru.v1as.tg.cat;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.DbData;
import ru.v1as.tg.cat.model.ScoreData;

@Component
@Getter
public class CatBotData extends DbData<CatChatData> {

    private final ScoreData scoreData;

    public CatBotData(ScoreData scoreData) {
        super(CatChatData::new);
        this.scoreData = scoreData;
    }

}
