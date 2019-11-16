package ru.v1as.tg.cat;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.commands.TgCommandProcessor;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.messages.TgMessageProcessor;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Getter
@Component
class CatBot extends AbstractTgBot {

    private final TgUpdateBeforeHandler updateBeforeHandler;
    private final TgCallbackProcessor callbackProcessor;
    private final TgCommandProcessor commandProcessor;
    private final TgMessageProcessor messageProcessor;

    public CatBot(
            @Lazy TgUpdateBeforeHandler updateBeforeHandler,
            @Lazy TgCallbackProcessor callbackProcessor,
            @Lazy TgCommandProcessor commandProcessor,
            @Lazy TgMessageProcessor messageProcessor) {
        this.updateBeforeHandler = updateBeforeHandler;
        this.callbackProcessor = callbackProcessor;
        this.commandProcessor = commandProcessor;
        this.messageProcessor = messageProcessor;
    }

    @Override
    protected void onUpdateCommand(TgCommandRequest command, TgChat chat, TgUser user) {
        this.commandProcessor.process(command, chat, user);
    }

    @Override
    protected void before(Update update) {
        updateBeforeHandler.register(update);
    }

    @Override
    @SneakyThrows
    protected void onUpdateCallbackQuery(CallbackQuery callbackQuery, TgChat chat, TgUser user) {
        callbackProcessor.process(callbackQuery, chat, user);
    }

    @Override
    protected void onUpdateMessage(Message message, TgChat chat, TgUser user) {
        messageProcessor.process(message, chat, user);
    }

    @Override
    public String getBotUsername() {
        return "Котобот";
    }
}
