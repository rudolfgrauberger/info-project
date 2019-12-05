package cloudgateway.app.ServiceDiscovery;

import cloudgateway.app.service.HalParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AggregateRootTraverseRoutes.class)
public class AggregateRootTraverseRoutesTest {


    private Set<String> compareSet = new HashSet<String>();
    private Set<String> mockingSet = new HashSet<String>();


    @Autowired
    AutoDiscoveryRoutes autoDiscoveryRoutes;

    @MockBean
    HalParser halParser;

    @Before
    public void setUp() {
        compareSet.addAll(Arrays.asList("/profiles/**","/students/**"));
        mockingSet.addAll(Arrays.asList("http://localhost:8080/profiles","http://localhost:8080/students"));

        Mockito.when(halParser.getResources("http://localhost:8080")).thenReturn(mockingSet);
    }

    @Test
    public void cutResourceOutOfUrlTest()
        throws Exception {
        Assert.assertEquals(compareSet, autoDiscoveryRoutes.getRoutes(new URI("http://localhost:8080")));
    }

}
