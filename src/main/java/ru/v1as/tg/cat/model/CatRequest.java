package ru.v1as.tg.cat.model;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = PRIVATE)
@Slf4j
public class CatRequest extends TgRequestPoll<CatRequestVote> {

    final Message sourceMessage;
    final UserData owner;
    final Map<UserData, CatRequestVote> votes = new ConcurrentHashMap<>();
    Boolean isReal = false;
    InlineKeyboardMarkup pollButtons;

    public CatRequest(Message sourceMessage, UserData owner, ChatData chat) {
        super(chat);
        this.sourceMessage = sourceMessage;
        this.owner = owner;
        log.info(
                "Cat poll registered for user '{}' with message {}",
                owner,
                sourceMessage.getMessageId());
    }

    public RequestAnswerResult vote(UserData user, CatRequestVote vote) {
        Integer userId = user.getId();
        if (finished || canceled) {
            return RequestAnswerResult.FINISHED;
        }
        if (userId.equals(owner.getId()) && vote.equals(CatRequestVote.NOT_CAT)) {
            return RequestAnswerResult.CANCELED;
        }
        log.info(
                "User '{}' just voted: {} for request {}",
                user,
                vote,
                sourceMessage.getMessageId());
        CatRequestVote prevVote = votes.get(user);
        if (owner.getId().equals(userId)) {
            return RequestAnswerResult.FORBIDDEN;
        } else if (vote.equals(prevVote)) {
            return RequestAnswerResult.SAME;
        } else if (null == votes.put(user, vote)) {
            return RequestAnswerResult.VOTED;
        } else {
            return RequestAnswerResult.CHANGED;
        }
    }

    public String getVotesButtonPrefix(CatRequestVote cat1) {
        long count = votes.values().stream().filter(cat1::equals).count();
        return count == 0L ? "" : "(" + count + ")";
    }

    public void cancel() {
        super.cancel();
        log.info("Request for user '{}' is canceled.", owner);
        this.result = CatRequestVote.NOT_CAT;
    }

    public void finish(CatRequestVote result) {
        super.finish(result);
        log.info("Request for user '{}' is finished: {}", owner, result);
    }
}
