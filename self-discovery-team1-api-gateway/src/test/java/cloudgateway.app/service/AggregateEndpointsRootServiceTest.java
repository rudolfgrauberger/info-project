package cloudgateway.app.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import cloudgateway.app.service.CombineAggregateRootHalGenerator;

import javax.xml.ws.Service;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AggregateEndpointsRootServiceTest {

   private static final String STUDENT_ROOT_HAL_JSON_OUTPUT = "{\"_links\":{\"students\":{\"href\":\"http://localhost:8888/students{?page,size,sort}\",\"templated\":true},\"profile\":{\"href\":\"http://localhost:8888/profile\"}}}";

   private static final String LEANING_MS_ROOT_HAL_JSON_OUTPUT = "{\n" +
         "  \"_links\" : {\n" +
         "    \"quizzes\" : {\n" +
         "      \"href\" : \"http://localhost:8888/quizzes{?page,size,sort}\",\n" +
         "      \"templated\" : true\n" +
         "    },\n" +
         "    \"questions\" : {\n" +
         "      \"href\" : \"http://localhost:8888/questions{?page,size,sort,projection}\",\n" +
         "      \"templated\" : true\n" +
         "    },\n" +
         "    \"profile\" : {\n" +
         "      \"href\" : \"http://localhost:8888/profile\"\n" +
         "    }\n" +
         "  }\n" +
         "}";

   private static final String COLLECTOR_SINGLE_MS_OUTPUT = "{\n" +
         "  \"_links\" : {\n" +
         "    \"students\" : {\n" +
         "      \"href\" : \"http://localhost:8888/students{?page,size,sort}\",\n" +
         "      \"templated\" : true\n" +
         "    },\n" +
         "    \"profile\" : {\n" +
         "      \"href\" : \"http://localhost:8888/profile\"\n" +
         "    }\n" +
         "  }\n" +
         "}";

   private static final String COLLECTOR_LEARNING_AND_STUDENT_OUTPUT = "{\"_links\":{\"quizzes\":{\"href\":\"http://localhost:8888/quizzes{?page,size,sort}\",\"templated\":true},\"questions\":{\"href\":\"http://localhost:8888/questions{?page,size,sort,projection}\",\"templated\":true},\"profile\":{\"href\":\"http://localhost:8888/profile\"},\"students\":{\"href\":\"http://localhost:8888/students{?page,size,sort}\",\"templated\":true},\"profile\":{\"href\":\"http://localhost:8888/profile\"}}}";

   @MockBean
   private ServiceRootCollector collector;

   @Autowired
   private RootService rootService;

   @Test
   public void testCollectRootsFromOneMicroservice() throws URISyntaxException {
      List<String> jsonList = Arrays.asList(COLLECTOR_SINGLE_MS_OUTPUT);

      URI rel = new URI("http://localhost:8888");

      Mockito.when(collector.getRoots(rel)).thenReturn(jsonList);

      assertThat(rootService.getAggregatedEndpoints(rel)).isEqualToIgnoringWhitespace(STUDENT_ROOT_HAL_JSON_OUTPUT);
   }

   @Test
   public void testCollectRootsFromTwoMicroservices() throws URISyntaxException {
      List<String> jsonList = Arrays.asList(LEANING_MS_ROOT_HAL_JSON_OUTPUT, COLLECTOR_SINGLE_MS_OUTPUT);

      URI rel = new URI("http://localhost:8888");

      Mockito.when(collector.getRoots(rel)).thenReturn(jsonList);

      assertThat(rootService.getAggregatedEndpoints(rel)).isEqualToIgnoringWhitespace(COLLECTOR_LEARNING_AND_STUDENT_OUTPUT);
   }


}
