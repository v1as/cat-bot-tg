package ru.v1as.tg.cat.callbacks.is_cat;

import static ru.v1as.tg.cat.EmojiConst.CAT;
import static ru.v1as.tg.cat.EmojiConst.HEAVY_MULTIPLY;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult.CANCELED;
import static ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult.CHANGED;
import static ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult.FINISHED;
import static ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult.VOTED;
import static ru.v1as.tg.cat.tg.KeyboardUtils.clearButtons;
import static ru.v1as.tg.cat.tg.KeyboardUtils.getUpdateButtonsMsg;
import static ru.v1as.tg.cat.tg.KeyboardUtils.inlineKeyboardMarkup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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
@RequiredArgsConstructor
public class CatRequestVoteHandler implements TgCallBackHandler<CatRequestVote> {

    private final CatBotData data;
    private final TgSender sender;
    private final CatEventService catService;

    public static InlineKeyboardMarkup getCatPollButtons(CatRequest catRequest) {
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
            sender.execute(clearButtons(msg));
            return;
        }
        RequestAnswerResult voted = req.vote(user, vote);
        log.info("User '{}' just voted: {} for request {}", user, vote, msgId);
        sender.execute(getVoteAnswerMsg(callbackQuery, voted));
        if (FINISHED.equals(voted)) {
            sender.execute(clearButtons(msg));
        } else if (CANCELED.equals(voted)) {
            log.info("Request for user '{}' is canceled.", user.getUsernameOrFullName());
            final Integer messageId = req.getMessageId();
            final Long chatId = req.getChatId();
            catService.saveRealCatPoll(req);
            sender.execute(new DeleteMessage(chatId, messageId));
        } else if (VOTED.equals(voted) || CHANGED.equals(voted)) {
            if (req.isClosed()) {
                saveFinishedPoll(vote, req);
                sender.execute(
                        new EditMessageText()
                                .setChatId(req.getChatId())
                                .setMessageId(req.getMessageId())
                                .setText(req.getResult().getAmount() + "x" + CAT));
            } else {
                InlineKeyboardMarkup pollButtons = getCatPollButtons(req);
                if (!req.getPollButtons().equals(pollButtons)) {
                    req.setPollButtons(pollButtons);
                    sender.execute(getUpdateButtonsMsg(msg, pollButtons));
                }
            }
        }
    }

    private void saveFinishedPoll(CatRequestVote vote, CatRequest req) {
        log.info("Request for user '{}' is finished with result '{}'.", req.getOwner(), vote);
        catService.saveRealCatPoll(req);
    }

    private AnswerCallbackQuery getVoteAnswerMsg(
            CallbackQuery callbackQuery, RequestAnswerResult voted) {
        return new AnswerCallbackQuery()
                .setCallbackQueryId(callbackQuery.getId())
                .setText(voted.getText());
    }
}
