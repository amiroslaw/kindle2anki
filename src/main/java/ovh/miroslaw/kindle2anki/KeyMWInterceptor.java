package ovh.miroslaw.kindle2anki;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

/**
 * Interceptor for adding API key to the HTTP request for Merriam-Webster service.
 */
@Component
public class KeyMWInterceptor implements ClientHttpRequestInterceptor {
    @Value("${api.key}")
    String key;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        return execution.execute(new KeyHttpRequestWrapper(request), body);
    }

    private class KeyHttpRequestWrapper extends HttpRequestWrapper {
        private final URI uri;
        public KeyHttpRequestWrapper(HttpRequest request) {
            super(request);
            uri = request.getURI();
        }
        @Override
        public URI getURI() {
            return UriComponentsBuilder.fromUri(uri).queryParam("key", key)
                    .build().toUri();
        }
    }
}
