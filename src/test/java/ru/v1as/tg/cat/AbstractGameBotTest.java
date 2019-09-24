package ru.v1as.tg.cat;

import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class AbstractGameBotTest {

    @Autowired protected TestAbsSender sender;
    @Autowired protected AbstractGameBot bot;
    protected Integer lastMsgId = 0;
    protected Integer lastCallbackQueryId = 0;

    private int userId = 0;

    @Before
    public void before() {
        sender.setMessageProducer(() -> getMessage(++lastMsgId));
        lastMsgId = 0;
        lastCallbackQueryId = 0;
        AbsSender sender = mock(AbsSender.class);
        bot.setSender(sender);
        this.sender.clear();
    }

    @After
    public void after() {
        if (0 != sender.getMethodsAmount()) {
            fail("There are unexptected methods" + sender.getMethods());
        }
    }

    protected void switchToFirstUser() {
        userId = 0;
    }

    protected void switchToSecondUser() {
        userId = 1;
    }

    protected void switchToThirdUser() {
        userId = 2;
    }

    protected void switchToFourthUser() {
        userId = 3;
    }

    protected Update getMessageUpdate() {
        Message message = getMessage(++lastMsgId);
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        return update;
    }

    protected Message getMessage(Integer newId) {
        Message message = mock(Message.class);
        when(message.getMessageId()).thenReturn(newId);
        when(message.isUserMessage()).thenReturn(true);
        User user = getUser();
        when(message.getFrom()).thenReturn(user);
        Chat chat = getChat();
        when(message.getChat()).thenReturn(chat);
        return message;
    }

    protected Message sendPhotoMessage() {
        Update update = getMessageUpdate();
        when(update.getMessage().hasPhoto()).thenReturn(true);
        bot.onUpdateReceived(update);
        return update.getMessage();
    }

    protected void sendCallback(Integer msgId, String data) {
        Update update = getCallbackUpdate(msgId, data);
        bot.onUpdateReceived(update);
    }

    private Update getCallbackUpdate(Integer msgId, String data) {
        Update update = mock(Update.class);
        when(update.hasCallbackQuery()).thenReturn(true);
        CallbackQuery callbackQuery = getCallbackQuery(msgId, data);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        return update;
    }

    private CallbackQuery getCallbackQuery(Integer msgId, String data) {
        CallbackQuery query = mock(CallbackQuery.class);
        User user = getUser();
        when(query.getFrom()).thenReturn(user);
        when(query.getData()).thenReturn(data);
        when(query.getId()).thenReturn((++lastCallbackQueryId).toString());
        Message message = getMessage(msgId);
        when(query.getMessage()).thenReturn(message);
        return query;
    }

    protected User getUser() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(getUserId());
        String userName = "User" + getUserId();
        when(user.toString()).thenReturn(userName);
        when(user.getUserName()).thenReturn(userName);
        when(user.getFirstName()).thenReturn("User");
        when(user.getLastName()).thenReturn(getUserId().toString());
        return user;
    }

    protected Integer getUserId() {
        return userId;
    }

    protected Chat getChat() {
        Chat chat = mock(Chat.class);
        when(chat.getId()).thenReturn(getChatId());
        when(chat.isSuperGroupChat()).thenReturn(true);
        return chat;
    }

    protected Long getChatId() {
        return 0L;
    }

    private <T extends BotApiMethod> T popMethod(Class<T> clazz) {
        T pop =
                sender.getMethods().stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .findFirst()
                        .orElseThrow(
                                () -> new AssertionFailedError("Wrong type expected: " + clazz));
        assertTrue(sender.getMethods().remove(pop));
        return pop;
    }

    protected SendMessage popSendMessage() {
        SendMessage message = popMethod(SendMessage.class);
        assertEquals(getChatId().toString(), message.getChatId());
        return message;
    }

    protected SendMessage popSendMessage(String text) {
        SendMessage message = popSendMessage();
        assertEquals(text, message.getText());
        return message;
    }

    protected AnswerCallbackQuery popAnswerCallbackQuery() {
        AnswerCallbackQuery query = popMethod(AnswerCallbackQuery.class);
        assertEquals(lastCallbackQueryId.toString(), query.getCallbackQueryId());
        return query;
    }

    protected AnswerCallbackQuery popAnswerCallbackQuery(String text) {
        AnswerCallbackQuery answerCallbackQuery = popAnswerCallbackQuery();
        assertEquals(text, answerCallbackQuery.getText());
        return answerCallbackQuery;
    }

    protected DeleteMessage popDeleteMessage() {
        DeleteMessage deleteMessage = popMethod(DeleteMessage.class);
        assertEquals(deleteMessage.getChatId(), getChatId().toString());
        assertNotNull(deleteMessage.getMessageId());
        return deleteMessage;
    }

    protected EditMessageReplyMarkup popEditMessageReplyMarkup() {
        EditMessageReplyMarkup markup = popMethod(EditMessageReplyMarkup.class);
        assertEquals(getChatId().toString(), markup.getChatId());
        assertNotNull(markup.getMessageId());
        return markup;
    }

    protected EditMessageText popEditMessageText() {
        EditMessageText editMessageText = popMethod(EditMessageText.class);
        assertEquals(getChatId().toString(), editMessageText.getChatId());
        assertNotNull(editMessageText.getMessageId());
        return editMessageText;
    }

    protected EditMessageText popEditMessageText(String text) {
        EditMessageText editMessageText = popEditMessageText();
        assertEquals(text, editMessageText.getText());
        return editMessageText;
    }
}
