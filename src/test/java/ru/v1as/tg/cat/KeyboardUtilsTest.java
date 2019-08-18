package ru.v1as.tg.cat;

import static org.junit.Assert.*;
import static ru.v1as.tg.cat.KeyboardUtils.inlineKeyboardMarkup;

import java.util.List;
import org.apache.http.util.Asserts;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class KeyboardUtilsTest {

    @Test
    public void should_create_keyboard_with_one_button() {
        InlineKeyboardMarkup keyboard = inlineKeyboardMarkup("name", "data");
        assertEquals(1, keyboard.getKeyboard().size());
        assertEquals(1, keyboard.getKeyboard().get(0).size());
    }

    @Test
    public void should_create_keyboard_with_three_button() {
        InlineKeyboardMarkup keyboard =
                inlineKeyboardMarkup(
                        "name", "data",
                        "name2", "data2",
                        "name3", "data3");
        assertEquals(1, keyboard.getKeyboard().size());
        List<InlineKeyboardButton> buttons = keyboard.getKeyboard().get(0);

        assertEquals(3, buttons.size());

        assertEquals("name", buttons.get(0).getText());
        assertEquals("data", buttons.get(0).getCallbackData());
        assertEquals("name2", buttons.get(1).getText());
        assertEquals("data2", buttons.get(1).getCallbackData());
        assertEquals("name3", buttons.get(2).getText());
        assertEquals("data3", buttons.get(2).getCallbackData());
    }
}
