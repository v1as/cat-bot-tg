package ru.v1as.tg.cat;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Data
@FieldDefaults(level = PRIVATE)
@Slf4j
class CatRequest {

    final Message sourceMessage;
    final UserData owner;
    final Map<UserData, CatRequestVote> votes = new HashMap<>();
    final ChatData chat;
    final LocalDateTime created;
    Message voteMessage;
    InlineKeyboardMarkup pollButtons;
    boolean finished = false;
    boolean canceled = false;
    CatRequestVote result;

    CatRequest(Message sourceMessage, UserData owner, ChatData chat, LocalDateTime created) {
        this.sourceMessage = sourceMessage;
        this.owner = owner;
        this.chat = chat;
        this.created = created;
        log.info(
                "Cat poll registered for user '{}' with message {}",
                owner,
                sourceMessage.getMessageId());
    }

    CatRequestAnswerResult vote(UserData user, CatRequestVote vote) {
        Integer userId = user.getId();
        if (finished) {
            throw new IllegalArgumentException("This request is finished");
        }
        log.info("User '{}' just voted: {}", user, vote);
        if (userId.equals(owner.getId()) && vote.equals(CatRequestVote.NOT_CAT)) {
            return CatRequestAnswerResult.CANCELED;
        }
        if (owner.getId().equals(userId)) {
            return CatRequestAnswerResult.FORBIDDEN;
        } else if (null == votes.put(user, vote)) {
            return CatRequestAnswerResult.VOTED;
        } else {
            return CatRequestAnswerResult.CHANGED;
        }
    }

    void setVoteMessage(Message voteMessage) {
        if (this.voteMessage != null) {
            throw new IllegalStateException("Vote message is already set");
        }
        this.voteMessage = voteMessage;
    }

    String getVotesButtonPrefix(CatRequestVote cat1) {
        long count = votes.values().stream().filter(cat1::equals).count();
        return count == 0L ? "" : "(" + count + ")";
    }

    void cancel() {
        if (finished) {
            throw new IllegalStateException("This request is already closed");
        }
        log.info("Request for user '{}' is canceled.", owner);
        this.canceled = true;
        this.finished = true;
        this.result = CatRequestVote.NOT_CAT;
    }

    void finish(CatRequestVote result) {
        if (finished) {
            throw new IllegalStateException("This request is already closed");
        }
        log.info("Request for user '{}' is finished: {}", owner, result);
        this.finished = true;
        this.result = result;
    }
}
