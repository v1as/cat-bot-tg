package ru.v1as.tg.cat.commands.impl;

import static java.util.stream.Collectors.joining;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.LongProperty;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@Component
public class GlobalScoreCommandHandler implements CommandHandler {

    @Autowired private ScoreData scoreData;
    @Autowired private UnsafeAbsSender sender;

    @Override
    public String getCommandName() {
        return "global_score";
    }

    @Override
    public void handle(TgCommandRequest command, Chat chat, User user) {
        String text =
                scoreData
                        .getWinnersStream(chat.getId(), getDateAfter())
                        .map(LongProperty::toString)
                        .collect(joining("\n"));
        SendMessage message = new SendMessage().setChatId(chat.getId());
        if (text.length() > 0) {
            sender.executeUnsafe(message.setText(getMessagePrefix() + text));
        } else {
            sender.executeUnsafe(message.setText("Пока что тут пусто"));
            log.info("No score data to send");
        }
    }

    protected String getMessagePrefix() {
        return "Счёт за всё время: \n\n";
    }

    public LocalDateTime getDateAfter() {
        return null;
    }

}
