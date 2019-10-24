package ru.v1as.tg.cat.commands.impl;

import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.callbacks.phase.AbstractPhase;
import ru.v1as.tg.cat.model.ChatData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

public class TestPhase extends AbstractPhase {

    public TestPhase(UnsafeAbsSender sender, TgCallbackProcessor callbackProcessor, ChatData chat) {
        super(sender, callbackProcessor, chat);
    }

    @Override
    public void open() {
        sendMessage("Привет");
        poll("Тест").choice("Выбор 1", this::choose1).choice("Выбор 2", this::choose2).send();
    }

    private void choose2() {
        sendMessage("Вы выбрали вариант 2");
        close();
    }

    private void choose1() {
        sendMessage("Вы выбрали вариант 1");
        close();
    }

}
