package xyz.rexhaif.jstore;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import xyz.rexhaif.jstore.impl.MapdbStorage;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ApiVerticle extends AbstractVerticle {

    public static final Charset UTF = StandardCharsets.UTF_8;

    private HttpServer mServer;
    private Router mRouter;
    private Storage mStorage;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        JsonObject conf = context.config();

        mStorage = new MapdbStorage(vertx, Consts.dbConfig.make());

        mServer = vertx.createHttpServer(
                new HttpServerOptions()
                        .setPort(Consts.PORT)
                        .setTcpKeepAlive(true)
        );

        mRouter = Router.router(vertx);

        mRouter.route().handler(BodyHandler.create());
        mRouter.route().handler(LoggerHandler.create());

        mRouter.post("/:key/").handler(
                rtx -> {
                    byte[] key = rtx.request().getParam("key").getBytes(UTF);
                    byte[] data = rtx.getBody().getBytes();

                    mStorage.create(key, data, result -> {
                        if(result.succeeded()) {
                            rtx.response().setStatusCode(201).end();
                        } else {
                            rtx.response().setStatusCode(501).end();
                        }
                    });
                }
        );
        mRouter.get("/:key/").handler(
                rtx -> {
                    byte[] key = rtx.request().getParam("key").getBytes(UTF);

                    mStorage.read(key, result -> {
                        if (result.succeeded()) {
                            rtx.response()
                                    .setChunked(true)
                                    .setStatusCode(200)
                                    .write(
                                            Buffer.buffer(
                                                    result.result()
                                            )
                                    )
                                    .end();
                        } else {
                            mStorage.existKey(key, existence -> { //TODO: Handle incorrect situations
                                if (!existence.result()) {
                                    rtx.response().setStatusCode(404).end();
                                } else {
                                    rtx.response().setStatusCode(501).end();
                                }
                            });
                        }
                    });
                }
        );
        mRouter.put("/:key/").handler(
                rtx -> {
                    byte[] key = rtx.request().getParam("key").getBytes(UTF);
                    byte[] data = rtx.getBody().getBytes();

                    mStorage.update(key, data, result -> {
                        if(result.succeeded()) {
                            rtx.response().setStatusCode(201).end();
                        } else {
                            rtx.response().setStatusCode(501).end();
                        }
                    });
                }
        );
        mRouter.delete("/:key/").handler(
                rtx -> {
                    byte[] key = rtx.request().getParam("key").getBytes(UTF);

                    mStorage.delete(key, result -> {
                        if (result.succeeded()) {
                            rtx.response().setStatusCode(200).end();
                        } else {
                            rtx.response().setStatusCode(501).end();
                        }
                    });
                }
        );
        mRouter.head("/:key/").handler(
                rtx -> {
                    byte[] key = rtx.request().getParam("key").getBytes(UTF);

                    mStorage.existKey(key, result -> {
                        if (result.result()) {
                            rtx.response().setStatusCode(200).end();
                        } else {
                            rtx.response().setStatusCode(404).end();
                        }
                    });
                }
        );

        mServer.requestHandler(mRouter::accept).listen();

        startFuture.complete();
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        mServer.close(result -> {
            if (result.succeeded()) {
                mRouter.clear();
                mStorage.close();
                stopFuture.complete();
            }
        });
    }
}
