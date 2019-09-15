package ru.v1as.tg.cat.callbacks;

public interface TgCallbackParser<T> {

    String getPrefix();

    T parse(String value);
}
