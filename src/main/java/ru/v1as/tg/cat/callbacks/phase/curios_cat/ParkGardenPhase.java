package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.random.RandomRequest;

@Component
public class ParkGardenPhase extends AbstractCuriosCatPhase {

    private RandomRequest<String> chillingMessages =
            new RandomRequest<String>()
                    .addAll(
                            "Коту немного подустал и решил устроиться рядом с вами на лавочке, свернувшись клубочком",
                            "На этот раз кота заинтересовал одуванчик",
                            "Кот с любопытством уставился на птицу на дереве",
                            "Мимо проходит ребёнок с маленьким щенком. Вы с котом провожаете их взглядом.",
                            "Кот лениво играет со своим хвостом.",
                            "Так бывает? Кот потёрся о вашу ногу.");

    @Override
    protected void open() {
        messages(
                "Что за свежий солнечный день!",
                "С окружением вам тоже повезло: сам Любопытный Кот составил вам компанию.",
                "Ваш общий путь лежит через зелёный цветущий парк.",
                "Похоже у Любопытного Кота сегодня игривое настроение.");
        poll("Что будем делать?")
                .choice("Присесть на скамейку", this::sitOnBench)
                .choice("Кот! " + EmojiConst.CAT, this::cat)
                .send();
    }

    private void cat(ChooseContext chooseContext) {
        messages(
                "Вы воспользовались занятостью кота и получили свой бал.",
                "И, конечно, отправились по своим важным делам.",
                "Скучный зануда.");
        catchUpCatAndClose(CatRequestVote.CAT1);
    }

    private void sitOnBench(ChooseContext chooseContext) {
        messages("Вы удобно устроились на скамье.", "Кот увлечённо играет с бабочкой.");
        poll("Что будем делать?")
                .choice("Отдыхать", this::rest)
                .choice("Кот!" + EmojiConst.CAT, this::goodBye)
                .send();
    }

    private void rest(ChooseContext chooseContext) {
        Integer loop = getPhaseContext().increment("LOOP");
        if (loop >= chillingMessages.size()) {
            messages(
                    "Похоже у кота появились более важные дела.",
                    "Он кивнул вам на прощанье и исчез.");
            catchUpCatAndClose(CatRequestVote.CAT1);
        } else {
            message(random(chillingMessages));
            poll("Что будем делать?")
                    .choice("Отдыхать", this::rest)
                    .choice("Кот!" + EmojiConst.CAT, this::goodBye)
                    .send();
        }
    }

    private void goodBye(ChooseContext chooseContext) {
        messages("Вы славно отдохнули.", "Да еще и балл свой получили. Разве не прекрасно?");
        catchUpCatAndClose(CatRequestVote.CAT1);
    }
}
