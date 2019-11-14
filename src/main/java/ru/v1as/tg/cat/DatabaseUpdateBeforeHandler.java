package ru.v1as.tg.cat;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.function.Function.identity;
import static ru.v1as.tg.cat.model.UpdateUtils.getChat;
import static ru.v1as.tg.cat.model.UpdateUtils.getUser;

import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

@Component
@RequiredArgsConstructor
public class DatabaseUpdateBeforeHandler implements TgUpdateBeforeHandler {

    private final UserDao userDao;
    private final ChatDao chatDao;
    private Map<Integer, UserEntity> users = emptyMap();
    private Map<Long, ChatEntity> chats = emptyMap();

    @PostConstruct
    public void init() {
        users = userDao.findAll().stream().collect(Collectors.toMap(UserEntity::getId, identity()));
        chats = chatDao.findAll().stream().collect(Collectors.toMap(ChatEntity::getId, identity()));
    }

    @Override
    public void register(Update update) {
        TgChat chat = getChat(update);
        TgUser user = getUser(update);
        if (chat == null || user == null) {
            return;
        }
        updateUserEntity(chat, user);
        updateChatEntity(chat);
    }

    private void updateChatEntity(TgChat chat) {
        ChatEntity chatEntity = chats.get(chat.getId());
        boolean chatToSave;
        if (chatEntity == null) {
            chatEntity =
                    new ChatEntity(
                            chat.getId(), chat.getTitle(), chat.getDescription(), -1, emptyList());
            // todo update amount
            chatToSave = true;
        } else {
            chatToSave = chatEntity.update(chat);
        }
        if (chatToSave) {
            chatDao.save(chatEntity);
        }
    }

    private void updateUserEntity(TgChat chat, TgUser user) {
        boolean userToSave;
        UserEntity userEntity = users.get(user.getId());
        if (null == userEntity) {
            userEntity =
                    new UserEntity(
                            user.getId(),
                            user.getUserName(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getLanguageCode(),
                            false);
            userToSave = true;
        } else {
            userToSave = userEntity.update(user);
        }
        if (chat.isUserChat() && !userEntity.getPrivateChat()) {
            userToSave = true;
            userEntity.setPrivateChat(true);
        }
        if (userToSave) {
            userDao.save(userEntity);
        }
    }
}
