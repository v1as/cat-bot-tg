package ru.v1as.tg.cat.tasks;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static ru.v1as.tg.cat.EmojiConst.CAT;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.DbData;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@RequiredArgsConstructor
public class RequestsChecker implements Runnable {

    private final UnsafeAbsSender sender;
    private final DbData<CatChatData> data;
    private final ScoreData scoreData;

    @Override
    public void run() {
        CatRequest[] catRequests =
                data.getChats().stream()
                        .flatMap(c -> c.getNotFinishedCatRequests().stream())
                        .toArray(CatRequest[]::new);
        for (CatRequest request : catRequests) {
            Map<CatRequestVote, Long> votes =
                    request.getVotes().entrySet().stream()
                            .collect(groupingBy(Entry::getValue, counting()));
            Optional<CatRequestVote> voteValue = Optional.empty();
            if (votes.size() == 1 && votes.values().iterator().next() >= 3L) {
                voteValue = Optional.of(votes.keySet().iterator().next());
            } else if (request.getAge().toHours() > 4 && votes.size() > 0) {
                Long maxVotes = votes.values().stream().max(Long::compareTo).get();
                voteValue =
                        votes.entrySet().stream()
                                .filter(e -> maxVotes.equals(e.getValue()))
                                .map(Entry::getKey)
                                .max(Comparator.comparing(CatRequestVote::getAmount));
            }
            if (voteValue.isPresent()) {
                CatRequestVote vote = voteValue.get();
                request.finish(vote);
                scoreData.save(request);
                Message message = request.getVoteMessage();
                sender.executeUnsafe(
                        new EditMessageText()
                                .setChatId(message.getChatId())
                                .setMessageId(message.getMessageId())
                                .setText(vote.getAmount() + "x" + CAT));
            }
        }
    }
}
