package ru.v1as.tg.cat.callbacks;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class SimpleCallbackHandler implements TgCallBackHandler<String> {

    private final String data;

    @Override
    public String getPrefix() {
        return data;
    }

    @Override
    public String parse(String value) {
        Preconditions.checkArgument(data.equals(value));
        return data;
    }
}
