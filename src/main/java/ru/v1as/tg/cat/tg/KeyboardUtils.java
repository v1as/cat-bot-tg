package ru.v1as.tg.cat.tg;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.util.Asserts;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public class KeyboardUtils {

    private KeyboardUtils() {}

    public static InlineKeyboardMarkup inlineKeyboardMarkup(String... buttonAndData) {
        Asserts.check(buttonAndData.length % 2 == 0, "Arguments amount should be even.");
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (int i = 0; i < buttonAndData.length / 2; i++) {
            rowInline.add(
                    new InlineKeyboardButton()
                            .setText(buttonAndData[2 * i])
                            .setCallbackData(buttonAndData[2 * i + 1]));
        }
        if (!rowInline.isEmpty()) {
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public static ReplyKeyboardMarkup replyKeyboardMarkup(String... buttons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        final List<KeyboardRow> keyboard = new ArrayList<>(buttons.length);
        for (String text : buttons) {
            KeyboardRow row = new KeyboardRow();
            final KeyboardButton keyboardButton = new KeyboardButton(text);
            row.add(keyboardButton);
            keyboard.add(row);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public static EditMessageReplyMarkup getUpdateButtonsMsg(
            Chat chat, Integer messageId, InlineKeyboardMarkup pollButtons) {
        return new EditMessageReplyMarkup()
                .setChatId(chat.getId())
                .setMessageId(messageId)
                .setReplyMarkup(pollButtons);
    }

    public static EditMessageReplyMarkup getUpdateButtonsMsg(
            Message message, InlineKeyboardMarkup pollButtons) {
        return new EditMessageReplyMarkup()
                .setChatId(message.getChatId())
                .setMessageId(message.getMessageId())
                .setReplyMarkup(pollButtons);
    }

    public static EditMessageReplyMarkup clearButtons(Message message) {
        return new EditMessageReplyMarkup()
                .setChatId(message.getChatId())
                .setMessageId(message.getMessageId())
                .setReplyMarkup(new InlineKeyboardMarkup());
    }

    public static EditMessageText editMessageText(Message msg, String newText) {
        return new EditMessageText()
                .setChatId(msg.getChatId())
                .setMessageId(msg.getMessageId())
                .setText(newText);
    }

    public static DeleteMessage deleteMsg(Message message) {
        return new DeleteMessage(message.getChatId(), message.getMessageId());
    }
}
