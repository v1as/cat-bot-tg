package ru.v1as.tg.cat.service;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.ChatParamValueDao;
import ru.v1as.tg.cat.jpa.dao.ChatUserParamValueDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.dao.UserEventDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.chat.ChatParamValue;
import ru.v1as.tg.cat.jpa.entities.events.ChatParamChangeEvent;
import ru.v1as.tg.cat.jpa.entities.events.ChatUserParamChangeEvent;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParam;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParamValue;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

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
            Long chat, Integer user, ChatUserParam param, @NonNull Object newValue) {
        String value = newValue.toString();
        final ChatEntity chatEntity = chatDao.findById(chat).get();
        final UserEntity userEntity = userDao.findById(user).get();
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
            paramDao.save(value);
            eventDao.save(event);
            return singletonList(event);
        } else {
            return emptyList();
        }
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
        UserEntity admin = userDao.findByUserName(adminUserName).get();
        if (paramValueOptional.isPresent()) {
            final ChatParamValue paramValue = paramValueOptional.get();
            final String oldValue = paramValue.getValue();
            if (!param.getDefaultValue().equals(oldValue)) {
                paramValue.setValue(param.getDefaultValue());
                eventDao.save(
                        new ChatParamChangeEvent(
                                chat, admin, param, oldValue, param.getDefaultValue()));
                paramDao.save(paramValue);
            }
        }
    }
}
