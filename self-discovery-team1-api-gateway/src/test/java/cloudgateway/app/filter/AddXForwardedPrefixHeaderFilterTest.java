package cloudgateway.app.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.LinkedHashSet;

import static cloudgateway.app.filter.AddXForwardedPrefixHeaderFilter.X_FORWARDED_PREFIX_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AddXForwardedPrefixHeaderFilter.class)
public class AddXForwardedPrefixHeaderFilterTest {

    @Test
    public void AddXForwardedPrefixIfItIsMissing() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest
            .get("http://localhost:8080/EXP-MS1/students")
            .remoteAddress(new InetSocketAddress(InetAddress.getByName("10.0.0.1"), 80))
            .header(HttpHeaders.HOST, "myhost")
            .build();

        AddXForwardedPrefixHeaderFilter filter = new AddXForwardedPrefixHeaderFilter();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        LinkedHashSet<URI> originalUris = new LinkedHashSet<>();

        // Add (fake) original-URIs and request-URI (from where the request originally came from) so it's not 'null'.
        // This is required for the filter to do something
        originalUris.add(new URI("http://localhost:8888/EXP-MS1/"));
        originalUris.add(new URI("lb://EXP-MS1:8888/"));
        exchange.getAttributes().put(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, originalUris);
        URI requestUri = new URI("http://localhost:8081/");
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUri);

        HttpHeaders headers = filter.filter(request.getHeaders(), exchange);

        assertThat(headers).containsKeys(X_FORWARDED_PREFIX_HEADER);

        assertThat(headers.getFirst(X_FORWARDED_PREFIX_HEADER)).isEqualTo("/EXP-MS1");
    }

    @Test
    public void SkipAddingXForwardedHeaderIfItsAlreadyThere() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest
            .get("http://localhost:8080/EXP-MS1/students")
            .remoteAddress(new InetSocketAddress(InetAddress.getByName("10.0.0.1"), 80))
            .header(HttpHeaders.HOST, "myhost")
            .header(X_FORWARDED_PREFIX_HEADER, "I-AM-ALEARDY-HERE")
            .build();

        AddXForwardedPrefixHeaderFilter filter = new AddXForwardedPrefixHeaderFilter();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        LinkedHashSet<URI> originalUris = new LinkedHashSet<>();

        // Add (fake) original-URIs and request-URI (from where the request originally came from) so it's not 'null'.
        // This is required for the filter to do something
        originalUris.add(new URI("http://localhost:8888/EXP-MS1/"));
        originalUris.add(new URI("lb://EXP-MS1:8888/"));
        exchange.getAttributes().put(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, originalUris);
        URI requestUri = new URI("http://localhost:8081/");
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUri);

        HttpHeaders headers = filter.filter(request.getHeaders(), exchange);

        assertThat(headers).containsKeys(X_FORWARDED_PREFIX_HEADER);

        // Test if the Filter returns the original (unmodified) request if there already is a X-Forwarded-Prefix-Header
        assertThat(headers.get(X_FORWARDED_PREFIX_HEADER)).contains("I-AM-ALEARDY-HERE");
        assertThat(headers.get(X_FORWARDED_PREFIX_HEADER)).size().isEqualTo(1);
    }


}
