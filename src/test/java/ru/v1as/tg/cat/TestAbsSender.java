package ru.v1as.tg.cat;

import java.io.Serializable;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMembersCount;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import ru.v1as.tg.cat.tg.TestChat;
import ru.v1as.tg.cat.tg.TgSender;

public class TestAbsSender implements TgSender {

    private LinkedList<MethodCall<?>> items = new LinkedList<>();
    private Map<String, TestChat> chats = new HashMap<>();

    @Override
    public <
                    T extends Serializable,
                    Method extends BotApiMethod<T>,
                    Callback extends SentCallback<T>>
            void executeAsync(Method method, Callback callback) {
        final T response = execute(method);
        callback.onResult(method, response);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        T result = null;
        Message message = null;
        if (method instanceof GetChatMembersCount) {
            result = (T) new Integer(-1);
        } else if (method instanceof SendMessage) {
            final SendMessage sendMessage = (SendMessage) method;
            message = produceMessage(sendMessage.getChatId(), null, sendMessage.getText());
            result = (T) message;
        } else if (method instanceof DeleteMessage) {
            final DeleteMessage deleteMessage = (DeleteMessage) method;
            message =
                    chats.get(deleteMessage.getChatId()).findMessage(deleteMessage.getMessageId());
            result = (T) Boolean.TRUE;
        }
        items.add(new MethodCall<>(method, result, message));
        return result;
    }

    @Override
    public Message executeDoc(SendDocument sendDocument) {
        return produceMessage(sendDocument.getChatId(), null, "%document%");
    }

    private Message produceMessage(String chatId, User user, String text) {
        return chats.get(chatId).produceMessage(text, user);
    }

    public void clear() {
        items.clear();
    }

    public int getMethodsAmount() {
        return items.size();
    }

    public Deque<MethodCall<?>> getMethodCalls() {
        return items;
    }

    public void registerChat(TestChat publicChat) {
        this.chats.put(publicChat.getId().toString(), publicChat);
    }
}
