package cloudgateway.app.ServiceDiscovery;

import cloudgateway.app.service.HalParser;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

public class AggregateRootTraverseRoutes implements AutoDiscoveryRoutes {

   private HalParser halParser;

   public AggregateRootTraverseRoutes(HalParser halParser) {
      this.halParser = halParser;
   }

   @Override
   public Set<String> getRoutes(URI microservice) {

      Set<String> aggregateRoots = halParser.getResources(microservice.toString());

      return aggregateRoots.stream()
              .map(x -> { return x.replaceAll(microservice.toString(), "").concat("/**"); })
              .collect(Collectors.toSet());
   }
}
