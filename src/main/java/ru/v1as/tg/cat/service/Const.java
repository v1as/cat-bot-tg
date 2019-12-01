package ru.v1as.tg.cat.service;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.model.TgUser;

@Slf4j
@Component
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

    @Value("${tg.bot.username:}")
    public void setBotName(String botName) {
        Const.botName = botName;
    }

    public static void onlyForAdminCheck(TgUser user) {
        if (isEmpty(user.getUserName()) || !adminUserNames.contains(user.getUserName())) {
            throw new OnlyForAdmins();
        }
    }

    public static Set<String> getAdminUserNames() {
        return adminUserNames;
    }

    public static void setAdminUserNames(Set<String> adminUserNames) {
        Const.adminUserNames = adminUserNames;
    }

    public static String getUrlFileDocument(String filePath) {
        return String.format("https://api.telegram.org/file/bot%s/%s", botToken, filePath);
    }

    @PostConstruct
    public void init() {
        log.info("Initialized");
    }

    @Value("${tg.bot.token}")
    public void setBotToken(String botToken) {
        Const.botToken = botToken;
    }

    @Value("${tg.bot.admin_username:}")
    public void setAdminUserName(String admins) {
        Const.adminUserNames = Arrays.stream(admins.split("[;,\\s]")).collect(Collectors.toSet());
    }

    public static class OnlyForAdmins extends RuntimeException {}
}
