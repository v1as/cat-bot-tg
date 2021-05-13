package ru.v1as.tg.cat.callbacks.phase.curios_db;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhaseTest;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.RegattaDreamPhaseTest;

@Import({
    RegattaDreamPhaseTest.CuriosConfiguration.class,
    DbCuriosCatPhaseProcessorTest.DbCuriosCatPhaseProcessorConfiguration.class
})
public class DbCuriosCatPhaseProcessorTest extends AbstractCuriosCatPhaseTest {

    @Autowired DbCuriosCatPhaseProcessor phase;

    @Test
    public void name() {
        DbCatPhase phase = new DbCatPhase();
        DbCatPhaseNode startNode = new DbCatPhaseNode();
        DbCatPhaseNode secondNode = new DbCatPhaseNode();
        phase.setNodes(ImmutableList.of(startNode, secondNode));
        this.phase.setDbCatPhase(
                phase);
    }

    static class DbCuriosCatPhaseProcessorConfiguration {

        @Bean
        DbCuriosCatPhaseProcessor testDbCatPhase() {
            return new DbCuriosCatPhaseProcessor();
        }
    }
}
