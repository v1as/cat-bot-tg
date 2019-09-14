package ru.v1as.tg.cat;

import static java.util.stream.Collectors.joining;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.LongProperty;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

public class ScoreCommandHandler implements CommandHandler {

    private final ScoreData scoreData;
    private UnsafeAbsSender sender;

    public ScoreCommandHandler(ScoreData scoreData, UnsafeAbsSender sender) {
        this.scoreData = scoreData;
        this.sender = sender;
    }

    @Override
    public String getCommandName() {
        return null;
    }

    @Override
    public void handle(TgCommandRequest command, Chat chat, User user) {
        String text =
                scoreData
                        .getWinnersStream(chat.getId(), null)
                        .map(LongProperty::toString)
                        .collect(joining("\n"));
        sender.executeUnsafe(new SendMessage().setChatId(chat.getId()).setText(text));
    }
}
