package hk.ust.cse.comp4111;

import hk.ust.cse.comp4111.handler.*;
import org.apache.http.*;
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

    public static void main(String[] args) throws IOException, InterruptedException {
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

        HttpAsyncRequestHandler<?> loginHandler = new LoginRequestHandler();
        HttpAsyncRequestHandler<?> logoutHandler = new LogoutRequestHandler();
        MultiRequestHandler transactionHandler = new MultiRequestHandler();
        transactionHandler.registerPostHandler(new TransactionPostRequestHandler());
        transactionHandler.registerPutHandler(new TransactionActionRequestHandler());
        HttpAsyncRequestHandler<?> authTransactionHandler = new AuthRequestHandler(transactionHandler);

        MultiRequestHandler bookAndSearchHandler = new MultiRequestHandler();
        bookAndSearchHandler.registerGetHandler(new BookSearchRequestHandler());
        bookAndSearchHandler.registerPostHandler(new AddBookRequestHandler());
        HttpAsyncRequestHandler<?> authBookAddSearchHandler = new AuthRequestHandler(bookAndSearchHandler);

        MultiRequestHandler bookRequestHandler = new MultiRequestHandler();
        bookRequestHandler.registerPutHandler(new BookPutRequestHandler());
        bookRequestHandler.registerDeleteHandler(new BookDeleteRequestHandler());
        bookRequestHandler.registerGetHandler(new BookSearchRequestHandler());
        HttpAsyncRequestHandler<?> authBookRequestHandler = new AuthRequestHandler(bookRequestHandler);


        IOReactorConfig socketConfig = IOReactorConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();
        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(8080)
                .setHttpProcessor(httpproc)
                .setIOReactorConfig(socketConfig)
                .registerHandler("/BookManagementService/login", loginHandler)
                .registerHandler("/BookManagementService/logout", logoutHandler)
                .registerHandler("/BookManagementService/books", authBookAddSearchHandler)
                .registerHandler("/BookManagementService/books/*", authBookRequestHandler)
                .registerHandler("/BookManagementService/transaction", authTransactionHandler)
                .registerHandler("*", myRequestHandler)
                .setExceptionLogger(ExceptionLogger.STD_ERR)
                .create();
        server.start();
        server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.shutdown(1, TimeUnit.SECONDS);
            }
        });
    }
}
