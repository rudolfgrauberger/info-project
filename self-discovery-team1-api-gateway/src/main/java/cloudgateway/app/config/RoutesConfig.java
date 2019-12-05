package cloudgateway.app.config;

import cloudgateway.app.ServiceDiscovery.AggregateRootTraverseRoutes;
import cloudgateway.app.ServiceDiscovery.AutoDiscoveryClientRouteDefinitionLocator;
import cloudgateway.app.ServiceDiscovery.AutoDiscoveryRoutes;
import cloudgateway.app.filter.AddXForwardedPrefixHeaderFilter;
import cloudgateway.app.service.HalParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Bean
    @ConditionalOnProperty(name = "team1.discovery.locator.enabled")
    public AutoDiscoveryClientRouteDefinitionLocator discoveryClientRouteLocator(DiscoveryClient discoveryClient, DiscoveryLocatorProperties properties, AutoDiscoveryRoutes dynamicTargetRoutes) {
        return new AutoDiscoveryClientRouteDefinitionLocator(discoveryClient, properties, dynamicTargetRoutes);
    }

    @Bean
    public AutoDiscoveryRoutes routeDiscovery(HalParser halParser) {
        return new AggregateRootTraverseRoutes(halParser);
    }

    @Bean
    @ConditionalOnExpression("${spring.cloud.gateway.discovery.locator.enabled:true} && ${spring.cloud.gateway.x-forwarded.enabled:true}")
    public AddXForwardedPrefixHeaderFilter xForwaredPrefixHeaderGlobalFilter() {
        return new AddXForwardedPrefixHeaderFilter();
    }
}
