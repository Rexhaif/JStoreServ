package xyz.rexhaif.jstore.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import xyz.rexhaif.jstore.Storage;

/**
 * Created by Rexhaif on 5/15/2017.
 */
public class MapdbStorage implements Storage {

    private DB database;
    private BTreeMap<byte[], byte[]> storage;
    private Vertx vertx;

    public MapdbStorage(Vertx vertx, DB database) {

        this.database = database;
        storage = database.treeMap(
                "storage",
                Serializer.BYTE_ARRAY,
                Serializer.BYTE_ARRAY
        ).createOrOpen();
        this.vertx = vertx;

    }

    @Override
    public void create(byte[] key, byte[] data, Handler<AsyncResult<Void>> handler) {
        vertx.executeBlocking(
                future -> {
                    storage.put(key, data);
                    future.complete();
                },
                handler
        );
    }

    @Override
    public void read(byte[] key, Handler<AsyncResult<byte[]>> handler) {
        vertx.executeBlocking(
                future -> existKey(key, result -> {
                    if (result.result()) {
                        future.complete(storage.get(key));
                    } else {
                        future.fail("Not Found");
                    }
                }),
                handler
        );
    }

    @Override
    public void update(byte[] key, byte[] newData, Handler<AsyncResult<Void>> handler) {
        vertx.executeBlocking(
                future -> {
                    storage.replace(key, newData);
                    future.complete();
                },
                handler
        );
    }

    @Override
    public void delete(byte[] key, Handler<AsyncResult<Void>> handler) {
        vertx.executeBlocking(
                future -> {
                    storage.remove(key);
                    future.complete();
                },
                handler
        );
    }

    @Override
    public void existKey(byte[] key, Handler<AsyncResult<Boolean>> handler) {
        vertx.executeBlocking(
                future -> future.complete(storage.containsKey(key)),
                handler
        );
    }

    @Override
    public void close() {
        storage.close();
        database.close();
    }

}
