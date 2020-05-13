package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.random.RandomRequest;

import java.util.List;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;

/*
   author: AshaOwl
*/
@Component
public class MoreCatsPhase extends AbstractCuriosCatPhase {

    private static final String MORE_WORD = "Больше";
    private static final String MORE_MARK = "!";
    private static final String CATS_COUNTER = "CATS";
    private static final String DREAM_RESULT = "DREAM_RESULT";
    public static final int CAT_MAX = 3;
    private static final List<String> ONE_CAT =
            ImmutableList.of(
                    "рыжий кот был найден",
                    "смешная трёхцветная кошка была найдена",
                    "серая кошка с большими зелёными глазами была найдена");
    private static final List<String> TWO_CATS =
            ImmutableList.of(
                    "беленький котёнок гулял",
                    "два рыжих кота гуляли",
                    "чёрный кот и белая кошка гуляли");
    private static final List<String> THREE_CATS =
            ImmutableList.of(
                    "мама-кошка с совсем маленьким котёнком",
                    "три спящих клубка котов или кошек",
                    "две рыжие кошки и белый кот");

    @Override
    protected void open() {
        messages(
                "Любопытный Кот ведёт вас за собой.",
                "Побродив за котом какое-то время, вы вспомнили об особом дворе.",
                "Там часто встречается много котов и кошек, иногда даже котята.",
                "Но путь туда неблизкий.");

        poll("Стоит ли удлинять прогулку?")
                .choice("Вперёд за котами!", this::checkPlaces)
                .choice("Мне и Любопытного хватит", this::curiosCat)
                .send();
    }

    private void curiosCat(ChooseContext ctx) {
        messages( "Любопытный Кот рад вашей компании.",
                "Прогулявшись с ним, вы приятно провели время.");
        catchUpCatAndClose(CAT1);
    }

    private void checkPlaces(ChooseContext ctx) {
        messages(
                "Покинув Кота, вы продолжаете свой путь по памяти.",
                "Довольно долго проблуждав, вы изрядно утомились.",
                "Устроившись на скамейке в парке, вы смогли дать отдых уставшим ногам.",
                "Ещё немного отдыха, и можно будет пойти дальше."
        );
        getPhaseContext().set(CATS_COUNTER, 0);
        messages(
                "Тем временем небо хмурится, возможно скоро будет дождь.",
                "При такой погоде все коты могут попрятаться, и вы никого не найдёте."
        );
        poll("Как много котов вы ожидаете встретить в этом дворе?")
                .choice("Ни одного, к сожалению", this::awake)
                .choice("Один-то точно будет", this::catsDream)
                .send();
    }

    private void catsDream(ChooseContext ctx) {
        getPhaseContext().increment(CATS_COUNTER);
        Integer dreamCats = getPhaseContext().get(CATS_COUNTER);
        if (dreamCats <= CAT_MAX) {
            poll("Вдруг там больше котов, чем " + dreamCats + "?")
                    .choice( more(), this::catsDream)
                    .choice("Вряд ли больше, чем " + dreamCats, this::awake)
                    .send();
        } else {
            awake(ctx);
        }
    }

    private String more() {
        StringBuilder sign = new StringBuilder(MORE_MARK);
        Integer dreamCats = getPhaseContext().get(CATS_COUNTER);
        for (int i = 1; i < dreamCats; i++) {
            sign.append(MORE_MARK);
        }
        return MORE_WORD + sign;
    }

    private void awake(ChooseContext ctx) {
        messages(
                "Внезапно вздрогнув, вы понимаете, что незаметно для себя задремали на скамейке.",
                "Что ж, пора продолжить путь."
        );
        Integer dreamCats = getPhaseContext().get(CATS_COUNTER, 0);
        RandomRequest<Integer> request;
        if (dreamCats == 0) {
            // никаких мечтаний, можно и вознаградить - равный шанс на всё
            request = new RandomRequest<Integer>()
                    .add(0, 25)
                    .add(1, 25)
                    .add(2, 25)
                    .add(3, 25);
        } else if (dreamCats == 1) {
            // более вероятно встретить одного, чем никого
            request = new RandomRequest<Integer>()
                    .add(0, 25)
                    .add(1, 75);
        } else if (dreamCats == 2) {
            // шанс встретить двух чуть ниже, чем никого
            request = new RandomRequest<Integer>()
                    .add(0, 55)
                    .add(2, 45);
        } else if (dreamCats == 3) {
            // есть небольшой шанс встретить трёх, но вероятнее никого
            request = new RandomRequest<Integer>()
                    .add(0, 75)
                    .add(3, 25);
        } else {
            // слишком пожадничал и никого не встретишь
            request = new RandomRequest<Integer>()
                    .add(0, 100);
        }
        Integer dreamResult = random(request);
        getPhaseContext().set(DREAM_RESULT, dreamResult);
        finish(ctx);
    }


    private void finish(ChooseContext ctx) {
        messages(
                "Наконец-то вы добрались до кошачьего двора!",
                "Не терпится узнать сколько же там котов?"
        );
        poll("Как быстро вы входите во двор?")
                .choice( "Стремительно!", this::result)
                .choice("Неспешно", this::result)
                .send();
    }

    private void result(ChooseContext ctx) {
        Integer cats = getPhaseContext().get(DREAM_RESULT, 0);
        if (cats == 1) {
            message("Вам повезло, " + random(ONE_CAT) + " в этот раз на прежнем месте.");
            catchUpCatAndClose(CatRequestVote.CAT1);
        } else if (cats == 2) {
            message("Как вы и надеялись, " + random(TWO_CATS) + " в том дворе.");
            catchUpCatAndClose(CatRequestVote.CAT2);
        } else if (cats == 3) {
            message("Просто замечательно, что " + random(THREE_CATS) + " оказались там, где вы их искали.");
            catchUpCatAndClose(CatRequestVote.CAT3);
        } else {
            message("Вы никого не нашли, может повезёт в другой раз?");
            catchUpCatAndClose(CatRequestVote.NOT_CAT);
        }
    }

}