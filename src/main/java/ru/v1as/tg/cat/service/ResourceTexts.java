package ru.v1as.tg.cat.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class ResourceTexts {

    @SneakyThrows
    public String load(@NonNull String name) {
        if (!name.startsWith("text/")) {
            name = "text/" + name;
        }
        if (!name.endsWith(".txt")) {
            name += ".txt";
        }
        return new BufferedReader(
                        new InputStreamReader(new ClassPathResource(name).getInputStream(), UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
