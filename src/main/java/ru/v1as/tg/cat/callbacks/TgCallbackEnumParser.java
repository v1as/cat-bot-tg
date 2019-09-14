package ru.v1as.tg.cat.callbacks;

public interface TgCallbackEnumParser<T extends Enum> {

    String getPrefix();

    T parse(String value);

}
