package cloudgateway.app.controller;

import cloudgateway.app.service.RootService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@RestController
@ConditionalOnProperty(name = "team1.gateway.entrypoint.root.enabled")
public class RootController {

   private RootService service;

   public RootController(RootService service) {
      this.service = service;
   }

   @GetMapping(value="/", produces = TEXT_HTML_VALUE)
   public ResponseEntity html() {
      HttpHeaders headers = new HttpHeaders();
      headers.setLocation(URI.create("browser.html"));
      return new ResponseEntity<>(headers, HttpStatus.MOVED_TEMPORARILY);
   }

   @GetMapping(value = "/", produces = { "application/hal+json", APPLICATION_JSON_VALUE })
   public @ResponseBody String json(ServerHttpRequest request) {
      return service.getAggregatedEndpoints(request.getURI());
   }
}
