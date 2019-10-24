package ru.v1as.tg.cat.callbacks.phase.poll;

import java.util.function.Consumer;
import lombok.Value;

@Value
public class PollChoice {
    private final String uuid;
    private final PollChoiceType type;
    private final String text;
    private final String url;
    private final Consumer<ChooseContext> callable;
}
