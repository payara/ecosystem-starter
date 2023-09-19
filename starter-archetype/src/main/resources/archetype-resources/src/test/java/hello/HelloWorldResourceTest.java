package ${package}.hello;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import ${eePackage}.ws.rs.client.Client;
import ${eePackage}.ws.rs.client.ClientBuilder;
import ${eePackage}.ws.rs.core.MediaType;
import ${eePackage}.ws.rs.core.Response;
import java.net.URL;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@RunAsClient
public class HelloWorldResourceTest {


    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(HelloWorldResource.class)
                .addClass(RestConfiguration.class);
    }
    
    @ArquillianResource
    private URL deploymentUrl;
    
    @Test
    public void testHelloEndpoint() {
        String baseUrl = deploymentUrl.toString();
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(baseUrl + "resources/hello")
                .queryParam("name", "John")
                .request(MediaType.TEXT_PLAIN)
                .get();


        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String responseBody = response.readEntity(String.class);
        assertEquals("John", responseBody);

        client.close();
    }
}
