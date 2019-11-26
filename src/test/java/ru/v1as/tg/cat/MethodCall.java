package ru.v1as.tg.cat;

import java.io.Serializable;
import lombok.Value;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

@Value
public class MethodCall<T extends Serializable> {
    BotApiMethod<T> request;
    T response;

    @SuppressWarnings("unchecked")
    public <K extends BotApiMethod<T>> K getRequest() {
        return (K) request;
    }

    @Override
    public String toString() {
        return "MethodCall{"
                + "request="
                + (request != null ? request.getClass().getSimpleName() : "")
                + ", response="
                + (response != null ? response.getClass().getSimpleName() : "")
                + '}';
    }
}
