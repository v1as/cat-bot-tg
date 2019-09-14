package ru.v1as.tg.cat.tasks;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static ru.v1as.tg.cat.EmojiConst.CAT;

import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.DbData;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@RequiredArgsConstructor
public class RequestsChecker implements Runnable {

    private final UnsafeAbsSender sender;
    private final DbData data;
    private final ScoreData scoreData;

    @Override
    public void run() {
        for (CatRequest request : data.getNotFinishedCatRequests()) {
            Map<CatRequestVote, Long> votes =
                    request.getVotes().entrySet().stream()
                            .collect(groupingBy(Entry::getValue, counting()));
            if (votes.size() == 1 && votes.values().iterator().next() >= 3L) {
                CatRequestVote vote = votes.keySet().iterator().next();
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
