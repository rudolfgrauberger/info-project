package cloudgateway.app.service;

import org.springframework.http.HttpHeaders;

import java.util.Set;

public interface HalParser {

    String getJson(String startURI, HttpHeaders header);
    String getJson(String startURI);

    Set<String> getResources(String startingPoint);

    Set<String> discoverAllUris(String entryPoint);

}