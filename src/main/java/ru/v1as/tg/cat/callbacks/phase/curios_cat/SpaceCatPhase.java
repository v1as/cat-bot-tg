package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.random.RandomRequest;

/*
 * author: azhirma
 * */
@Component
public class SpaceCatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages(
                "Медленно открывая глаза, вы пробуждаетесь ото сна.",
                "Всё тело затекло: по всей видимости, спали вы долго и неудобно.",
                "Кажется, вы лежите на совершенно твёрдой, плоской поверхности... Похоже на пол.",
                "Вы не совсем понимаете, где вы и как здесь оказались.");
        poll("Что делаем?")
                .choice("Оглядеться", this::lookAround)
                .choice("Продолжить лежать", this::stayDown)
                .send();
    }

    private void lookAround(ChooseContext ctx) {
        messages(
                "Вы оглядываетесь по сторонам и видите совершенно пустую комнату с голыми металлическими стенами.",
                "Похоже, что вы на космической станции.");
        openSpace();
    }

    private void stayDown(ChooseContext ctx) {
        messages(
                "Вы продолжаете лежать с закрытыми глазами, постепенно приходя в себя и пытаясь вспомнить, что происходило с вами ранее.",
                "Неожиданно вы чувствуете легкое давление в одной точке на вашем животе.",
                "Теперь давление уже в двух точках и ощущается чуть сильнее.");
        stayDownCat(ctx);
    }

    private void openSpace() {
        messages(
                "Вы решаете встать, но ваше тело еще вялое после долгого сна.",
                "Придерживаясь за стену, вы подходите к иллюминатору. И выглядываете через стекло наружу.",
                "Всё, что вы видите, - это мириады звёзд. Ни планет, ни кораблей в зоне видимости.",
                "Внезапно у вас начинает кружиться голова: вы слишком долго спали и, похоже, ничего не ели.");
        poll("Что делаем?")
                .choice("Продолжить смотреть", this::openSpaceCat)
                .choice("Прилечь", this::stayDown)
                .send();
    }

    private void stayDownCat(ChooseContext ctx) {
        messages(
                "Стараясь не пошевелить телом, вы слегка приподнимаете голову и бросаете робкий взгляд на свой живот.");
        random(
                        new RandomRequest<Runnable>()
                                .add(this::stayDownCatNo1)
                                .add(this::stayDownCatNo2)
                                .add(this::stayDownCatKitten))
                .run();
    }

    private void stayDownCatNo1() {
        messages(
                "Вскрикнув от ужаса, вы вскакиваете на ноги.",
                "Лучше бы вы этого не видели. Что это вообще было за существо?!");
        catchUpCatAndClose(CatRequestVote.NOT_CAT);
    }

    private void stayDownCatNo2() {
        messages(
                "Вы видите крошечное двуногое существо с гигантскими черными глазами, которые смотрят на вас.",
                "Это явно не кот, но настроено оно явно дружелюбно.",
                "Похоже, что вас похитили милые инопланетяне.");
        catchUpCatAndClose(CatRequestVote.NOT_CAT);
    }

    private void stayDownCatKitten() {
        messages(
                "Вы видите пушистого котенка с большими глазами, который удивлен вам так же сильно, как вы ему.",
                "Посмотрев на вас пристально несколько секунд, котенок смелеет и забирается полностью на ваш живот.",
                "Свернувшись клубочком и тихонько замурчав, котенок засыпает у вас на животе. Чувствуя его тепло, вы тоже снова засыпаете.");
        catchUpCatAndClose(CatRequestVote.CAT2);
    }

    private void openSpaceCat(ChooseContext ctx) {
        messages(
                "Потерев рукой слипающиеся глаза и широко зевнув, вы продолжаете смотреть в бесконечность, наполненную мириадами звёзд.");
        random(
                        new RandomRequest<Runnable>()
                                .add(this::openSpaceCatNo1)
                                .add(this::openSpaceCatNo2)
                                .add(this::openSpaceCatFamily))
                .run();
    }

    private void openSpaceCatNo1() {
        messages(
                "Простояв так несколько минут, вы устаете, присаживаетесь на пол и закрываете глаза.",
                "Действительно, что вы ожидали в открытом космосе? Неужели кота?");
        catchUpCatAndClose(CatRequestVote.NOT_CAT);
    }

    private void openSpaceCatNo2() {
        messages(
                "В помещении абсолютная тишина, в иллюминаторе ничего не происходит.",
                "Неожиданно вы чувствуете, как кто-то трогает вас за плечо.",
                "Вы оборачиваетесь, но никого не видите.",
                "Вы снова ощущаете прикосновение на вашем плече, но теперь оно еще и начинает потряхивать вас.",
                "Откуда-то издалека, с постепенно увеличивающейся громкостью, раздается голос: \"Мужчина! Конечная! Поезд дальше не идёт.\"",
                "Оказывается, вы уснули в метро.",
                "Что ж... Может, это даже лучше, чем внезапно оказаться на космической станции?",
                "По крайней мере, на Земле больше шансов встретить кота...");
        catchUpCatAndClose(CatRequestVote.NOT_CAT);
    }

    private void openSpaceCatFamily() {
        messages(
                "Вы крутите головой, пытаясь увеличить ширину обзора. Но иллюминатор совсем небольшой.",
                "И тут вы видите, как мимо вас в открытом космосе проплывает крошечное двуногое существо в скафандре.",
                "Оно смотрит на вас своими огромными черными глазами, подмигивает и летит дальше.",
                "Что это у него в руке? Поводок?!",
                "Похоже, что да. Следом за крошечным инопланетянином, на поводке, летит полноразмерная земная кошка в скафандре.",
                "Ничего себе! К ней на коротком ремешке пристегнут еще и котенок в малюсеньком скафандре!",
                "Вашему удивлению нет предела.",
                "Вы пытаетесь ущипнуть себя, чтобы проснуться.",
                "Но нет, похоже, что всё это не сон.");
        catchUpCatAndClose(CatRequestVote.CAT3);
    }
}