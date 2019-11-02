package ru.v1as.tg.cat.commands.impl;

import static java.util.stream.Collectors.joining;

import lombok.extern.slf4j.Slf4j;
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
public class ScoreCommandHandler implements CommandHandler {

    private final ScoreData scoreData;
    private UnsafeAbsSender sender;

    public ScoreCommandHandler(ScoreData scoreData, UnsafeAbsSender sender) {
        this.scoreData = scoreData;
        this.sender = sender;
    }

    @Override
    public String getCommandName() {
        return "score";
    }

    @Override
    public void handle(TgCommandRequest command, Chat chat, User user) {
        String text =
                scoreData
                        .getWinnersStream(chat.getId(), null)
                        .map(LongProperty::toString)
                        .collect(joining("\n"));
        if (text.length() > 0) {
            sender.executeUnsafe(new SendMessage().setChatId(chat.getId()).setText(text));
        } else {
            log.info("No score data to send");
        }
    }
}
