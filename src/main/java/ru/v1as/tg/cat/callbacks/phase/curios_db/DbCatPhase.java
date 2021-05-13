package ru.v1as.tg.cat.callbacks.phase.curios_db;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DbCatPhase {

    private DbCatPhaseNode start;
    private List<DbCatPhaseNode> nodes;
}
