package hk.ust.cse.comp4111.handler;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.*;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BasicRequestHandler implements HttpAsyncRequestHandler<HttpRequest> {

    private static int NUM_OF_THREADS = 16;
    private static ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_THREADS);

    @Override
    public HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest request, HttpContext context) throws HttpException, IOException {
        return new BasicAsyncRequestConsumer();
    }

    @Override
    public void handle(HttpRequest data, HttpAsyncExchange httpExchange, HttpContext context) throws HttpException, IOException {
        executorService.submit(() -> {
            HttpResponse response = httpExchange.getResponse();
            try {
                handle(data, response, context);
            } catch (IOException e) {
                response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                response.setEntity(new NStringEntity(e.getLocalizedMessage(), ContentType.create("plain/text", "UTF-8")));
            }
            httpExchange.submitResponse(new BasicAsyncResponseProducer(response));
        });
    }

    public abstract void handle(HttpRequest data, HttpResponse response, HttpContext context) throws IOException;

}
