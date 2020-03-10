package ru.v1as.tg.cat.callbacks.phase.multi_curios;

import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static ru.v1as.tg.cat.utils.PredicateUtils.not;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.v1as.tg.cat.callbacks.phase.MultiUserPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;
import ru.v1as.tg.cat.model.TgUser;

@Setter
@Accessors(fluent = true, chain = true)
public class MarkerOrderChoice<T extends UserMarker> {

    private final String marketName;
    private final List<T> markerValues;
    private final MultiUserPhaseContext phase;
    private final Map<T, MarkerChoice> choices;

    private String description;

    private Consumer<TgUser> userOrderStart;
    private BiConsumer<TgUser, T> userOrderFinish;
    private BiFunction<TgUser, List<T>, T> onTimeout;
    private Consumer<TgUser> onTimeoutMessage;
    private Runnable onFinish;
    private Boolean updating = true;
    private Function<String, TgInlinePoll> pollFactory;
    private Iterator<TgUser> users;

    public MarkerOrderChoice(Class<T> markerClass, MultiUserPhaseContext phase) {
        markerValues = Arrays.asList(markerClass.getEnumConstants());
        marketName = markerClass.getSimpleName();
        this.phase = phase;
        choices = new HashMap<>();
    }

    public MarkerOrderChoice<T> description(String description) {
        this.description = description;
        return this;
    }

    public MarkerOrderChoice<T> choice(T marker, String text) {
        this.choices.put(marker, new MarkerChoice(marker, text));
        return this;
    }

    public MarkerOrderChoice<T> done(Function<String, TgInlinePoll> pollFactory) {
        this.pollFactory = pollFactory;
        checkUpdating();
        updating = false;
        validation();
        users = phase.getUsers().iterator();
        sendPollToNextUserOrFinish();
        return null;
    }

    private void sendPollToNextUserOrFinish() {
        if (users.hasNext()) {
            final TgUser user = users.next();
            final TgInlinePoll poll = pollFactory.apply(description);
            poll.chatId(user.getChatId())
                    .timeout(
                            new PollTimeoutConfiguration(ofSeconds(10))
                                    .removeMsg(true)
                                    .onTimeout(this.onUserTimeout(user)));
            getFreeValues().forEach(v -> poll.choice(choices.get(v).text, this.choiceMade(v)));
            poll.send();
        } else {

        }
    }

    private Runnable onUserTimeout(TgUser user) {
        final T userChoice = onTimeout.apply(user, getFreeValues());
        phase.set(user, marketName, userChoice);
        return null;
    }

    private List<T> getFreeValues() {
        final Set<T> usedValues =
                phase.getUsers().stream().map(u -> phase.<T>get(u, marketName)).collect(toSet());
        return markerValues.stream().filter(not(usedValues::contains)).collect(toList());
    }

    private Consumer<ChooseContext> choiceMade(T v) {
        return ctx -> {
            phase.set(ctx.getUser(), marketName, v);
            sendPollToNextUserOrFinish();
        };
    }

    private void checkUpdating() {
        if (!updating) {
            throw new IllegalStateException("Not updating already.");
        }
    }

    private void validation() {
        final int usersAmount = phase.getUsers().size();
        if (markerValues.size() < usersAmount) {
            throw new IllegalStateException("Too much users for markers amount.");
        }
    }

    @Setter
    @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor
    private class MarkerChoice {
        private final T marker;
        private final String text;
    }
}
