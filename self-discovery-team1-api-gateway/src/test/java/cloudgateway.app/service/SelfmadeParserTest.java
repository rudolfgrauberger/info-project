package cloudgateway.app.service;


import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SelfmadeParser.class)
public class SelfmadeParserTest {

    private String JSON_LEARINIG_OUTCOMES = String.format("{%n" +
            "  \"_links\": {%n" +
            "    \"learningOutcomes\": {%n" +
            "      \"templated\": true,%n" +
            "      \"href\": \"http://localhost:9999/learning-outcome/learning-outcomes{?page,size,sort,projection}\"%n" +
            "    },%n" +
            "    \"verbSuggestions\": {%n" +
            "      \"templated\": true,%n" +
            "      \"href\": \"http://localhost:9999/learning-outcome/verb-suggestions{?page,size,sort}\"%n" +
            "    },%n" +
            "    \"profile\": {%n" +
            "      \"href\": \"http://localhost:9999/learning-outcome/profile\"%n" +
            "    }%n" +
            "  }%n" +
            "}");

    private Set<String> URIS_LEARNING_OUTCOMES = new HashSet<>(Arrays.asList(
            "http://localhost:9999/learning-outcome/verb-suggestions",
            "http://localhost:9999/learning-outcome/profile",
            "http://localhost:9999/learning-outcome/learning-outcomes"));

    private Set<String> ALL_RECURSIVE_URIS_FROM_LEARNING_OUTCOMES = new HashSet<>(Arrays.asList(
            "http://localhost:9999/learning-outcome/profile/learning-outcomes",
            "http://localhost:9999/learning-outcome/profile/verb-suggestions",
            "http://localhost:9999/learning-outcome/learning-outcomes",
            "http://localhost:9999/learning-outcome/learning-outcomes/195",
            "http://localhost:9999/learning-outcome/learning-outcomes/2",
            "http://localhost:9999/learning-outcome/learning-outcomes/256",
            "http://localhost:9999/learning-outcome/learning-outcomes/266",
            "http://localhost:9999/learning-outcome/learning-outcomes/268",
            "http://localhost:9999/learning-outcome/learning-outcomes/270",
            "http://localhost:9999/learning-outcome/learning-outcomes/272",
            "http://localhost:9999/learning-outcome/learning-outcomes/274",
            "http://localhost:9999/learning-outcome/learning-outcomes/276",
            "http://localhost:9999/learning-outcome/learning-outcomes/278",
            "http://localhost:9999/learning-outcome/learning-outcomes/280",
            "http://localhost:9999/learning-outcome/learning-outcomes/282",
            "http://localhost:9999/learning-outcome/learning-outcomes/284",
            "http://localhost:9999/learning-outcome/learning-outcomes/286",
            "http://localhost:9999/learning-outcome/learning-outcomes/288",
            "http://localhost:9999/learning-outcome/learning-outcomes/290",
            "http://localhost:9999/learning-outcome/learning-outcomes/292",
            "http://localhost:9999/learning-outcome/learning-outcomes/294",
            "http://localhost:9999/learning-outcome/learning-outcomes/3",
            "http://localhost:9999/learning-outcome/learning-outcomes/4",
            "http://localhost:9999/learning-outcome/profile",
            "http://localhost:9999/learning-outcome/profile/learning-outcomes",
            "http://localhost:9999/learning-outcome/profile/verb-suggestions",
            "http://localhost:9999/learning-outcome/verb-suggestions",
            "http://localhost:9999/learning-outcome/verb-suggestions/340",
            "http://localhost:9999/learning-outcome/verb-suggestions/341",
            "http://localhost:9999/learning-outcome/verb-suggestions/342",
            "http://localhost:9999/learning-outcome/verb-suggestions/343"
    ));

    private WireMockServer wmServer;

    @Autowired
    private SelfmadeParser halParser;

    @Before
    public void setUp() {
        wmServer = new WireMockServer(9999);

    }

    @Test
    public void getJson() {
        // given
        String preparedUri = testUriFix("https://api.archi-lab.io/learning-outcome");

        wmServer.start();
        String json = halParser.getJson(preparedUri);
        wmServer.stop();
        // than
        assertEquals(JSON_LEARINIG_OUTCOMES, json);
    }

    @Test
    public void getUris() {
        // given
        Set<String> results = halParser.getUris(JSON_LEARINIG_OUTCOMES);

        // then
        assertEquals(results, URIS_LEARNING_OUTCOMES);
    }

    @Test
    public void recursiveUriFilter() {
        // given
        Set<String> textFixeduris = new HashSet<>();
        for(String uri: URIS_LEARNING_OUTCOMES)textFixeduris.add(testUriFix(uri));

        wmServer.start();
        Set<String> results = halParser.recursiveUriFilter(textFixeduris);
        wmServer.stop();

        // then
        assertEquals(ALL_RECURSIVE_URIS_FROM_LEARNING_OUTCOMES, results);
    }

    @Test
    public void getResources() {
        // given
        String preparedUri = testUriFix("https://api.archi-lab.io/learning-outcome");

        wmServer.start();
        Set<String> results = halParser.getResources(preparedUri);
        wmServer.stop();

        // then
        assertEquals(URIS_LEARNING_OUTCOMES, results);

    }

    @Test
    public void discoverAllUris() {
        // given
        String preparedUri = testUriFix("https://api.archi-lab.io/learning-outcome");

        wmServer.start();
        Set<String> results = halParser.discoverAllUris(preparedUri);
        wmServer.stop();

        // then
        assertEquals(ALL_RECURSIVE_URIS_FROM_LEARNING_OUTCOMES, results);

    }

    /**
     * Refactor URIs for testing, so they can run on the wiremock server
     * @param startJSON
     * @return
     */
    public String testUriFix(String startJSON){
        if(startJSON.contains("https://")){
            return startJSON.replaceAll("https://api.archi-lab.io", "http://localhost:9999");
        } else {
            return startJSON.replaceAll("http://api.archi-lab.io", "http://localhost:9999");
        }
    }
}