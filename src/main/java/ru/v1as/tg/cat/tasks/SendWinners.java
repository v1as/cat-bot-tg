package ru.v1as.tg.cat.tasks;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.MedalsListBuilder;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.ChatData;
import ru.v1as.tg.cat.model.DbData;
import ru.v1as.tg.cat.model.LongProperty;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@RequiredArgsConstructor
public class SendWinners implements Runnable {

    private final UnsafeAbsSender sender;
    private final DbData<CatChatData> data;
    private final ScoreData scoreData;

    @Override
    public void run() {
        log.info("Start sending winners data...");
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        for (ChatData chat : data.getChats()) {
            try {
                StringBuilder text = new StringBuilder();
                LongProperty[] topPlayers =
                        scoreData
                                .getWinnersStream(chat.getChatId(), yesterday)
                                .toArray(LongProperty[]::new);
                List<String> result = new MedalsListBuilder().getPlayersWithMedals(topPlayers);

                if (result.isEmpty()) {
                    continue;
                }
                result.forEach(text::append);
                sender.executeUnsafe(
                        new SendMessage().setChatId(chat.getChatId()).setText(text.toString()));
                log.info("Winners data '{}' was sent to chat {}", text, chat);
            } catch (Exception ex) {
                log.error("Error while send winners to the chat " + chat, ex);
            }
        }
    }
}
