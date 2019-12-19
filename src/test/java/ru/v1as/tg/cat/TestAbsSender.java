package ru.v1as.tg.cat;

import java.io.Serializable;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMembersCount;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import ru.v1as.tg.cat.tg.TgSender;

public class TestAbsSender implements TgSender {

    private LinkedList<MethodCall<?>> items = new LinkedList<>();
    private BiFunction<String, String, Message> messageProducer = (chatId, text) -> null;
    private List<SendDocument> documents = new LinkedList<>();
    private Map<Integer, Message> messages = new HashMap<>();

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
            message = produceMessage(sendMessage.getChatId(), sendMessage.getText());
            result = (T) message;
        } else if (method instanceof DeleteMessage) {
            final DeleteMessage deleteMessage = (DeleteMessage) method;
            final Boolean messageExist = messages.containsKey(deleteMessage.getMessageId());
            message = messages.get(deleteMessage.getMessageId());
            result = (T) messageExist;
        }
        items.add(new MethodCall<>(method, result, message));
        return result;
    }

    @Override
    public Message executeDoc(SendDocument sendDocument) {
        documents.add(sendDocument);
        return produceMessage(sendDocument.getChatId(), "%document%");
    }

    public void setMessageProducer(BiFunction<String, String, Message> messageProducer) {
        this.messageProducer = messageProducer;
    }

    private Message produceMessage(String chatId, String text) {
        final Message message = messageProducer.apply(chatId, text);
        messages.put(message.getMessageId(), message);
        return message;
    }

    public void clear() {
        items.clear();
        documents.clear();
    }

    public int getMethodsAmount() {
        return items.size();
    }

    public Deque<MethodCall<?>> getMethodCalls() {
        return items;
    }
}
