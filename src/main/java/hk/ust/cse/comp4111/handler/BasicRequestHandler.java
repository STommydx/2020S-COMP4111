package hk.ust.cse.comp4111.handler;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.nio.protocol.*;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public abstract class BasicRequestHandler implements HttpAsyncRequestHandler<HttpRequest> {
    @Override
    public HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest request, HttpContext context) throws HttpException, IOException {
        return new BasicAsyncRequestConsumer();
    }

    @Override
    public void handle(HttpRequest data, HttpAsyncExchange httpExchange, HttpContext context) throws HttpException, IOException {
        HttpResponse response = httpExchange.getResponse();
        handle(data, response, context);
        httpExchange.submitResponse(new BasicAsyncResponseProducer(response));
    }

    public abstract void handle(HttpRequest data, HttpResponse response, HttpContext context) throws HttpException, IOException;

}
