package ru.v1as.tg.cat.commands.impl;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class SeasonScoreCommandHandler extends GlobalScoreCommandHandler {

    @Override
    public String getCommandName() {
        return "season_score";
    }

    @Override
    public String getCommandDescription() {
        return "Вывести счёт за текущий сезон (год)";
    }

    @Override
    public LocalDateTime getDateAfter() {
        int year = LocalDateTime.now().getYear();
        return LocalDateTime.of(year, 1, 1, 0, 0, 0, 0);
    }

    @Override
    protected String getMessagePrefix() {
        return "Начиная с " + getDateAfter().toLocalDate() + ":\n\n";
    }
}
