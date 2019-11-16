package ru.v1as.tg.cat.callbacks.phase.poll;

import static ru.v1as.tg.cat.callbacks.phase.poll.PollChoiceType.LINK;

import java.util.UUID;
import java.util.function.Consumer;
import lombok.Value;
import ru.v1as.tg.cat.service.Const;

@Value
public class PollChoice {

    private static final String START_URL_FORMAT = "https://telegram.me/%s?start=%s";

    private final String uuid;
    private final PollChoiceType type;
    private final String text;
    private final String url;
    private final Consumer<ChooseContext> callable;

    public static PollChoice startCommandUrl(String text) {
        String uuid = UUID.randomUUID().toString();
        return new PollChoice(
                uuid, LINK, text, String.format(START_URL_FORMAT, Const.getBotName(), uuid), null);
    }
}
