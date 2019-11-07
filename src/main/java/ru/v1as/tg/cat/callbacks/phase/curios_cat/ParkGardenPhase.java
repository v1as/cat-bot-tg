package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.utils.RandomChoice;
import ru.v1as.tg.cat.utils.RandomNoRepeats;

@Component
public class ParkGardenPhase extends AbstractCuriosCatPhase {

    private RandomChoice<String> chillingMessages =
            new RandomNoRepeats<>(
                    ImmutableList.of(
                            "Коту немного подустал и решил устроиться рядом с вами на лавочке, свернувшись клубочком",
                            "На этот раз кота заинтересовал одуванчик",
                            "Кот с любопытством уставился на птицу на дереве",
                            "Мимо проходит ребёнок с маленьким щенком. Вы с котом провожаете их взглядом.",
                            "Кот лениво играет со своим хвостом.",
                            "Так бывает? Кот потёрся о вашу ногу."));

    @Override
    protected void open() {
        message("Что за свежий солнечный день!");
        message("С окружением вам тоже повезло: сам Любопытный Кот составил вам компанию.");
        message("Ваш общий путь лежит через зелёный цветущий парк.");
        message("Похоже у Любопытного Кота сегодня игривое настроение.");
        poll("Что будем делать?")
                .choice("Присесть на скамейку", this::sitOnBench)
                .choice("Кот! " + EmojiConst.CAT, this::cat)
                .send();
    }

    private void cat(ChooseContext chooseContext) {
        message("Вы воспользовались занятостью кота и получили свой бал.");
        message("И, конечно, отправились по своим важным делам.");
        message("Скучный зануда.");
        catchUpCatAndClose(chooseContext, CatRequestVote.CAT1);
    }

    private void sitOnBench(ChooseContext chooseContext) {
        message("Вы удобно устроились на скамье.");
        message("Кот всё так же играет с бабочкой.");
        poll("Что будем делать?")
                .choice("Отдыхать", this::rest)
                .choice("Кот!" + EmojiConst.CAT, this::goodBye)
                .send();
    }

    private void rest(ChooseContext chooseContext) {
        Integer loop = getPhaseContext().increment("LOOP");
        if (loop >= chillingMessages.size()) {
            message("Похоже у кота появились более важные дела.");
            message("Он кивнул вам на прощанье и исчез.");
            catchUpCatAndClose(chooseContext, CatRequestVote.CAT1);
        } else {
            message(chillingMessages.get());
            poll("Что будем делать?")
                    .choice("Отдыхать", this::rest)
                    .choice("Кот!" + EmojiConst.CAT, this::goodBye)
                    .send();
        }
    }

    private void goodBye(ChooseContext chooseContext) {
        message("Вы славно отдохнули.");
        message("Да еще и балл свой получили. Разве не прекрасно?");
        catchUpCatAndClose(chooseContext, CatRequestVote.CAT1);
    }
}
