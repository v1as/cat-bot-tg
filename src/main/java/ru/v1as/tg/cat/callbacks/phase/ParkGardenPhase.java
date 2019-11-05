package ru.v1as.tg.cat.callbacks.phase;

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
        timeout(2000);
        message("Что за свежий солнечный день!");
        timeout(3000);
        message("С окружением вам тоже повезло: сам Любопытный Кот составил вам компанию.");
        timeout(3000);
        message("Ваш общий путь лежит через зелёный цветущий парк.");
        timeout(3000);
        message("Похоже у Любопытного Кота сегодня игривое настроение.");
        timeout(3000);
        poll("Что будем делать?")
                .choice("Присесть на скамейку", this::sitOnBench)
                .choice("Кот! " + EmojiConst.CAT, this::cat)
                .send();
    }

    private void cat(ChooseContext chooseContext) {
        timeout(1500);
        message("Вы воспользовались занятостью кота и получили свой бал.");
        timeout(2000);
        message("И, конечно, отправились по своим важным делам.");
        timeout(4000);
        message("Скучный зануда.");
        catchUpCatAndClose(chooseContext, CatRequestVote.CAT1);
    }

    private void sitOnBench(ChooseContext chooseContext) {
        timeout(1500);
        message("Вы удобно устроились на скамье.");
        timeout(2000);
        message("Кот всё так же играет с бабочкой.");
        timeout(2000);
        poll("Что будем делать?")
                .choice("Отдыхать", this::rest)
                .choice("Кот!" + EmojiConst.CAT, this::goodBye)
                .send();
    }

    private void rest(ChooseContext chooseContext) {
        Integer loop = getPhaseContext().increment("LOOP");
        timeout(1500);
        if (loop >= chillingMessages.size()) {
            message("Похоже у кота появились более важные дела.");
            timeout(1500);
            message("Он кивнул вам на прощанье и исчез.");
            timeout(1500);
            catchUpCatAndClose(chooseContext, CatRequestVote.CAT1);
        } else {
            message(chillingMessages.get());
            timeout(1500);
            poll("Что будем делать?")
                    .choice("Отдыхать", this::rest)
                    .choice("Кот!" + EmojiConst.CAT, this::goodBye)
                    .send();
        }
    }

    private void goodBye(ChooseContext chooseContext) {
        timeout(1500);
        message("Вы славно отдохнули.");
        timeout(1500);
        message("Да еще и балл свой получили. Разве не прекрасно?");
        timeout(1500);
        catchUpCatAndClose(chooseContext, CatRequestVote.CAT1);
    }
}
