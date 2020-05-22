package hk.ust.cse.comp4111.handler;

import hk.ust.cse.comp4111.exception.InternalServerException;
import hk.ust.cse.comp4111.exception.LockWaitTimeoutException;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.protocol.HttpContext;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public abstract class ServerRequestHandler extends BasicRequestHandler {
    @Override
    public void handle(HttpRequest data, HttpResponse response, HttpContext context) throws IOException {
        RequestLine requestLine = data.getRequestLine();
        String httpMethod = requestLine.getMethod();
        URI uri = URI.create(data.getRequestLine().getUri());
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

        InputStream requestBody = null;
        if (data instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) data).getEntity();
            if (entity.getContentLength() > 0) requestBody = entity.getContent();
        }

        try {
            handle(httpMethod, path, queryParam, requestBody, response);
        } catch (InternalServerException e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            response.setEntity(new NStringEntity(e.getLocalizedMessage(), ContentType.create("text/plain", "UTF-8")));
        } catch (LockWaitTimeoutException e) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }

    public abstract void handle(String httpMethod, String path, Map<String, String> param, @Nullable InputStream requestBody, HttpResponse response) throws IOException, InternalServerException, LockWaitTimeoutException;

}
