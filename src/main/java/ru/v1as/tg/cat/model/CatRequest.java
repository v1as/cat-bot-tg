package ru.v1as.tg.cat.model;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static ru.v1as.tg.cat.model.TgRequestPoll.State.OPENED;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult;

@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class CatRequest extends TgRequestPoll<CatRequestVote> {

    private final Integer srcMsgId;
    private final TgUser owner;
    private final Map<TgUser, CatRequestVote> votes = new ConcurrentHashMap<>();
    private Boolean isReal = false;
    private InlineKeyboardMarkup pollButtons;

    public CatRequest(TgUser owner, Integer srcMsgId, Long chatId) {
        super(chatId);
        this.srcMsgId = srcMsgId;
        this.owner = owner;
        this.state = OPENED;
    }

    public RequestAnswerResult vote(TgUser user, CatRequestVote vote) {
        Integer userId = user.getId();
        if (!OPENED.equals(state)) {
            return RequestAnswerResult.FINISHED;
        }
        if (userId.equals(owner.getId()) && vote.equals(CatRequestVote.NOT_CAT)) {
            return RequestAnswerResult.CANCELED;
        }
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

    public boolean checkVotesEnoughToFinish() {
        Map<CatRequestVote, Long> votes =
                getVotes().entrySet().stream().collect(groupingBy(Entry::getValue, counting()));
        Optional<CatRequestVote> voteValue = Optional.empty();
        if (votes.size() == 1 && votes.values().iterator().next() >= 3L) {
            voteValue = Optional.of(votes.keySet().iterator().next());
        } else if (getAge().toHours() > 4 && votes.size() > 0) {
            Long maxVotes = votes.values().stream().max(Long::compareTo).get();
            voteValue =
                    votes.entrySet().stream()
                            .filter(e -> maxVotes.equals(e.getValue()))
                            .map(Entry::getKey)
                            .max(Comparator.comparing(CatRequestVote::getAmount));
        }
        if (voteValue.isPresent()) {
            CatRequestVote vote = voteValue.get();
            close(vote);
        }
        return voteValue.isPresent();
    }

    public String getVotesButtonPrefix(CatRequestVote cat1) {
        long count = votes.values().stream().filter(cat1::equals).count();
        return count == 0L ? "" : "(" + count + ")";
    }

    public void cancel() {
        super.cancel();
        this.result = CatRequestVote.NOT_CAT;
    }

    public void close(CatRequestVote result) {
        super.close(result);
    }
}
