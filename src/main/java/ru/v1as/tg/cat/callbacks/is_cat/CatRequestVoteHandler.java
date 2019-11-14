package ru.v1as.tg.cat.callbacks.is_cat;

import static ru.v1as.tg.cat.EmojiConst.CAT;
import static ru.v1as.tg.cat.EmojiConst.HEAVY_MULTIPLY;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult.CANCELED;
import static ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult.FINISHED;
import static ru.v1as.tg.cat.tg.KeyboardUtils.clearButtons;
import static ru.v1as.tg.cat.tg.KeyboardUtils.deleteMsg;
import static ru.v1as.tg.cat.tg.KeyboardUtils.getUpdateButtonsMsg;
import static ru.v1as.tg.cat.tg.KeyboardUtils.inlineKeyboardMarkup;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.TgCallBackHandler;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.CatEventService;
import ru.v1as.tg.cat.tg.TgSender;

@Component
public class CatRequestVoteHandler implements TgCallBackHandler<CatRequestVote> {

    private final CatBotData data;
    private final TgSender sender;
    private final CatEventService catEventService;

    public CatRequestVoteHandler(
            CatBotData data, TgSender sender, CatEventService catEventService) {
        this.data = data;
        this.sender = sender;
        this.catEventService = catEventService;
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

    public String getPrefix() {
        return CatRequestVote.PREFIX;
    }

    public CatRequestVote parse(String value) {
        return CatRequestVote.parse(value);
    }

    @Override
    public void handle(CatRequestVote vote, TgChat chat, TgUser user, CallbackQuery callbackQuery) {
        Message msg = callbackQuery.getMessage();
        CatRequest request = data.getChatData(chat.getId()).getCatRequest(msg.getMessageId());
        if (request == null || vote == null) {
            sender.executeTg(clearButtons(msg));
            return;
        }
        RequestAnswerResult voted = request.vote(user, vote);
        sender.executeTg(getVoteAnswerMsg(callbackQuery, voted));
        if (FINISHED.equals(voted)) {
            sender.executeTg(clearButtons(msg));
        } else if (CANCELED.equals(voted)) {
            request.cancel();
            catEventService.poll(chat, user, request.getVoteMessage(), NOT_CAT);
            sender.executeTg(deleteMsg(request.getVoteMessage()));
        } else {
            InlineKeyboardMarkup pollButtons = getCatePollButtons(request);
            if (!request.getPollButtons().equals(pollButtons)) {
                request.setPollButtons(pollButtons);
                sender.executeTg(getUpdateButtonsMsg(msg, pollButtons));
            }
        }
    }

    private AnswerCallbackQuery getVoteAnswerMsg(
            CallbackQuery callbackQuery, RequestAnswerResult voted) {
        return new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQuery.getId())
                .setText(voted.getText());
    }
}
