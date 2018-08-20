# @MaxKlyukin/time-writer

### Usage:

Write timestamps to DB:
```
MONGODB_URL="mongodb://127.0.0.1:27017" MONGODB_DB="time_writer" sh run.sh
```

Print timestamps from DB:
```
MONGODB_URL="mongodb://127.0.0.1:27017" MONGODB_DB="time_writer" sh run.sh -p
```

P.S.: Reconnection to DB in case of DB unavailability, is taken care of by MongoDB driver itself.
