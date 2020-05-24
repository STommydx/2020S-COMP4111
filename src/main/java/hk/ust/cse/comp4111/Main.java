package hk.ust.cse.comp4111;

import hk.ust.cse.comp4111.handler.*;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int LISTEN_PORT = 8080;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Server starting...");

        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("MyServer-HTTP/1.1"))
                .add(new ResponseContent())
                .add(new ResponseConnControl())
                .build();

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

        HttpAsyncRequestHandler<?> defaultHandler = new ServerRequestHandler() {
            @Override
            public void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) {
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            }
        };

        IOReactorConfig socketConfig = IOReactorConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();
        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(LISTEN_PORT)
                .setHttpProcessor(httpproc)
                .setIOReactorConfig(socketConfig)
                .registerHandler("/BookManagementService/login", loginHandler)
                .registerHandler("/BookManagementService/logout", logoutHandler)
                .registerHandler("/BookManagementService/books", authBookAddSearchHandler)
                .registerHandler("/BookManagementService/books/*", authBookRequestHandler)
                .registerHandler("/BookManagementService/transaction", authTransactionHandler)
                .registerHandler("*", defaultHandler)
                .setExceptionLogger(ExceptionLogger.STD_ERR)
                .create();

        server.start();

        System.out.println("Server listening on port " + LISTEN_PORT + "...");

        server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.shutdown(1, TimeUnit.SECONDS)));
    }
}
