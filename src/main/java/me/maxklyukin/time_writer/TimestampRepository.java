package me.maxklyukin.time_writer;

import java.time.Instant;

public interface TimestampRepository {
    Iterable<Instant> getAll();
    void save(Instant instant) throws TimestampSaveException;
}
