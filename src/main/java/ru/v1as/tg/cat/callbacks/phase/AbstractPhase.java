package ru.v1as.tg.cat.callbacks.phase;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.callbacks.phase.poll.SimplePoll;
import ru.v1as.tg.cat.callbacks.phase.poll.interceptor.PhaseContextChoiceAroundInterceptor;
import ru.v1as.tg.cat.model.UserData;
import ru.v1as.tg.cat.service.clock.BotClock;
import ru.v1as.tg.cat.tg.KeyboardUtils;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@RequiredArgsConstructor
public abstract class AbstractPhase<T extends PhaseContext> implements Phase<T> {

    private final Logger log = getLogger(this.getClass());

    @Autowired protected UnsafeAbsSender sender;
    @Autowired private TgCallbackProcessor callbackProcessor;
    @Autowired protected BotClock clock;

    private ThreadLocal<T> phaseContext = new ThreadLocal<>();

    protected void message(UserData userData, String text) {
        sender.executeUnsafe(new SendMessage(userData.getChatId(), text));
    }

    protected void deleteMsg(Message msg) {
        sender.executeUnsafe(KeyboardUtils.deleteMsg(msg));
    }

    protected void deleteMsg(Message msg, Consumer<Boolean> consumer) {
        if (msg != null) {
            sender.executeAsyncPromise(
                    KeyboardUtils.deleteMsg(msg), consumer, t -> consumer.accept(false));
        }
    }

    protected SimplePoll poll(String text) {
        T phase = phaseContext.get();
        SimplePoll poll = phase.poll(text);
        poll.setSender(sender);
        poll.setCallbackProcessor(callbackProcessor);
        poll.setChoiceAroundInterceptor(getChoiceAroundInterceptor(poll, phaseContext));
        return poll;
    }

    protected PhaseContextChoiceAroundInterceptor<T> getChoiceAroundInterceptor(
            SimplePoll poll, ThreadLocal<T> phaseContext) {
        return new PhaseContextChoiceAroundInterceptor<>(phaseContext);
    }

    protected void message(String text) {
        PhaseContext phaseContext = this.phaseContext.get();
        Long chatId = phaseContext.getChatId();
        log.info("Sending message '{}' to chat '{}'", text, chatId);
        sender.executeUnsafe(new SendMessage(chatId, text));
    }

    protected void message(Chat chat, String text) {
        sender.executeUnsafe(new SendMessage(chat.getId(), text));
    }

    protected void onClose(Runnable onClose) {
        phaseContext.get().onClose(onClose);
    }

    public final void open(T phaseContext) {
        this.phaseContext.set(phaseContext);
        try {
            this.open();
        } finally {
            this.phaseContext.remove();
        }
    }

    protected abstract void open();

    @SneakyThrows
    protected void timeout(int ms) {
        clock.wait(ms);
    }

    @Override
    public void close() {
        this.phaseContext.get().close();
        this.phaseContext.remove();
    }

    protected T getPhaseContext() {
        return this.phaseContext.get();
    }

    protected <L> Consumer<L> contextWrap(Consumer<L> consumer) {
        PhaseContext ctx = this.phaseContext.get();
        return t -> {
            phaseContext.set((T) ctx);
            try {
                consumer.accept(t);
            } finally {
                phaseContext.remove();
            }
        };
    }

    protected Runnable contextWrap(Runnable runnable) {
        PhaseContext ctx = this.phaseContext.get();
        return () -> {
            phaseContext.set((T) ctx);
            try {
                runnable.run();
            } finally {
                phaseContext.remove();
            }
        };
    }
}
