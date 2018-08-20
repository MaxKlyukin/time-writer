package me.maxklyukin.time_writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Queue;

public class TimestampFlusher {

    private final static Logger logger = LoggerFactory.getLogger(TimestampFlusher.class);

    private final TimestampRepository timestampRepo;

    public TimestampFlusher(TimestampRepository timestampRepo) {
        this.timestampRepo = timestampRepo;
    }

    public void flush(Queue<Instant> queue) {
        while (!queue.isEmpty()) {
            boolean saved = saveTimestamp(queue);
            if (!saved) {
                return;
            }
        }
    }

    private boolean saveTimestamp(Queue<Instant> queue) {
        Instant instant = queue.peek();
        try {
            timestampRepo.save(instant);
            queue.poll();
            return true;
        } catch (TimestampSaveException e) {
            logger.warn("Could not save timestamp.", e);
            return false;
        }
    }
}
