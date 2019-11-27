package ru.v1as.tg.cat.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

@RequiredArgsConstructor
public class AssertAnswerCallbackQuery {

    private final AnswerCallbackQuery answerCallbackQuery;

    public AssertAnswerCallbackQuery assertText(String text) {
        assertEquals(text, answerCallbackQuery.getText());
        return this;
    }

    public AssertAnswerCallbackQuery assertContainText(String value) {
        assertTrue(answerCallbackQuery.getText().contains(value));
        return this;
    }
}
