package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.RABIES_MEDICINE;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;

@Component
public class MadCatPhase extends AbstractCuriosCatPhase {

    @Override
    public String getName() {
        return "MadCatPhase2";
    }

    @Override
    protected void open() {
        messages("В этот раз точно что-то не так.", " Кот бежит явно быстрее обычного.");
        poll("Что делаем?")
                .choice("Ускоряем шаг", this::goFaster)
                .choice("Идём спокойно", this::justGo)
                .send();
    }

    private void justGo(ChooseContext chooseContext) {
        messages(
                "Вы продолжаете идти как и шли, а странный кот скрывается за поворотом.",
                " Туда ему и дорога.");
        catchUpCatAndClose(NOT_CAT);
    }

    private void goFaster(ChooseContext ctx) {
        message("Кот останаливается и начинает на вас шипеть!");
        final TgInlinePoll poll =
                poll("Что дальше?")
                        .choice("Ретироваться", this::runAway)
                        .choice("Стоять на месте", this::stand);
        if (paramResource.paramBool(
                getPhaseContext().getPublicChatId(), ctx.getUser().getId(), RABIES_MEDICINE)) {
            poll.choice("Использовать лекарство", this::useMedicine);
        }
        poll.send();
    }

    private void useMedicine(ChooseContext ctx) {
        paramResource.param(
                getPhaseContext().getPublicChatId(),
                ctx.getUser().getId(),
                RABIES_MEDICINE,
                "false");
        messages(
                "Сегодня вы оказались во всеоружии!",
                "Кот бросается, но вы ловко хватаете его на лету и делаете ему укол!");
        catchUpCatAndClose(CAT1);
    }

    private void stand(ChooseContext chooseContext) {
        messages(
                " О нет, похоже, вы обознались, и это не любопытный кот, это бешеный кот!",
                "Вы отправляетесь восвояси с расцарапанным лицом. Вероятно, вам следует обратиться к врачу.");
        catchUpCatAndClose(NOT_CAT);
    }

    private void runAway(ChooseContext chooseContext) {
        messages(
                "Вы осторожно отходите от кота.",
                "Странный кот, не прекращая шипеть, галопом умчался.",
                "Что ж, могло бы быть и хуже.");
        catchUpCatAndClose(NOT_CAT);
    }
}
