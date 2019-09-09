package ru.v1as.tg.cat.callback;

public interface TgCallbackEnumParser<T extends Enum> {

    String getPrefix();

    T parse(String value);

}
