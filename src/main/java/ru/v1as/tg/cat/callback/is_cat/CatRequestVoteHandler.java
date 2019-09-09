package ru.v1as.tg.cat.callback.is_cat;

import static ru.v1as.tg.cat.CatRequestAnswerResult.CANCELED;
import static ru.v1as.tg.cat.EmojiConst.CAT;
import static ru.v1as.tg.cat.EmojiConst.HEAVY_MULTIPLY;
import static ru.v1as.tg.cat.KeyboardUtils.deleteMsg;
import static ru.v1as.tg.cat.KeyboardUtils.getUpdateButtonsMsg;
import static ru.v1as.tg.cat.KeyboardUtils.inlineKeyboardMarkup;
import static ru.v1as.tg.cat.callback.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callback.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callback.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callback.is_cat.CatRequestVote.NOT_CAT;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.CatRequest;
import ru.v1as.tg.cat.CatRequestAnswerResult;
import ru.v1as.tg.cat.DbData;
import ru.v1as.tg.cat.ScoreData;
import ru.v1as.tg.cat.UnsafeAbsSender;
import ru.v1as.tg.cat.UserData;
import ru.v1as.tg.cat.callback.EnumCallBackHandler;

public class CatRequestVoteHandler implements EnumCallBackHandler<CatRequestVote> {

    private final DbData data;
    private final UnsafeAbsSender sender;
    private final ScoreData scoreData;

    public CatRequestVoteHandler(DbData data, UnsafeAbsSender sender) {
        this.data = data;
        this.sender = sender;
        this.scoreData = data.getScoreData();
    }

    public static InlineKeyboardMarkup getCatePollButtons(CatRequest catRequest) {
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
    public void handle(CatRequestVote vote, Chat chat, User user, CallbackQuery callbackQuery) {
        CatRequest catRequest = data.getCatRequest(chat, callbackQuery);
        UserData userData = data.getUserData(user);
        Integer messageId = callbackQuery.getMessage().getMessageId();
        if (catRequest == null || vote == null) {
            sender.executeUnsafe(getUpdateButtonsMsg(chat, messageId, inlineKeyboardMarkup()));
            return;
        }
        CatRequestAnswerResult voted = catRequest.vote(userData, vote);
        sender.executeUnsafe(getVoteAnswerMsg(callbackQuery, voted));
        if (CANCELED.equals(voted)) {
            catRequest.cancel();
            scoreData.save(catRequest);
            sender.executeUnsafe(deleteMsg(chat, catRequest));
        } else {
            InlineKeyboardMarkup pollButtons = getCatePollButtons(catRequest);
            if (!catRequest.getPollButtons().equals(pollButtons)) {
                catRequest.setPollButtons(pollButtons);
                sender.executeUnsafe(getUpdateButtonsMsg(chat, messageId, pollButtons));
            }
        }
    }

    private AnswerCallbackQuery getVoteAnswerMsg(
            CallbackQuery callbackQuery, CatRequestAnswerResult voted) {
        return new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQuery.getId())
                .setText(voted.getText());
    }
}
