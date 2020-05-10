package ru.v1as.tg.cat.service;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.ChatParamValueDao;
import ru.v1as.tg.cat.jpa.dao.ChatUserParamValueDao;
import ru.v1as.tg.cat.jpa.dao.NoSuchEntityException;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.dao.UserEventDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.chat.ChatParamValue;
import ru.v1as.tg.cat.jpa.entities.events.ChatParamChangeEvent;
import ru.v1as.tg.cat.jpa.entities.events.ChatUserParamChangeEvent;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParam;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParamValue;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatParamResource {

    private final ChatParamValueDao paramDao;
    private final ChatUserParamValueDao userParamDao;
    private final ChatDao chatDao;
    private final UserDao userDao;
    private final UserEventDao eventDao;
    private final BotConfiguration conf;

    public boolean paramBool(Long chatId, Integer userId, ChatUserParam param) {
        return Boolean.parseBoolean(param(chatId, userId, param));
    }

    public int paramInt(ChatEntity chat, ChatParam param) {
        return paramInt(chat.getId(), param);
    }

    public int paramInt(Long chat, ChatParam param) {
        return parseInt(param(chat, param));
    }

    public int paramInt(ChatEntity chat, UserEntity user, ChatUserParam param) {
        return paramInt(chat.getId(), user.getId(), param);
    }

    public int paramInt(Long chat, Integer user, ChatUserParam param) {
        return userParamDao
                .findByChatIdAndUserIdAndParam(chat, user, param)
                .map(ChatUserParamValue::getInt)
                .orElse(parseInt(param.getDefaultValue()));
    }

    public String param(Long chat, ChatParam param) {
        return paramDao.findByChatIdAndParam(chat, param)
                .map(ChatParamValue::getValue)
                .orElse(param.getDefaultValue());
    }

    public String param(ChatEntity chat, UserEntity user, ChatUserParam param) {
        return param(chat.getId(), user.getId(), param);
    }

    public String param(Long chat, Integer user, ChatUserParam param) {
        return userParamDao
                .findByChatIdAndUserIdAndParam(chat, user, param)
                .map(ChatUserParamValue::getValue)
                .orElse(param.getDefaultValue());
    }

    public List<ChatUserParamChangeEvent> param(
            TgChat chat, TgUser user, ChatUserParam param, @NonNull Object newValue) {
        return param(chat.getId(), user.getId(), param, newValue);
    }

    public List<ChatUserParamChangeEvent> param(
            Long chat, Integer user, ChatUserParam param, @NonNull Object newValue) {
        String value = newValue.toString();
        final ChatEntity chatEntity =
                chatDao.findById(chat).orElseThrow(NoSuchEntityException::new);
        final UserEntity userEntity =
                userDao.findById(user).orElseThrow(NoSuchEntityException::new);
        final ChatUserParamValue paramValue =
                this.userParamDao
                        .findByChatIdAndUserIdAndParam(chat, user, param)
                        .orElse(
                                new ChatUserParamValue(
                                        chatEntity, userEntity, param, param.getDefaultValue()));
        String oldValue = paramValue.getValue();
        if (Objects.equals(oldValue, value)) {
            return emptyList();
        }
        paramValue.setValue(value);
        log.info("User {} in chat {} set param {} = {}", userEntity, chatEntity, param, newValue);
        final ChatUserParamChangeEvent event =
                new ChatUserParamChangeEvent(chatEntity, userEntity, param, oldValue, value);
        userParamDao.save(paramValue);
        eventDao.save(event);
        return singletonList(event);
    }

    public List<ChatParamChangeEvent> increment(
            ChatEntity chat, UserEntity user, ChatParam param, int delta) {
        if (delta == 0) {
            return emptyList();
        }
        final ChatParamValue value =
                paramDao.findByChatIdAndParam(chat.getId(), param)
                        .orElseGet(
                                () ->
                                        new ChatParamValue(
                                                param,
                                                chatDao.getOne(chat.getId()),
                                                param.getDefaultValue()));
        String oldValue = value.getValue();
        int newValue = Integer.parseInt(oldValue) + delta;
        if (param.inRange(newValue)) {
            value.setValue(newValue);
            final ChatParamChangeEvent event =
                    new ChatParamChangeEvent(chat, user, param, oldValue, value.getValue());
            log.info(
                    "User {} in chat {} set param {}: '{}'->'{}'",
                    user,
                    chat,
                    param,
                    oldValue,
                    newValue);
            paramDao.save(value);
            eventDao.save(event);
            return singletonList(event);
        } else {
            return emptyList();
        }
    }

    public List<ChatUserParamChangeEvent> increment(
            Long chat, Integer user, ChatUserParam param, int delta) {
        final ChatEntity chatEntity =
                chatDao.findById(chat).orElseThrow(NoSuchEntityException::new);
        final UserEntity userEntity =
                userDao.findById(user).orElseThrow(NoSuchEntityException::new);
        return increment(chatEntity, userEntity, param, delta);
    }

    public List<ChatUserParamChangeEvent> increment(
            ChatEntity chat, UserEntity user, ChatUserParam param, int delta) {
        if (delta == 0) {
            return emptyList();
        }
        final ChatUserParamValue value =
                userParamDao
                        .findByChatIdAndUserIdAndParam(chat.getId(), user.getId(), param)
                        .orElseGet(
                                () ->
                                        new ChatUserParamValue(
                                                chat, user, param, param.getDefaultValue()));
        String oldValue = value.getValue();
        int newValue = Integer.parseInt(oldValue) + delta;
        if (param.inRange(newValue)) {
            value.setValue(newValue);
            final ChatUserParamChangeEvent event =
                    new ChatUserParamChangeEvent(chat, user, param, oldValue, value.getValue());
            log.info("User {} in chat {} set param {}: '{}'->'{}'", user, chat, param, oldValue, newValue);
            eventDao.save(event);
            userParamDao.save(value);
            return singletonList(event);
        } else {
            return emptyList();
        }
    }

    public void reset(ChatEntity chat, ChatParam param) {
        final Optional<ChatParamValue> paramValueOptional =
                paramDao.findByChatIdAndParam(chat.getId(), param);
        final String adminUserName = conf.getAdminUserNames().iterator().next();
        UserEntity admin =
                userDao.findByUserName(adminUserName).orElseThrow(NoSuchEntityException::new);
        if (paramValueOptional.isPresent()) {
            final ChatParamValue paramValue = paramValueOptional.get();
            final String oldValue = paramValue.getValue();
            if (!param.getDefaultValue().equals(oldValue)) {
                log.info("Chat {} reset param {}", chat, param);
                paramValue.setValue(param.getDefaultValue());
                eventDao.save(
                        new ChatParamChangeEvent(
                                chat, admin, param, oldValue, param.getDefaultValue()));
                paramDao.save(paramValue);
            }
        }
    }

    public void reset(ChatEntity chat, UserEntity user, ChatUserParam param) {
        final Optional<ChatUserParamValue> paramValueOptional =
                userParamDao.findByChatIdAndUserIdAndParam(chat.getId(), user.getId(), param);
        final String adminUserName = conf.getAdminUserNames().iterator().next();
        UserEntity admin =
                userDao.findByUserName(adminUserName).orElseThrow(NoSuchEntityException::new);
        if (paramValueOptional.isPresent()) {
            final ChatUserParamValue paramValue = paramValueOptional.get();
            final String oldValue = paramValue.getValue();
            if (!param.getDefaultValue().equals(oldValue)) {
                log.info("User {} in chat {} reset param {}", user, chat, param);
                paramValue.setValue(param.getDefaultValue());
                eventDao.save(
                        new ChatUserParamChangeEvent(
                                chat, admin, param, oldValue, param.getDefaultValue()));
                userParamDao.save(paramValue);
            }
        }
    }
}
