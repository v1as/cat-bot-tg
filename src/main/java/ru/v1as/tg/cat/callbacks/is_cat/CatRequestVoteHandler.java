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
import static ru.v1as.tg.cat.tg.KeyboardUtils.getUpdateButtonsMsg;
import static ru.v1as.tg.cat.tg.KeyboardUtils.inlineKeyboardMarkup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
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

@Slf4j
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
        final Integer msgId = msg.getMessageId();
        CatRequest req = data.getChatData(chat).getCatRequest(msgId);
        if (req == null || vote == null) {
            sender.executeTg(clearButtons(msg));
            return;
        }
        RequestAnswerResult voted = req.vote(user, vote);
        log.info("User '{}' just voted: {} for request {}", user, vote, msgId);
        sender.executeTg(getVoteAnswerMsg(callbackQuery, voted));
        if (FINISHED.equals(voted)) {
            sender.executeTg(clearButtons(msg));
        } else if (CANCELED.equals(voted)) {
            req.cancel();
            log.info("Request for user '{}' is canceled.", user.getUsernameOrFullName());
            final Integer messageId = req.getMessageId();
            final Long chatId = req.getChatId();
            catEventService.poll(NOT_CAT, messageId, chatId, user.getId());
            sender.executeTg(new DeleteMessage(chatId, messageId));
        } else {
            InlineKeyboardMarkup pollButtons = getCatePollButtons(req);
            if (!req.getPollButtons().equals(pollButtons)) {
                req.setPollButtons(pollButtons);
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
