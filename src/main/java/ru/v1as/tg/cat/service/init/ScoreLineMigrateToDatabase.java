package ru.v1as.tg.cat.service.init;

import static java.util.function.Function.identity;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMembersCount;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.dao.PublicChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.chat.PublicChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.model.ScoreData.ScoreLine;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScoreLineMigrateToDatabase {
    private final UserDao userDao;
    private final PublicChatDao publicChatDao;
    private final CatUserEventDao catUserEventDao;
    private final ScoreData scoreData;
    private final UnsafeAbsSender unsafeAbsSender;

    @Transactional
    public void init2() {
        log.info("Initializing....");
        final List<UserEntity> all = userDao.findAll();
        log.info("users" + all);
        final List<ScoreLine> lines = scoreData.getScore(null);
        final Map<Integer, UserEntity> id2User =
                lines.stream()
                        .map(
                                line ->
                                        UserEntity.builder()
                                                .username(line.getUserName())
                                                .firstName(line.getFullName())
                                                .id(line.getUserId())
                                                .build())
                        .distinct()
                        .collect(Collectors.toMap(UserEntity::getId, identity()));

        log.info("Loaded {} users", id2User.size());

        final Map<Long, PublicChatEntity> id2Chat =
                lines.stream()
                        .map(ScoreLine::getChatId)
                        .map(
                                id -> {
                                    PublicChatEntity chat = new PublicChatEntity();
                                    chat.setId(id);
                                    return chat;
                                })
                        .collect(
                                Collectors.toMap(PublicChatEntity::getId, identity(), (a, b) -> b));

        log.info("Loaded {} chats", id2Chat.size());

        Set<Long> toRemoveChatIds = new HashSet<>();
        for (Long chatId : id2Chat.keySet()) {
            try {
                final Chat chat = unsafeAbsSender.executeUnsafe(new GetChat(chatId));
                if (chat.isUserChat()) {
                    log.info("Loaded user chat {}", chat);
                    toRemoveChatIds.add(chatId);
                } else {
                    final PublicChatEntity publicChatEntity = id2Chat.get(chatId);
                    publicChatEntity.setTitle(chat.getTitle());
                    publicChatEntity.setDescription(chat.getDescription());
                    try {
                        final Integer amount =
                                unsafeAbsSender.executeUnsafe(
                                        new GetChatMembersCount().setChatId(chatId));
                        publicChatEntity.setMembersAmount(amount);
                    } catch (Exception e) {
                        log.error(
                                "Error with chat  {} members amount loading, error: {}",
                                chatId,
                                e.getMessage());
                    }
                    log.info("Loaded public chat {}", publicChatEntity);
                }

            } catch (Exception e) {
                log.error("Error with chat {} loading, error {}", chatId, e.getMessage());
                toRemoveChatIds.add(chatId);
            }
        }

        toRemoveChatIds.forEach(id2Chat::remove);
        publicChatDao.saveAll(id2Chat.values());

        Set<Integer> toRemoveUsersIds = new HashSet<>();
        for (UserEntity userEntity : id2User.values()) {
            for (PublicChatEntity chat : id2Chat.values()) {
                try {
                    final ChatMember chatMember =
                            unsafeAbsSender.executeUnsafe(
                                    new GetChatMember()
                                            .setUserId(userEntity.getId())
                                            .setChatId(chat.getId()));
                    if (!"left".equals(chatMember.getStatus())) {
                        final User user = chatMember.getUser();
                        userEntity.setFirstName(user.getFirstName());
                        userEntity.setLastName(user.getLastName());
                        userEntity.setUsername(user.getUserName());
                        userEntity.setLanguageCode(user.getLanguageCode());
                        chat.getUsers().add(userEntity);
                        log.info(
                                "User '{}' loaded with for chat '{}'",
                                userEntity.getUsernameOrFullName(),
                                chat.getTitle());
                        break;
                    }
                    Thread.sleep(2000);
                } catch (Exception e) {
                    toRemoveUsersIds.add(userEntity.getId());
                    log.error(
                            "Error with user {} loading, message: {}", userEntity, e.getMessage());
                }
            }
        }
        toRemoveUsersIds.forEach(id2User::remove);
        userDao.saveAll(id2User.values());
        publicChatDao.saveAll(id2Chat.values());

        System.out.println(publicChatDao.findAll());
        System.out.println(userDao.findAll());
    }

}
