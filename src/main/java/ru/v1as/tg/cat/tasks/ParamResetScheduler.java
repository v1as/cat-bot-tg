package ru.v1as.tg.cat.tasks;

import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.CONCENTRATION_POTION;
import static ru.v1as.tg.cat.service.ChatParam.CAT_BITE_LEVEL;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.jpa.dao.ChatParamValueDao;
import ru.v1as.tg.cat.jpa.dao.ChatUserParamValueDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.chat.ChatParamValue;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParam;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParamValue;
import ru.v1as.tg.cat.service.ChatParam;
import ru.v1as.tg.cat.service.ChatParamResource;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParamResetScheduler implements Runnable {

    private final ChatParamValueDao chatParamDao;
    private final ChatUserParamValueDao userParamDao;
    private final ChatParamResource paramResource;

    @Override
    @Scheduled(cron = "0 58 9 * * *")
    public void run() {
        reset(CAT_BITE_LEVEL);
        reset(CONCENTRATION_POTION);
    }

    private void reset(ChatParam param) {
        for (ChatParamValue value : chatParamDao.findByParam(param)) {
            ChatEntity c = value.getChat();
            try {
                paramResource.reset(c, param);
            } catch (Exception e) {
                log.error(String.format("Error while reset param %s for chat %s ", param, c), e);
            }
        }
    }

    private void reset(ChatUserParam param) {
        for (ChatUserParamValue value : userParamDao.findByParam(param)) {
            try {
                paramResource.reset(value.getChat(), value.getUser(), CONCENTRATION_POTION);
            } catch (Exception e) {
                final String message =
                        String.format(
                                "Error while reset param %s for chat %s and user %s",
                                param, value.getChat(), value.getUser());
                log.error(message, e);
            }
        }
    }
}
