package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.EmojiConst.COLLISION;

import lombok.RequiredArgsConstructor;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.UserData;

@RequiredArgsConstructor
public class RedStonePhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        poll("Кот неторопливо бежит впереди вас")
                .choice("Попытаться догнать кота", this::fastFollowCat)
                .choice("Спокойно следовать", this::followTheCat)
                .timeout(TIMEOUT_LEAVE_CAT)
                .send();
    }

    private void fastFollowCat(ChooseContext choice) {
        message(
                "Кот испугался и рванул что было сил, вам не удалось его догнать."
                        + " Хуже того, никто не слышал как вы кричали 'Кот'.");
        catchUpCatAndClose(choice, CatRequestVote.NOT_CAT);
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

        UserData user = data.getUserData(choice.getUser());
        message(
                "Вы остановились чтобы разглядеть находку. "
                        + "Это оказался затейливый красный камешек "
                        + COLLISION
                        + ", пожалуй вы заберёте его себе - в хозяйстве всё пригодится.");

        message(
                getPhaseContext().getPublicChat(),
                "Игрок " + user.getUsernameOrFullName() + " находит красный камень" + COLLISION);

        message(
                "Пока вы разглядывали драгоценность кота и след простыл,"
                        + " хотя в воздухе остался лишь след улыбки кота."
                        + " Похоже, он не просто так вас сюда привёл.");

        catchUpCatAndClose(choice, CatRequestVote.CAT1);
    }

    private void catchCat(ChooseContext choice) {

        UserData user = data.getUserData(choice.getUser());
        message("Кот остановился, и внимательно посмотрел на вас, похоже вы его не поняли.");

        message("Любопытный кот подбежал и потёрся о вашу ногу.");

        catchUpCatAndClose(choice, CatRequestVote.CAT1);
        close();
    }
}
