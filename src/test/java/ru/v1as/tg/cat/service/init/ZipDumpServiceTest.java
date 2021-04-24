package ru.v1as.tg.cat.service.init;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ZipDumpServiceTest {

    public static final String FILE_CONTENT = "Hello world!";
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    @SneakyThrows
    public void should_pack_and_unpack() {
        TestDumpService testDumpService = new TestDumpService(FILE_CONTENT);
        ZipDumpService zipDumpService = new ZipDumpService(testDumpService);

        zipDumpService.deleteAllAndLoadDump(zipDumpService.write());

        assertEquals(FILE_CONTENT, testDumpService.getLastReadLine());
    }

    @Getter
    @RequiredArgsConstructor
    private class TestDumpService implements DumpService {

        private String lastReadLine;
        private final String string;

        @Override
        @SneakyThrows
        public String write() {
            File file = folder.newFile("test.txt");
            Files.write(file.toPath(), string.getBytes());
            return file.getAbsolutePath();
        }

        @Override
        @SneakyThrows
        public void deleteAllAndLoadDump(String fileName) {
            this.lastReadLine = new String(Files.readAllBytes(Paths.get(fileName)));
        }
    }
}
