package ru.v1as.tg.cat.service.init;

import static java.util.function.Function.identity;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.CURIOS_CAT;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.REAL;
import static ru.v1as.tg.cat.service.CatEventService.CAT_REWARD;
import static ru.v1as.tg.cat.service.init.ResourceService.MONEY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMembersCount;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.ChatDetailsDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.dao.UserEventDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatDetailsEntity;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;
import ru.v1as.tg.cat.jpa.entities.events.UserEvent;
import ru.v1as.tg.cat.jpa.entities.resource.ResourceEvent;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.FileScoreDataReader;
import ru.v1as.tg.cat.model.FileScoreDataReader.ScoreLine;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
// @Profile("!test")
// @Component
@RequiredArgsConstructor
public class ScoreLineMigrateToDatabase {
    private final UserDao userDao;
    private final ChatDao chatDao;
    private final ChatDetailsDao chatDetailsDao;
    private final UserEventDao userEventDao;
    private final TgSender sender;
    private FileScoreDataReader scoreData = new FileScoreDataReader();

    //    @PostConstruct
    public void init() {
        System.out.println("chatDao" + chatDao.findAll());
        System.out.println("userDao" + userDao.findAll());
        System.out.println("catUserEventDao" + userEventDao.count());
    }

    @PostConstruct
    @Transactional
    public void init2() {
        log.info("Initializing....");
        final List<UserEntity> all = userDao.findAll();
        log.info("users" + all);
        scoreData.init();
        final List<ScoreLine> lines = scoreData.getScore(null);
        final Map<Integer, UserEntity> id2User =
                lines.stream()
                        .map(
                                line ->
                                        UserEntity.builder()
                                                .userName(line.getUserName())
                                                .firstName(line.getFullName())
                                                .id(line.getUserId())
                                                .build())
                        .distinct()
                        .collect(Collectors.toMap(UserEntity::getId, identity()));

        log.info("Loaded {} users", id2User.size());

        final Map<Long, ChatEntity> id2Chat =
                lines.stream()
                        .map(ScoreLine::getChatId)
                        .map(
                                id -> {
                                    ChatEntity chat = new ChatEntity();
                                    chat.setId(id);
                                    return chat;
                                })
                        .collect(Collectors.toMap(ChatEntity::getId, identity(), (a, b) -> b));

        log.info("Loaded {} chats", id2Chat.size());

        Set<Long> toRemoveChatIds = new HashSet<>();
        for (Long chatId : id2Chat.keySet()) {
            try {
                final Chat chat = sender.execute(new GetChat(chatId));
                if (chat.isUserChat()) {
                    log.info("Loaded user chat {}", chat);
                    toRemoveChatIds.add(chatId);
                } else {
                    final ChatEntity publicChatEntity = id2Chat.get(chatId);
                    publicChatEntity.setTitle(chat.getTitle());
                    publicChatEntity.setDescription(chat.getDescription());
//                    try {
//                        final Integer amount =
//                                sender.execute(new GetChatMembersCount().setChatId(chatId));
//                        publicChatEntity.setMembersAmount(amount);
//                    } catch (Exception e) {
//                        log.error(
//                                "Error with chat  {} members amount loading, error: {}",
//                                chatId,
//                                e.getMessage());
//                    }
                    log.info("Loaded public chat {}", publicChatEntity);
                }
            } catch (Exception e) {
                log.error("Error with chat {} loading, error {}", chatId, e.getMessage());
                toRemoveChatIds.add(chatId);
            }
        }

        toRemoveChatIds.forEach(id2Chat::remove);
        chatDao.saveAll(id2Chat.values());

        Set<Integer> toRemoveUsersIds = new HashSet<>();
        for (UserEntity userEntity : id2User.values()) {
            for (ChatEntity chat : id2Chat.values()) {
                try {
                    final ChatMember chatMember =
                            sender.execute(
                                    new GetChatMember()
                                            .setUserId(userEntity.getId())
                                            .setChatId(chat.getId()));
                    if (!"left".equals(chatMember.getStatus())) {
                        final User user = chatMember.getUser();
                        userEntity.setFirstName(user.getFirstName());
                        userEntity.setLastName(user.getLastName());
                        userEntity.setUserName(user.getUserName());
                        userEntity.setLanguageCode(user.getLanguageCode());
                        userEntity.setPrivateChat(false);
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
        chatDao.saveAll(id2Chat.values());

        List<UserEvent> events = new ArrayList<>();
        for (ScoreLine line : lines) {
            CatUserEvent event = new CatUserEvent();
            final CatRequestVote cats = line.getResult();
            final UserEntity owner = id2User.get(line.getUserId());
            final ChatEntity chat = id2Chat.get(line.getChatId());
            event.setUser(owner);
            event.setChat(chat);
            event.setResult(cats);
            event.setCatType(line.getIsReal() ? REAL : CURIOS_CAT);
            event.setMessageId(line.getId());
            event.setDate(line.getDate());

            final BigDecimal catsRewards = CAT_REWARD.multiply(new BigDecimal(cats.getAmount()));
            events.add(new ResourceEvent(MONEY, catsRewards, event, owner, chat));
            events.add(event);
        }
        userEventDao.saveAll(events);
        chatDetailsDao.saveAll(
                id2Chat.values().stream()
                        .map(
                                c -> {
                                    final ChatDetailsEntity details = new ChatDetailsEntity();
                                    details.setChat(c);
                                    details.setCatPollEnabled(false);
                                    return details;
                                })
                        .collect(Collectors.toList()));

        System.out.println("chatDao" + chatDao.findAll());
        System.out.println("userDao" + userDao.findAll());
        System.out.println("catUserEventDao" + userEventDao.count());
    }
}
