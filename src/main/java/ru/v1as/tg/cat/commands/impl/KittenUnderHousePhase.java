package ru.v1as.tg.cat.commands.impl;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.phase.AbstractPhase;
import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.model.ScoreData;

@Component
@RequiredArgsConstructor
public class KittenUnderHousePhase extends AbstractPhase<KittenUnderHousePhase.Context> {

    private static final PollTimeoutConfiguration TIMEOUT_LEAVE_CAT =
        new PollTimeoutConfiguration(Duration.of(15, SECONDS))
            .removeMsg(true)
            .message("Любопытный кот убежал");

    private final CatBotData data;
    private final ScoreData scoreData;

    @Override
    protected void open() {}

    public class Context extends PhaseContext {
        public Context(Chat chat) {
            super(chat);
        }
    }
}
