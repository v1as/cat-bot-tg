package ru.v1as.tg.cat.callbacks.phase.multi_curios;

import static ru.v1as.tg.cat.utils.TimeoutUtils.getMsForTextReading;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.v1as.tg.cat.callbacks.phase.AbstractPublicChatPhase;
import ru.v1as.tg.cat.callbacks.phase.MultiUserPhaseContext;
import ru.v1as.tg.cat.model.TgUser;

public abstract class AbstractMultiUserPhase<T extends MultiUserPhaseContext>
        extends AbstractPublicChatPhase<T> {

    public void messages(List<TgUser> users, String... messages) {
        for (String message : messages) {
            for (TgUser user : users) {
                message(user, message);
            }
            botClock.wait(getMsForTextReading(message.length()));
        }
    }

    public void everyoneMessages(String... messages) {
        messages(getPhaseContext().getUsers(), messages);
    }

    public void everyoneMessage(Function<TgUser, String> message) {
        getUsers().forEach(u -> message(u, message.apply(u)));
        final int msgLen = message.apply(getUsers().get(0)).length();
        botClock.wait(getMsForTextReading(msgLen));
    }

    public List<TgUser> getUsers() {
        return getPhaseContext().getUsers();
    }

    public List<TgUser> getOthers(TgUser me) {
        return getPhaseContext().getOthers(me);
    }

    public String toString(Collection<TgUser> users) {
        return users.stream().map(Objects::toString).collect(Collectors.joining(", "));
    }
}
