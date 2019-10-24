package ru.v1as.tg.cat.callbacks.phase;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true, fluent = true)
@RequiredArgsConstructor
public class PollTimeoutConfiguration {
    private final Duration delay;
    private boolean removeMsg = false;
    private String message;
}
