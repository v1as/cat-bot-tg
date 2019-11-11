package ru.v1as.tg.cat;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.telegram.telegrambots.meta.api.objects.User;

public class Const {

    public static final String LINE = "\n";
    private static String botName;
    private static Set<String> adminUserNames;
    private static String botToken;

    public static String getBotName() {
        if (botName == null) {
            throw new IllegalStateException("This variable is not init yet.");
        }
        return botName;
    }

    public static Set<String> getAdminUsername() {
        if (adminUserNames == null) {
            throw new IllegalStateException("This variable is not init yet.");
        }
        return adminUserNames;
    }

    public static void setBotName(String botName) {
        if (Const.botName == null) {
            Const.botName = botName;
        } else {
            throw new IllegalStateException("This variable is already defined");
        }
    }

    public static void setAdminUserName(String admins) {
        if (Const.adminUserNames == null) {
            Const.adminUserNames =
                    Arrays.stream(admins.split("[;,\\s]")).collect(Collectors.toSet());
        } else {
            throw new IllegalStateException("This variable is already defined");
        }
    }

    public static void onlyForAdminCheck(User user) {
        if (isEmpty(user.getUserName()) || !adminUserNames.contains(user.getUserName())) {
            throw new OnlyForAdmins();
        }
    }

    public static void setBotToken(String botToken) {
        if (Const.botToken == null) {
            Const.botToken = botToken;
        } else {
            throw new IllegalStateException("This variable is already defined");
        }
    }

    public static String getBotToken() {
        return botToken;
    }

    public static class OnlyForAdmins extends RuntimeException {}

    public static String getUrlFileDocument(String filePath) {
        return String.format("https://api.telegram.org/file/bot%s/%s", botToken, filePath);
    }
}
