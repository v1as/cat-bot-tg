package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.EmojiConst.COLLISION;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;

import lombok.RequiredArgsConstructor;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@RequiredArgsConstructor
public class RedStonePhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        poll("Кот неторопливо бежит впереди вас.")
                .choice("Попытаться догнать кота", this::fastFollowCat)
                .choice("Спокойно следовать", this::followTheCat)
                .timeout(TIMEOUT_LEAVE_CAT)
                .send();
    }

    private void fastFollowCat(ChooseContext choice) {
        messages(
                "Кот испугался и рванул, что было сил, вам не удалось его догнать.",
                " Хуже того, никто не слышал как вы кричали 'Кот'.");
        catchUpCatAndClose(CatRequestVote.NOT_CAT);
    }

    private void followTheCat(ChooseContext data) {
        poll("Вы продолжаете осторожно следовать за котом, пристально следя за ним взглядом. "
                        + "Боковым зрением вы вдруг замечаете, как что-то блестит на дороге")
                .choice("Разглядеть находку", this::resStone)
                .choice("Не отвлекаться", this::catchCat)
                .timeout(TIMEOUT_LEAVE_CAT)
                .send();
    }

    private void resStone(ChooseContext choice) {

        messages(
                "Вы остановились чтобы разглядеть находку.",
                "Это оказался затейливый красный камешек " + COLLISION,
                "Пожалуй, вы заберёте его себе - в хозяйстве всё пригодится.");

        message(
                getPhaseContext().getPublicChat(),
                "Игрок "
                        + choice.getUser().getUsernameOrFullName()
                        + " находит красный камень"
                        + COLLISION);

        messages(
                "Пока вы разглядывали драгоценность, кота и след простыл,"
                        + " хотя в воздухе остался лишь след улыбки кота.",
                " Похоже, он не просто так вас сюда привёл.");

        catchUpCatAndClose(CAT1);
    }

    private void catchCat(ChooseContext choice) {
        messages(
                "Кот остановился, и внимательно посмотрел на вас, похоже вы его не поняли.",
                "Любопытный кот подбежал и потёрся о вашу ногу.");
        catchUpCatAndClose(CAT1);
        close();
    }
}
