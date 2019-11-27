package ru.v1as.tg.cat.model;

public class TestTgUser implements TgUser {

    private final Integer id;

    private TestTgUser(Integer id) {
        this.id = id;
    }

    public static TestTgUser tgUser(Integer id) {
        return new TestTgUser(id);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getUserName() {
        return "user" + id;
    }

    @Override
    public String getFirstName() {
        return "User";
    }

    @Override
    public String getLastName() {
        return id + "";
    }

    @Override
    public String getLanguageCode() {
        return null;
    }

}
