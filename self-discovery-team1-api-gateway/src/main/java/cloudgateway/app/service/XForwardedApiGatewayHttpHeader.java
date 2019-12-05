package cloudgateway.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class XForwardedApiGatewayHttpHeader implements ApiGatewayHttpHeader {

   @Override
   public HttpHeaders getHeaders(URI requestedURI) {
      HttpHeaders headers = new HttpHeaders();
      headers.set("X-Forwarded-Host", requestedURI.getAuthority());
      headers.set("X-Forwarded-Port", Integer.toString(requestedURI.getPort()));
      headers.set("X-Forwarded-Proto", requestedURI.getScheme());

      return headers;
   }
}
