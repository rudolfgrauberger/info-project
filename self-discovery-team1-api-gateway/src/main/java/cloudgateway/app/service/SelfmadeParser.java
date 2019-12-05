package cloudgateway.app.service;

import com.jayway.jsonpath.JsonPath;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SelfmadeParser implements HalParser{


    private String entryPoint;
    private HashSet<String> discoverdUris;
    private RestTemplate restTemplate;

    public SelfmadeParser() {
        this.discoverdUris = new HashSet<>();
        this.restTemplate = new RestTemplate();
    }

    /**
     * get uri as JSON
     * @param startURI
     * @return
     */
    @Override
    public String getJson(String startURI) {
        return this.getJson(startURI, null);
    }

    /**
     * get uri as JSON
     * @param startURI
     * @return
     */
    @Override
    public String getJson(String startURI, HttpHeaders header) {

        HttpHeaders local = header;
        if (local == null) {
            local = new HttpHeaders();
        }

        local.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>("parameters", local);

        return restTemplate.exchange(startURI, HttpMethod.GET, entity, String.class).getBody();
    }

    /**
     * get all subURIS from a given jsonURI
     * @param json JSON to parse
     * @return in JSON contained Uris
     */
    public Set<String> getUris(String json) {

        ArrayList<String> rawUris = JsonPath.read(json,"$..href");

        HashSet<String> noDuplicateUris = new HashSet<>();
        // loop through unfilteredUris and remove ?unwanted? link attachments
        for (String uri : rawUris) {
            if (uri.contains("{")) {
                String cleanUri = uri.substring(0, uri.indexOf("{"));
                noDuplicateUris.add(cleanUri);
            } else if (uri.contains("?")) {
                String cleanUri = uri.substring(0, uri.indexOf("?"));
                noDuplicateUris.add(cleanUri);
            } else {
                noDuplicateUris.add(uri);
            }

        }
        return noDuplicateUris;
    }

    /**
     * Parse a set of URIs recursive for containing sub-URIS
     * Returns set of URIs when no new subURIS can be found
     * @param startUris Set of URIS which should be parsed
     * @return set of all subURIs
     */
    public Set<String> recursiveUriFilter(Set<String> startUris){
        Set<String> filteredUris = new HashSet<>();

        for (String uri : startUris){
            filteredUris.addAll(getResources(uri));
        }

        // Abbruchbedingung
        if(filteredUris.equals(startUris)){
            return filteredUris;
        } else {
            return recursiveUriFilter(filteredUris);
        }
    }

    /**
     * wrapper function for testing
     * @param startingPoint
     * @return
     */
    @Override
    public Set<String> getResources(String startingPoint) {

        return getUris(getJson(startingPoint));
    }

    /**
     * Connects getResources() and recursiveUriFilter() for returning all sub-URIs from a given entry point
     * @param entryPoint URI to stat parsing from
     * @return set of all discovered URIs
     */
    @Override
    public Set<String> discoverAllUris(String entryPoint) {
        return new TreeSet<>(recursiveUriFilter(getResources(entryPoint)));
    }

    public String getEntryPoint() {
        return entryPoint;
    }

}
