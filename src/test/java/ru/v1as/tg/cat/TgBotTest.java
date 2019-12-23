package ru.v1as.tg.cat;

import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static ru.v1as.tg.cat.model.TgUserWrapper.wrap;

import java.io.Serializable;
import java.util.function.Predicate;
import java.util.function.Supplier;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgChatWrapper;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.tg.TgUpdateProcessor;
import ru.v1as.tg.cat.utils.AssertAnswerCallbackQuery;
import ru.v1as.tg.cat.utils.AssertDeleteMessage;
import ru.v1as.tg.cat.utils.AssertEditMessage;
import ru.v1as.tg.cat.utils.AssertEditMessageReplyMarkup;
import ru.v1as.tg.cat.utils.AssertEditMessageText;
import ru.v1as.tg.cat.utils.AssertSendMessage;

public abstract class TgBotTest implements TgTestInvoker {

    private static final int USER_1_ID = 0;
    private static final int USER_2_ID = 1;
    private static final int USER_3_ID = 2;
    private static final int USER_4_ID = 3;
    public static final Long PUBLIC_CHAT_ID_1 = 100L;

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
        Update update = new Update();
        setField(update, "message", message);
        return update;
    }

    protected Message getMessage(Integer newId, String chatIdStr) {
        Long chatId = Long.valueOf(chatIdStr);
        final Message message = getMessage(newId);
        setField(message, "chat", getChat(chatId, PUBLIC_CHAT_ID_1.equals(chatId)));
        return message;
    }

    protected Message getMessage(Integer newId) {
        Message message = new Message();
        User user = getUser();
        setField(message, "messageId", newId);
        setField(message, "chat", chat);
        setField(message, "from", user);
        return message;
    }

    public Message sendCommand(String text) {
        Update update = getMessageUpdate();
        final Message message = update.getMessage();
        setField(message, "text", text);
        final MessageEntity messageEntity = new MessageEntity();
        setField(messageEntity, "type", EntityType.BOTCOMMAND);
        setField(messageEntity, "offset", 0);
        setField(message, "entities", singletonList(messageEntity));
        updateProcessor.onUpdateReceived(update);
        return update.getMessage();
    }

    protected Message sendTextMessage(String text) {
        Update update = getMessageUpdate();
        setField(update.getMessage(), "text", text);
        updateProcessor.onUpdateReceived(update);
        return update.getMessage();
    }

    protected Message sendPhotoMessage() {
        Update update = getMessageUpdate();
        setField(update.getMessage(), "photo", singletonList(new PhotoSize()));
        updateProcessor.onUpdateReceived(update);
        return update.getMessage();
    }

    @Override
    public void sendCallback(Integer msgId, String data) {
        Update update = getCallbackUpdate(msgId, data);
        updateProcessor.onUpdateReceived(update);
    }

    public Update getCallbackUpdate(Integer msgId, String data) {
        Update update = new Update();
        setField(update, "callbackQuery", getCallbackQuery(msgId, data));
        return update;
    }

    private CallbackQuery getCallbackQuery(Integer msgId, String data) {
        CallbackQuery query = new CallbackQuery();
        setField(query, "from", getUser());
        setField(query, "data", data);
        setField(query, "id", (++lastCallbackQueryId).toString());
        setField(query, "message", getMessage(msgId));
        return query;
    }

    protected TgUser getTgUser() {
        return wrap(getUser());
    }

    protected User getUser() {
        User user = new User();
        setField(user, "id", getUserId());
        String userName = "User" + getUserId();
        setField(user, "userName", userName);
        setField(user, "firstName", "User");
        setField(user, "lastName", getUserId().toString());
        return user;
    }

    protected Integer getUserId() {
        return userId;
    }

    protected TgChat getTgChat() {
        return TgChatWrapper.wrap(getChat());
    }

    protected void switchToPublicChat() {
        this.chat = getChat(PUBLIC_CHAT_ID_1, true);
    }

    protected void switchFirstUserChat() {
        this.chat = getChat((long) USER_1_ID, false);
    }

    protected void switchSecondUserChat() {
        this.chat = getChat((long) USER_2_ID, false);
    }

    protected void switchThirdUserChat() {
        this.chat = getChat((long) USER_3_ID, false);
    }

    protected void switchFourthUserChat() {
        this.chat = getChat((long) USER_4_ID, false);
    }

    protected Chat getChat() {
        return chat;
    }

    private Chat getChat(Long chatId, boolean isPublic) {
        Chat res = new Chat();
        setField(res, "id", chatId);
        if (isPublic) {
            setField(res, "type", "supergroup");
        } else {
            setField(res, "type", "private");
        }
        return res;
    }

    protected Long getChatId() {
        return chat.getId();
    }

    @SuppressWarnings("unchecked")
    private <P extends Serializable, T extends BotApiMethod<P>> T popMethod(
            Class<T> clazz, Predicate<T> filter) {
        final MethodCall<?> methodCall =
                sender.getMethodCalls().stream()
                        .filter(obj -> clazz.isInstance(obj.getRequest()))
                        .filter(obj -> filter.test((T) obj.getRequest()))
                        .findFirst()
                        .orElseThrow(getAssertionFailedErrorSupplier(clazz));
        assertTrue(sender.getMethodCalls().remove(methodCall));
        return (T) methodCall.getRequest();
    }

    private Supplier<AssertionFailedError> getAssertionFailedErrorSupplier(Class<?> clazz) {
        return () ->
                new AssertionFailedError(
                        String.format(
                                "Wrong type expected: '%s' but [%s]",
                                clazz.getSimpleName(), sender.getMethodCalls()));
    }

    @SuppressWarnings("unchecked")
    private <P extends Serializable, T extends BotApiMethod<P>> MethodCall<P> popMethodCall(
            Class<T> clazz, Predicate<T> filter) {
        final MethodCall<?> methodCall =
                sender.getMethodCalls().stream()
                        .filter(obj -> clazz.isInstance(obj.getRequest()))
                        .filter(obj -> filter.test((T) obj.getRequest()))
                        .findFirst()
                        .orElseThrow(getAssertionFailedErrorSupplier(clazz));
        assertTrue(sender.getMethodCalls().remove(methodCall));
        return (MethodCall<P>) methodCall;
    }

    protected AssertEditMessage popEditMessage() {
        final EditMessageText message =
                popMethodCall(
                                EditMessageText.class,
                                m -> getChatId().toString().equals(m.getChatId()))
                        .getRequest();
        return new AssertEditMessage(this, message);
    }

    protected AssertEditMessageReplyMarkup popEditMessageReplyMarkup() {
        final EditMessageReplyMarkup edit =
                popMethodCall(
                                EditMessageReplyMarkup.class,
                                m -> getChatId().toString().equals(m.getChatId()))
                        .getRequest();
        assertEquals(getChatId().toString(), edit.getChatId());
        return new AssertEditMessageReplyMarkup(this, edit);
    }

    protected AssertSendMessage popSendMessage() {
        final MethodCall<Message> call =
                popMethodCall(SendMessage.class, m -> getChatId().toString().equals(m.getChatId()));
        SendMessage sendMessage = call.getRequest();
        assertEquals("Wrong chat expectation", getChatId().toString(), sendMessage.getChatId());
        return new AssertSendMessage(this, sendMessage, call.getResponse());
    }

    protected AssertAnswerCallbackQuery popAnswerCallbackQuery() {
        AnswerCallbackQuery query = popMethod(AnswerCallbackQuery.class, a -> true);
        assertEquals(lastCallbackQueryId.toString(), query.getCallbackQueryId());
        return new AssertAnswerCallbackQuery(query);
    }

    protected AssertDeleteMessage popDeleteMessage() {
        final MethodCall<Boolean> deleteCall =
                popMethodCall(
                        DeleteMessage.class, m -> getChatId().toString().equals(m.getChatId()));
        DeleteMessage deleteMessage = deleteCall.getRequest();
        assertEquals(deleteMessage.getChatId(), getChatId().toString());
        assertNotNull(deleteMessage.getMessageId());
        return new AssertDeleteMessage(
                deleteMessage, deleteCall.getMessage(), deleteCall.getResponse());
    }

    protected AssertEditMessageText popEditMessageText() {
        EditMessageText editMessageText =
                popMethod(EditMessageText.class, m -> getChatId().toString().equals(m.getChatId()));
        return new AssertEditMessageText(editMessageText);
    }
}
