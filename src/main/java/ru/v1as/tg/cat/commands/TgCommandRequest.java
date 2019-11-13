package ru.v1as.tg.cat.commands;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import static org.apache.http.util.TextUtils.isEmpty;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.telegram.telegrambots.meta.api.objects.Message;

@Value
@RequiredArgsConstructor
public class TgCommandRequest {

    private static final Pattern PATTERN_WITH_NAME =
            Pattern.compile("/([\\w0-9_]+)[@]?([\\w0-9_]+)?(\\s)?(.*)");

    Message message;
    String name;
    String botName;
    List<String> arguments;

    public static TgCommandRequest parse(Message msg) {
        final String text = msg.getText();
        Matcher matcher = PATTERN_WITH_NAME.matcher(text);
        checkArgument(matcher.matches(), "Unsupported command format: " + text);
        String name = matcher.group(1);
        String botName = matcher.group(2);
        String argsStr = matcher.group(4);
        List<String> args = isEmpty(argsStr) ? emptyList() : Arrays.asList(argsStr.split(" "));
        return new TgCommandRequest(msg, name, botName, args);
    }

    public String getFirstArgument() {
        return getArgument(0);
    }

    public String getSecondArgument() {
        return getArgument(1);
    }

    public String getThirdArgument() {
        return getArgument(2);
    }

    public String getArgument(int index) {
        checkArgument(index >= 0, "Argument index should be not negative");
        return arguments.size() > index ? arguments.get(index) : null;
    }
}
