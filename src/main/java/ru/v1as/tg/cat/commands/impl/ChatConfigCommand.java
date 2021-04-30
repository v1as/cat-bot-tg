package ru.v1as.tg.cat.commands.impl;

import static java.time.temporal.ChronoUnit.MINUTES;
import static ru.v1as.tg.cat.service.ChatParam.PICTURE_POLL_ENABLED;
import static ru.v1as.tg.cat.service.ChatParam.PUBLIC_NEWS_ENABLED;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.ChatParam;
import ru.v1as.tg.cat.service.ChatParamResource;
import ru.v1as.tg.cat.service.TgInlinePollFactory;
import ru.v1as.tg.cat.tg.TgSender;

@Component
public class ChatConfigCommand extends AbstractCommand {

    @Autowired private ChatDao chatDao;
    @Autowired private ChatParamResource chatParamResource;
    @Autowired private TgSender tgSender;
    @Autowired private TgInlinePollFactory pollFactory;

    public ChatConfigCommand() {
        super(cfg().commandName("chat_config").onlyPrivateChat(true));
    }

    @Override
    protected void process(TgCommandRequest command, TgChat chat, TgUser user) {
        List<ChatEntity> chats =
                chatDao.findByUsersId(user.getId()).stream()
                        .filter(ChatEntity::updatedRecently)
                        .collect(Collectors.toList());
        if (chats.isEmpty()) {
            tgSender.message(chat, "Вы не состоите ни в одном чате");
        } else if (chats.size() == 1) {
            startChatConfig(null, chats.get(0), user);
        } else {
            TgInlinePoll poll = pollFactory.poll(chat.getId(), "Какой чат будем конфигурировать?");
            chats.forEach(
                    c ->
                            poll.choice(
                                    c.getTitle(),
                                    ctx -> {
                                        startChatConfig(null, c, user);
                                        poll.close();
                                    }));
            poll.send();
        }
    }

    private void startChatConfig(TgInlinePoll poll, TgChat chat, TgUser user) {
        final TgInlinePoll updatedPoll =
                poll == null
                        ? pollFactory
                                .poll(user.getChatId(), "Что будем конфигурировать?")
                                .timeout(new PollTimeoutConfiguration(Duration.of(1, MINUTES)))
                                .closeOnChoose(false)
                        : poll;
        updatedPoll.clearChoices();
        updatedPoll
                .choice(
                        emojiEnabled(chat, PICTURE_POLL_ENABLED) + "Запросы 'Это кот?'",
                        ctx -> toggleParam(updatedPoll, chat, user, PICTURE_POLL_ENABLED))
                .choice(
                        emojiEnabled(chat, PUBLIC_NEWS_ENABLED) + "Получение новостей",
                        ctx -> toggleParam(updatedPoll, chat, user, PUBLIC_NEWS_ENABLED))
                .choice("Закончить", ctx -> updatedPoll.close())
                .send();
    }

    private void toggleParam(TgInlinePoll poll, TgChat chat, TgUser user, ChatParam param) {
        boolean value = chatParamResource.paramBool(chat.getId(), param);
        chatParamResource.param(chat.getId(), user.getId(), param, !value);
        startChatConfig(poll, chat, user);
    }

    private String emojiEnabled(TgChat chat, ChatParam param) {
        return (chatParamResource.paramBool(chat.getId(), param)
                        ? EmojiConst.GREEN_CIRCLE
                        : EmojiConst.RED_CIRCLE)
                + " ";
    }
}
