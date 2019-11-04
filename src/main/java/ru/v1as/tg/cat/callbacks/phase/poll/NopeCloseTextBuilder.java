package ru.v1as.tg.cat.callbacks.phase.poll;

public class NopeCloseTextBuilder implements CloseOnTextBuilder  {
    @Override
    public String build(String description, String choose) {
        return description;
    }
}
