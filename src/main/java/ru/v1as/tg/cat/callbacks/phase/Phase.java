package ru.v1as.tg.cat.callbacks.phase;

public interface Phase<T extends PhaseContext> {

    void open(T phaseContext);

    void close();
}
