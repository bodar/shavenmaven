package com.googlecode.shavenmaven;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.HttpClient;

import java.io.ByteArrayOutputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;

import static com.googlecode.utterlyidle.ResponseBuilder.modify;

public class UnPackHandler implements HttpClient {
    private final HttpHandler handler;

    public UnPackHandler(final HttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        Response response = handler.handle(request);
        if(request.uri().path().endsWith(".pack.gz")){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Pack200.newUnpacker().unpack(new GZIPInputStream(response.entity().inputStream()), new JarOutputStream(outputStream));
            return modify(response).entity(outputStream.toByteArray()).build();
        }
        return response;
    }
}
