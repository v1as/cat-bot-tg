package ru.v1as.tg.cat.callbacks.phase.multi_curios;

import static java.util.Collections.singletonList;
import static ru.v1as.tg.cat.utils.TimeoutUtils.getMsForTextReading;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.v1as.tg.cat.callbacks.phase.AbstractPublicChatPhase;
import ru.v1as.tg.cat.callbacks.phase.MultiUserPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;
import ru.v1as.tg.cat.model.TgUser;

public abstract class AbstractMultiUserPhase<T extends MultiUserPhaseContext>
        extends AbstractPublicChatPhase<T> {

    @Override
    protected void message(String text) {
        super.message(getPhaseContext().getPublicChat(), text);
        botClock.wait(getMsForTextReading(text.length()));
    }

    @Override
    protected TgInlinePoll poll(String text) {
        T ctx = getPhaseContext();
        return poll(text, ctx.getPublicChatId());
    }

    public void messages(TgUser user, String... messages) {
        messages(singletonList(user), messages);
    }

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
        return users.stream().map(TgUser::getUsernameOrFullName).collect(Collectors.joining(", "));
    }
}
