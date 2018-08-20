package me.maxklyukin.time_writer;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.cli.*;
import org.bson.Document;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    enum RunMode {WRITE, PRINT}

    public static void main(String[] args) {
        RunMode mode = determineMode(args);

        MongoDatabase database = connectDatabase();
        MongoCollection<Document> collection = database.getCollection("timestamps");

        TimestampRepository repo = new MongoTimestampRepository(collection);

        switch (mode) {
            case WRITE:
                TimestampFlusher flusher = new TimestampFlusher(repo);
                TimestampWriter writer = new TimestampWriter(flusher);

                System.out.println("Writing timestamps... Press enter to stop.");
                writer.start(1, TimeUnit.SECONDS);

                new Scanner(System.in).nextLine();
                writer.stop();

                break;
            case PRINT:
                TimestampPrinter printer = new TimestampPrinter(repo);

                System.out.println("Printing timestamps...");
                printer.print();

                break;
        }
    }

    private static MongoDatabase connectDatabase() {
        String url = System.getenv("MONGODB_URL");
        String dbName = System.getenv("MONGODB_DB");

        if (url == null || dbName == null) {
            System.err.println("Please provide MONGODB_URL and MONGODB_DB environment variables.");
            System.exit(1);
        }

        MongoClient mongoClient = MongoClients.create(url);

        return mongoClient.getDatabase(dbName);
    }

    private static RunMode determineMode(String[] args) {
        Options options = new Options();
        options.addOption("help", "print this message");
        options.addOption("p", "print existing timestamps from DB");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printCmdParseError(e);
            printHelpMessage(options);
            System.exit(1);
            return null;
        }

        if (cmd.hasOption("help")) {
            printHelpMessage(options);
            System.exit(0);
            return null;
        } else if (cmd.hasOption("p")) {
            return RunMode.PRINT;
        } else {
            return RunMode.WRITE;
        }
    }

    private static void printCmdParseError(ParseException e) {
        System.err.println(e.getMessage());
    }

    private static void printHelpMessage(Options options) {
        String header = "Program writes timestamps to DB and prints timestamps from DB.";
        String footer = "To write timestamps to DB run program without arguments.";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("sh run.sh", header, options, footer);
    }
}
