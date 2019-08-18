package ru.v1as.tg.cat;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.http.util.Asserts;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@UtilityClass
class KeyboardUtils {

    static InlineKeyboardMarkup inlineKeyboardMarkup(String... buttonAndData) {
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
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
}
