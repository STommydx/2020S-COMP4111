package hk.ust.cse.comp4111;

import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.handler.*;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.impl.bootstrap.AsyncServerBootstrap;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncServer;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.AsyncServerRequestHandler;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.apache.hc.core5.http.protocol.*;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello World!");


        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("MyServer-HTTP/1.1"))
                .add(new ResponseContent())
                .add(new ResponseConnControl())
                .build();

        AsyncServerRequestHandler<?> loginHandler = new LoginRequestHandler();
        AsyncServerRequestHandler<?> logoutHandler = new LogoutRequestHandler();
        MultiRequestHandler transactionHandler = new MultiRequestHandler();
        transactionHandler.registerPostHandler(new TransactionPostRequestHandler());
        transactionHandler.registerPutHandler(new TransactionActionRequestHandler());
        AsyncServerRequestHandler<?> authTransactionHandler = new AuthRequestHandler(transactionHandler);

        MultiRequestHandler bookAndSearchHandler = new MultiRequestHandler();
        bookAndSearchHandler.registerGetHandler(new BookSearchRequestHandler());
        bookAndSearchHandler.registerPostHandler(new AddBookRequestHandler());
        AsyncServerRequestHandler<?> authBookAddSearchHandler = new AuthRequestHandler(bookAndSearchHandler);

        MultiRequestHandler bookRequestHandler = new MultiRequestHandler();
        bookRequestHandler.registerPutHandler(new BookPutRequestHandler());
        bookRequestHandler.registerDeleteHandler(new BookDeleteRequestHandler());
        bookRequestHandler.registerGetHandler(new BookSearchRequestHandler());
        AsyncServerRequestHandler<?> authBookRequestHandler = new AuthRequestHandler(bookRequestHandler);

        AsyncServerRequestHandler<?> defaultHandler = new ServerRequestHandler() {
            @Override
            public AsyncResponseProducer handle(String httpMethod, String path, Map<String, String> param, @Nullable byte[] requestBody) throws IOException, InternalServerException {
                return AsyncResponseBuilder.create(HttpStatus.SC_NOT_FOUND).build();
            }
        };


        IOReactorConfig socketConfig = IOReactorConfig.custom()
                .setSoTimeout(15, TimeUnit.SECONDS)
                .setTcpNoDelay(true)
                .build();
        final HttpAsyncServer server = AsyncServerBootstrap.bootstrap()
                .setHttpProcessor(httpproc)
                .setIOReactorConfig(socketConfig)
                .register("/BookManagementService/login", loginHandler)
                .register("/BookManagementService/logout", logoutHandler)
                .register("/BookManagementService/books", authBookAddSearchHandler)
                .register("/BookManagementService/books/*", authBookRequestHandler)
                .register("/BookManagementService/transaction", authTransactionHandler)
                .register("*", defaultHandler)
                .setExceptionCallback(Throwable::printStackTrace)
                .create();
        server.start();
        server.listen(new InetSocketAddress(8080));
        server.awaitShutdown(TimeValue.MAX_VALUE);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.close(CloseMode.GRACEFUL)));
    }
}
