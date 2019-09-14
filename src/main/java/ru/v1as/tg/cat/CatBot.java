package ru.v1as.tg.cat;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.counting;
import static ru.v1as.tg.cat.EmojiConst.CAT;
import static ru.v1as.tg.cat.EmojiConst.FIRST_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.SECOND_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.THIRD_PLACE_MEDAL;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
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
import ru.v1as.tg.cat.ScoreData.ScoreLine;
import ru.v1as.tg.cat.callback.TgEnumCallbackProcessor;
import ru.v1as.tg.cat.callback.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callback.is_cat.CatRequestVoteHandler;
import ru.v1as.tg.cat.callback.is_cat.CatRequestVoteParser;

@Slf4j
@Getter
class CatBot extends AbstractGameBot {

    private static final String[] MEDALS =
            new String[] {FIRST_PLACE_MEDAL, SECOND_PLACE_MEDAL, THIRD_PLACE_MEDAL};
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

    static List<String> getPlayersWithMedals(LongProperty[] topPlayers) {
        List<String> result = new ArrayList<>();
        LongProperty last = null;
        int medalIndex = 0;
        for (LongProperty player : topPlayers) {
            if (last != null && !Objects.equals(last.getValue(), player.getValue())) {
                medalIndex++;
            }
            if (medalIndex == MEDALS.length) {
                break;
            }
            result.add(MEDALS[medalIndex] + player.toString());
            last = player;
        }
        return result;
    }

    private Stream<LongProperty> getWinnersStream(Long chatId, LocalDateTime after) {
        Stream<ScoreLine> scoreStream =
                scoreData.getScore(chatId).stream()
                        .filter(line -> line.getDate() != null)
                        .filter(line -> after == null || after.isBefore(line.getDate()));

        Map<String, IntSummaryStatistics> grouped =
                scoreStream.collect(
                        groupingBy(ScoreLine::getUserString, summarizingInt(ScoreLine::getAmount)));
        return grouped.entrySet().stream()
                .sorted(Comparator.comparingLong(e -> -1 * e.getValue().getSum()))
                .map(e -> new LongProperty(e.getKey(), e.getValue().getSum()));
    }

    @Override
    protected void onUpdateCommand(TgCommandRequest command, Chat chat, User user) {
        if ("score".equals(command.getName())) {
            String text =
                    getWinnersStream(chat.getId(), null)
                            .map(LongProperty::toString)
                            .collect(joining("\n"));
            executeUnsafe(new SendMessage().setChatId(chat.getId()).setText(text));
        } else if ("winners".equals(command.getName())) {
            sendDayWinners(singletonList(data.getChatData(chat.getId())));
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
                            .collect(groupingBy(Entry::getValue, counting()));
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

    private void sendDayWinners(List<ChatData> chats) {
        log.info("Start sending winners data...");
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        for (ChatData chat : chats) {
            try {
                StringBuilder text = new StringBuilder();
                LongProperty[] topPlayers =
                        scoreData
                                .getWinnersStream(chat.getChatId(), yesterday).toArray(LongProperty[]::new);
                List<String> result = getPlayersWithMedals(topPlayers);

                if (result.isEmpty()) {
                    continue;
                }
                execute(new SendMessage().setChatId(chat.getChatId()).setText(text.toString()));
                log.info("Winners data '{}' was sent to chat {}", text, chat);
            } catch (Exception ex) {
                log.error("Error while send winners to the chat " + chat);
            }
        }
    }

    void sendAllDayWinners() {
        sendDayWinners(data.getChats());
    }
}
