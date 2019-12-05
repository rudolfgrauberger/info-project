package cloudgateway.app.ServiceDiscovery;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory.PATTERN_KEY;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AutoDiscoveryClientRouteDefinitionLocator.class)
public class AutoDiscoveryClientRouteDefinitionLocatorTest {

    @Autowired
    private AutoDiscoveryClientRouteDefinitionLocator autoDiscoveryClientRouteDefinitionLocator;

    @MockBean
    private DiscoveryClient discoveryClient;

    @MockBean
    private DiscoveryLocatorProperties discoveryLocatorProperties;

    @MockBean
    private AutoDiscoveryRoutes autoDiscoveryRoutes;

    private RouteDefinition routeDef1;
    private RouteDefinition routeDef2;
    private RouteDefinition routeDef3;
    private RouteDefinition routeDef4;

    @Before
    public void setUp() {

        String prefix = discoveryClient.getClass().getSimpleName();

        //setup results
        //rd1
        routeDef1 = new RouteDefinition();
        routeDef1.setId(prefix+"_EXP-MS1_profiles");

        List<PredicateDefinition> predicates1 = new ArrayList<>();
        PredicateDefinition predicateDefinition1 = new PredicateDefinition();
        predicateDefinition1.setName("Path");

        predicateDefinition1.addArg(PATTERN_KEY, "/profiles/**");

        predicates1.add(predicateDefinition1);
        routeDef1.setPredicates(predicates1);
        routeDef1.setUri(URI.create("lb://EXP-MS1"));

        //rd2
        routeDef2 = new RouteDefinition();
        routeDef2.setId(prefix+"_EXP-MS1_students");

        List<PredicateDefinition> predicates2 = new ArrayList<>();
        PredicateDefinition predicateDefinition2 = new PredicateDefinition();
        predicateDefinition2.setName("Path");
        predicateDefinition2.addArg(PATTERN_KEY, "/students/**");

        predicates2.add(predicateDefinition2);
        routeDef2.setPredicates(predicates2);
        routeDef2.setUri(URI.create("lb://EXP-MS1"));

        //rd 3
        routeDef3 = new RouteDefinition();
        routeDef3.setId(prefix+"_EXP-MS2_profiles");

        List<PredicateDefinition> predicates3 = new ArrayList<>();
        PredicateDefinition predicateDefinition3 = new PredicateDefinition();
        predicateDefinition3.setName("Path");

        predicateDefinition3.addArg(PATTERN_KEY, "/profiles/**");

        predicates3.add(predicateDefinition3);
        routeDef3.setPredicates(predicates3);
        routeDef3.setUri(URI.create("lb://EXP-MS2"));

        //rd4
        routeDef4 = new RouteDefinition();
        routeDef4.setId(prefix+"_EXP-MS2_students");

        List<PredicateDefinition> predicates4 = new ArrayList<>();
        PredicateDefinition predicateDefinition4 = new PredicateDefinition();
        predicateDefinition4.setName("Path");
        predicateDefinition4.addArg(PATTERN_KEY, "/students/**");

        predicates4.add(predicateDefinition4);
        routeDef4.setPredicates(predicates4);
        routeDef4.setUri(URI.create("lb://EXP-MS2"));

    }

    @Test
    public void getRouteDefinitions() throws Exception{

        // DiscoveryClient Mock
        DefaultServiceInstance instance1 = new DefaultServiceInstance("EXP-MS1", "localhost", 8080,
                false, Collections.singletonMap("edge", "true"));
        DefaultServiceInstance instance2 = new DefaultServiceInstance("EXP-MS2", "localhost", 8081,
                false, Collections.singletonMap("service", "2"));

        when(discoveryClient.getServices()).thenReturn(Arrays.asList("EXP-MS1", "EXP-MS2"));
        when(discoveryClient.getInstances("EXP-MS1")).
                thenReturn(Collections.singletonList(instance1));
        when(discoveryClient.getInstances("EXP-MS2")).
                thenReturn(Collections.singletonList(instance2));

        // DiscoveryLocatorProperties Mock
        String urlExpr = "'lb://'+serviceId";
        String includeExpr = "true";

        when(discoveryLocatorProperties.getRouteIdPrefix()).thenReturn("CompositeDiscoveryClient_");
        when(discoveryLocatorProperties.getUrlExpression()).thenReturn(urlExpr);
        when(discoveryLocatorProperties.getIncludeExpression()).thenReturn(includeExpr);

        // AutoDiscoveryRoutes Mock
        Set<String> dynRoutes1 = new HashSet<>();
        dynRoutes1.addAll(Arrays.asList("/profiles/**","/students/**"));

        Set<String> dynRoutes2 = new HashSet<>();
        dynRoutes2.addAll(Arrays.asList("/profiles/**","/students/**"));

        when(autoDiscoveryRoutes.getRoutes(new URI("http://localhost:8080"))).thenReturn(dynRoutes1);
        when(autoDiscoveryRoutes.getRoutes(new URI("http://localhost:8081"))).thenReturn(dynRoutes2);

        log.debug("MockedRoutes from MS1" + (autoDiscoveryRoutes.getRoutes(new URI("http://localhost:8080"))).toString());

        // get RouteDefinitions
        Flux<RouteDefinition> flux = autoDiscoveryClientRouteDefinitionLocator.getRouteDefinitions().log();

        // actual tests
        StepVerifier.create(flux)
                .expectNext(routeDef1)
                .assertNext(routeDefinition -> Assertions.assertThat(routeDefinition.getUri().equals(URI.create("lb://EXP-MS1"))))
                .expectNext(routeDef3)
                .assertNext(routeDefinition -> Assertions.assertThat(routeDefinition.getPredicates().get(0).equals(routeDef4.getPredicates())))
                .verifyComplete();

    }

}