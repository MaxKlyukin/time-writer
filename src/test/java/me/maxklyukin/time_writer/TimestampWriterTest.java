package me.maxklyukin.time_writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TimestampWriterTest {

    private TimestampRepository repo;
    private TimestampFlusher flusher;
    private TimestampWriter writer;

    @BeforeEach
    void createWriter() {
        repo = mock(TimestampRepository.class);
        flusher = new TimestampFlusher(repo);

        writer = new TimestampWriter(flusher);
    }

    @Test
    void it_saves_to_repo() throws TimestampSaveException {
        writer.start(10, TimeUnit.MILLISECONDS);
        waitFor(25, TimeUnit.MILLISECONDS);
        writer.stop();

        verify(repo, atLeast(3)).save(any());
    }

    private void waitFor(int timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}