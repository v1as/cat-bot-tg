package ru.v1as.tg.cat.commands.impl;

import org.junit.Test;
import ru.v1as.tg.cat.AbstractCatBotTest;

public class SendMessageCommandTest extends AbstractCatBotTest {

    @Test
    public void send_empty_chat_id() {
        bob.inPrivate().sendCommand("/send");

        bob.inPrivate().getSendMessage().assertText("Не смог распарсить id чата (первый аргумент)");
    }

    @Test
    public void no_such_chat_id() {
        bob.inPrivate().sendCommand("/send 123");

        bob.inPrivate().getSendMessage().assertText("Чат с таким id не найден");
    }

    @Test
    public void happy_path() {
        bob.inPrivate().sendCommand("/send 100");

        bob.inPrivate()
                .getSendMessage()
                .assertText("Какое сообщение вы хотите отравить в чат 'Public test chat'?");

        bob.inPrivate().sendTextMessage("Hello world!");

        bob.inPrivate()
                .getSendMessageToSend()
                .assertText(
                        "Вы хотите отправить следующее сообщение в чат 'Public test chat'?\nHello world!")
                .findCallbackToSend("Да")
                .send();
        bob.inPrivate().getEditMessageReplyMarkup();
        bob.inPrivate().getSendMessage().assertText("Сообщение отправлено");
        bob.inPublic().getSendMessage().assertText("Hello world!");
    }
    @Test
    public void discard_sending() {
        bob.inPrivate().sendCommand("/send 100");

        bob.inPrivate()
                .getSendMessage()
                .assertText("Какое сообщение вы хотите отравить в чат 'Public test chat'?");

        bob.inPrivate().sendTextMessage("Hello world!");

        bob.inPrivate()
                .getSendMessageToSend()
                .assertText(
                        "Вы хотите отправить следующее сообщение в чат 'Public test chat'?\nHello world!")
                .findCallbackToSend("Нет")
                .send();
        bob.inPrivate().getEditMessageReplyMarkup();
        bob.inPrivate().getSendMessage().assertText("Сообщение не отправлено");
    }


}
