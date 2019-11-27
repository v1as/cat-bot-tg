package ru.v1as.tg.cat.utils;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

@RequiredArgsConstructor
public class AssertAnswerCallbackQuery {

    private final AnswerCallbackQuery answerCallbackQuery;

    public AssertAnswerCallbackQuery assertText(String text) {
        Assert.assertEquals(text, answerCallbackQuery.getText());
        return this;
    }

}
