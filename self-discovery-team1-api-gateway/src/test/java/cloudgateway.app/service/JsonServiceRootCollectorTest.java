package cloudgateway.app.service;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JsonServiceRootCollector.class)
public class JsonServiceRootCollectorTest {

   private final static String EMPTY_LINKS_LIST = "{\"_links\":{}}";

   private static final String STUDENT_ROOT_HAL_JSON_OUTPUT = "{\n" +
         "    \"_links\": {\n" +
         "        \"students\": {\n" +
         "            \"href\": \"http://localhost:8888/students{?page,size,sort}\",\n" +
         "            \"templated\": true\n" +
         "        },\n" +
         "        \"profile\": {\n" +
         "            \"href\": \"http://localhost:8888/profile\"\n" +
         "        }\n" +
         "    }\n" +
         "}";

   private static final String LEARNING_MS_ROOT_HAL_JSON_OUTPUT = "{\n" +
         "    \"_links\": {\n" +
         "        \"quizzes\": {\n" +
         "            \"href\": \"http://localhost:8888/quizzes{?page,size,sort}\",\n" +
         "            \"templated\": true\n" +
         "        },\n" +
         "        \"questions\": {\n" +
         "            \"href\": \"http://localhost:8888/questions{?page,size,sort,projection}\",\n" +
         "            \"templated\": true\n" +
         "        },\n" +
         "        \"profile\": {\n" +
         "            \"href\": \"http://localhost:8888/profile\"\n" +
         "        }\n" +
         "    }\n" +
         "}";

   @Autowired
   private ServiceRootCollector collector;

   @MockBean
   private HalParser halParser;

   @MockBean
   private ApiGatewayHttpHeader httpHeader;

   @MockBean
   private DiscoveryClient discoveryClient;

   public class FakeServiceInstance implements ServiceInstance {

      private String serviceId;
      private String url;

      public FakeServiceInstance(String serviceId, String url) {
         this.serviceId = serviceId;
         this.url = url;
      }

      @Override
      public String getServiceId() {
         return this.serviceId;
      }

      @Override
      public String getHost() {
         return null;
      }

      @Override
      public int getPort() {
         return 0;
      }

      @Override
      public boolean isSecure() {
         return false;
      }

      @Override
      public URI getUri()  {
         try {
            return new URI(this.url);
         }
         catch (Exception e){
            return null;
         }
      }

      @Override
      public Map<String, String> getMetadata() {
         return null;
      }
   }

   @Test
   public void testGetRootHALOfTwoDiscoveredServices() throws Exception {
      List<String> services = Arrays.asList("exp-ms1", "learning-ms");
      URI myURI = new URI("http://localhost:8888");

      ServiceInstance instanceMS1 = new FakeServiceInstance(services.get(0), "http://localhost:8081");
      ServiceInstance instanceLearningMS = new FakeServiceInstance(services.get(1), "http://localhost:8080");

      Mockito.when(discoveryClient.getServices()).thenReturn(services);

      Mockito.when(discoveryClient.getInstances(services.get(0))).thenReturn(Arrays.asList(instanceMS1));
      Mockito.when(discoveryClient.getInstances(services.get(1))).thenReturn(Arrays.asList(instanceLearningMS));

      XForwardedApiGatewayHttpHeader header = new XForwardedApiGatewayHttpHeader();
      HttpHeaders test = header.getHeaders(myURI);

      Mockito.when(httpHeader.getHeaders(myURI)).thenReturn(test);

      Mockito.when(halParser.getJson(instanceMS1.getUri().toString(), test)).thenReturn(STUDENT_ROOT_HAL_JSON_OUTPUT);
      Mockito.when(halParser.getJson(instanceLearningMS.getUri().toString(), test)).thenReturn(LEARNING_MS_ROOT_HAL_JSON_OUTPUT);

      List<String> expected = Arrays.asList(STUDENT_ROOT_HAL_JSON_OUTPUT, LEARNING_MS_ROOT_HAL_JSON_OUTPUT);

      List<String> result = collector.getRoots(myURI);

      assertThat(result).isEqualTo(expected);
   }

   @Test
   public void testGetRootIfNoDiscoveredServiceExists() throws URISyntaxException {
      List<String> services = new ArrayList<String>();

      URI myURI = new URI("http://localhost:8888");


      List<String> result = collector.getRoots(myURI);

      assertThat(result).isEqualTo(Lists.emptyList());
   }


}
