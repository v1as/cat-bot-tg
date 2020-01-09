package ru.v1as.tg.cat.tg;

import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import junit.framework.AssertionFailedError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.MethodCall;
import ru.v1as.tg.cat.TestAbsSender;
import ru.v1as.tg.cat.utils.AssertAnswerCallbackQuery;
import ru.v1as.tg.cat.utils.AssertDeleteMessage;
import ru.v1as.tg.cat.utils.AssertEditMessageReplyMarkup;
import ru.v1as.tg.cat.utils.AssertEditMessageText;
import ru.v1as.tg.cat.utils.AssertSendMessage;

@RequiredArgsConstructor
public class TestChat extends TgTestObject {

    @Getter private final TestAbsSender sender;
    @Getter private final TgUpdateProcessor updateProcessor;
    @Getter private final Chat chat;
    protected Map<Integer, Message> messages = new HashMap<>();

    @Setter @Getter private Integer messageId = 0;

    @Override
    protected void registerMessage(Integer msgId, Message message) {
        this.messages.put(msgId, message);
    }

    protected Message sendCommand(User user, String text) {
        Update update = getMessageUpdate(chat, user);
        final Message message = update.getMessage();
        setField(message, "text", text);
        final MessageEntity messageEntity = new MessageEntity();
        setField(messageEntity, "type", EntityType.BOTCOMMAND);
        setField(messageEntity, "offset", 0);
        setField(message, "entities", singletonList(messageEntity));
        updateProcessor.onUpdateReceived(update);
        return update.getMessage();
    }

    protected Message sendTextMessage(User user, String text) {
        Update update = getMessageUpdate(chat, user);
        setField(update.getMessage(), "text", text);
        updateProcessor.onUpdateReceived(update);
        return update.getMessage();
    }

    protected Message sendPhotoMessage(User user) {
        Update update = getMessageUpdate(chat, user);
        setField(update.getMessage(), "photo", singletonList(new PhotoSize()));
        updateProcessor.onUpdateReceived(update);
        return update.getMessage();
    }

    protected void sendCallback(User user, Integer msgId, String data) {
        Update update = getCallbackUpdate(user, msgId, data);
//        setField(update, "message", findMessage(msgId));
        updateProcessor.onUpdateReceived(update);
    }

    protected <P extends Serializable, T extends BotApiMethod<P>> T getMethod(
            Class<T> clazz, Predicate<T> filter) {
        return getMethodCall(clazz, filter).getRequest();
    }

    protected <P extends Serializable, T extends BotApiMethod<P>> MethodCall<P> getMethodCall(
            Class<T> clazz, Predicate<T> filter) {
        final MethodCall<P> methodCall = findMethodCall(clazz, filter);
        assertTrue(sender.getMethodCalls().remove(methodCall));
        return methodCall;
    }

    @SuppressWarnings("unchecked")
    protected <P extends Serializable, T extends BotApiMethod<P>> MethodCall<P> findMethodCall(
            Class<T> clazz, Predicate<T> filter) {
        return (MethodCall<P>)
                sender.getMethodCalls().stream()
                        .filter(obj -> clazz.isInstance(obj.getRequest()))
                        .filter(obj -> filter.test((T) obj.getRequest()))
                        .findFirst()
                        .orElseThrow(getAssertionFailedErrorSupplier(clazz));
    }

    private Supplier<AssertionFailedError> getAssertionFailedErrorSupplier(Class<?> clazz) {
        return () ->
                new AssertionFailedError(
                        String.format(
                                "Wrong type expected: '%s' but [%s]",
                                clazz.getSimpleName(), sender.getMethodCalls()));
    }

    public AssertAnswerCallbackQuery getAnswerCallbackQuery() {
        AnswerCallbackQuery query =
                getMethod(
                        AnswerCallbackQuery.class,
                        a -> getMessageId().toString().equals(a.getCallbackQueryId()));
        return new AssertAnswerCallbackQuery(query);
    }

    public AssertDeleteMessage getDeleteMessage() {
        final MethodCall<Boolean> deleteCall =
                getMethodCall(DeleteMessage.class, m -> getChatIdStr().equals(m.getChatId()));
        DeleteMessage deleteMessage = deleteCall.getRequest();
        assertNotNull(deleteMessage.getMessageId());
        return new AssertDeleteMessage(
                deleteMessage, deleteCall.getMessage(), deleteCall.getResponse());
    }

    public AssertEditMessageText getEditMessage() {
        EditMessageText editMessageText =
                getMethod(EditMessageText.class, m -> getChatIdStr().equals(m.getChatId()));
        return new AssertEditMessageText(editMessageText);
    }

    public AssertEditMessageReplyMarkup getEditMessageReplyMarkup() {
        EditMessageReplyMarkup editMessageText =
                getMethod(EditMessageReplyMarkup.class, m -> getChatIdStr().equals(m.getChatId()));
        return new AssertEditMessageReplyMarkup(editMessageText);
    }

    protected String getChatIdStr() {
        return chat.getId().toString();
    }

    public AssertSendMessage getSendMessage() {
        final MethodCall<Message> call =
                getMethodCall(SendMessage.class, m -> getChatIdStr().equals(m.getChatId()));
        SendMessage sendMessage = call.getRequest();
        return new AssertSendMessage(sendMessage, call.getResponse());
    }

    public static TestChat publicTestChat(
            TestAbsSender sender, TgUpdateProcessor updateProcessor, Long chatId) {
        return new TestChat(sender, updateProcessor, getChat(chatId, true));
    }

    @Override
    public Message findMessage(Integer msgId) {
        return messages.get(msgId);
    }

    @Override
    public Integer incrementId() {
        return ++messageId;
    }

    public Message produceMessage(String text, User user) {
        final Integer msgId = incrementId();
        final Message msg = getMessage(chat, user, msgId);
        setField(msg, "text", text);
        return msg;
    }

    public Long getId() {
        return chat.getId();
    }
}
