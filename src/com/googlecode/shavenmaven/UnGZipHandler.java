package com.googlecode.shavenmaven;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.HttpClient;

import java.util.zip.GZIPInputStream;

import static com.googlecode.utterlyidle.ResponseBuilder.modify;

public class UnGZipHandler implements HttpClient {
    private final HttpHandler handler;

    public UnGZipHandler(final HttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        Response response = handler.handle(request);
        if("gzip".equalsIgnoreCase(response.headers().getValue(HttpHeaders.CONTENT_ENCODING))) {
            return modify(response).entity(new GZIPInputStream(response.entity().inputStream())).removeHeaders(HttpHeaders.CONTENT_ENCODING).build();
        }
        return response;
    }
}
