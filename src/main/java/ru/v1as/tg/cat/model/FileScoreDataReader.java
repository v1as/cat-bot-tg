package ru.v1as.tg.cat.model;

import static java.lang.System.lineSeparator;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.http.util.TextUtils.isEmpty;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;

@Slf4j
@RequiredArgsConstructor
public class FileScoreDataReader {

    private static final String SPLIT_CHAR = "%";
    private static final String CAT_SCORES_TXT = "cat_scores.txt";
    private List<ScoreLine> lines = new ArrayList<>();

    @SneakyThrows
    public void init() {
        File file = new File(CAT_SCORES_TXT);
        if (file.createNewFile()) {
            log.info("File created: " + file.getAbsolutePath());
        } else {
            log.info("File already exists '{}'", file.getAbsolutePath());
        }
        lines.clear();
        Files.lines(Paths.get(CAT_SCORES_TXT))
                .filter(s -> !s.isEmpty())
                .forEach(
                        str -> {
                            try {
                                lines.add(new ScoreLine(str));
                            } catch (Exception ex) {
                                log.error("Can't read line: " + str, ex);
                            }
                        });
        log.info(
                "Scored is loaded. {} lines was read. File: '{}'",
                lines.size(),
                file.getAbsoluteFile());
    }

    public List<ScoreLine> getScore(Long chatId) {
        Stream<ScoreLine> stream = this.lines.stream();
        if (null != chatId) {
            stream = stream.filter(l -> Objects.equals(chatId, l.chatId));
        }
        return stream.collect(Collectors.toList());
    }

    @FieldDefaults(level = PRIVATE, makeFinal = true)
    @Getter
    public static final class ScoreLine {
        Integer id;
        Integer userId;
        String fullName;
        String userName;
        CatRequestVote result;
        LocalDateTime date;
        Long chatId;
        Boolean isReal;

        ScoreLine(CatRequest request) {
            this.id = request.getSrcMsgId();
            this.userId = request.getOwner().getId();
            this.fullName = request.getOwner().getFullName();
            this.userName = request.getOwner().getUserName();
            this.result = request.getResult();
            this.date = request.getCreated();
            this.chatId = request.getChatId();
            this.isReal = request.getIsReal();
        }

        ScoreLine(String data) {
            String[] fields = data.split(SPLIT_CHAR);
            this.isReal = toBoolean(fields[0]);
            this.id = toInt(fields[1]);
            this.userId = toInt(fields[2]);
            this.fullName = fields[3];
            this.userName = fields[4];
            this.result = CatRequestVote.valueOf(fields[5]);
            this.date =
                    isEmpty(fields[6])
                            ? null
                            : LocalDateTime.parse(fields[6], DateTimeFormatter.ISO_DATE_TIME);
            this.chatId = toLong(fields[7]);
        }

        private Long toLong(String field) {
            return isEmpty(field) ? null : Long.valueOf(field);
        }

        private Integer toInt(String field) {
            return isEmpty(field) ? null : Integer.valueOf(field);
        }

        private Boolean toBoolean(String field) {
            return isEmpty(field) ? null : Boolean.valueOf(field);
        }

        String getUserString() {
            return isEmpty(userName) ? fullName : String.format("%s (%s)", fullName, userName);
        }

        Integer getAmount() {
            return result.getAmount();
        }

        @Override
        public String toString() {
            return Stream.of(
                            isReal != null ? isReal : false,
                            id,
                            userId,
                            fullName,
                            userName,
                            result,
                            date != null ? date.format(DateTimeFormatter.ISO_DATE_TIME) : "",
                            chatId,
                            lineSeparator())
                    .map(str -> str == null ? "" : str)
                    .map(Object::toString)
                    .map(str -> str.replace(SPLIT_CHAR, "_"))
                    .collect(Collectors.joining(SPLIT_CHAR));
        }
    }
}
