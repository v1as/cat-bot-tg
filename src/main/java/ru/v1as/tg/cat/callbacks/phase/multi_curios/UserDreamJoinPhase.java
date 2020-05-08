package ru.v1as.tg.cat.callbacks.phase.multi_curios;

import static java.util.Collections.emptyList;
import static ru.v1as.tg.cat.EmojiConst.DREAMING;
import static ru.v1as.tg.cat.EmojiConst.ZZZ;

import java.time.Duration;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.phase.MultiUserPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.Phase;
import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.UserDreamJoinPhase.UserDreamJoinPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.PollChoice;
import ru.v1as.tg.cat.commands.ArgumentCallbackCommand.CallbackCommandContext;
import ru.v1as.tg.cat.commands.impl.StartCommand;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.BotConfiguration;

@Component
@RequiredArgsConstructor
public class UserDreamJoinPhase extends AbstractMultiUserPhase<UserDreamJoinPhaseContext> {

    private final BotConfiguration conf;
    private final StartCommand startCommand;

    @Override
    protected void open() {
        final UserDreamJoinPhaseContext ctx = getPhaseContext();
        PollChoice joinTheDream =
                PollChoice.startCommandUrl(conf.getBotName(), "Присоединиться" + ZZZ);

        final PollTimeoutConfiguration closeInOneMinute =
                new PollTimeoutConfiguration(Duration.ofMinutes(1))
                        .message("Сегодня эта история приснится только одному.")
                        .removeMsg(true)
                        .onTimeout(this::close);

        publicPoll(joinMessage(ctx))
                .choice(joinTheDream)
                .onSend(msg -> ctx.joinMessage = msg)
                .timeout(closeInOneMinute)
                .removeOnClose(true)
                .send();

        ctx.startCommandArgument = joinTheDream.getUuid();
        startCommand.register(ctx.startCommandArgument, contextWrap(this::join));
    }

    private void join(CallbackCommandContext callback) {
        final UserDreamJoinPhaseContext ctx = getPhaseContext();
        final TgUser user = callback.getUser();
        boolean toUpdateJoinMsg = false;
        if (!ctx.isGuest(user) && !user.equals(ctx.getOwner())) {
            ctx.addGuest(user);
            toUpdateJoinMsg = true;
            sender.executeAsync(new SendMessage(callback.getUserId().toString(), "Вы ожидаете дрёму"));
        }
        if (ctx.getGuestAmounts() == ctx.getUsersAmount()) {
            toUpdateJoinMsg = false;
            close();
            ctx.getConnect().connect(ctx);
        }
        if (toUpdateJoinMsg) {
            updateJoinMessage();
        }
    }

    private void updateJoinMessage() {
        final UserDreamJoinPhaseContext ctx = getPhaseContext();
        final Message msg = ctx.joinMessage;
        this.sender.execute(
                new EditMessageText()
                        .setChatId(msg.getChatId())
                        .setMessageId(msg.getMessageId())
                        .setText(joinMessage(ctx)));
    }

    private String joinMessage(UserDreamJoinPhaseContext ctx) {
        return String.format(
                "%s приглашает всех в свой сон %s\n"
                        + "Собираем %d игроков. \n"
                        + "Откликнулись: \n"
                        + "%s",
                ctx.getOwner().getUsernameOrFullName(),
                DREAMING,
                ctx.getUsersAmount(),
                ctx.getGuests().stream()
                        .map(TgUser::getUsernameOrFullName)
                        .collect(Collectors.joining("\n")));
    }

    @Getter
    public static class UserDreamJoinPhaseContext extends MultiUserPhaseContext {

        private final UserDreamJoinPhaseConnect<?> connect;
        public Message joinMessage;
        private int usersAmount;
        private String startCommandArgument;

        public UserDreamJoinPhaseContext(
                TgChat chat,
                TgChat publicChat,
                TgUser owner,
                UserDreamJoinPhaseConnect<?> connect) {
            super(chat, publicChat, owner, emptyList());
            this.connect = connect;
            this.usersAmount = connect.getUsersAmount();
        }
    }

    @Value
    public static class UserDreamJoinPhaseConnect<C extends PhaseContext> {
        private final Phase<C> phase;
        private final int usersAmount;
        private final Function<UserDreamJoinPhaseContext, C> contextBuilder;

        public void connect(UserDreamJoinPhaseContext context) {
            phase.open(contextBuilder.apply(context));
        }
    }
}
