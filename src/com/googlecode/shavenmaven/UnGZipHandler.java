package com.googlecode.shavenmaven;

import com.googlecode.utterlyidle.*;
import com.googlecode.utterlyidle.handlers.HttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import static com.googlecode.totallylazy.functions.Functions.modify;
import static com.googlecode.utterlyidle.Parameters.Builder.remove;

public class UnGZipHandler implements HttpClient {
    private final HttpHandler handler;

    public UnGZipHandler(final HttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public Response handle(final Request request) throws Exception {
        Response response = handler.handle(modify(request,
                HttpMessage.Builder.header(HttpHeaders.ACCEPT_ENCODING, "gzip")));
        if("gzip".equalsIgnoreCase(response.headers().getValue(HttpHeaders.CONTENT_ENCODING))) {
            return response.entity(gzipInputStream(response.entity().inputStream())).headers(remove(HttpHeaders.CONTENT_ENCODING));
        }
        return response;
    }

    public static GZIPInputStream gzipInputStream(InputStream inputStream) throws IOException {
        if(inputStream instanceof GZIPInputStream) return (GZIPInputStream) inputStream;
        return new GZIPInputStream(inputStream);
    }
}
