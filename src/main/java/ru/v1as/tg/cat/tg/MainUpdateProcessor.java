package ru.v1as.tg.cat.tg;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static ru.v1as.tg.cat.model.UpdateUtils.getChat;
import static ru.v1as.tg.cat.model.UpdateUtils.getUser;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Slf4j
public abstract class MainUpdateProcessor implements TgUpdateProcessor {

    private Map<Long, Object> chatToMonitor = new ConcurrentHashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        try {
            TgChat chat = getChat(update);
            TgUser user = getUser(update);
            if (chat == null || user == null) {
                log.warn("Such type updated is not supported '{}'", update);
                return;
            }
            setupMdc(chat, user);
            synchronized (chatToMonitor.computeIfAbsent(chat.getId(), (id) -> chat.getId())) {
                before(update);
                if (update.hasMessage() && update.getMessage().isCommand()) {
                    final Message msg = update.getMessage();
                    log.debug("Command '{}' received from {} in {}.", msg.getText(), user, chat);
                    onUpdateCommand(TgCommandRequest.parse(msg), chat, user);
                } else if (update.hasMessage()) {
                    onUpdateMessage(update.getMessage(), chat, user);
                } else if (update.hasCallbackQuery()) {
                    log.debug(
                            "Callback received '{}' from {} in {}",
                            update.getCallbackQuery().getData(),
                            user,
                            chat);
                    onUpdateCallbackQuery(update.getCallbackQuery(), chat, user);
                } else {
                    log.debug("Unsupported update type: " + update);
                }
            }
        } catch (Exception e) {
            log.error("Something gone wrong for update: " + update, e);
        } finally {
            MDC.clear();
        }
    }

    private void setupMdc(TgChat chat, TgUser user) {
        final String userDesc = format("[%s:%d", user.getUsernameOrFullName(), user.getId());
        final String chatDesc =
                chat.isUserChat()
                        ? ":private]"
                        : format(":%s:%d]", ofNullable(chat.getTitle()).orElse(""), chat.getId());
        MDC.setContextMap(ImmutableMap.of("username", userDesc, "chat", chatDesc));
    }

    protected abstract void onUpdateCommand(TgCommandRequest command, TgChat chat, TgUser user);

    protected abstract void before(Update update);

    protected abstract void onUpdateCallbackQuery(
            CallbackQuery callbackQuery, TgChat chat, TgUser user);

    protected abstract void onUpdateMessage(Message message, TgChat chat, TgUser user);
}
