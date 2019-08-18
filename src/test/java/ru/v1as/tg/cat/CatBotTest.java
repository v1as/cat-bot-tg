package ru.v1as.tg.cat;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.ScoreData.ScoreLine;

public class CatBotTest {

    private CatBot bot = new CatBot(mock(ScoreData.class));
    private Integer messageId = 0;

    //    @Before
    //    public void before() {
    //        bot.setSender(mock(AbsSender.class));
    //    }

    @Test
    public void test1() {
        sendPhotoMessage();
    }

    private void sendPhotoMessage() {
        bot.onUpdateReceived(getMessage());
    }

    private User getUser() {
        return mock(User.class);
    }

    private Chat getChat() {
        Chat chat = mock(Chat.class);
        when(chat.getId()).thenReturn(getChatId());
        when(chat.isSuperGroupChat()).thenReturn(true);
        return chat;
    }

    private Update getMessage() {
        Message message = mock(Message.class);
        when(message.getMessageId()).thenReturn(messageId++);
        when(message.isUserMessage()).thenReturn(true);
        Chat chat = getChat();
        when(message.getChat()).thenReturn(chat);
        Update update = mock(Update.class);
        when(update.getMessage()).thenReturn(message);
        when(update.hasMessage()).thenReturn(true);
        return update;
    }

    private Long getChatId() {
        return 0L;
    }

    @Test
    public void scoreTest() {
        String tempFile = "tempFile";
        ScoreData scoreData = new ScoreData(tempFile);
        new File(tempFile).delete();
        scoreData.init();

        CatRequest catRequest = getCatRequest();
        catRequest.finish(CatRequestVote.CAT1);
        scoreData.save(catRequest);

        CatRequest catRequest2 = getCatRequest();
        catRequest2.finish(CatRequestVote.CAT1);
        scoreData.save(catRequest2);

        scoreData.flush();
        scoreData.init();
        List<ScoreLine> scoreLines = scoreData.getScore(getChatId());
        assertEquals(2, scoreLines.size());
    }

    private CatRequest getCatRequest() {
        return new CatRequest(
                getMessage().getMessage(),
                new UserData(getUser()),
                new ChatData(getChat(), false),
                LocalDateTime.now());
    }
}
