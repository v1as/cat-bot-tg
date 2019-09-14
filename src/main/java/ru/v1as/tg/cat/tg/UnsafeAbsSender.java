package ru.v1as.tg.cat.tg;

import java.io.Serializable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

public interface UnsafeAbsSender {

    <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>>
            void executeAsyncUnsafe(Method method, Callback callback);

    <T extends Serializable, Method extends BotApiMethod<T>> T executeUnsafe(Method method);

    void setSender(AbsSender sender);
}
