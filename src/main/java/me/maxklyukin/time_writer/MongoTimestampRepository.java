package me.maxklyukin.time_writer;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.time.Instant;

public class MongoTimestampRepository implements TimestampRepository {

    private final MongoCollection<Document> collection;

    public MongoTimestampRepository(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public Iterable<Instant> getAll() {
        return collection
                .find()
                .sort(Sorts.ascending("_id"))
                .map(doc -> doc.getDate("timestamp").toInstant());
    }

    @Override
    public void save(Instant instant) throws TimestampSaveException {
        try {
            collection.insertOne(new Document("timestamp", instant));
        } catch (MongoException e) {
            throw new TimestampSaveException(e);
        }
    }
}
