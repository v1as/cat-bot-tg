package ru.v1as.tg.cat;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import org.glassfish.jersey.internal.util.Producer;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import ru.v1as.tg.cat.tg.TgSender;

public class TestAbsSender implements TgSender {

    protected LinkedList<BotApiMethod<?>> methods = new LinkedList<>();
    protected Producer<? extends Serializable> messageProducer = () -> null;
    private List<SendDocument> documents = new LinkedList<>();

    @SuppressWarnings("unchecked")
    @Override
    public <
                    T extends Serializable,
                    Method extends BotApiMethod<T>,
                    Callback extends SentCallback<T>>
            void executeAsync(Method method, Callback callback) {
        methods.add(method);
        callback.onResult(method, (T) messageProducer.call());
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        methods.add(method);
        return null;
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
        methods.clear();
    }

    public int getMethodsAmount() {
        return methods.size();
    }

    public LinkedList<BotApiMethod<?>> getMethods() {
        return methods;
    }
}
