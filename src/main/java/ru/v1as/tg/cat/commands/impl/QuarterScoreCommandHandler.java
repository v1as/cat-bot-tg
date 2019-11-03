package ru.v1as.tg.cat.commands.impl;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class QuarterScoreCommandHandler extends GlobalScoreCommandHandler {

    @Override
    public String getCommandName() {
        return "quarter_score";
    }

    @Override
    public LocalDateTime getDateAfter() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int quarter = now.getMonthValue() / 3;
        int month = Math.max(quarter * 3, 1);
        return LocalDateTime.of(year, month, 1, 0, 0, 0, 0);
    }

    @Override
    protected String getMessagePrefix() {
        return "Начиная с " + getDateAfter().toLocalDate() + ":\n\n";
    }
}
