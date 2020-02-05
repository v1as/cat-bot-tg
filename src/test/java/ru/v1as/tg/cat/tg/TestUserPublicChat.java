package ru.v1as.tg.cat.tg;

import static ru.v1as.tg.cat.commands.impl.StartCommand.SLASH_START_COMMAND_NAME;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class TestUserPublicChat extends TestUserChat {

    private final TestChat privateChat;

    public TestUserPublicChat(TestChat chat, TestChat privateChat, User user) {
        super(chat, user);
        this.privateChat = privateChat;
    }

    @Override
    public Message sendCommand(String text) {
        final TestChat chat =
                text.startsWith(SLASH_START_COMMAND_NAME) ? this.privateChat : this.chat;
        return chat.sendCommand(user, text);
    }
}
