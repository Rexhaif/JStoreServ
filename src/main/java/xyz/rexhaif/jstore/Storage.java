package xyz.rexhaif.jstore;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import javax.xml.ws.AsyncHandler;

/**
 * Created by Rexhaif on 4/20/2017.
 */
public interface Storage {

    void create(byte[] key, byte[] data, Handler<AsyncResult<Void>> handler);

    void read(byte[] key, Handler<AsyncResult<byte[]>> handler);

    void update(byte[] key, byte[] newData, Handler<AsyncResult<Void>> handler);

    void delete(byte[] key, Handler<AsyncResult<Void>> handler);

    void existKey(byte[] key, Handler<AsyncResult<Boolean>> handler);

    void close();

}
