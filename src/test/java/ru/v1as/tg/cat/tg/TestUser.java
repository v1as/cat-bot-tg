package ru.v1as.tg.cat.tg;

import static ru.v1as.tg.cat.tg.TgTestObject.getChat;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.User;

public class TestUser {

    private final TestChat publicChat;
    @Getter private final TestChat privateChat;
    @Getter private final User user;

    public TestUser(Integer userId, String userName, TestChat publicChat) {
        this.user = publicChat.getUser(userId, userName);
        this.publicChat = publicChat;
        this.privateChat =
                new TestChat(
                        publicChat.getSender(),
                        publicChat.getUpdateProcessor(),
                        getChat(userId.longValue(), false));
    }

    protected TestUser(TestChat publicChat, TestChat privateChat, User user) {
        this.publicChat = publicChat;
        this.privateChat = privateChat;
        this.user = user;
    }

    public TestUserChat inPublic() {
        return new TestUserPublicChat(publicChat, privateChat, user);
    }

    public TestUserChat inPrivate() {
        return new TestUserChat(privateChat, user);
    }

    public Integer getUserId() {
        return user.getId();
    }
}
