package ru.v1as.tg.cat.callbacks.phase;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.callbacks.phase.poll.SimplePoll;
import ru.v1as.tg.cat.callbacks.phase.poll.UpdateWithChoiceTextBuilder;
import ru.v1as.tg.cat.callbacks.phase.poll.interceptor.PhaseContextChoiceAroundInterceptor;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.model.random.RandomRequest;
import ru.v1as.tg.cat.service.clock.BotClock;
import ru.v1as.tg.cat.service.random.RandomChoice;
import ru.v1as.tg.cat.tg.TgSender;

@RequiredArgsConstructor
public abstract class AbstractPhase<T extends PhaseContext> implements Phase<T> {

    private final Logger log = getLogger(this.getClass());
    private final ThreadLocal<T> phaseContext = new ThreadLocal<>();

    @Autowired protected BotClock botClock;
    @Autowired protected TgSender sender;
    @Autowired protected BotClock clock;
    @Autowired private TgCallbackProcessor callbackProcessor;
    @Autowired private RandomChoice randomChoice;

    protected void message(TgUser userData, String text) {
        sender.execute(new SendMessage(userData.getId().longValue(), text));
    }

    protected void editMessageText(Message message, String newText) {
        sender.execute(
                new EditMessageText()
                        .setChatId(message.getChatId())
                        .setMessageId(message.getMessageId())
                        .setText(newText)
                        .setReplyMarkup(message.getReplyMarkup()));
    }

    protected SimplePoll poll(String text) {
        T phase = phaseContext.get();
        SimplePoll poll = phase.poll(text);
        poll.setSender(sender);
        poll.setBotClock(botClock);
        poll.setCallbackProcessor(callbackProcessor);
        poll.closeTextBuilder(new UpdateWithChoiceTextBuilder());
        poll.choiceAroundInterceptor(getChoiceAroundInterceptor(poll, phaseContext));
        return poll;
    }

    protected PhaseContextChoiceAroundInterceptor<T> getChoiceAroundInterceptor(
            SimplePoll poll, ThreadLocal<T> phaseContext) {
        return new PhaseContextChoiceAroundInterceptor<>(phaseContext);
    }

    protected <R> R random(RandomRequest<R> randomRequest) {
        return randomChoice.get(randomRequest);
    }

    protected <R> R random(R... values) {
        return random(new RandomRequest<R>().addAll(values));
    }

    protected <R> R random(Iterable<R> values) {
        return random(new RandomRequest<R>().addAll(values));
    }

    protected void messages(String... texts) {
        for (String text : texts) {
            message(text);
        }
    }

    protected void message(String text) {
        PhaseContext phaseContext = this.phaseContext.get();
        Long chatId = phaseContext.getChatId();
        log.info("Sending message '{}' to chat '{}'", text, phaseContext.getChat());
        sender.execute(new SendMessage(chatId, text));
    }

    protected void message(TgChat chat, String text) {
        sender.execute(new SendMessage(chat.getId(), text));
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

    protected void timeout(int ms) {
        clock.wait(ms);
    }

    @Override
    public void close() {
        log.info("Phase {} is closing...", this.getClass().getSimpleName());
        this.phaseContext.get().close();
    }

    protected T getPhaseContext() {
        return this.phaseContext.get();
    }

    protected <L> Consumer<L> contextWrap(Consumer<L> consumer) {
        T ctx = this.phaseContext.get();
        return t -> {
            phaseContext.set(ctx);
            try {
                consumer.accept(t);
            } finally {
                phaseContext.remove();
            }
        };
    }

    protected Runnable contextWrap(Runnable runnable) {
        T ctx = this.phaseContext.get();
        return () -> {
            phaseContext.set(ctx);
            try {
                runnable.run();
            } finally {
                phaseContext.remove();
            }
        };
    }
}
