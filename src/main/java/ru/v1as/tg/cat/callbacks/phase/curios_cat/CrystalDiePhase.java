package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.EmojiConst.DIE;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.DIE_AMULET;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.MONEY;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Component
@RequiredArgsConstructor
public class CrystalDiePhase extends AbstractCuriosCatPhase {

    @Override
    public boolean filter(TgUser user, TgChat chat) {
        final int dieCharges = paramResource.paramInt(chat.getId(), user.getId(), DIE_AMULET);
        return dieCharges == 0;
    }

    @Override
    protected void open() {
        messages(
                "Немного поблуждав на шумном рынке и утомившись, вы решили отойти в сторону и малость отдохнуть.",
                "Опершись локтём на железный исцарапанный столик, вы чувствуете, как ваши ноги гудят.",
                "Сбоку раздаётся звук отодвигающегося окна и из-под кучерявых черных волос вас встречает вопросительный взгляд.");
        poll("Похоже это продавец ожидает ваш заказ")
                .choice("Заказать чаю   (1\uD83D\uDCB0)", this::tea)
                .choice("Отвести взгляд", this::notTea)
                .send();
    }

    private void notTea(ChooseContext chooseContext) {
        messages(
                "Не сумев подавить разочарование, продавец скрывается, что-то недовольно бурча под нос.",
                "Рассматривая подсыхающую грязь на своих ногах вы раздумываете о своих дальнейших планах.");
        cat();
    }

    private void tea(ChooseContext chooseContext) {
        final CuriosCatContext phaseCtx = getPhaseContext();
        final boolean spent =
                !paramResource
                        .increment(
                                phaseCtx.getPublicChatId(), phaseCtx.getUser().getId(), MONEY, -1)
                        .isEmpty();
        if (spent) {
            messages(
                    "Железная кружка перекочевала в ваши замерзшие ладони из рук улыбчивого продавца, сверкающего золотым зубом.",
                    "Грея руки и отхлёбывая ароматный несладкий чай, вы любуетесь паром, поднимающимся над вашей кружкой.");
            cat();
        } else {
            message("Но у вас нет даже этих денег!");
            notTea(chooseContext);
        }
    }

    private void cat() {
        messages(
                "С одного из близлежащих домов вы слышите шум, словно кто-то стукнул по пустому железному чайнику.");
        poll("Пытаясь найти источник звука, вы натыкаетесь взглядом на Любопытно Кота,"
                        + " который ждет пока вы его обнаружите на одном из дождевых желобов.")
                .choice("Подойти", this::come)
                .send();
    }

    private void come(ChooseContext chooseContext) {
        messages(
                "Протискиваясь мимо людей, занятых своими покупками,"
                        + " и шаг за шагом пачкая свои только подсохшие башмаки в грязи, похожей на горячий шоколад,"
                        + " вы приближаетесь к дому, на крыше которого восседает Кот.");
        poll("Кот замечает ваше приближение и не спеша начинает идти по желобу вдоль крыши дома.")
                .choice("Поискать путь на крышу", this::wayToRoof)
                .choice("Следовать за Котом", this::followCat)
                .send();
    }

    private void wayToRoof(ChooseContext chooseContext) {
        messages("Поспешно разглядывая здание, вы находите лестницу, ведущую на крышу.");
        poll("Кот тем временем удалился на приличное расстояние.")
                .choice("Подняться на крышу", this::roof)
                .choice("Нагнать Кота", this::hastilyFollow)
                .send();
    }

    private void roof(ChooseContext chooseContext) {
        message(
                "Пока вы карабкались на верх, Любопытный Кот убежал слишком далеко, и вам его теперь не нагнать.");
        randomWay(chooseContext, this::gingerCat, this::nothing);
    }

    private void nothing(ChooseContext chooseContext) {
        message("В этот раз приключение закончилось ничем.");
        catchUpCatAndClose(NOT_CAT);
    }

    private void gingerCat(ChooseContext chooseContext) {
        message(
                "Пытаясь восстановить дыхание, вы замечаете на крыше противоположного дома рыжего вылизывающегося кота.");
        catchUpCatAndClose(CAT1);
    }

    private void hastilyFollow(ChooseContext chooseContext) {
        messages(
                "Вы спешите за удаляющимся котом, проклиная свою рассеянность.",
                "Наконец, вам удаётся нагнать его.");
        followCat(chooseContext);
    }

    private void followCat(ChooseContext chooseContext) {
        message("Вы продолжаете осторожно следовать за котом, пристально следя за ним взглядом.");
        poll("Боковым зрением вы вдруг замечаете, как что-то блестит на дороге.")
                .choice("Разглядеть находку", this::die)
                .choice("Не отвлекаться", this::catchCat)
                .timeout(TIMEOUT_LEAVE_CAT)
                .send();
    }

    private void die(ChooseContext choice) {
        messages(
                "Вы остановились чтобы разглядеть находку.",
                "Это оказался игральные кости, сделанные из материала похожего на стекло. " + DIE,
                "Пожалуй, вы заберёте их себе.");

        final TgChat publicChat = getPhaseContext().getPublicChat();
        message(
                publicChat,
                "Игрок "
                        + choice.getUser().getUsernameOrFullName()
                        + " находит игральные кости "
                        + DIE);
        paramResource.increment(
                publicChat.getId(), getPhaseContext().getUser().getId(), DIE_AMULET, 5);
        messages(
                "Пока вы разглядывали драгоценность, кота и след простыл,"
                        + " хотя в воздухе остался лишь след улыбки кота.",
                " Похоже, он не просто так вас сюда привёл.");
        catchUpCatAndClose(CAT1);
    }

    private void catchCat(ChooseContext choice) {
        messages(
                "Кот остановился, и внимательно посмотрел на вас, похоже вы его не поняли.",
                "Но кота вам, конечно, все равно засчитают.");
        catchUpCatAndClose(CAT1);
        close();
    }
}
