package ru.v1as.tg.cat;

import static ru.v1as.tg.cat.UpdateUtils.getChat;
import static ru.v1as.tg.cat.UpdateUtils.getUser;

import java.io.Serializable;
import java.util.Arrays;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

@Slf4j
public abstract class AbstractGameBot extends TelegramLongPollingBot implements UnsafeAbsSender {

    private AbsSender sender = this;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Chat chat = getChat(update);
            User user = getUser(update);
            before(update);
            if (!chat.isGroupChat() && !chat.isSuperGroupChat()) {
                return;
            }
            if (update.hasMessage() && update.getMessage().isCommand()) {
                String text = update.getMessage().getText();
                String[] data = text.split(" ");
                String[] arguments = Arrays.copyOfRange(data, 1, data.length);
                onUpdateCommand(data[0], arguments, chat, user);
            } else if (update.hasMessage()) {
                onUpdateMessage(update.getMessage(), chat, user);
            } else if (update.hasCallbackQuery()) {
                onUpdateCallbackQuery(update.getCallbackQuery(), chat, user);
            } else {
                log.debug("Unsupported update type: " + update);
            }
        } catch (Exception e) {
            log.error("Something gone wrong ", e);
        }
    }

    protected abstract void onUpdateCommand(String datum, String[] arguments, Chat chat, User user);

    protected abstract void before(Update update);

    protected abstract void onUpdateCallbackQuery(
            CallbackQuery callbackQuery, Chat chat, User user);

    protected abstract void onUpdateMessage(Message message, Chat chat, User user);

    @SneakyThrows
    @Override
    public <
                    T extends Serializable,
                    Method extends BotApiMethod<T>,
                    Callback extends SentCallback<T>>
            void executeAsyncUnsafe(Method method, Callback callback) {
        sender.executeAsync(method, callback);
    }

    @SneakyThrows
    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T executeUnsafe(Method method) {
        return sender.execute(method);
    }

    public void setSender(AbsSender sender) {
        this.sender = sender;
    }
}
