package com.googlecode.shavenmaven;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class Http {

    public static  String urlOf(HttpServer server) throws MalformedURLException {
        InetSocketAddress address = server.getAddress();
        return String.format("http://localhost:%s/", address.getPort());
    }

    public static HttpServer createHttpsServer(final HttpHandler httpHandler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/", httpHandler);
        server.setExecutor(newFixedThreadPool(1));
        server.start();
        return server;
    }

    public static HttpHandler returnResponse(final int code, final String value) {
        return new HttpHandler() {
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.sendResponseHeaders(code, 0);
                httpExchange.getResponseBody().write(value.getBytes());
                httpExchange.close();
            }
        };
    }
}
