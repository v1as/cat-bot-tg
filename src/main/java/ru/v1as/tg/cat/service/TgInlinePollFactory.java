package ru.v1as.tg.cat.service;

import static java.time.Duration.ofMinutes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;
import ru.v1as.tg.cat.service.clock.BotClock;
import ru.v1as.tg.cat.tg.TgSender;

@Component
@RequiredArgsConstructor
public class TgInlinePollFactory {

    private final TgSender sender;
    private final BotClock clock;
    private final TgCallbackProcessor callbackProcessor;

    public TgInlinePoll poll(Long chatId, String text) {
        final TgInlinePoll poll = new TgInlinePoll();
        poll.setSender(sender);
        poll.setBotClock(clock);
        poll.setCallbackProcessor(callbackProcessor);
        poll.chatId(chatId);
        poll.text(text);
        poll.timeout(new PollTimeoutConfiguration(ofMinutes(1)));
        return poll;
    }
}
