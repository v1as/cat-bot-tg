package ru.v1as.tg.cat;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.callbacks.SimpleCallbackParser;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.callbacks.curios.CuriosCatVoteHandler;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVoteHandler;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVoteParser;
import ru.v1as.tg.cat.commands.TgCommandProcessor;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.commands.impl.ScoreCommandHandler;
import ru.v1as.tg.cat.messages.CatRequestMessageCreator;
import ru.v1as.tg.cat.messages.MessageProcessor;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.DbData;
import ru.v1as.tg.cat.model.ScoreData;

@Slf4j
@Getter
class CatBot extends AbstractGameBot {

    private final DbData<CatChatData> data;
    private final TgCallbackProcessor callbackProcessor;
    private final TgCommandProcessor commandProcessor;
    private final MessageProcessor messageProcessor;

    public CatBot(ScoreData scoreData) {
        super();
        this.data = new DbData<>(scoreData, CatChatData::new);
        this.callbackProcessor =
                new TgCallbackProcessor()
                        .register(new CatRequestVoteParser(), new CatRequestVoteHandler(data, this))
                        .register(
                                new SimpleCallbackParser("curiosCat"),
                                new CuriosCatVoteHandler(data, scoreData, this));
        this.commandProcessor =
                new TgCommandProcessor().register(new ScoreCommandHandler(scoreData, this));
        this.messageProcessor =
                new MessageProcessor().register(new CatRequestMessageCreator(data, this));
    }

    @Override
    protected void onUpdateCommand(TgCommandRequest command, Chat chat, User user) {
        this.commandProcessor.process(command, chat, user);
    }

    @Override
    protected void before(Update update) {
        data.register(update);
    }

    @Override
    @SneakyThrows
    protected void onUpdateCallbackQuery(CallbackQuery callbackQuery, Chat chat, User user) {
        callbackProcessor.process(callbackQuery, chat, user);
    }

    @Override
    protected void onUpdateMessage(Message message, Chat chat, User user) {
        messageProcessor.process(message, chat, user);
    }

    @Override
    public String getBotUsername() {
        return "Котобот";
    }

    @Override
    public String getBotToken() {
        return "";
    }
}
