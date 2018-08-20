package me.maxklyukin.time_writer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampPrinter {

    private final TimestampRepository timestampRepo;

    private final SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");

    public TimestampPrinter(TimestampRepository timestampRepo) {
        this.timestampRepo = timestampRepo;
    }

    public void print() {
        timestampRepo.getAll().forEach(instant ->
                System.out.println(dtFormat.format(Date.from(instant)))
        );
    }
}
