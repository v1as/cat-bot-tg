package ru.v1as.tg.cat;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.DbData;

@Component
@Getter
public class CatBotData extends DbData<CatChatData> {

    public CatBotData() {
        super(CatChatData::new);
    }
}
