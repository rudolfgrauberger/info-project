package cloudgateway.app.service;

import org.springframework.http.HttpHeaders;

import java.net.URI;

public interface ApiGatewayHttpHeader {

   HttpHeaders getHeaders(URI requestedURI);
}
