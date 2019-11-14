package ru.v1as.tg.cat.model;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.v1as.tg.cat.model.UpdateUtils.getChat;
import static ru.v1as.tg.cat.model.UpdateUtils.getUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.v1as.tg.cat.TgUpdateBeforeHandler;

@RequiredArgsConstructor
public abstract class DbData<T extends ChatData> implements TgUpdateBeforeHandler {

    private final Logger log = getLogger(this.getClass());

    private final Map<Long, T> chats = new HashMap<>();
    private final Function<TgChat, T> chatDataFactory;

    public void register(Update update) {
        TgChat chat = getChat(update);
        TgUser user = getUser(update);
        if (chat == null || user == null) {
            return;
        }
        ChatData chatData =
                chats.computeIfAbsent(
                        chat.getId(),
                        (id) -> {
                            log.info("Chat registered {}", chat);
                            return chatDataFactory.apply(chat);
                        });
        chatData.update(chat);
    }

    public T getChatData(Long chatId) {
        return chats.get(chatId);
    }

    public Collection<T> getChats() {
        return chats.values();
    }
}
