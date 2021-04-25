package ru.v1as.tg.cat;

import static junit.framework.TestCase.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.service.clock.TestBotClock;
import ru.v1as.tg.cat.tg.TestBot;
import ru.v1as.tg.cat.tg.TestChat;
import ru.v1as.tg.cat.tg.TestUser;
import ru.v1as.tg.cat.tg.TgUpdateProcessor;

public abstract class TgBotTest {

    @Autowired protected TestAbsSender sender;
    @Autowired protected CatBotData catBotData;
    @Autowired protected TgUpdateProcessor updateProcessor;
    @Autowired protected TestBotClock clock;

    protected TestChat inPublic;
    protected TestChat inAnotherPublic;

    protected TestUser bob;
    protected TestUser mary;
    protected TestUser jho;
    protected TestUser zakh;
    protected TestBot bot;

    @Before
    public void before() {
        inPublic = TestChat.publicTestChat(sender, updateProcessor, 100L);
        inAnotherPublic = TestChat.publicTestChat(sender, updateProcessor, 101L);
        bob = new TestUser(1, "bob", inPublic);
        mary = new TestUser(2, "mary", inPublic);
        jho = new TestUser(3, "jho", inPublic);
        zakh = new TestUser(4, "zakh", inPublic);
        bot = new TestBot(5, "bot", inPublic);

        sender.registerChat(inPublic);
        sender.registerChat(inAnotherPublic);
        sender.registerChat(bob.getPrivateChat());
        sender.registerChat(mary.getPrivateChat());
        sender.registerChat(jho.getPrivateChat());
        sender.registerChat(zakh.getPrivateChat());

        clock.reset();
    }

    protected void clearMethodsQueue() {
        this.sender.clear();
    }

    protected void assertMethodsQueueIsEmpty() {
        assertEquals(
                "Method queue is not empty: " + sender.getMethodCalls(),
                0,
                this.sender.getMethodsAmount());
    }

    @After
    public void after() {
        assertMethodsQueueIsEmpty();
        clock.reset();
    }

    public Long getChatId() {
        return inPublic.getId();
    }
}
