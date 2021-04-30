package ru.v1as.tg.cat.service;

import static ru.v1as.tg.cat.service.ChatParam.PICTURE_POLL_ENABLED;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.jpa.dao.ChatDetailsDao;

public class PicturePollEnabledParamTest extends AbstractCatBotTest {

    @Autowired ChatDetailsDao chatDetailsDao;
    @Autowired ChatParamResource chatParamResource;

    @Test
    public void shouldEnableAndDisablePollsInChat() {
        chatParamResource.param(inPublic.getId(), bob.getUserId(), PICTURE_POLL_ENABLED, true);

        bob.inPublic().sendPhotoMessage();
        bob.inPublic().getSendMessage().assertContainText("Это кот?");
        assertMethodsQueueIsEmpty();

        chatParamResource.param(inPublic.getId(), bob.getUserId(), PICTURE_POLL_ENABLED, false);
        bob.inPublic().sendPhotoMessage();
        assertMethodsQueueIsEmpty();
    }
}
