package cloudgateway.app.ServiceDiscovery;

import java.net.URI;
import java.util.Set;

public interface AutoDiscoveryRoutes {

   Set<String> getRoutes(URI microservice);
}
