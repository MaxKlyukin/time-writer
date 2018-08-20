package me.maxklyukin.time_writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TimestampFlusherTest {

    private TimestampRepository timestampRepo;
    private TimestampFlusher timestampFlusher;

    @BeforeEach
    void createWriter() {
        timestampRepo = mock(TimestampRepository.class);
        timestampFlusher = new TimestampFlusher(timestampRepo);
    }

    @Test
    void it_flushes_when_repo_saves_successfully() throws TimestampSaveException {
        Queue<Instant> queue = new LinkedList<>(List.of(Instant.now(), Instant.now()));

        doThrow(new TimestampSaveException(null)).when(timestampRepo).save(any());
        timestampFlusher.flush(queue);

        assertEquals(2, queue.size());

        doNothing().when(timestampRepo).save(any());
        timestampFlusher.flush(queue);

        assertTrue(queue.isEmpty());
    }
}