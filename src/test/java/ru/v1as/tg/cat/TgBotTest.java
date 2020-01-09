package ru.v1as.tg.cat;

import static junit.framework.TestCase.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.tg.TestChat;
import ru.v1as.tg.cat.tg.TestUser;
import ru.v1as.tg.cat.tg.TgUpdateProcessor;

public abstract class TgBotTest {

    @Autowired protected TestAbsSender sender;
    @Autowired protected CatBotData catBotData;
    @Autowired protected TgUpdateProcessor updateProcessor;

    protected TestChat public0;

    protected TestUser bob;
    protected TestUser mary;
    protected TestUser jho;
    protected TestUser zakh;

    @Before
    public void before() {
        public0 = TestChat.publicTestChat(sender, updateProcessor, 100L);
        bob = new TestUser(1, "bob", public0);
        mary = new TestUser(2, "mary", public0);
        jho = new TestUser(3, "jho", public0);
        zakh = new TestUser(4, "zakh", public0);

        sender.registerChat(public0);
        sender.registerChat(bob.getPrivateChat());
        sender.registerChat(mary.getPrivateChat());
        sender.registerChat(jho.getPrivateChat());
        sender.registerChat(zakh.getPrivateChat());
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
    }

    public Long getChatId() {
        return public0.getId();
    }
}
