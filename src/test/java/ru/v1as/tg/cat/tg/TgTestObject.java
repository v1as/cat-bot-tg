package ru.v1as.tg.cat.tg;

import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public abstract class TgTestObject {

    protected static Chat getChat(Long chatId, boolean isPublic) {
        Chat res = new Chat();
        setField(res, "id", chatId);
        if (isPublic) {
            setField(res, "type", "supergroup");
        } else {
            setField(res, "type", "private");
        }
        return res;
    }

    protected User getUser(Integer userId) {
        User user = new User();
        setField(user, "id", userId);
        String userName = "User" + userId;
        setField(user, "userName", userName);
        setField(user, "firstName", "User");
        setField(user, "lastName", userId.toString());
        return user;
    }

    protected Message getMessage(Chat chat, User user, Integer msgId) {
        Message message = new Message();
        setField(message, "messageId", msgId);
        setField(message, "chat", chat);
        setField(message, "from", user);
        registerMessage(msgId, message);
        return message;
    }

    protected void registerMessage(Integer msgId, Message message) {
    }

    protected Update getMessageUpdate(Chat chat, User user) {
        Message message = getMessage(chat, user, incrementId());
        Update update = new Update();
        setField(update, "message", message);
        return update;
    }

    private CallbackQuery getCallbackQuery(User user, Integer msgId, String data) {
        CallbackQuery query = new CallbackQuery();
        setField(query, "from", user);
        setField(query, "data", data);
        setField(query, "id", incrementId().toString());
        setField(query, "message", findMessage(msgId));
        return query;
    }

    public Update getCallbackUpdate(User user, Integer msgId, String data) {
        Update update = new Update();
        setField(update, "callbackQuery", getCallbackQuery(user, msgId, data));
        return update;
    }

    public abstract Message findMessage(Integer msgId);

    public abstract Integer incrementId();

}
