package cloudgateway.app.service;

import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.util.List;

public interface ServiceRootCollector {

   public List<String> getRoots(URI httpRequest);
}
