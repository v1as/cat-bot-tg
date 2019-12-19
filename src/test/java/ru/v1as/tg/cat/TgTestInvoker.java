package ru.v1as.tg.cat;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface TgTestInvoker {

    void sendCallback(Integer msgId, String data);

    Message sendCommand(String text);

}
