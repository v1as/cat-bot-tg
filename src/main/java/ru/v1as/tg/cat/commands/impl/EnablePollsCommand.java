package ru.v1as.tg.cat.commands.impl;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.jpa.dao.ChatDetailsDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatDetailsEntity;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.tg.TgSender;

@Component
public class EnablePollsCommand extends AbstractCommand {

    private final ChatDetailsDao chatDetailsDao;
    private final TgSender sender;

    public EnablePollsCommand(ChatDetailsDao chatDetailsDao, TgSender sender) {
        super(cfg().onlyPublicChat(true).commandName("enable_polls").onlyAdmin(true));
        this.chatDetailsDao = chatDetailsDao;
        this.sender = sender;
    }

    @Override
    public String getCommandDescription() {
        return "Включить опросы 'Это кот?' на все фото в чате.";
    }

    @Override
    public void process(TgCommandRequest command, TgChat chat, TgUser user) {
        final ChatDetailsEntity details = chatDetailsDao.findByChatId(chat.getId());
        if (details.isCatPollEnabled()) {
            sender.message(chat, "Создание опросов уже включено");
        } else {
            details.setCatPollEnabled(true);
            chatDetailsDao.save(details);
            sender.message(chat, "Создание опросов теперь включено");
        }
    }
}
