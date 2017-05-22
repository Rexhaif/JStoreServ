package xyz.rexhaif.jstore;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import xyz.rexhaif.jstore.impl.MapdbMemoryStorage;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ApiVerticle extends AbstractVerticle {

    public static final Charset UTF = StandardCharsets.UTF_8;

    private HttpServer mServer;
    private Router mRouter;
    private Map<byte[], byte[]> mStorage;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        JsonObject conf = context.config();

        mStorage = new ConcurrentHashMap<>();

        mServer = vertx.createHttpServer(
                new HttpServerOptions()
                        .setPort(Consts.PORT)
                        .setTcpKeepAlive(true)
        );

        mRouter = Router.router(vertx);

        mRouter.route().handler(BodyHandler.create());
        mRouter.route().handler(LoggerHandler.create());

        mRouter.post("/:key").handler(
                rtx -> {
                    String key = rtx.request().getParam("key");
                    byte[] data = rtx.getBody().getBytes();
                    rtx.response().setChunked(true).end(key);
                }
        );
        mRouter.get("/:key/").handler(
                rtx -> {
                    String key = rtx.request().getParam("key");

                }
        );
        mRouter.put("/:key/").handler(
                rtx -> {
                    String key = rtx.request().getParam("key");
                    byte[] data = rtx.getBody().getBytes();


                }
        );
        mRouter.delete("/:key/").handler(
                rtx -> {
                    String key = rtx.request().getParam("key");

                }
        );

        mServer.requestHandler(mRouter::accept).listen();

        startFuture.complete();
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {

    }
}
