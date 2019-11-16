package ru.v1as.tg.cat.tasks;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static ru.v1as.tg.cat.EmojiConst.CAT;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.service.CatEventService;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestsChecker {

    private final int RATE = 2_000;
    private final TgSender sender;
    private final CatBotData data;
    private final CatEventService catService;

    @PostConstruct
    public void init() {
        log.info("Request checked started with rate {}ms", RATE);
    }

    @Scheduled(fixedRate = RATE)
    public void run() {
        log.debug("tick");
        CatRequest[] catRequests =
                data.getChats().stream()
                        .flatMap(c -> c.getNotFinishedCatRequests().stream())
                        .toArray(CatRequest[]::new);
        for (CatRequest req : catRequests) {
            Map<CatRequestVote, Long> votes =
                    req.getVotes().entrySet().stream()
                            .collect(groupingBy(Entry::getValue, counting()));
            Optional<CatRequestVote> voteValue = Optional.empty();
            if (votes.size() == 1 && votes.values().iterator().next() >= 3L) {
                voteValue = Optional.of(votes.keySet().iterator().next());
            } else if (req.getAge().toHours() > 4 && votes.size() > 0) {
                Long maxVotes = votes.values().stream().max(Long::compareTo).get();
                voteValue =
                        votes.entrySet().stream()
                                .filter(e -> maxVotes.equals(e.getValue()))
                                .map(Entry::getKey)
                                .max(Comparator.comparing(CatRequestVote::getAmount));
            }
            if (voteValue.isPresent()) {
                CatRequestVote vote = voteValue.get();
                req.finish(vote);
                log.info(
                        "Request for user '{}' is finished with result '{}'.",
                        req.getOwner(),
                        vote);
                catService.poll(
                        req.getResult(),
                        req.getMessageId(),
                        req.getChatId(),
                        req.getOwner().getId());
                sender.execute(
                        new EditMessageText()
                                .setChatId(req.getChatId())
                                .setMessageId(req.getMessageId())
                                .setText(vote.getAmount() + "x" + CAT));
            }
        }
    }
}
