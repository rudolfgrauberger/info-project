package cloudgateway.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class JsonServiceRootCollector implements ServiceRootCollector {

   @Autowired
   private HalParser halParser;

   @Autowired
   private ApiGatewayHttpHeader httpHeader;

   private final DiscoveryClient discoveryClient;

   public JsonServiceRootCollector(DiscoveryClient discoveryClient) {
      this.discoveryClient = discoveryClient;
   }

   @Override
   public List<String> getRoots(URI httpRequest) {
      List<String> rootJsonList = new ArrayList<String>();

      List<String> lsServices = discoveryClient.getServices();

      lsServices.stream()
            .map(discoveryClient::getInstances)
            .filter(instances -> !instances.isEmpty())
            .map(instances -> instances.get(0))
            .forEach(serviceInstance -> {
               String serviceUri = serviceInstance.getUri().toString();

               HttpHeaders test = httpHeader.getHeaders(httpRequest);

               String byHalParser = halParser.getJson(serviceUri, test);
               rootJsonList.add(byHalParser);
            });

      return rootJsonList;
   }
}
