package cloudgateway.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AggregateEndpointsRootService implements RootService {

   @Autowired
   private ServiceRootCollector collector;

   @Autowired
   private HalGenerator halGenerator;

   @Override
   public String getAggregatedEndpoints(URI httpRequest) {
      List<String> rootResults = collector.getRoots(httpRequest);

      return halGenerator.getHalString(rootResults);
   }
}
