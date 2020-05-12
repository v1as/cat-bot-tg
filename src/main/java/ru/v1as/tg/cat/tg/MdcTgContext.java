package ru.v1as.tg.cat.tg;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

import java.util.function.Consumer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import ru.v1as.tg.cat.callbacks.phase.PersonalPublicChatPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Slf4j
public class MdcTgContext implements AutoCloseable {

    public static final String USER_MDC_KEY = "username";
    public static final String CHAT_MDC_KEY = "chat";

    private final String chat;
    private final String user;

    public MdcTgContext(String chat, String user) {
        this.chat = chat;
        this.user = user;
        apply();
    }

    public MdcTgContext(@NonNull TgChat chat, TgUser user) {
        this.chat =
                chat.isUserChat()
                        ? ":private]"
                        : format(":%s:%d]", ofNullable(chat.getTitle()).orElse(""), chat.getId());
        this.user =
                user != null ? format("[%s:%d", user.getUsernameOrFullName(), user.getId()) : null;
        apply();
    }

    public static MdcTgContext fromPhaseContext(@NonNull PhaseContext ctx) {
        if (ctx instanceof PersonalPublicChatPhaseContext) {
            return fromPhaseContext((PersonalPublicChatPhaseContext) ctx);
        }
        return new MdcTgContext(ctx.getChat(), null);
    }

    public static MdcTgContext fromPhaseContext(@NonNull PersonalPublicChatPhaseContext ctx) {
        return new MdcTgContext(ctx.getChat(), ctx.getUser());
    }

    public Runnable wrap(Runnable runnable) {
        return () -> {
            try (final MdcTgContext mdc = new MdcTgContext(chat, user)) {
                runnable.run();
            }
        };
    }

    public <T> Consumer<T> wrap(Consumer<T> consumer) {
        return (value) -> {
            try (final MdcTgContext mdc = new MdcTgContext(chat, user)) {
                consumer.accept(value);
            }
        };
    }

    public static MdcTgContext fromCurrentMdc() {
        return new MdcTgContext(MDC.get(CHAT_MDC_KEY), MDC.get(USER_MDC_KEY));
    }

    public MdcTgContext apply() {
        if (chat != null) {
            final String currentChat = MDC.get(CHAT_MDC_KEY);
            if (currentChat != null && !chat.equals(currentChat)) {
                log.warn("Overwriting mdc chat from '{}' to '{}'", currentChat, chat);
            }
            MDC.put(CHAT_MDC_KEY, chat);
        }
        if (user != null) {
            final String currentUser = MDC.get(USER_MDC_KEY);
            if (currentUser != null && !user.equals(currentUser)) {
                log.warn("Overwriting mdc username from '{}' to '{}'", currentUser, user);
            }
            MDC.put(USER_MDC_KEY, this.user);
        }
        return this;
    }

    @Override
    public void close() {
        if (user != null) {
            MDC.remove("username");
        }
        if (chat != null) {
            MDC.remove("chat");
        }
    }
}
