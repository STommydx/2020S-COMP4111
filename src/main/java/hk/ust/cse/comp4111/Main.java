package hk.ust.cse.comp4111;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.protocol.*;
import org.apache.http.protocol.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main (String[] args) throws IOException, InterruptedException {
        System.out.println("Hello World!");


        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("MyServer-HTTP/1.1"))
                .add(new ResponseContent())
                .add(new ResponseConnControl())
                .build();

        HttpAsyncRequestHandler<HttpRequest> myRequestHandler = new HttpAsyncRequestHandler<HttpRequest>() {

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

            public void handle(
                    HttpRequest request,
                    HttpResponse response,
                    HttpContext context) throws HttpException, IOException {
                response.setStatusCode(HttpStatus.SC_OK);
                response.setEntity(
                        new StringEntity("Hello Comp4111",
                                ContentType.TEXT_PLAIN));
            }

        };

        IOReactorConfig socketConfig = IOReactorConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();
        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(8080)
                .setHttpProcessor(httpproc)
                .setIOReactorConfig(socketConfig)
                .registerHandler("*", myRequestHandler)
                .create();
        server.start();
        server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.shutdown(5, TimeUnit.SECONDS);
            }
        });
    }
}
