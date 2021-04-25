package ru.v1as.tg.cat.commands.impl;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.messages.request.MessageRequest;
import ru.v1as.tg.cat.messages.request.RequestMessageHandler;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.BotConfiguration;
import ru.v1as.tg.cat.service.TgInlinePollFactory;
import ru.v1as.tg.cat.tg.TgSender;

@Component
public class FeedbackCommand extends AbstractCommand {

    private final TgSender sender;
    private final RequestMessageHandler requestMsg;
    private final TgInlinePollFactory pollFactory;
    private final BotConfiguration conf;
    private final UserDao userDao;

    public FeedbackCommand(
        TgSender sender, RequestMessageHandler requestMsg, TgInlinePollFactory pollFactory,
        BotConfiguration conf, UserDao userDao) {
        super(cfg().onlyPrivateChat(true).commandName("feedback"));
        this.sender = sender;
        this.requestMsg = requestMsg;
        this.pollFactory = pollFactory;
        this.conf = conf;
        this.userDao = userDao;
    }

    @Override
    public String getCommandDescription() {
        return "Связаться с разработчиком.";
    }

    @Override
    protected void process(TgCommandRequest command, TgChat chat, TgUser user) {
        sender.message(chat, "Что вы хотите сообщить разработчикам?");
        requestMsg.addRequest(
                new MessageRequest(command.getMessage())
                        .filter(m -> !isEmpty(m.getText()))
                        .onResponse(m -> this.pollSendMessage(chat, user, m.getText()))
                        .onTimeout(() -> sender.message(chat, "Превышен лимит ожидания")));
    }

    private void pollSendMessage(TgChat chat, TgUser user, String message) {
        final TgInlinePoll poll =
                pollFactory
                        .poll(
                                chat.getId(),
                                "Вы хотите отправить следующее сообщение разработчикам?\n\n "
                                        + message)
                        .choice(
                                "Да",
                                c -> {
                                    this.sendMessageToAdmin(user, message);
                                    sender.message(chat, "Сообщение отправлено");
                                });
        poll.choice("Нет", c -> sender.message(chat, "Сообщение не отправлено")).send();
    }

    private void sendMessageToAdmin(TgUser from, String message) {
        final List<UserEntity> admins =
                conf.getAdminUserNames().stream()
                        .map(userDao::findByUserName)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(UserEntity::isPrivateChat)
                        .collect(Collectors.toList());
        for (UserEntity admin : admins) {
            String text =
                    String.format("От пользователя %s пришло сообщение: \n %s", from, message);
            sender.message(admin, text);
        }
    }

}
