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
public class BotConfiguration {

    public static final String LINE = "\n";
    private String botName;
    private Set<String> adminUserNames;
    private String botToken;

    public String getBotName() {
        if (botName == null) {
            throw new IllegalStateException("This variable is not init yet.");
        }
        return botName;
    }

    @Value("${tg.bot.username:}")
    public void setBotName(String botName) {
        this.botName = botName;
    }

    public boolean isBotAdmin(TgUser user) {
        return !isEmpty(user.getUserName()) && adminUserNames.contains(user.getUserName());
    }

    public Set<String> getAdminUserNames() {
        return adminUserNames;
    }

    public String getUrlFileDocument(String filePath) {
        return String.format("https://api.telegram.org/file/bot%s/%s", botToken, filePath);
    }

    @PostConstruct
    public void init() {
        log.info("Initialized");
    }

    @Value("${tg.bot.token}")
    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    @Value("${tg.bot.admin_username:}")
    public void setAdminUserName(String admins) {
        this.adminUserNames = Arrays.stream(admins.split("[;,\\s]")).collect(Collectors.toSet());
    }
}
