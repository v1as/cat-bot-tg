package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import java.util.function.Consumer;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.random.RandomRequest;

/*
 * author: AnnaTemnaya
 * */
@Component
public class BlackCatAndMeetingPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages(
                "Вы спешите на очень важную деловую встречу",
                "Спешите так сильно, что даже не смотрите по сторонам",
                "Тут прямо перед вами проносится чёрный Кот",
                "Вы резко останавливаетесь");
        poll("Что делаем?")
                .choice("Кот!", this::n1)
                .choice("Смотрим вслед убежавшему Коту", this::n2)
                .choice("Это плохая примета, пойду другим путём", this::n3)
                .choice("Вам всё равно, главное - не опоздать", this::n4)
                .send();
    }

    private void n1(ChooseContext chooseContext) {
        messages(
                "Выкрикнув от неожиданности \"Кот!\", вы продолжили быстро идти",
                "Беспокойно поглядывая на часы, ускоряете шаг",
                "Тут вам снова перебегает дорогу чёрный Кот");
        poll("Что делаем?")
                .choice("Кот!", this::n1d1)
                .choice("Внимательно оглядываемся", this::n1d2)
                .send();
    }

    private void n1d1(ChooseContext chooseContext) {
        messages(
                "Снова кричите \"Кот!\" и думаете, а не один ли это и тот же?",
                "Может, они просто братья?",
                "Кто знает, вы уже почти опоздали, и нет времени на эту загадку",
                "Выбросив Котов из головы, снова торопитесь на встречу, ничего больше не замечая вокруг");
        catchUpCatAndClose(random(CAT1, CAT2));
    }

    private void n1d2(ChooseContext chooseContext) {
        messages(
                "Оглянувшись по сторонам, вы замечаете чёрную мордочку в кустах",
                "На встречу всё равно уже опоздали, так что неспеша подходите",
                "Там сидит два абсолютно одинаковых чёрных Кота и с любопытством смотрят на вас",
                "Насмотревшись друг на друга, идёте каждый по своим делам");
        catchUpCatAndClose(CAT2);
    }

    private void n2(ChooseContext chooseContext) {
        messages(
                "Посмотрев на кусты, в которых скрылся Кот, пожимаете плечами",
                "Мало ли, какие у него там дела",
                "Но тут вы слышите тихое мяуканье из этих кустов");
        poll("Что делать?")
                .choice("Пройти мимо", this::n2d1)
                .choice("Раздвинуть ветки", this::n2d2)
                .send();
    }

    private void n2d1(ChooseContext chooseContext) {
        messages(
                "У вас нет времени на лазанье по кустам",
                "Вы в новом костюме, и не хотите его испортить",
                "Добежав к месту встречи, вы получаете сообщение с извинениями о задержке вашего оппонента",
                "Эх, надо было заглянуть в те кусты!");
        catchUpCatAndClose(NOT_CAT);
    }

    private void n2d2(ChooseContext chooseContext) {
        messages(
                "Чертыхаясь, лезете в кусты, стараясь не испачкать новый костюм",
                "Кота нигде не видно, да и мяуканье стихло",
                "Но тут что-то зашевелилось сбоку");
        random(
                        new RandomRequest<Consumer<ChooseContext>>()
                                .add(this::n2d2random1)
                                .add(this::n2d2random2))
                .accept(chooseContext);
    }

    private void n2d2random1(ChooseContext chooseContext) {
        messages(
                "Вы вглядываетесь в гущу куста и видите своего чёрного Кота",
                "Он просто говорил сам с собой, а вы ему помешали",
                "Смутившись под взглядом Кота, вылезаете из куста и снова спешите на свою встречу");
        catchUpCatAndClose(CAT1);
    }

    private void n2d2random2(ChooseContext chooseContext) {
        messages(
                "Посмотрев в ту сторону, видите кошку, которая разговаривала со своим котёнком",
                "Извинившись за грубое вторжение, вылезаете из куста и спешите дальше по делам");
        catchUpCatAndClose(CAT3);
    }

    private void n3(ChooseContext chooseContext) {
        messages(
                "С детства верите во все приметы и, даже опаздывая, не позволите себе нарушить хоть одну из них",
                "Возвращаясь назад, чтобы пройти другой дорогой, подсчитываете, на сколько вы опоздаете",
                "Вот вы снова сосредоточены на предстоящей встрече и не замечаете ничего вокруг",
                "Пока вам снова не перебегает дорогу чёрный Кот!");
        poll("Что делаем?").choice("Ждём", this::n3d1).choice("Плачем", this::n3d2).send();
    }

    private void n3d1(ChooseContext chooseContext) {
        messages(
                "Вы стоите на месте",
                "Никого вокруг нет, ни Котов, ни людей, которые прошли бы первые через путь чёрного Кота и сняли бы примету");
        poll("Что делаем?")
                .choice("Оглядываемся", this::n3d1d1)
                .choice("Всё-таки встреча важнее примет, бежим на неё", this::n3d1d2)
                .send();
    }

    private void n3d1d1(ChooseContext chooseContext) {
        messages(
                "Внимательно оглянувшись, замечаете своего Кота",
                "Ой, у него белое пятно на груди!");
        random(
                        new RandomRequest<Consumer<ChooseContext>>()
                                .add(this::n3d1d1random1)
                                .add(this::n3d1d1random2))
                .accept(chooseContext);
    }

    private void n3d1d1random1(ChooseContext chooseContext) {
        messages(
                "И это не взрослый Кот, а котёнок-подросток!",
                "Радуясь, вы бежите на свою встречу, пока совсем не опоздали");
        catchUpCatAndClose(CAT2);
    }

    private void n3d1d1random2(ChooseContext chooseContext) {
        messages("Облегчённо вздохнув, бежите по своим делам уже спокойно");
        catchUpCatAndClose(CAT1);
    }

    private void n3d1d2(ChooseContext chooseContext) {
        messages(
                "Впервые нарушив примету, бежите на встречу",
                "Опоздали всего на пару минут, ничего страшного",
                "За весь день с вами не случилось ничего плохого",
                "Вы начинаете сомневаться в приметах",
                "Но тут вы понимаете, что Кота вам никто не засчитает!",
                "Вот вам и чёрный Кот!");
        catchUpCatAndClose(NOT_CAT);
    }

    private void n3d2(ChooseContext chooseContext) {
        messages(
                "На пике эмоционального состояния и стресса, вы пускаете две слезинки",
                "Потом ещё две",
                "Потом всхлипываете",
                "На эти звуки с разных сторон к вам подходят два чёрных Кота",
                "Они мурлычат и трутся о ваши ноги, успокаивая",
                "Пока вы их гладите, вам приходит сообщение о переносе встречи на полчаса",
                "Вы подружились с Котами и отлично провели встречу, на которую не опоздали,"
                        + " да ещё и получили новый проект по итогам, вы молодец!",
                "Теперь у вас новая примета: чёрная кошка - счастливая дорожка!");
        catchUpCatAndClose(CAT2);
    }

    private void n4(ChooseContext chooseContext) {
        messages(
                "Продолжаете бежать, поглядывая на часы",
                "Тут вам перебежал дорогу второй чёрный Кот");
        poll("Что делаем?").choice("Кот!", this::n4d1).choice("Продолжаем идти", this::n4d2).send();
    }

    private void n4d1(ChooseContext chooseContext) {
        messages(
                "Вы недовольно выкрикиваете \"Кот!\" и продолжаете свой бег",
                "Главное - не опоздать, повторяете вы про себя",
                "Что ж, вы не опоздали, да и Кота увидели");
        catchUpCatAndClose(CAT1);
    }

    private void n4d2(ChooseContext chooseContext) {
        messages(
                "Вы всё ускоряете шаг",
                "Ничего не замечаете вокруг",
                "Пока вам под ноги не кидается ещё один Кот!");
        poll("Что делаем?")
                .choice("Кот!", this::n4d2d1)
                .choice("Перепрыгиваем через Кота", this::n4d2d2)
                .send();
    }

    private void n4d2d1(ChooseContext chooseContext) {
        messages(
                "На этот раз вы даже притормозили и посмотрели вслед убегающему Коту",
                "Откуда их столько?",
                "Вам некогда об этом задумываться, вы опаздываете",
                "Забыв о Котах, идёте дальше",
                "На встречу вы успели, а Котов больше не встречали");
        catchUpCatAndClose(CAT1);
    }

    private void n4d2d2(ChooseContext chooseContext) {
        messages(
                "Подпрыгнув, чтобы не столкнуться с котом, вы продолжаете спешить",
                "Что ж, вы успели",
                "И встреча прошла хорошо",
                "Только ни одного Кота вы так и не получили");
        catchUpCatAndClose(NOT_CAT);
    }

}
