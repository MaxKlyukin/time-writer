package me.maxklyukin.time_writer;

import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimestampWriter {

    private final TimestampFlusher timestampFlusher;

    private final ConcurrentLinkedQueue<Instant> queue = new ConcurrentLinkedQueue<>();

    private final ScheduledExecutorService spawnExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService flushExecutor = Executors.newSingleThreadScheduledExecutor();

    public TimestampWriter(TimestampFlusher timestampFlusher) {
        this.timestampFlusher = timestampFlusher;
    }

    public void start(long rate, TimeUnit timeUnit) {
        guardRate(rate, timeUnit);

        spawnExecutor.scheduleAtFixedRate(this::spawn, 0, rate, timeUnit);
        flushExecutor.scheduleAtFixedRate(this::flush, 0, timeUnit.toMillis(rate) / 10, TimeUnit.MILLISECONDS);
    }

    private void spawn() {
        queue.add(Instant.now());
    }

    private void flush() {
        timestampFlusher.flush(queue);
    }

    private void guardRate(long rate, TimeUnit timeUnit) {
        if (timeUnit.toMillis(rate) < 10) {
            throw new RuntimeException("Invalid rate, must be greater or equal 10 milliseconds.");
        }
    }

    public void stop() {
        stopSpawning();
        stopFlushing();
    }

    private void stopSpawning() {
        spawnExecutor.shutdownNow();
        try {
            spawnExecutor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    private void stopFlushing() {
        flushExecutor.shutdown();
        try {
            flushExecutor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }
}
