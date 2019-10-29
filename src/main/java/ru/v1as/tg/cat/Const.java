package ru.v1as.tg.cat;

public class Const {

    public static final String LINE = "\n";
    private static String botName;

    public static String getBotName() {
        if (botName == null) {
            throw new IllegalStateException("This variable is not init yet.");
        }
        return botName;
    }

    public static void setBotName(String botName) {
        if (botName != null) {
            Const.botName = botName;
        } else {
            throw new IllegalStateException("This variable is already defined");
        }
    }

}
