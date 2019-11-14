package ru.v1as.tg.cat;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.v1as.tg.cat.model.UpdateUtils.getChat;
import static ru.v1as.tg.cat.model.UpdateUtils.getUser;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import ru.v1as.tg.cat.Const.OnlyForAdmins;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.tg.TgSender;

public abstract class AbstractTgBot extends TelegramLongPollingBot implements TgSender {

    private final Logger log = getLogger(this.getClass());

    private AbsSender sender = this;
    private Map<Long, Object> chatToMonitor = new ConcurrentHashMap<>();

    @Value("${tg.bot.username}")
    private String botUsername;

    @Value("${tg.bot.token}")
    private String botToken;

    @Value("${tg.bot.admin_username:}")
    private String botAdmins;

    @PostConstruct
    public void init() {
        Const.setBotName(botUsername);
        Const.setAdminUserName(botAdmins);
        Const.setBotToken(botToken);
        log.info("Set up const bot user name: '{}'", botUsername);
        log.info("Set up const admin user names: '{}'", botAdmins);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            TgChat chat = getChat(update);
            TgUser user = getUser(update);
            if (chat == null || user == null) {
                log.warn("Such type updated does not supported '{}'", update);
                return;
            }
            synchronized (chatToMonitor.computeIfAbsent(chat.getId(), (id) -> chat.getId())) {
                before(update);
                if (update.hasMessage() && update.getMessage().isCommand()) {
                    final Message msg = update.getMessage();
                    log.info("Command '{}' received.", msg.getText());
                    onUpdateCommand(TgCommandRequest.parse(msg), chat, user);
                } else if (update.hasMessage()) {
                    onUpdateMessage(update.getMessage(), chat, user);
                } else if (update.hasCallbackQuery()) {
                    log.info("Callback received '{}'", update.getCallbackQuery().getData());
                    onUpdateCallbackQuery(update.getCallbackQuery(), chat, user);
                } else {
                    log.debug("Unsupported update type: " + update);
                }
            }
        } catch (OnlyForAdmins noAdmin) {
            log.info("Update '{}' allowed only for admins", update);
        } catch (Exception e) {
            log.error("Something gone wrong ", e);
        }
    }

    protected abstract void onUpdateCommand(TgCommandRequest command, TgChat chat, TgUser user);

    protected abstract void before(Update update);

    protected abstract void onUpdateCallbackQuery(
            CallbackQuery callbackQuery, TgChat chat, TgUser user);

    protected abstract void onUpdateMessage(Message message, TgChat chat, TgUser user);

    @SneakyThrows
    @Override
    public <
                    T extends Serializable,
                    Method extends BotApiMethod<T>,
                    Callback extends SentCallback<T>>
            void executeTgAsync(Method method, Callback callback) {
        log.debug(method.toString());
        sender.executeAsync(method, callback);
    }

    @Override
    @SneakyThrows
    public <T extends Serializable, Method extends BotApiMethod<T>> T executeTg(Method method) {
        log.debug(method.toString());
        return sender.execute(method);
    }

    public void setSender(AbsSender sender) {
        this.sender = sender;
    }

    @Override
    @SneakyThrows
    public Message executeTg(SendDocument sendDocument) {
        log.debug(sendDocument.toString());
        return sender.execute(sendDocument);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
