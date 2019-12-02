package ru.v1as.tg.cat.callbacks.phase.poll;

public enum State {
    CREATED,
    SENDING,
    SENT,
    UPDATING,
    UPDATED,
    ERROR,
    CLOSED,
    CANCELED
}
