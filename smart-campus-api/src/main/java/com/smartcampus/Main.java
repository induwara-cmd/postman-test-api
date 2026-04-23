package com.smartcampus;

import com.smartcampus.api.SmartCampusApplication;
import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public final class Main {
    private static final URI BASE_URI = URI.create("http://0.0.0.0:8080/");

    private Main() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, new SmartCampusApplication());
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
        System.out.println("Smart Campus API running at " + BASE_URI + "api/v1/");
        System.out.println("Press Ctrl+C to stop.");
        Thread.currentThread().join();
    }
}
