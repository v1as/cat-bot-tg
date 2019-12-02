package ru.v1as.tg.cat.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.callbacks.phase.TestPhase;
import ru.v1as.tg.cat.callbacks.phase.TestPhase.TestPhaseContext;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

// @Component
public class TestPhaseCommand extends AbstractCommand {

    @Autowired private TestPhase phase;

    public TestPhaseCommand() {
        super(cfg().commandName("phase").onlyPrivateChat(true));
    }

    @Override
    protected void process(TgCommandRequest command, TgChat chat, TgUser user) {
        phase.open(new TestPhaseContext(chat));
    }
}
