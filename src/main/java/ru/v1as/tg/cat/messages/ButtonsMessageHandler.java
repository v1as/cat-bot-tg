package ru.v1as.tg.cat.messages;

import static java.util.stream.Stream.concat;
import static ru.v1as.tg.cat.tg.KeyboardUtils.replyKeyboardMarkup;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.ResourceTexts;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class ButtonsMessageHandler implements MessageHandler {

    private final TgSender sender;
    private final ShopService shopService;
    private final Map<String, ButtonMenu> requests = new HashMap<>();
    private final Map<String, ButtonCallback> callbacks = new HashMap<>();
    private final ResourceTexts texts;

    public static final String BACK = "◀️ Назад";

    public static final String GO_TO_THE_CITY = "\uD83C\uDFD9 Пойти в город";
    private String shopText;

    @PostConstruct
    public void init() {
        shopText = texts.load("shop");
        final ButtonMenu root =
                buttonMenu()
                        .message("Что делаем?")
                        .isRoot(true)
                        .button(
                                GO_TO_THE_CITY,
                                buttonMenu()
                                        .message("Куда пойдём?")
                                        .button(
                                                "\uD83D\uDECD Магазин",
                                                buttonMenu()
                                                        .message(shopText)
                                                        .callback(
                                                                "\uD83D\uDC1F Кошачье угощение",
                                                                shopService::buyCatBite)
                                                        .callback(
                                                                "\uD83E\uDDEA Зелье концентрации",
                                                                shopService::buyConcentrationPotion)
                                                        .callback(
                                                                "\uD83D\uDC89 Лекарство от бешенства",
                                                                shopService::buyRabiesMedicine)
                                                        .build())
                                        .build())
                        .build();
        requests.put(BACK, root);
        Stack<ButtonMenu> buttons = new Stack<>();
        buttons.push(root);
        while (!buttons.empty()) {
            final ButtonMenu button = buttons.pop();
            requests.putAll(button.buttons);
            callbacks.putAll(button.callbacks);
            button.buttons.values().forEach(buttons::push);
        }
    }

    private ButtonMenu.ButtonMenuBuilder buttonMenu() {
        return new ButtonMenu.ButtonMenuBuilder();
    }

    @Override
    public void handle(Message message, TgChat chat, TgUser user) {
        if (!chat.isUserChat()) {
            return;
        }
        final String text = message.getText();
        final ButtonMenu menu = requests.get(text);
        if (menu != null) {
            log.info("User {} choose button {}", user.getUsernameOrFullName(), text);
            final String[] buttonsTest = menu.getButtonsTest();
            sender.execute(
                    new SendMessage(chat.getId(), menu.message)
                            .setReplyMarkup(replyKeyboardMarkup(buttonsTest)));
            return;
        }
        final ButtonCallback callback = callbacks.get(text);
        if (callback != null) {
            log.info("User {} choose button {}", user.getUsernameOrFullName(), text);
            callback.process(message, chat, user);
        }
    }

    private interface ButtonCallback {
        void process(Message message, TgChat chat, TgUser user);
    }

    @Builder
    private static class ButtonMenu {

        String message;
        @Singular Map<String, ButtonMenu> buttons;
        @Singular Map<String, ButtonCallback> callbacks;
        boolean isRoot;

        String[] getButtonsTest() {
            return concat(
                            concat(buttons.keySet().stream(), callbacks.keySet().stream()),
                            isRoot ? Stream.of() : Stream.of(BACK))
                    .toArray(String[]::new);
        }
    }
}
