package ru.v1as.tg.cat;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import org.glassfish.jersey.internal.util.Producer;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMembersCount;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import ru.v1as.tg.cat.tg.TgSender;

public class TestAbsSender implements TgSender {

    protected LinkedList<MethodCall> items = new LinkedList<>();
    protected Producer<? extends Serializable> messageProducer = () -> null;
    private List<SendDocument> documents = new LinkedList<>();

    @SuppressWarnings("unchecked")
    @Override
    public <
                    T extends Serializable,
                    Method extends BotApiMethod<T>,
                    Callback extends SentCallback<T>>
            void executeAsync(Method method, Callback callback) {
        final T response = (T) messageProducer.call();
        items.add(new MethodCall(method, response));
        callback.onResult(method, response);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        T result = null;
        if (method instanceof GetChatMembersCount) {
            result = (T) new Integer(-1);
        }
        items.add(new MethodCall(method, null));
        return result;
    }

    @Override
    public Message executeDoc(SendDocument sendDocument) {
        documents.add(sendDocument);
        return (Message) messageProducer.call();
    }

    public void setMessageProducer(Producer<? extends Serializable> messageProducer) {
        this.messageProducer = messageProducer;
    }

    public void clear() {
        items.clear();
        documents.clear();
    }

    public int getMethodsAmount() {
        return items.size();
    }

    public Deque<MethodCall> getMethodCalls() {
        return items;
    }
}
