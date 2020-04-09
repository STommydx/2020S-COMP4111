package hk.ust.cse.comp4111.handler;

import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.nio.AsyncRequestConsumer;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.AsyncServerRequestHandler;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityConsumer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.apache.hc.core5.http.nio.support.BasicRequestConsumer;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BasicRequestHandler implements AsyncServerRequestHandler<Message<HttpRequest, byte[]>> {

    private static final int NUM_OF_THREADS = 16;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_THREADS);

    @Override
    public AsyncRequestConsumer<Message<HttpRequest, byte[]>> prepare(HttpRequest request, EntityDetails entityDetails, HttpContext context) throws HttpException {
        return new BasicRequestConsumer<>(entityDetails != null ? new BasicAsyncEntityConsumer() : null);
    }

    @Override
    public void handle(Message<HttpRequest, byte[]> requestObject, ResponseTrigger responseTrigger, HttpContext context) throws HttpException, IOException {
        executorService.submit(() -> {
            try {
                responseTrigger.submitResponse(handle(requestObject, context), context);
            } catch (IOException | HttpException e) {
                try {
                    responseTrigger.submitResponse(AsyncResponseBuilder.create(HttpStatus.SC_SERVER_ERROR)
                            .setEntity(e.getLocalizedMessage())
                            .build(), context);
                } catch (HttpException | IOException httpException) {
                    httpException.printStackTrace();
                }
            }
        });
    }

    public abstract AsyncResponseProducer handle(Message<HttpRequest, byte[]> data, HttpContext context) throws IOException, HttpException;

}
