package ru.v1as.tg.cat;

import static java.time.LocalDateTime.now;
import static ru.v1as.tg.cat.model.UpdateUtils.getChat;
import static ru.v1as.tg.cat.model.UpdateUtils.getUser;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.ChatDetailsDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatDetailsEntity;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.ChatService;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseUpdateBeforeHandler implements TgUpdateBeforeHandler {

    private final UserDao userDao;
    private final ChatDao chatDao;
    private final ChatDetailsDao chatDetailsDao;
    private final ChatService chatService;

    @Override
    public void register(Update update) {
        TgChat chat = getChat(update);
        TgUser user = getUser(update);
        if (chat == null || user == null) {
            return;
        }
        updateUserEntity(chat, user);
        updateChatEntity(chat, user);
    }

    private void updateChatEntity(TgChat chat, TgUser user) {
        if (chat.isUserChat()) {
            return;
        }
        ChatEntity chatEntity = chatDao.findById(chat.getId()).orElse(null);
        UserEntity userEntity = userDao.findById(user.getId()).orElse(null);
        ChatDetailsEntity chatDetails = null;
        boolean chatToSave;
        if (chatEntity == null) {
            chatEntity =
                    new ChatEntity(
                            chat.getId(),
                            chat.getTitle(),
                            chat.getDescription(),
                            now(),
                            new ArrayList<>());
            chatDetails = new ChatDetailsEntity();
            chatDetails.setId(chat.getId());
            chatDetails.setChat(chatEntity);
            chatDetails.setCatPollEnabled(false);
            chatDetails.setMembersAmount(-1);
            chatToSave = true;
            chatEntity.getUsers().add(userEntity);
        } else {
            chatToSave = chatEntity.update(chat, userEntity);
        }
        if (chatToSave) {
            chatDao.save(chatEntity);
        }
        if (chatDetails != null) {
            chatDetailsDao.save(chatDetails);
            chatService.updateChatDetailsAmount(chat.getId());
        }
    }

    private void updateUserEntity(TgChat chat, TgUser user) {
        boolean userToSave;
        UserEntity userEntity = userDao.findById(user.getId()).orElse(null);
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
        if (chat.isUserChat() && !userEntity.isPrivateChat()) {
            userToSave = true;
            userEntity.setPrivateChat(true);
        }
        if (userToSave) {
            userDao.save(userEntity);
        }
    }
}
