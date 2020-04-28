package ru.v1as.tg.cat.messages;

import static java.util.stream.Stream.concat;
import static ru.v1as.tg.cat.messages.MessageHandler.MessageHandlerResult.BREAK;
import static ru.v1as.tg.cat.messages.MessageHandler.MessageHandlerResult.SKIPPED;
import static ru.v1as.tg.cat.messages.ShopService.CAT_BITE_PRICE;
import static ru.v1as.tg.cat.messages.ShopService.CONCENTRATION_POTION_PRICE;
import static ru.v1as.tg.cat.messages.ShopService.RABIES_MEDICINE_PRICE;
import static ru.v1as.tg.cat.messages.ShopService.prc;
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
import ru.v1as.tg.cat.CatBotData;
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
    private final CatBotData catBotData;
    private final ResourceTexts texts;

    public static final String BACK = "◀️ Назад";

    public static final String GO_TO_THE_CITY = "\uD83C\uDFD9 Пойти в город";
    public static final String[] ROOT = {GO_TO_THE_CITY};
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
                                        .button("\uD83D\uDECD Магазин", getShopMenu())
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

    private ButtonMenu getShopMenu() {
        return buttonMenu()
                .message(shopText)
                .callback(
                        "\uD83D\uDC1F Кошачье угощение" + prc(CAT_BITE_PRICE),
                        shopService::buyCatBite)
                .callback(
                        "\uD83E\uDDEA Зелье концентрации" + prc(CONCENTRATION_POTION_PRICE),
                        shopService::buyConcentrationPotion)
                .callback(
                        "\uD83D\uDC89 Лекарство от бешенства" + prc(RABIES_MEDICINE_PRICE),
                        shopService::buyRabiesMedicine)
                .build();
    }

    private ButtonMenu.ButtonMenuBuilder buttonMenu() {
        return new ButtonMenu.ButtonMenuBuilder();
    }

    @Override
    public MessageHandlerResult handle(Message message, TgChat chat, TgUser user) {
        if (!chat.isUserChat() || catBotData.inPhase(user.getId())) {
            return SKIPPED;
        }
        final String text = message.getText();
        final ButtonMenu menu = requests.get(text);
        if (menu != null) {
            log.info("User choose button '{}'", text);
            final String[] buttonsTest = menu.getButtonsTest();
            sender.execute(
                    new SendMessage(chat.getId(), menu.message)
                            .setReplyMarkup(replyKeyboardMarkup(buttonsTest)));
            return BREAK;
        }
        final ButtonCallback callback = callbacks.get(text);
        if (callback != null) {
            log.info("User choose button '{}'", text);
            callback.process(message, chat, user);
            return BREAK;
        }
        return SKIPPED;
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
