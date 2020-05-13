package ru.v1as.tg.cat.commands.impl;

import static java.util.function.Function.identity;
import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase.CuriosCatContext;
import ru.v1as.tg.cat.callbacks.phase.impl.JoinCatFollowPhase;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.model.TgUserChat;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
public class TestCommandHandler extends AbstractCommand {

    private final JoinCatFollowPhase joinPhase;
    private final TgSender sender;
    private final Map<String, AbstractCuriosCatPhase> catPhases;

    public TestCommandHandler(
            JoinCatFollowPhase testPhase, TgSender sender, List<AbstractCuriosCatPhase> catPhases) {
        super(cfg().onlyBotAdmins(true).onlyPublicChat(true).commandName("test"));
        this.joinPhase = testPhase;
        this.sender = sender;
        this.catPhases =
                catPhases.stream()
                        .collect(Collectors.toMap(p -> p.getClass().getSimpleName(), identity()));
    }

    @Override
    public void process(TgCommandRequest command, TgChat chat, TgUser user) {
        final String phase = command.getFirstArgument();
        if (isEmpty(phase) || !catPhases.containsKey(phase)) {
            joinPhase.open(chat);
            sender.execute(new DeleteMessage(chat.getId(), command.getMessageId()));
            log.info("Test phase started...");
        } else {
            final AbstractCuriosCatPhase curiosCatPhase = catPhases.get(phase);
            curiosCatPhase.open(
                    new CuriosCatContext(new TgUserChat(user), chat, user, command.getMessage()));
        }
    }
}
