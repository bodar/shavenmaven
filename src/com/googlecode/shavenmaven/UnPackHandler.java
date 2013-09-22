package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.Closeables;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.HttpClient;

import java.io.ByteArrayOutputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

import static com.googlecode.shavenmaven.UnGZipHandler.gzipInputStream;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;

public class UnPackHandler implements HttpClient {
    private final HttpHandler handler;

    public UnPackHandler(final HttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        final Response response = handler.handle(request);
        if(request.uri().path().endsWith(".pack.gz")){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Closeables.using(new JarOutputStream(outputStream), new Block<JarOutputStream>() {
                @Override
                protected void execute(final JarOutputStream out) throws Exception {
                    Pack200.newUnpacker().unpack(gzipInputStream(response.entity().inputStream()), out);
                }
            });
            return modify(response).entity(outputStream.toByteArray()).build();
        }
        return response;
    }
}
