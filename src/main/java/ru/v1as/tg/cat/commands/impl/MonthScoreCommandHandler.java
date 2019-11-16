package ru.v1as.tg.cat.commands.impl;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MonthScoreCommandHandler extends GlobalScoreCommandHandler {

    @Override
    public String getCommandName() {
        return "score";
    }

    @Override
    public LocalDateTime getDateAfter() {
        return LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
    }

    @Override
    protected String getMessagePrefix() {
        return "Начиная с " + getDateAfter().toLocalDate() + ":\n\n";
    }
}
