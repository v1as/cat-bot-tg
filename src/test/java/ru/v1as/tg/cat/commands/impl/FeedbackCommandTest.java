package ru.v1as.tg.cat.commands.impl;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import ru.v1as.tg.cat.tg.TestUserChat;

public class FeedbackCommandTest extends AbstractCatBotTestWithPoll {

    @Test
    public void positive_scenario() {
        TestUserChat maryPrivate = mary.inPrivate();
        maryPrivate.sendCommand("/feedback");
        maryPrivate.getSendMessage().assertText("Что вы хотите сообщить разработчикам?");
        maryPrivate.sendTextMessage("lol");
        maryPrivate
                .getSendMessageToSend()
                .assertText("Вы хотите отправить следующее сообщение разработчикам?\n\n lol")
                .findCallbackToSend("Да")
                .send();

        bob.inPrivate()
                .getSendMessage()
                .assertText("От пользователя User(mary:mary 2:2) пришло сообщение: \n" + " lol");
        assertFalse(maryPrivate.getEditMessageReplyMarkup().hasCallbacks());
        maryPrivate.getSendMessage().assertText("Сообщение отправлено");
    }

    @Test
    public void no_confirmation_test() {
        mary.inPrivate().sendCommand("/feedback");
        mary.inPrivate().getSendMessage().assertText("Что вы хотите сообщить разработчикам?");
        mary.inPrivate().sendTextMessage("lol");
        mary.inPrivate()
                .getSendMessageToSend()
                .assertText("Вы хотите отправить следующее сообщение разработчикам?\n\n lol")
                .findCallbackToSend("Нет")
                .send();

        assertFalse(mary.inPrivate().getEditMessageReplyMarkup().hasCallbacks());
        mary.inPrivate().getSendMessage().assertText("Сообщение не отправлено");
    }

}
