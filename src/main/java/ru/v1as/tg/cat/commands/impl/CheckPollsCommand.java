package ru.v1as.tg.cat.commands.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.*;
import ru.v1as.tg.cat.tg.TgSender;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CheckPollsCommand extends AbstractCommand {

    private final TgSender sender;
    private final CatBotData data;

    public CheckPollsCommand(TgSender sender, CatBotData data) {
        super(cfg().commandName("check_poll").onlyPublicChat(true));
        this.sender = sender;
        this.data = data;
    }

    @Override
    public String getCommandDescription() {
        return "Проверить незакрытые опросы 'Это кот?'";
    }

    @Override
    protected void process(TgCommandRequest command, TgChat chat, TgUser user) {
        List<CatRequest> openRequest =
                data.getChatData(chat).getCatRequests().stream()
                        .filter(TgRequestPoll::isOpen)
                        .sorted(Comparator.comparing(TgRequestPoll::getCreated))
                        .collect(Collectors.toList());
        if (openRequest.isEmpty()) {
            sender.message(chat, "Все опросы закрыты");
        } else {
            SendMessage message =
                    new SendMessage(
                            chat.getId(),
                            "Самый ранний не закрытый опрос.\n\n Всего не закрыто: " + openRequest.size());
            message.setReplyToMessageId(openRequest.get(0).getMessageId());
            sender.executeAsync(message);
        }
    }

}
