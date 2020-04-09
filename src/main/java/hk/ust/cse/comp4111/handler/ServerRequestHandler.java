package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.InternalServerException;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.support.AsyncResponseBuilder;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public abstract class ServerRequestHandler extends BasicRequestHandler {
    @Override
    public AsyncResponseProducer handle(Message<HttpRequest, byte[]> data, HttpContext context) throws IOException, HttpException {
        String httpMethod = data.getHead().getMethod();
        URI uri;
        try {
            uri = data.getHead().getUri();
        } catch (URISyntaxException ex) {
            throw new ProtocolException(ex.getMessage(), ex);
        }
        String path = uri.getPath();

        Map<String, String> queryParam = new HashMap<>();
        if (uri.getQuery() != null) {
            for (String para : uri.getRawQuery().split("&")) {
                int idx = para.indexOf('=');
                if (idx == -1) {
                    queryParam.put(URLDecoder.decode(para, "UTF-8"), "");
                } else {
                    queryParam.put(URLDecoder.decode(para.substring(0, idx), "UTF-8"), URLDecoder.decode(para.substring(idx + 1), "UTF-8"));
                }
            }
        }

        try {
            return handle(httpMethod, path, queryParam, data.getBody());
        } catch (InternalServerException e) {
            e.printStackTrace();
            return AsyncResponseBuilder.create(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .setEntity(e.getLocalizedMessage(), ContentType.create("text/plain", "UTF-8"))
                    .build();
        }
    }

    public abstract AsyncResponseProducer handle(String httpMethod, String path, Map<String, String> param, @Nullable byte[] requestBody) throws IOException, InternalServerException;

}
