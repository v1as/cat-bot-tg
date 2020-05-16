package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.random.RandomRequest;

import java.time.LocalDateTime;
import java.util.List;

/*
   author: AshaOwl
*/
@Component
public class DayAndNightPhase extends AbstractCuriosCatPhase {

    public LocalDateTime now = LocalDateTime.now();
    private static final String VISIBILITY = "VISIBILITY";
    private static final int DAY_VISIBILITY = 100;
    private static final int NIGHT_VISIBILITY = 60;

    @Override
    protected void open() {
        int visibility = defineVisibility();
        getPhaseContext().set(VISIBILITY, visibility);
        message(visibility == DAY_VISIBILITY
                ? "Пока светло, вы решили немного прогуляться."
                : "Вы решили прогуляться, хоть на улице темновато.");
        poll("Вперёд за котом?")
                .choice("Вперёд!", visibility == DAY_VISIBILITY ? this::dayStory : this::nightStory)
                .send();
    }

    private int defineVisibility() {
        // todo: когда-нибудь сделать зависимость осветления от времени года
        int hours = now.getHour(); // 0-23
        if (hours >= 4 && hours < 16) {
            return DAY_VISIBILITY; // 100
        }
        if (hours >= 16 && hours < 23) { // 16,17,18,19,20,21,22
            return NIGHT_VISIBILITY - (hours - 16) * 3; // 50 -> 50,47,44,41,38,35,32
        }
        if (hours == 23) {
            return NIGHT_VISIBILITY - hours; // 50 -> 27
        }
        // 0,1,2,3
        return NIGHT_VISIBILITY - (5 - hours) * 5; // 50 -> 25, 20, 15, 10
    }

    private void dayStory(ChooseContext ctx) {
        messages(
                "Следуя за котом, вы попадаете в парк.",
                "Вы неспеша движетесь по тропинке, смотрите по сторонам, слушаете пение птиц.",
                "Кот прибавил шаг и устремился к кусту у края дороги.",
                "Куст достаточно густой, чтобы полностью скрыть кота, когда тот завернул за него.",
                "Спустя мгновение, кот появляется снова.",
                "Вы видите его напряжённую спину, он пятится и не сводит глаз с куста...",
                "Из куста выходит ещё один кот!",
                "Коты шипят друг на друга, кружат и готовятся к драке.",
                "Вы стоите поодаль и колеблетесь."
        );
        poll("Стоит ли вмешаться?")
                .choice("Распугать котов", this::shooTheCats)
                .choice("Обойти", this::avoidTheCats)
                .send();
    }

    private void shooTheCats(ChooseContext ctx) {
        message("Ваш окрик разгоняет котов, и вы не успели их посчитать.");
        catchUpCatAndClose(CatRequestVote.NOT_CAT);
    }

    private void avoidTheCats(ChooseContext ctx) {
        message("Вы очень осторожно обходите двух серьёзно настроенных котов.");
        catchUpCatAndClose(CatRequestVote.CAT2);
    }

    private void nightStory(ChooseContext ctx) {
        int visibility = getPhaseContext().get(VISIBILITY);
        messages(
                "Идя по освещённым улицам, вы следуете за котом.",
                "Вы помните, что эта дорога вела в парк.",
                "Похоже, в парке сломалось освещение - фонари не горят."
        );
        List<String> NIGHT_STORY_STEPS = ImmutableList.of(
            "Кот шмыгает куда-то в темноту.",
            "Где же кот?"
        );
        boolean endOfStory = false;
        for (String nightStoryStep : NIGHT_STORY_STEPS) {
            boolean tooDark = random(new RandomRequest<Boolean>()
                    .add(false, visibility)
                    .add(true, 100 - visibility));
            if (tooDark) {
                endOfStory = true;
                break;
            } else {
                message(nightStoryStep);
            }
        }
        if (endOfStory) {
            message("Было слишком темно, вы потеряли кота из виду.");
            poll("Позвать кота?")
                    .choice("Кот?!", this::nightWithoutCat)
            .send();
            return;
        }
        messages(
                "А, нет, вот он медленно возвращается обратно.",
                "Кажется, он пятится. Его что-то насторожило?",
                "Вслед за ним появляется несколько котоподобных теней.",
                "Слышны завывания и шипение. Похоже, будет драка.",
                "Вы стоите поодаль и колеблетесь.");

        poll("Что дальше?")
                .choice("Обойти", this::avoidNightCats)
                .choice("Попробовать сосчитать", this::countShadowCats)
                .send();
    }

    private void nightWithoutCat(ChooseContext ctx) {
        message("Кот не откликнулся.");
        catchUpCatAndClose(CatRequestVote.NOT_CAT);
    }

    private void avoidNightCats(ChooseContext ctx) {
        message("Вы осторожно обходите эту компанию, даже не пытаясь их сосчитать.");
        catchUpCatAndClose(CatRequestVote.NOT_CAT);
    }

    private void countShadowCats(ChooseContext ctx) {
        message("Вы пытаетесь сосчитать смутные тени котов.");
        catchUpCatAndClose(random(new RandomRequest<CatRequestVote>()
                .add(CatRequestVote.CAT1, 40)
                .add(CatRequestVote.CAT2, 50)
                .add(CatRequestVote.CAT3, 10)
        ));
    }

}
