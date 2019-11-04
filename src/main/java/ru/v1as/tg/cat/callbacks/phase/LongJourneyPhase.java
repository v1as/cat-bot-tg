package ru.v1as.tg.cat.callbacks.phase;

import static ru.v1as.tg.cat.utils.RandomUtils.random;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
public class LongJourneyPhase extends AbstractCuriosCatPhase {

    private static final List<String> WALKING =
            ImmutableList.of(
                    "Вы так себе и идёте дальше, улыбаясь и мурлыча что-то незатейливое под нос.",
                    "Дома сменяются домами, дороги - дорогами, а вы всё еще идёте за котом.",
                    "Ваш путь идёт сквозь парк, ветер озорно играет в ветвях деревьев.",
                    "Кот уверенно шлёпает своими лапками впереди.",
                    "Вы с котом зашли в какую-то неширокую улочку",
                    "А вот сейчас вы, похоже, идёте мимо булочной, аромат свежих булочек не оставляет вас равнодушным. Коту, впрочем, всё равно.");
    private static final String LOOP = "LOOP";

    @Override
    protected void open() {
        message("Сегодня выдался на удивление приятный денёк.");
        timeout(2000);
        message(
                "Вы с котом медленно прогуливаетесь по городу. Правда, он идёт в небольшом отдалении впереди.");
        timeout(3000);
        poll("Что будем делать?")
                .choice("Нагоним кота", this::catchUpCat)
                .choice("Гулять себе в удовольствие", this::walk)
                .send();
        getPhaseContext().increment(LOOP);
    }

    private void walk(ChooseContext chooseContext) {
        getPhaseContext().increment(LOOP);
        message(random(WALKING));
        timeout(3000);
        poll("Что будем делать?")
                .choice("Нагоним кота", this::catchUpCat)
                .choice("Гуляем дальше", this::walk)
                .send();
    }

    private void catchUpCat(ChooseContext chooseContext) {
        timeout(2000);
        Integer loop = getPhaseContext().get(LOOP);
        if (loop < 5) {
            message("Кот недовольно мяукая убегает.");
            catchUpCatAndClose(chooseContext, CatRequestVote.NOT_CAT);
        } else {
            message(
                    "Кот удивлённо смотрит на вас. Похоже, он совсем забыл о вашей компании во время этой дивной прогулки."
                            + " Пользуюясь его замешательством, вы воскликнули 'Кот!'");
            timeout(4000);
            catchUpCatAndClose(chooseContext, CatRequestVote.CAT1);
        }
    }
}
