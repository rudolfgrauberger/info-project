package cloudgateway.app.ServiceDiscovery;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory.PATTERN_KEY;
import static org.springframework.cloud.gateway.support.NameUtils.normalizeRoutePredicateName;

public class AutoDiscoveryClientRouteDefinitionLocator implements RouteDefinitionLocator {

   private final DiscoveryClient discoveryClient;
   private final DiscoveryLocatorProperties properties;
   private final String routeIdPrefix;

   private AutoDiscoveryRoutes dynamicTargetRoutes;

   public AutoDiscoveryClientRouteDefinitionLocator(DiscoveryClient discoveryClient, DiscoveryLocatorProperties properties, AutoDiscoveryRoutes dynamicTargetRoutes) {
      this.discoveryClient = discoveryClient;
      this.properties = properties;
      if (StringUtils.hasText(properties.getRouteIdPrefix())) {
         this.routeIdPrefix = properties.getRouteIdPrefix();
      } else {
         this.routeIdPrefix = this.discoveryClient.getClass().getSimpleName() + "_";
      }
      this.dynamicTargetRoutes = dynamicTargetRoutes;
   }

   @Override
   public Flux<RouteDefinition> getRouteDefinitions() {
      SimpleEvaluationContext evalCtxt = SimpleEvaluationContext
            .forReadOnlyDataBinding()
            .withInstanceMethods()
            .build();

      SpelExpressionParser parser = new SpelExpressionParser();
      Expression urlExpr = parser.parseExpression(properties.getUrlExpression());

      // Eigene Implementation -> Start
      List<RouteDefinition> routes = new ArrayList<RouteDefinition>();

      Set<ServiceInstance> services = getServiceInstances();

      services.stream().forEach(service -> {
         String serviceId = service.getServiceId();
         Set<String> dynRoutes = dynamicTargetRoutes.getRoutes(service.getUri());

         dynRoutes.forEach(dynRoute -> {
            RouteDefinition routeDefinition = new RouteDefinition();
            routeDefinition.setId(this.routeIdPrefix + serviceId + "_" + dynRoute.replaceAll("[/\\*]", ""));

            // Dieser Teil erstellt die URI an die der Request gesendet wird (normalerweise lb://<SERVICE-ID>)
            String uri = urlExpr.getValue(evalCtxt, service, String.class);
            routeDefinition.setUri(URI.create(uri));

            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setName(normalizeRoutePredicateName(PathRoutePredicateFactory.class));
            predicate.addArg(PATTERN_KEY, dynRoute);
            routeDefinition.getPredicates().add(predicate);
            routes.add(routeDefinition);
         });
      });

      // Eigene Implementation -> End

      return Flux.fromIterable(routes);
   }

   private Set<ServiceInstance> getServiceInstances(){
      SimpleEvaluationContext evalCtxt = SimpleEvaluationContext
            .forReadOnlyDataBinding()
            .withInstanceMethods()
            .build();

      SpelExpressionParser parser = new SpelExpressionParser();
      Expression includeExpr = parser.parseExpression(properties.getIncludeExpression());

      return discoveryClient.getServices().stream()
            .map(discoveryClient::getInstances)
            .filter(instances -> !instances.isEmpty())
            .map(instances -> instances.get(0))
            .filter(instance -> {
               Boolean include = includeExpr.getValue(evalCtxt, instance, Boolean.class);
               if (include == null) {
                  return false;
               }
               return include;
            }).collect(Collectors.toSet());
   }
}
