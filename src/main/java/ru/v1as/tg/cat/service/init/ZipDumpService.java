package ru.v1as.tg.cat.service.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service("dumpService")
@RequiredArgsConstructor
public class ZipDumpService implements DumpService {

    public static final String ARCH_SUFFIX = ".zip";

    @Qualifier("sqlDumpService")
    private final DumpService dumpService;

    @Override
    @SneakyThrows
    public String write() {
        String notCompressedFile = dumpService.write();
        String archFile = notCompressedFile + ARCH_SUFFIX;

        try (FileOutputStream fos = new FileOutputStream(archFile);
                ZipOutputStream zos = new ZipOutputStream(fos);
                FileInputStream in = new FileInputStream(notCompressedFile)) {
            ZipEntry ze = new ZipEntry(notCompressedFile);
            zos.putNextEntry(ze);
            IOUtils.copy(in, zos);
        }
        log.info("File '{}' compressed to '{}'", notCompressedFile, archFile);
        if (new File(notCompressedFile).delete()) {
            return archFile;
        }
        throw new IOException("Can't delete file: " + notCompressedFile);
    }

    @Override
    @SneakyThrows
    public void deleteAllAndLoadDump(String archFile) {
        String file = archFile.substring(0, archFile.length() - ARCH_SUFFIX.length());
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archFile));
                FileOutputStream fos = new FileOutputStream(file)) {
            zis.getNextEntry();
            IOUtils.copy(zis, fos);
            zis.closeEntry();
        }
        log.info("File '{}' uncompressed to '{}'", archFile, file);
        if (!new File(archFile).delete()) {
            throw new IOException("Can't delete file: " + archFile);
        }
        dumpService.deleteAllAndLoadDump(file);
    }
}
