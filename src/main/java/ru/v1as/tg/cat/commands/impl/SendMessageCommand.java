package ru.v1as.tg.cat.commands.impl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.commands.impl.AbstractCommand.Configuration.ConfigurationBuilder;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.messages.request.MessageRequest;
import ru.v1as.tg.cat.messages.request.RequestMessageHandler;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.model.TgUserChat;
import ru.v1as.tg.cat.service.TgInlinePollFactory;
import ru.v1as.tg.cat.service.clock.BotClock;

@Component
public class SendMessageCommand extends AbstractCommand {

    private final TgInlinePollFactory pollFactory;
    private final ChatDao chatDao;
    private final UserDao userDao;
    private final RequestMessageHandler requestMsg;
    private final BotClock botClock;
    public static final String COMMAND_NAME = "send";

    public SendMessageCommand(
            ChatDao chatDao,
            RequestMessageHandler requestMsg,
            TgInlinePollFactory pollFactory,
            UserDao userDao,
            BotClock botClock) {
        super(
                new ConfigurationBuilder()
                        .onlyPrivateChat(true)
                        .onlyBotAdmins(true)
                        .commandName(COMMAND_NAME));
        this.chatDao = chatDao;
        this.requestMsg = requestMsg;
        this.pollFactory = pollFactory;
        this.userDao = userDao;
        this.botClock = botClock;
    }

    @Override
    protected void process(TgCommandRequest command, TgChat tgChat, TgUser user) {

        String destination = command.getFirstArgument();
        List<TgChat> chats = emptyList();
        String chatTitle = "NONE";
        if ("all".equalsIgnoreCase(destination)) {
            chats =
                    chatDao.findAll().stream()
                            .filter(ChatEntity::updatedRecently)
                            .collect(Collectors.toList());
            chatTitle = "ALL:" + chats.size();
        } else {
            try {
                final long chatId = Long.parseLong(destination);
                final Optional<ChatEntity> chat = chatDao.findById(chatId);
                if (chat.isPresent()) {
                    chats = singletonList(chat.get());
                    chatTitle = chat.get().getTitle();
                } else {
                    Optional<UserEntity> userChat = userDao.findById(((Long) chatId).intValue());
                    if (userChat.isPresent()) {
                        chatTitle = userChat.get().toString();
                        chats = singletonList(new TgUserChat(userChat.get()));
                    } else {
                        sender.message(tgChat, "Чат с таким id не найден");
                    }
                }
            } catch (NumberFormatException e) {
                sender.message(tgChat, "Не смог распарсить id чата (первый аргумент)");
            }
        }
        if (chats.isEmpty()) {
            return;
        }
        sender.message(tgChat, "Какое сообщение вы хотите отравить в чат '%s'?", chatTitle);
        List<TgChat> finalChats = chats;
        requestMsg.addRequest(
                new MessageRequest(command.getMessage())
                        .filter(m -> !isEmpty(m.getText()))
                        .onResponse(m -> this.pollSendMessage(tgChat, finalChats, m.getText()))
                        .onTimeout(() -> sender.message(tgChat, "Превышен лимит ожидания")));
    }

    private void pollSendMessage(TgChat fromChat, List<TgChat> toChats, String message) {
        String title = toChats.size() == 1 ? toChats.get(0).getTitle() : "ALL:" + toChats.size();
        final TgInlinePoll poll =
                pollFactory
                        .poll(
                                fromChat.getId(),
                                "Вы хотите отправить следующее сообщение в чат '%s'?\n\n%s",
                                title,
                                message)
                        .choice(
                                "Да",
                                c -> {
                                    AtomicInteger sent = new AtomicInteger();
                                    for (int i = 0; i < toChats.size(); i++) {
                                        TgChat toChat = toChats.get(i);
                                        botClock.schedule(
                                                sendMessageTask(message, sent, toChat), i, SECONDS);
                                    }
                                    botClock.schedule(
                                            notificationTask(fromChat, sent),
                                            toChats.size(),
                                            SECONDS);
                                });
        poll.choice("Нет", c -> sender.message(fromChat, "Сообщение не отправлено")).send();
    }

    private Runnable sendMessageTask(String message, AtomicInteger sent, TgChat chat) {
        return () -> {
            sender.message(chat, message);
            sent.incrementAndGet();
        };
    }

    private Runnable notificationTask(TgChat chat, AtomicInteger sent) {
        return () -> {
            String msg = String.format("Сообщений в чаты отправлено %s", sent.get());
            sender.message(chat, msg);
        };
    }
}
