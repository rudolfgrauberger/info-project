package cloudgateway.app.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = XForwardedApiGatewayHttpHeader.class)
public class XForwardedApiGatewayHttpHeaderTest {

   @Autowired
   private ApiGatewayHttpHeader headerGenerator;

   @Test
   public void returnValidHttpXForwardedHeaderWithLocalhost() throws Exception {
      URI validHttp = new URI("http://localhost:8888");

      HttpHeaders httpXForwardedHeaders = new HttpHeaders();
      httpXForwardedHeaders.set("X-Forwarded-Host", "localhost:8888");
      httpXForwardedHeaders.set("X-Forwarded-Port", "8888");
      httpXForwardedHeaders.set("X-Forwarded-Proto", "http");

      HttpHeaders headers = headerGenerator.getHeaders(validHttp);

      assertThat(headers).isEqualToComparingFieldByField(httpXForwardedHeaders);
   }

   @Test
   public void returnValidHttpXForwardedHeaderWithLoopbackIP() throws Exception {
      URI validHttp = new URI("http://127.0.0.1:8888");

      HttpHeaders httpXForwardedHeaders = new HttpHeaders();
      httpXForwardedHeaders.set("X-Forwarded-Host", "127.0.0.1:8888");
      httpXForwardedHeaders.set("X-Forwarded-Port", "8888");
      httpXForwardedHeaders.set("X-Forwarded-Proto", "http");

      HttpHeaders headers = headerGenerator.getHeaders(validHttp);

      assertThat(headers).isEqualToComparingFieldByField(httpXForwardedHeaders);
   }

   @Test
   public void returnValidHttpsXForwardedHeaderWithLocalhost() throws Exception {
      URI validHttp = new URI("https://localhost:8888");

      HttpHeaders httpXForwardedHeaders = new HttpHeaders();
      httpXForwardedHeaders.set("X-Forwarded-Host", "localhost:8888");
      httpXForwardedHeaders.set("X-Forwarded-Port", "8888");
      httpXForwardedHeaders.set("X-Forwarded-Proto", "https");

      HttpHeaders headers = headerGenerator.getHeaders(validHttp);

      assertThat(headers).isEqualToComparingFieldByField(httpXForwardedHeaders);
   }

   @Test
   public void returnValidHttpsXForwardedHeaderWithLoopbackIP() throws Exception {
      URI validHttp = new URI("https://127.0.0.1:8888");

      HttpHeaders httpXForwardedHeaders = new HttpHeaders();
      httpXForwardedHeaders.set("X-Forwarded-Host", "127.0.0.1:8888");
      httpXForwardedHeaders.set("X-Forwarded-Port", "8888");
      httpXForwardedHeaders.set("X-Forwarded-Proto", "https");

      HttpHeaders headers = headerGenerator.getHeaders(validHttp);

      assertThat(headers).isEqualToComparingFieldByField(httpXForwardedHeaders);
   }
}
