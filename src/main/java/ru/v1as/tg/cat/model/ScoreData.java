package ru.v1as.tg.cat.model;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingInt;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.http.util.TextUtils.isEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
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
public class ScoreData {

    private static final String SPLIT_CHAR = "%";
    private final String fileName;
    private List<ScoreLine> lines = new ArrayList<>();
    private List<ScoreLine> toSave = new ArrayList<>();

    @SneakyThrows
    public void flush() {
        if (toSave.size() == 0) {
            return;
        }
        synchronized (ScoreData.class) {
            try (FileOutputStream fos = new FileOutputStream(fileName, true)) {
                for (ScoreLine line : toSave) {
                    fos.write(line.toString().getBytes());
                }
            }
            toSave.clear();
        }
    }

    @SneakyThrows
    public void init() {
        File file = new File(fileName);
        if (file.createNewFile()) {
            log.info("File created: " + file.getAbsolutePath());
        }
        synchronized (ScoreData.class) {
            lines.clear();
            toSave.clear();
            Files.lines(Paths.get(fileName))
                    .filter(s -> !s.isEmpty())
                    .forEach(
                            str -> {
                                try {
                                    lines.add(new ScoreLine(str));
                                } catch (Exception ex) {
                                    log.error("Can't read line: " + str, ex);
                                }
                            });
        }
        log.info(
                "Scored is loaded. {} lines was read. File: {}",
                lines.size(),
                file.getAbsoluteFile());
    }

    public void save(CatRequest catRequest) {
        ScoreLine line = new ScoreLine(catRequest);
        lines.add(line);
        toSave.add(line);
    }

    public List<ScoreLine> getScore(Long chatId) {
        return this.lines.stream()
                .filter(l -> Objects.equals(chatId, l.chatId))
                .collect(Collectors.toList());
    }

    public Stream<LongProperty> getWinnersStream(Long chatId, LocalDateTime after) {
        Map<String, IntSummaryStatistics> grouped =
                getScore(chatId).stream()
                        .filter(line -> line.getDate() != null)
                        .filter(scoreLine -> after == null || after.isBefore(scoreLine.getDate())).collect(
                        groupingBy(ScoreLine::getUserString, summarizingInt(ScoreLine::getAmount)));
        return grouped.entrySet().stream()
                .sorted(Comparator.comparingLong(e -> -1 * e.getValue().getSum()))
                .map(e -> new LongProperty(e.getKey(), e.getValue().getSum()));
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

        ScoreLine(CatRequest request) {
            this.id = request.getSourceMessage().getMessageId();
            this.userId = request.getOwner().getId();
            this.fullName = request.getOwner().getFullName();
            this.userName = request.getOwner().getUserName();
            this.result = request.getResult();
            this.date = request.getCreated();
            this.chatId = request.getChat().getChatId();
        }

        ScoreLine(String data) {
            String[] fields = data.split(SPLIT_CHAR);
            this.id = toInt(fields[0]);
            this.userId = toInt(fields[1]);
            this.fullName = fields[2];
            this.userName = fields[3];
            this.result = CatRequestVote.valueOf(fields[4]);
            this.date =
                    isEmpty(fields[5])
                            ? null
                            : LocalDateTime.parse(fields[5], DateTimeFormatter.ISO_DATE_TIME);
            this.chatId = toLong(fields[6]);
        }

        private Long toLong(String field) {
            return isEmpty(field) ? null : Long.valueOf(field);
        }

        private Integer toInt(String field) {
            return isEmpty(field) ? null : Integer.valueOf(field);
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
