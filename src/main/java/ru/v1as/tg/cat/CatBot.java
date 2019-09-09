package ru.v1as.tg.cat;

import static java.util.stream.Collectors.counting;
import static ru.v1as.tg.cat.EmojiConst.CAT;
import static ru.v1as.tg.cat.EmojiConst.FIRST_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.SECOND_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.THIRD_PLACE_MEDAL;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.callback.TgEnumCallbackProcessor;
import ru.v1as.tg.cat.callback.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callback.is_cat.CatRequestVoteHandler;
import ru.v1as.tg.cat.callback.is_cat.CatRequestVoteParser;

@Slf4j
@Getter
class CatBot extends AbstractGameBot {

    private static final String IS_THAT_CAT = "Это кот?";
    private final ScoreData scoreData;
    private final DbData data;

    private TgEnumCallbackProcessor callbackProcessor;

    public CatBot(ScoreData scoreData) {
        super();
        this.scoreData = scoreData;
        this.data = new DbData(scoreData);
        this.callbackProcessor =
                new TgEnumCallbackProcessor()
                        .register(
                                new CatRequestVoteParser(), new CatRequestVoteHandler(data, this));
    }

    @Override
    protected void onUpdateCommand(String datum, String[] arguments, Chat chat, User user) {
        if ("/score".equals(datum)) {
            Stream<String> winners = scoreData.getWinnersStream(chat.getId(), null);
            String text = winners.collect(Collectors.joining("\n"));
            executeUnsafe(new SendMessage().setChatId(chat.getId()).setText(text));
        } else if ("/winners".equals(datum)) {
            sendDayWinners();
        }
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
        if (isInvalidIncomeMessage(message)) {
            return;
        }
        UserData userData = data.getUserData(user);
        ChatData chatData = data.getChatData(chat.getId());
        CatRequest catRequest = new CatRequest(message, userData, chatData, LocalDateTime.now());
        executeAsyncUnsafe(
                buildIsThatCatMessage(message, chat, catRequest),
                new CatPollCallback(data, catRequest));
    }

    private boolean isInvalidIncomeMessage(Message message) {
        return !message.hasPhoto()
                && !message.hasVideo()
                && !message.hasVideoNote()
                && !message.hasDocument();
    }

    private SendMessage buildIsThatCatMessage(Message message, Chat chat, CatRequest catRequest) {
        InlineKeyboardMarkup buttons = CatRequestVoteHandler.getCatePollButtons(catRequest);
        catRequest.setPollButtons(buttons);
        return new SendMessage()
                .setReplyToMessageId(message.getMessageId())
                .setChatId(chat.getId())
                .setText(IS_THAT_CAT)
                .setReplyMarkup(buttons);
    }

    @Override
    public String getBotUsername() {
        return "Котобот";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    void checkCatRequests() {
        log.debug("Tick");
        checkRequests();
    }

    private void checkRequests() {
        for (CatRequest request : data.getNotFinishedCatRequests()) {
            Map<CatRequestVote, Long> votes =
                    request.getVotes().entrySet().stream()
                            .collect(Collectors.groupingBy(Entry::getValue, counting()));
            if (votes.size() == 1 && votes.values().iterator().next() >= 3L) {
                CatRequestVote vote = votes.keySet().iterator().next();
                request.finish(vote);
                scoreData.save(request);
                Message message = request.getVoteMessage();
                executeUnsafe(
                        new EditMessageText()
                                .setChatId(message.getChatId())
                                .setMessageId(message.getMessageId())
                                .setText(vote.getAmount() + "x" + CAT));
            }
        }
    }

    void sendDayWinners() {
        log.info("Start sending winners data...");
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        String[] medals = {FIRST_PLACE_MEDAL, SECOND_PLACE_MEDAL, THIRD_PLACE_MEDAL};
        for (ChatData chat : data.getChats()) {
            try {
                StringBuilder text = new StringBuilder();
                String[] winners =
                        scoreData
                                .getWinnersStream(chat.getChatId(), yesterday)
                                .limit(medals.length)
                                .toArray(String[]::new);
                if (winners.length == 0) {
                    continue;
                }
                for (int i = 0; i < winners.length; i++) {
                    text.append(medals[i]).append(winners[i]).append('\n');
                }
                execute(new SendMessage().setChatId(chat.getChatId()).setText(text.toString()));
                log.info("Winners data '{}' was sent to chat {}", text, chat);
            } catch (Exception ex) {
                log.error("Error while send winners to the chat " + chat);
            }
        }
    }
}
