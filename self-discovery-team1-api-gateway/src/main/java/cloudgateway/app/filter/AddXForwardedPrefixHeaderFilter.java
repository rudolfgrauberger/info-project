package cloudgateway.app.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;


/**
 * Workaround for the 'Spring Cloud Gateway doesn't add X-Forwarded-Prefix header'-Bug, solution copied from
 * the pull request https://github.com/spring-cloud/spring-cloud-gateway/pull/382.
 *
 * The original XForwardedHeadersFilter has the order 0, this workaroundfilter has 1. This means, that
 * the original filters adds first all needed/supported headers and than this filter check if no
 * X-Forwarded-Prefix is set, add them to every request.
 */
public class AddXForwardedPrefixHeaderFilter implements HttpHeadersFilter, Ordered {
   
   /** X-Forwarded-Prefix Header */
   public static final String X_FORWARDED_PREFIX_HEADER = "X-Forwarded-Prefix";

   @Override
   public HttpHeaders filter(HttpHeaders input, ServerWebExchange exchange) {
      ServerHttpRequest request = exchange.getRequest();
      HttpHeaders original = input;
      HttpHeaders updated = new HttpHeaders();

      if (input.containsKey(X_FORWARDED_PREFIX_HEADER)) {
         return original;
      }

      original.entrySet().stream()
            .forEach(entry -> updated.addAll(entry.getKey(), entry.getValue()));

      LinkedHashSet<URI> originalUris = exchange.getAttribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
      URI requestUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);

      if(originalUris != null && requestUri != null) {
         originalUris.stream().forEach(originalUri -> {
            String prefix = originalUri.getPath();

            //strip trailing slashes before checking if request path is end of original path
            String originalUriPath = originalUri.getPath().substring(0, originalUri.getPath().length() - (originalUri.getPath().endsWith("/") ? 1 : 0));
            String requestUriPath = requestUri.getPath().substring(0, requestUri.getPath().length() - (requestUri.getPath().endsWith("/") ? 1 : 0));

            if(requestUriPath!=null && (originalUriPath.endsWith(requestUriPath))) {
               prefix = originalUriPath.replace(requestUriPath, "");
            }

            if (prefix != null && prefix.length() > 0 && prefix.length() < originalUri.getPath().length()) {
               write(updated, X_FORWARDED_PREFIX_HEADER, prefix, true);
            }
         });
      }
      return updated;
   }


   private void write(HttpHeaders headers, String name, String value, boolean append) {

      if (append) {
         headers.add(name, value);
         // these headers should be treated as a single comma separated header
         List<String> values = headers.get(name);
         String delimitedValue = StringUtils.collectionToCommaDelimitedString(values);

         headers.set(name, delimitedValue);
      } else {
         headers.set(name, value);
      }
   }

   @Override
   public int getOrder() {
      return 1;
   }
}