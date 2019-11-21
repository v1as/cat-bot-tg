package ru.v1as.tg.cat.commands.impl;

import static java.util.stream.Collectors.joining;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.LongProperty;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.ScoreDataService;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
public class GlobalScoreCommandHandler implements CommandHandler {

    public static final LocalDateTime LONG_TIME_AGO = LocalDateTime.now().minusYears(100);
    @Autowired private ScoreDataService scoreData;
    @Autowired private TgSender sender;

    @Override
    public String getCommandDescription() {
        return "Вывести счёт за всё время";
    }

    @Override
    public String getCommandName() {
        return "global_score";
    }

    @Override
    public void handle(TgCommandRequest command, TgChat chat, TgUser user) {
        String text =
                scoreData
                        .getWinnersStream(chat.getId(), getDateAfter())
                        .map(LongProperty::toString)
                        .collect(joining("\n"));
        SendMessage message = new SendMessage().setChatId(chat.getId());
        if (text.length() > 0) {
            sender.execute(message.setText(getMessagePrefix() + text));
        } else {
            sender.execute(message.setText("Пока что тут пусто"));
            log.info("No score data to send");
        }
    }

    protected String getMessagePrefix() {
        return "Счёт за всё время: \n\n";
    }

    public LocalDateTime getDateAfter() {
        return LONG_TIME_AGO;
    }
}
