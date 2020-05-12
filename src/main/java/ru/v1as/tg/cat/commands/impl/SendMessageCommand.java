package ru.v1as.tg.cat.commands.impl;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.Optional;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.commands.impl.AbstractCommand.Configuration.ConfigurationBuilder;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.messages.request.MessageRequest;
import ru.v1as.tg.cat.messages.request.RequestMessageHandler;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.TgInlinePollFactory;

@Component
public class SendMessageCommand extends AbstractCommand {

    private final TgInlinePollFactory pollFactory;
    private final ChatDao chatDao;
    private final RequestMessageHandler requestMsg;
    public static final String COMMAND_NAME = "send";

    public SendMessageCommand(
            ChatDao chatDao, RequestMessageHandler requestMsg, TgInlinePollFactory pollFactory) {
        super(
                new ConfigurationBuilder()
                        .onlyPrivateChat(true)
                        .onlyBotAdmins(true)
                        .commandName(COMMAND_NAME));
        this.chatDao = chatDao;
        this.requestMsg = requestMsg;
        this.pollFactory = pollFactory;
    }

    @Override
    protected void process(TgCommandRequest command, TgChat tgChat, TgUser user) {
        try {
            final long chatId = Long.parseLong(command.getFirstArgument());
            final Optional<ChatEntity> chat = chatDao.findById(chatId);
            if (chat.isPresent()) {
                sender.message(
                        tgChat,
                        "Какое сообщение вы хотите отравить в чат '" + chat.get().getTitle() + "'?");
                requestMsg.addRequest(
                        new MessageRequest(command.getMessage())
                                .filter(m -> !isEmpty(m.getText()))
                                .onResponse(
                                        m -> this.pollSendMessage(tgChat, chat.get(), m.getText()))
                                .onTimeout(
                                        () -> sender.message(tgChat, "Превышен лимит ожидания")));
            } else {
                sender.message(tgChat, "Чат с таким id не найден");
            }
        } catch (NumberFormatException e) {
            sender.message(tgChat, "Не смог распарсить id чата (первый аргумент)");
        }
    }

    private void pollSendMessage(TgChat fromChat, ChatEntity toChat, String message) {
        final TgInlinePoll poll =
                pollFactory
                        .poll(
                                fromChat.getId(),
                                "Вы хотите отправить следующее сообщение в чат '"
                                        + toChat.getTitle()
                                        + "'?\n"
                                        + message)
                        .choice(
                                "Да",
                                c -> {
                                    sender.message(toChat, message);
                                    sender.message(fromChat, "Сообщение отправлено");
                                });
        poll.choice(
                        "Нет",
                        c -> sender.message(fromChat, "Сообщение не отправлено"))
                .send();
    }
}
