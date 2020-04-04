package ru.v1as.tg.cat.commands;

import static com.google.common.collect.ImmutableList.of;
import static ru.v1as.tg.cat.utils.TgObjects.message;

import org.junit.Test;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.BotConfiguration;

public class TgCommandProcessorByNameTest {

    @Test(expected = TestException.class)
    public void testFindCommand() {
        final TgCommandRequest cmd = TgCommandRequest.parse(message("/test@bot"));
        getProcessor().process(cmd, null, null);
    }


    @Test(expected = TestException.class)
    public void testFindCommandNoName() {
        final TgCommandRequest cmd = TgCommandRequest.parse(message("/test"));
        getProcessor().process(cmd, null, null);
    }

    @Test
    public void testSkipCommandWithOtherName() {
        final TgCommandRequest cmd = TgCommandRequest.parse(message("/test@bot2"));
        getProcessor().process(cmd, null, null);
    }

    private TgCommandProcessorByName getProcessor() {
        return new TgCommandProcessorByName(of(new ThrowCommandHandler()), getConf());
    }

    private BotConfiguration getConf() {
        final BotConfiguration botConfiguration = new BotConfiguration();
        botConfiguration.setBotName("bot");
        return botConfiguration;
    }

    private class ThrowCommandHandler implements CommandHandler {

        @Override
        public String getCommandName() {
            return "test";
        }

        @Override
        public void handle(TgCommandRequest command, TgChat chat, TgUser user) {
            throw new TestException();
        }
    }

    private class TestException extends RuntimeException {}
}
