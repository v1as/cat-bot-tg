package ru.v1as.tg.cat;

import static lombok.AccessLevel.PRIVATE;
import static ru.v1as.tg.cat.UpdateUtils.getChat;
import static ru.v1as.tg.cat.UpdateUtils.getUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DbData {

    Map<Long, ChatData> chats = new HashMap<>();
    Map<Integer, UserData> users = new HashMap<>();
    private final ScoreData scoreData;

    void register(Update update) {
        Chat chat = getChat(update);
        User user = getUser(update);
        if (chat == null) {
            return;
        }
        ChatData chatData =
                chats.computeIfAbsent(chat.getId(), id -> new ChatData(chat, chat.isUserChat()));
        UserData userData = users.computeIfAbsent(user.getId(), (id) -> new UserData(user));

        chatData.update(chat);
        userData.update(user);
    }

    public CatRequest getCatRequest(Chat chat, CallbackQuery callbackQuery) {
        ChatData chatData = chats.get(chat.getId());
        return chatData.getCatRequest(callbackQuery);
    }

    ChatData getChatData(Long chatId) {
        return chats.get(chatId);
    }

    public UserData getUserData(User user) {
        return users.get(user.getId());
    }

    void register(CatRequest catRequest, Message message) {
        ChatData chatData = chats.get(message.getChatId());
        Integer messageId = message.getMessageId();
        chatData.getCatRequests().put(messageId, catRequest);
    }

    List<CatRequest> getNotFinishedCatRequests() {
        return this.chats.values().stream()
                .flatMap(chat -> chat.getCatRequests().values().stream())
                .filter(r -> !r.isFinished())
                .collect(Collectors.toList());
    }

    List<ChatData> getChats() {
        return new ArrayList<>(chats.values());
    }

    public ScoreData getScoreData() {
        return scoreData;
    }
}
