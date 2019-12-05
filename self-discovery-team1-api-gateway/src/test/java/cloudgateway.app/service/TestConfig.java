package cloudgateway.app.service;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
   @Bean
   HalGenerator getHalGenerator() {
      return new CombineAggregateRootHalGenerator();
   }

   @Bean
   RootService getRootService() {
      return new AggregateEndpointsRootService();
   }
}
