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
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@RequiredArgsConstructor
public abstract class DbData<T extends ChatData> {

    private final Logger log = getLogger(this.getClass());

    private final Map<Long, T> chats = new HashMap<>();
    private final Map<Integer, UserData> users = new HashMap<>();
    private final Function<Chat, T> chatDataFactory;

    public void register(Update update) {
        Chat chat = getChat(update);
        User user = getUser(update);
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
        UserData userData =
                users.computeIfAbsent(
                        user.getId(),
                        (id) -> {
                            log.info("User registered: " + user);
                            return new UserData(user);
                        });
        if (chat.isUserChat()) {
            userData.setChatId(chat.getId());
        }
        chatData.update(chat);
        userData.update(user);
    }

    public T getChatData(Long chatId) {
        return chats.get(chatId);
    }

    public UserData getUserData(User user) {
        return users.get(user.getId());
    }

    public Collection<T> getChats() {
        return chats.values();
    }
}
