package ru.v1as.tg.cat.tg;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.MethodCall;
import ru.v1as.tg.cat.TgTestInvoker;
import ru.v1as.tg.cat.utils.AbstractAssertMessage;
import ru.v1as.tg.cat.utils.AssertAnswerCallbackQuery;
import ru.v1as.tg.cat.utils.AssertDeleteMessage;
import ru.v1as.tg.cat.utils.AssertEditMessageText;
import ru.v1as.tg.cat.utils.AssertSendMessage;
import ru.v1as.tg.cat.utils.AssertSendMessageToSend;

public class TestUserChat implements TgTestInvoker {

    private final TestChat chat;
    private final User user;

    public TestUserChat(TestChat chat, User user) {
        this.chat = chat;
        this.user = user;
    }

    @Override
    public void sendCallback(Integer msgId, String data) {
        chat.sendCallback(user, msgId, data);
    }

    public Message sendCommand(String text) {
        return chat.sendCommand(user, text);
    }

    public Message sendTextMessage(String text) {
        return chat.sendTextMessage(user, text);
    }

    public Message sendPhotoMessage() {
        return chat.sendPhotoMessage(user);
    }

    public AssertSendMessageToSend getSendMessageToSend() {
        final MethodCall<Message> call =
                chat.getMethodCall(
                        SendMessage.class, m -> chat.getChatIdStr().equals(m.getChatId()));
        SendMessage sendMessage = call.getRequest();
        return new AssertSendMessageToSend(this, sendMessage, call.getResponse());
    }

    public AssertSendMessageToSend findSendMessageToSend(String text) {
        final MethodCall<Message> call =
                chat.findMethodCall(
                        SendMessage.class,
                        m ->
                                m.getText().contains(text)
                                        && chat.getChatIdStr().equals(m.getChatId()));
        SendMessage sendMessage = call.getRequest();
        return new AssertSendMessageToSend(this, sendMessage, call.getResponse());
    }

    public AssertAnswerCallbackQuery getAnswerCallbackQuery() {
        return chat.getAnswerCallbackQuery();
    }

    public AssertSendMessage getSendMessage() {
        return chat.getSendMessage();
    }

    public AssertDeleteMessage getDeleteMessage() {
        return chat.getDeleteMessage();
    }

    public AssertEditMessageText getEditMessage() {
        return chat.getEditMessage();
    }

    public AbstractAssertMessage getEditMessageReplyMarkup() {
        return chat.getEditMessageReplyMarkup();
    }
}
