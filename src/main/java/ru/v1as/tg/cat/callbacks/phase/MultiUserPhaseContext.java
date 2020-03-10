package ru.v1as.tg.cat.callbacks.phase;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Getter
public class MultiUserPhaseContext extends PublicChatPhaseContext {

    private final Set<TgUser> guests = new LinkedHashSet<>();
    private final TgUser owner;
    private final Map<TgUser, Map<String, Object>> values = new HashMap<>();

    public MultiUserPhaseContext(
            TgChat chat, TgChat publicChat, TgUser owner, Collection<TgUser> guests) {
        super(chat, publicChat);
        this.owner = owner;
        this.guests.addAll(guests);
    }

    public void addGuest(TgUser user) {
        guests.add(user);
    }

    public int getGuestAmounts() {
        return guests.size();
    }

    public List<TgUser> getUsers() {
        return Stream.concat(guests.stream(), Stream.of(owner))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<TgUser> getOthers(TgUser me) {
        return getUsers().stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .collect(Collectors.toList());
    }

    public boolean isGuest(TgUser user) {
        return guests.contains(user);
    }

    public void set(TgUser user, String name, Object value) {
        checkNotClose();
        values.computeIfAbsent(user, u -> new HashMap<>()).put(name, value);
    }

    public <T> T get(TgUser user, String name) {
        return get(user, name, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(TgUser user, String name, T defaultValue) {
        return (T)
                values.computeIfAbsent(user, u -> new HashMap<>())
                        .computeIfAbsent(name, n -> defaultValue);
    }

    public Integer increment(TgUser user, String name) {
        checkNotClose();
        Integer val =
                (Integer)
                        values.computeIfAbsent(user, u -> new HashMap<>())
                                .computeIfAbsent(name, n -> 0);
        set(user, name, ++val);
        return val;
    }

}
