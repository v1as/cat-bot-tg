package ru.v1as.tg.cat;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingInt;
import static ru.v1as.tg.cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.EmojiConst.CAT;
import static ru.v1as.tg.cat.EmojiConst.FIRST_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.HEAVY_MULTIPLY;
import static ru.v1as.tg.cat.EmojiConst.SECOND_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.THIRD_PLACE_MEDAL;
import static ru.v1as.tg.cat.KeyboardUtils.inlineKeyboardMarkup;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.ScoreData.ScoreLine;

@Slf4j
class CatBot extends AbstractGameBot {

    private static final String IS_THAT_CAT = "Это кот?";
    private final ScoreData scoreData;
    private DbData data = new DbData();

    public CatBot(ScoreData scoreData) {
        super();
        this.scoreData = scoreData;
    }

    @Override
    protected void onUpdateCommand(String datum, String[] arguments, Chat chat, User user) {
        if ("/score".equals(datum)) {
            Stream<String> winners = getWinnersStream(chat.getId(), null);
            String text = winners.collect(Collectors.joining("\n"));
            execute(new SendMessage().setChatId(chat.getId()).setText(text));
        } else if ("/winners".equals(datum)) {
            sendDayWinners();
        }
    }

    private Stream<String> getWinnersStream(Long chatId, LocalDateTime after) {
        Stream<ScoreLine> scoreStream = scoreData.getScore(chatId).stream();
        if (after != null) {
            scoreStream =
                    scoreStream
                            .filter(line -> line.getDate() != null)
                            .filter(scoreLine -> after.isBefore(scoreLine.getDate()));
        }

        Map<String, IntSummaryStatistics> grouped =
                scoreStream.collect(
                        groupingBy(ScoreLine::getUserString, summarizingInt(ScoreLine::getAmount)));
        return grouped.entrySet().stream()
                .sorted(Comparator.comparingLong(e -> -1 * e.getValue().getSum()))
                .map(e -> String.format("%s       %d %s", e.getKey(), e.getValue().getSum(), CAT));
    }

    @Override
    protected void before(Update update) {
        data.register(update);
    }

    @Override
    protected void onUpdateCallbackQuery(CallbackQuery callbackQuery, Chat chat, User user) {
        CatRequestVote parse = CatRequestVote.parse(callbackQuery.getData());
        CatRequest catRequest = data.getCatRequest(chat, callbackQuery);
        UserData userData = data.getUserData(user);
        if (catRequest == null || parse == null) {
            return;
        }
        CatRequestAnswerResult voted = catRequest.vote(userData, parse);
        execute(getVoteAnswerMsg(callbackQuery, voted));
        if (voted.equals(CatRequestAnswerResult.CANCELED)) {
            catRequest.cancel();
            scoreData.save(catRequest);
            execute(deleteMsg(chat, catRequest));
        } else {
            InlineKeyboardMarkup pollButtons = getCatePollButtons(catRequest);
            if (!catRequest.getPollButtons().equals(pollButtons)) {
                catRequest.setPollButtons(pollButtons);
                execute(getUpdateButtonsMsg(chat, catRequest, pollButtons));
            }
        }
    }

    private AnswerCallbackQuery getVoteAnswerMsg(
            CallbackQuery callbackQuery, CatRequestAnswerResult voted) {
        return new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQuery.getId())
                .setText(voted.getText());
    }

    private EditMessageReplyMarkup getUpdateButtonsMsg(
            Chat chat, CatRequest catRequest, InlineKeyboardMarkup pollButtons) {
        return new EditMessageReplyMarkup()
                .setChatId(chat.getId())
                .setMessageId(catRequest.getVoteMessage().getMessageId())
                .setReplyMarkup(pollButtons);
    }

    private DeleteMessage deleteMsg(Chat chat, CatRequest catRequest) {
        return new DeleteMessage(chat.getId(), catRequest.getVoteMessage().getMessageId());
    }

    @Override
    protected void onUpdateMessage(Message message, Chat chat, User user) {
        if (isInvalidIncomeMessage(message)) {
            return;
        }
        UserData userData = data.getUserData(user);
        ChatData chatData = data.getChatData(chat.getId());
        CatRequest catRequest = new CatRequest(message, userData, chatData, LocalDateTime.now());
        executeAsync(
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
        InlineKeyboardMarkup buttons = getCatePollButtons(catRequest);
        catRequest.setPollButtons(buttons);
        return new SendMessage()
                .setReplyToMessageId(message.getMessageId())
                .setChatId(chat.getId())
                .setText(IS_THAT_CAT)
                .setReplyMarkup(buttons);
    }

    private InlineKeyboardMarkup getCatePollButtons(CatRequest catRequest) {
        return inlineKeyboardMarkup(
                catRequest.getVotesButtonPrefix(CAT1) + CAT,
                CAT1.getCallback(),
                catRequest.getVotesButtonPrefix(CAT2) + CAT + "x2",
                CAT2.getCallback(),
                catRequest.getVotesButtonPrefix(CAT3) + CAT + "x3",
                CAT3.getCallback(),
                catRequest.getVotesButtonPrefix(NOT_CAT) + HEAVY_MULTIPLY,
                NOT_CAT.getCallback());
    }

    @Override
    public String getBotUsername() {
        return "Котобот";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    void check() {
        log.debug("Tick");
        checkRequests();
        sendStatistic();
    }

    private void sendStatistic() {}

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
                execute(
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
                        getWinnersStream(chat.getChatId(), yesterday)
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
