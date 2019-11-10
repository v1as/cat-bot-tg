package ru.v1as.tg.cat.messages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoadDumpMessageHandler implements MessageHandler {

    private final UnsafeAbsSender sender;

    @Override
    public void handle(Message message, Chat chat, User user) {
        if (!message.hasDocument()) {
            return;
        }
        //        if (!id.equals(user.getId()) || !chatId.equals(chat.getId()) ||
        // !message.hasDocument()) {
        //            return;
        //        }
        final Document document = message.getDocument();
        if (document.getFileName().endsWith(".sql")) {
            final GetFile getFile = new GetFile().setFileId(document.getFileId());
            final File file = sender.executeUnsafe(getFile);
            log.info("File downloaded" + file.getFilePath());
            sender.executeUnsafe(new SendMessage(chat.getId(), "Файл загружен."));
        }
    }
}
