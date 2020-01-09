package ru.v1as.tg.cat.tg;

import static ru.v1as.tg.cat.tg.TgTestObject.getChat;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.User;

public class TestUser {

    private final TestChat publicChat;
    @Getter private final TestChat privateChat;
    private User user;

    public TestUser(Integer userId, String userName, TestChat publicChat) {
        this.user = publicChat.getUser(userId, userName);
        this.publicChat = publicChat;
        this.privateChat =
                new TestChat(
                        publicChat.getSender(),
                        publicChat.getUpdateProcessor(),
                        getChat(userId.longValue(), false));
    }

    public TestUserChat inPublic() {
        return new TestUserChat(publicChat, user);
    }

    public TestUserChat inPrivate() {
        return new TestUserChat(privateChat, user);
    }

    public Integer getUserId() {
        return user.getId();
    }
}
