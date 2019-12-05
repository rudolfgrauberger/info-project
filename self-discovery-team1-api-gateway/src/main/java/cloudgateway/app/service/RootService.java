package cloudgateway.app.service;

import java.net.URI;

public interface RootService {
   String getAggregatedEndpoints(URI httpRequest);
}
