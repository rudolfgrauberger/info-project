//package cloudgateway.app.ServiceDiscovery;
//
//import cloudgateway.app.config.RoutesConfig;
//import cloudgateway.app.service.CombineAggregateRootHalGenerator;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
//import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
//import org.springframework.cloud.gateway.route.RouteDefinition;
//import org.springframework.test.context.junit4.SpringRunner;
//import reactor.core.publisher.Flux;
//import reactor.test.StepVerifier;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = AutoDiscoveryClientRouteDefinitionLocator.class)
//public class AutoDiscoveryClientRouteDefinitionLocatorTest {
//
////    private RouteDefinition routeDef1;
////    private RouteDefinition routeDef2;
////
////
////
////
////    private DiscoveryLocatorProperties properties;
////
////    @Autowired
////    public AutoDiscoveryClientRouteDefinitionLocator autoDiscoveryClientRouteDefinitionLocator;
////
////    @Before
////    public void setUp(){
////
////        routeDef1 = new RouteDefinition();
////        routeDef1.setId("CompositeDiscoveryClient_EXP-MS1_profile");
////
////        List<PredicateDefinition> predicates1 = new ArrayList<>();
////        PredicateDefinition predicateDefinition1 = new PredicateDefinition();
////        predicateDefinition1.setName("Path");
////        predicateDefinition1.addArg("pattern", "/profile/**");
////
////        predicates1.add(predicateDefinition1);
////        routeDef1.setPredicates(predicates1);
////        routeDef1.setUri(URI.create("lb://EXP-MS1"));
////
////
////        routeDef2 = new RouteDefinition();
////        routeDef2.setId("CompositeDiscoveryClient_EXP-MS1_students");
////
////        List<PredicateDefinition> predicates2 = new ArrayList<>();
////        PredicateDefinition predicateDefinition2 = new PredicateDefinition();
////        predicateDefinition2.setName("Path");
////        predicateDefinition2.addArg("pattern", "/students/**");
////
////        predicates2.add(predicateDefinition2);
////        routeDef2.setPredicates(predicates2);
////        routeDef2.setUri(URI.create("lb://EXP-MS1"));
////
////
////
////    }
////
////    @Test
////    public void getRouteDefinitions() {
////
////        Flux<RouteDefinition> flux = autoDiscoveryClientRouteDefinitionLocator.getRouteDefinitions();
////
////        StepVerifier.create(flux)
////                .expectNext(routeDef1, routeDef2)
////                .verifyComplete();
////    }
//}