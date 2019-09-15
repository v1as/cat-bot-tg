package ru.v1as.tg.cat.model;

import static lombok.AccessLevel.PRIVATE;
import static ru.v1as.tg.cat.model.UpdateUtils.getChat;
import static ru.v1as.tg.cat.model.UpdateUtils.getUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DbData<T extends ChatData> {

    Map<Long, T> chats = new HashMap<>();
    Map<Integer, UserData> users = new HashMap<>();
    ScoreData scoreData;
    Function<Chat, T> chatDataFactory;

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

    public ScoreData getScoreData() {
        return scoreData;
    }
}
