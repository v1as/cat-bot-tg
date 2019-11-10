package ru.v1as.tg.cat.service.init;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DumpService {

    private final EntityManager em;

    public String write() {
        final String fileName = String.format("cat_database_%s.sql", new Date().getTime());
        final String sqlString = String.format("SCRIPT TO '%s';", fileName);
        final Query nativeQuery = em.createNativeQuery(sqlString);
        nativeQuery.getResultList();
        return fileName;
    }

    public void load(String fileName) {
        final String sqlString = String.format("RUNSCRIPT FROM '%s';", fileName);
        final Query nativeQuery = em.createNativeQuery(sqlString);
        nativeQuery.executeUpdate();
    }

    public void deleteAll() {
        final Query nativeQuery = em.createNativeQuery("DROP ALL OBJECTS");
        nativeQuery.executeUpdate();
    }

}
