package io.mateu.common.consolidador;

import io.vertx.core.AbstractVerticle;


public class VerticeTest extends AbstractVerticle {
    public void start() {
        vertx.createHttpServer().requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!");
        }).listen(8080);
    }
}
