package ru.v1as.tg.cat.tg;

public class TestBot extends TestUser {

    public TestBot(Integer userId, String userName, TestChat publicChat) {
        super(publicChat, null, publicChat.getBot(userId, userName));
    }

    @Override
    public TestUserChat inPrivate() {
        throw new UnsupportedOperationException();
    }

}
