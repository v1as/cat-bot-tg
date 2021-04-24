package ru.v1as.tg.cat.service.init;

public interface DumpService {

    String write();

    void deleteAllAndLoadDump(String fileName);

}
