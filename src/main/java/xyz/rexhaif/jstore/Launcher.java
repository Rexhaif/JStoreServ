package xyz.rexhaif.jstore;

import io.vertx.core.Vertx;

public class Launcher {

    public static void main(String[] args) {
        System.err.println("Running StoreServ node");
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ApiVerticle(), res -> {
            System.err.println("StoreServ node Deployed");
        });
    }

}
