package com.googlecode.shavenmaven;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.HttpMessage;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.HttpClient;

import static com.googlecode.utterlyidle.HttpMessage.Builder.header;
import static com.googlecode.totallylazy.functions.Functions.modify;
import static java.lang.System.getProperty;

public class UserAgentHandler implements HttpClient {
    private final HttpHandler httpHandler;

    public UserAgentHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public Response handle(Request request) throws Exception {
        return httpHandler.handle(modify(request,
                HttpMessage.Builder.header(HttpHeaders.USER_AGENT, getProperty("shavenmaven.user.agent", "Mozilla/5.0 (compatible; SM)"))));
    }
}
