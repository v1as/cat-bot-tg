package ru.v1as.tg.cat.callbacks.phase;

import ru.v1as.tg.cat.callbacks.phase.TestPhase.TestPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;
import ru.v1as.tg.cat.model.TgChat;

// @Component
public class TestPhase extends AbstractPhase<TestPhaseContext> {

    @Override
    protected void open() {
        getPhaseContext().poll =
                poll("Приветики!").closeOnChoose(false).choice("Привет", this::hello).send();
    }

    private void hello(ChooseContext chooseContext) {
        getPhaseContext().poll.choice("Пока", this::bye).send();
    }

    private void bye(ChooseContext chooseContext) {
        message("Пока! ");
        close();
    }

    public static class TestPhaseContext extends PhaseContext {
        TgInlinePoll poll;

        public TestPhaseContext(TgChat chat) {
            super(chat);
        }
    }
}
