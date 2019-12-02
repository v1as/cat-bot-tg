package ru.v1as.tg.cat;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.v1as.tg.cat.model.TgUserWrapper.wrap;

import java.io.Serializable;
import java.util.Deque;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.slf4j.Logger;
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
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgChatWrapper;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.tg.TgUpdateProcessor;
import ru.v1as.tg.cat.utils.AssertAnswerCallbackQuery;
import ru.v1as.tg.cat.utils.AssertEditMessage;
import ru.v1as.tg.cat.utils.AssertEditMessageReplyMarkup;
import ru.v1as.tg.cat.utils.AssertEditMessageText;
import ru.v1as.tg.cat.utils.AssertSendMessage;

public abstract class TgBotTest implements TgTestInvoker {

    private static final int USER_1_ID = 0;
    private static final int USER_2_ID = 1;
    private static final int USER_3_ID = 2;
    private static final int USER_4_ID = 3;
    private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired protected TestAbsSender sender;
    @Autowired protected CatBotData catBotData;
    @Autowired protected TgUpdateProcessor updateProcessor;

    protected Integer lastMsgId = 0;
    Integer lastCallbackQueryId = 0;

    private int userId = 0;
    private Chat chat;

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

    protected void switchToFirstUser() {
        userId = USER_1_ID;
    }

    protected void switchToSecondUser() {
        userId = USER_2_ID;
    }

    protected void switchToThirdUser() {
        userId = USER_3_ID;
    }

    protected void switchToFourthUser() {
        userId = USER_4_ID;
    }

    protected Update getMessageUpdate() {
        Message message = getMessage(++lastMsgId);
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        return update;
    }

    protected Message getMessage() {
        return getMessage(++lastMsgId);
    }

    protected Message getMessage(Integer newId) {
        Message message = mock(Message.class);
        when(message.getMessageId()).thenReturn(newId);
        when(message.isUserMessage()).thenReturn(true);
        User user = getUser();
        when(message.getFrom()).thenReturn(user);
        Chat chat = getChat();
        Long chatId = getChatId();
        when(message.getChat()).thenReturn(chat);
        when(message.getChatId()).thenReturn(chatId);
        return message;
    }

    protected Message sendCommand(String text) {
        Update update = getMessageUpdate();
        when(update.getMessage().getText()).thenReturn(text);
        when(update.getMessage().isCommand()).thenReturn(true);
        updateProcessor.onUpdateReceived(update);
        return update.getMessage();
    }

    protected Message sendTextMessage(String text) {
        Update update = getMessageUpdate();
        when(update.getMessage().getText()).thenReturn(text);
        updateProcessor.onUpdateReceived(update);
        return update.getMessage();
    }

    protected Message sendPhotoMessage() {
        Update update = getMessageUpdate();
        when(update.getMessage().hasPhoto()).thenReturn(true);
        updateProcessor.onUpdateReceived(update);
        return update.getMessage();
    }

    @Override
    public void sendCallback(Integer msgId, String data) {
        Update update = getCallbackUpdate(msgId, data);
        updateProcessor.onUpdateReceived(update);
    }

    public Update getCallbackUpdate(Integer msgId, String data) {
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

    protected TgUser getTgUser() {
        return wrap(getUser());
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

    protected TgChat getTgChat() {
        return TgChatWrapper.wrap(getChat());
    }

    protected void switchToPublicChat() {
        this.chat = mockChat(100L, true);
    }

    protected void switchFirstUserChat() {
        this.chat = mockChat((long) USER_1_ID, false);
    }

    protected Chat getChat() {
        return chat;
    }

    private Chat mockChat(Long chatId, boolean isPublic) {
        Chat res = mock(Chat.class);
        when(res.getId()).thenReturn(chatId);
        when(res.isSuperGroupChat()).thenReturn(isPublic);
        when(res.isUserChat()).thenReturn(!isPublic);
        return res;
    }

    protected Long getChatId() {
        return chat.getId();
    }

    @SuppressWarnings("unchecked")
    private <P extends Serializable, T extends BotApiMethod<P>> T popMethod(Class<T> clazz) {
        final MethodCall methodCall =
                sender.getMethodCalls().stream()
                        .filter(obj -> clazz.isInstance(obj.getRequest()))
                        .findFirst()
                        .orElseThrow(getAssertionFailedErrorSupplier(clazz));
        assertTrue(sender.getMethodCalls().remove(methodCall));
        return (T) methodCall.getRequest();
    }

    private Supplier<AssertionFailedError> getAssertionFailedErrorSupplier(Class clazz) {
        return () ->
                new AssertionFailedError(
                        String.format(
                                "Wrong type expected: '%s' but [%s]",
                                clazz.getSimpleName(), sender.getMethodCalls()));
    }

    @SuppressWarnings("unchecked")
    private <P extends Serializable, T extends BotApiMethod<P>> MethodCall<P> popMethodCall(
            Class<T> clazz) {
        final MethodCall methodCall =
                sender.getMethodCalls().stream()
                        .filter(obj -> clazz.isInstance(obj.getRequest()))
                        .findFirst()
                        .orElseThrow(getAssertionFailedErrorSupplier(clazz));
        assertTrue(sender.getMethodCalls().remove(methodCall));
        return (MethodCall<P>) methodCall;
    }

    protected void printQueueMessages() {
        log.info(sender.getMethodCalls().toString());
    }

    private String getMethodsStr(Deque<MethodCall> methods) {
        return methods.stream()
                .map(Object::getClass)
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", "));
    }

    protected AssertEditMessage popEditMessage() {
        final EditMessageText message = popMethodCall(EditMessageText.class).getRequest();
        assertEquals(getChatId().toString(), message.getChatId());
        return new AssertEditMessage(this, message);
    }

    protected AssertEditMessageReplyMarkup popEditMessageReplyMarkup() {
        final EditMessageReplyMarkup edit =
                popMethodCall(EditMessageReplyMarkup.class).getRequest();
        assertEquals(getChatId().toString(), edit.getChatId());
        return new AssertEditMessageReplyMarkup(this, edit);
    }

    protected AssertSendMessage popSendMessage() {
        final MethodCall<Message> call = popMethodCall(SendMessage.class);
        SendMessage sendMessage = call.getRequest();
        assertEquals(getChatId().toString(), sendMessage.getChatId());
        return new AssertSendMessage(this, sendMessage, call.getResponse());
    }

    protected AssertAnswerCallbackQuery popAnswerCallbackQuery() {
        AnswerCallbackQuery query = popMethod(AnswerCallbackQuery.class);
        assertEquals(lastCallbackQueryId.toString(), query.getCallbackQueryId());
        return new AssertAnswerCallbackQuery(query);
    }

    protected DeleteMessage popDeleteMessage() {
        DeleteMessage deleteMessage = popMethod(DeleteMessage.class);
        assertEquals(deleteMessage.getChatId(), getChatId().toString());
        assertNotNull(deleteMessage.getMessageId());
        return deleteMessage;
    }

    protected AssertEditMessageText popEditMessageText() {
        EditMessageText editMessageText = popMethod(EditMessageText.class);
        assertEquals(getChatId().toString(), editMessageText.getChatId());
        return new AssertEditMessageText(editMessageText);
    }
}
