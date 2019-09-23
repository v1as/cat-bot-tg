package ru.v1as.tg.cat.callbacks;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class SimpleCallbackHandler {

    private final String data;

    public String getPrefix() {
        return data;
    }

    public String parse(String value) {
        Preconditions.checkArgument(data.equals(value));
        return data;
    }
}
