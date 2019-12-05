package cloudgateway.app.controller;

import cloudgateway.app.service.RootService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@Slf4j
public class RootControllerTest {

    String AGGREGATE_ROUTES_STRING = "{%n" +
            "  \"_links\": {%n" +
            "    \"students\": {%n" +
            "      \"href\": \"http://localhost:8888/students{?page,size,sort}\",%n" +
            "      \"templated\": true%n" +
            "    },%n" +
            "    \"profile\": {%n" +
            "      \"href\": \"http://localhost:8888/profile\"%n" +
            "    }%n" +
            "  }%n" +
            "}";

    RootController rootController;

    @Mock
    RootService rootService;

    MockServerHttpRequest mockRequest;

    HttpHeaders httpHeaders;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        httpHeaders = new HttpHeaders();
        rootController = new RootController(rootService);
    }

    @Test
    public void html() {

        httpHeaders.setLocation(URI.create("browser.html"));
        ResponseEntity result = new ResponseEntity(httpHeaders, HttpStatus.MOVED_TEMPORARILY);

        ResponseEntity controllerResult = rootController.html();
        log.info("Controller Result for html(): " + controllerResult);
        assertEquals(rootController.html(), result);
    }

    @Test
    public void json() {

        URI testURI = URI.create("http://localhost:8888/");

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ServerHttpRequest serverHttpRequest = MockServerHttpRequest
                .method(HttpMethod.GET, testURI)
                .headers(httpHeaders)
                .build();

        when(rootService.getAggregatedEndpoints(testURI)).thenReturn(AGGREGATE_ROUTES_STRING);

        assertEquals(rootController.json(serverHttpRequest), AGGREGATE_ROUTES_STRING);
        verify(rootService, times(1)).getAggregatedEndpoints(testURI);
    }


    @Test
    public void jsonCall() {

        WebTestClient webTestClient = WebTestClient.bindToController(rootController).build();

        log.debug("Testing RootController application/json");
        webTestClient.get().uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("charset","UTF8");

        log.debug("Testing RootController for application/hal+json");
        webTestClient.get().uri("/")
                .accept(new MediaType("application","hal+json"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(new MediaType("application","hal+json", parameterMap));
    }

    @Test
    public void htmlCall() {

        WebTestClient webTestClient = WebTestClient.bindToController(rootController).build();

        log.debug("Testing RootController text/html");
        webTestClient.get().uri("/")
                .accept(MediaType.TEXT_HTML)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "browser.html");
    }

    @Test
    public void falseMediaTypeTest() {
        WebTestClient webTestClient = WebTestClient.bindToController(rootController).build();

        log.debug("Testing unaccepted media type");
        webTestClient.get().uri("/")
                .accept(MediaType.APPLICATION_ATOM_XML)
                .exchange()
                .expectStatus().is4xxClientError();

        log.debug("Testing another unaccepted media type");
        webTestClient.get().uri("/")
                .accept(MediaType.APPLICATION_XML)
                .exchange()
                .expectStatus().is4xxClientError();

    }
}