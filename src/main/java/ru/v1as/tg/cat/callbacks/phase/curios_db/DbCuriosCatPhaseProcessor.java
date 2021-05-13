package ru.v1as.tg.cat.callbacks.phase.curios_db;

import lombok.Setter;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

public class DbCuriosCatPhaseProcessor extends AbstractCuriosCatPhase {

    public static final String FINISH_VARIABLE = CatRequestVote.class.getSimpleName();

    @Setter
    private DbCatPhase dbCatPhase;

    @Override
    protected void open() {
        DbCatPhaseNode node = dbCatPhase.getStart();
        processNode(node);
    }

    private void processNode(DbCatPhaseNode node) {
        messages(node.getMessages());
        Optional<CatRequestVote> first =
                node.getActions().stream()
                        .filter(a -> a.getVariable().equals(FINISH_VARIABLE))
                        .map(PhaseNodeVarAction::getValue)
                        .map(CatRequestVote::parse)
                        .filter(Objects::nonNull)
                        .findFirst();
        if (first.isPresent()) {
            message(node.getLastMessage());
            catchUpCatAndClose(first.get());
        } else {
            generatePoll(node);
        }
    }

    private void generatePoll(DbCatPhaseNode node) {
        TgInlinePoll poll = poll(node.getLastMessage());
        for (PhaseNodeChoice choice : node.getChoices()) {
            poll.choice(choice.getTitle(), ctx -> processNode(choice.getNode()));
        }
        if (node.getTimeout() != null) {
            poll.timeout(
                    new PollTimeoutConfiguration(Duration.ofSeconds(node.getTimeout()))
                            .onTimeout(
                                    node.getTimeoutNode() != null
                                            ? () -> processNode(node.getTimeoutNode())
                                            : () -> catchUpCatAndClose(NOT_CAT)));
        }
        poll.send();
    }

}
