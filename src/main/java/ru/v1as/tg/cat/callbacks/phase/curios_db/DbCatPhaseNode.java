package ru.v1as.tg.cat.callbacks.phase.curios_db;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

@Getter
@Setter
public class DbCatPhaseNode {
    private String messagesText;
    private List<PhaseNodeChoice> choices;
    private List<PhaseNodeVarAction> actions;
    private Integer timeout;
    private DbCatPhaseNode timeoutNode;

    public String[] getMessages() {
        String[] lines = getAllLines();
        String[] result = new String[lines.length];
        System.arraycopy(lines, 0, result, 0, lines.length - 1);
        return result;
    }

    private String[] getAllLines() {
        return Arrays.stream(messagesText.split("[\\n\\r]")).filter(s -> !isEmpty(s)).toArray(String[]::new);
    }

    public String getLastMessage() {
        String[] lines = getAllLines();
        return lines[lines.length - 1];
    }

}
