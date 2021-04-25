package ru.v1as.tg.cat.commands.impl;

import java.util.concurrent.TimeUnit;
import org.junit.Test;
import ru.v1as.tg.cat.AbstractCatBotTest;

public class SendMessageCommandTest extends AbstractCatBotTest {

    @Test
    public void send_empty_chat_id() {
        bob.inPrivate().sendCommand("/send");

        bob.inPrivate().getSendMessage().assertText("Не смог распарсить id чата (первый аргумент)");
    }

    @Test
    public void send_to_all_chats() {
        bob.inPrivate().sendCommand("/send all");

        bob.inPrivate().getSendMessage().assertText("Какое сообщение вы хотите отравить в чат 'ALL:2'?");

        bob.inPrivate().sendTextMessage("Hello world!");

        bob.inPrivate()
            .getSendMessageToSend()
            .assertText(
                "Вы хотите отправить следующее сообщение в чат 'ALL:2'?\n\nHello world!")
            .findCallbackToSend("Да")
            .send();
        bob.inPrivate().getEditMessageReplyMarkup();
        clock.skip(3, TimeUnit.SECONDS);

        bob.inPublic().getSendMessage().assertText("Hello world!");
        bob.inPublic(inAnotherPublic).getSendMessage().assertText("Hello world!");
        bob.inPrivate().getSendMessage().assertText("Сообщений в чаты отправлено 2");
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
                        "Вы хотите отправить следующее сообщение в чат 'Public test chat'?\n\nHello world!")
                .findCallbackToSend("Да")
                .send();
        bob.inPrivate().getEditMessageReplyMarkup();
        bob.inPublic().getSendMessage().assertText("Hello world!");
        clock.skip(2, TimeUnit.SECONDS);
        bob.inPrivate().getSendMessage().assertText("Сообщений в чаты отправлено 1");
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
                        "Вы хотите отправить следующее сообщение в чат 'Public test chat'?\n\nHello world!")
                .findCallbackToSend("Нет")
                .send();
        bob.inPrivate().getEditMessageReplyMarkup();
        bob.inPrivate().getSendMessage().assertText("Сообщение не отправлено");
    }
}
