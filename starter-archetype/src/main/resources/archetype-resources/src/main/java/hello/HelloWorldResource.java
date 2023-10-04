package ${package}.hello;
<% if (mpConfig) { %>
import ${eePackage}.inject.Inject;<% } %>
import ${eePackage}.ws.rs.GET;
import ${eePackage}.ws.rs.Path;
import ${eePackage}.ws.rs.QueryParam;
import ${eePackage}.ws.rs.core.Response;<% if (mpConfig) { %>
import org.eclipse.microprofile.config.inject.ConfigProperty;<% } %><% if (mpFaultTolerance) { %>
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;<% } %><% if (mpMetrics) { %>
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;<% } %><% if (mpOpenAPI) { %>
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;<% } %>

@Path("hello")
public class HelloWorldResource {
<% if (mpConfig) { %>
    @Inject
    @ConfigProperty(name = "defaultName", defaultValue = "world")
    private String defaultName;<% } %>

    @GET<% if (mpOpenAPI) { %>
    @Operation(summary = "Get a personalized greeting")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Successful operation"),
        @APIResponse(responseCode = "400", description = "Invalid input")
    })<% } %><% if (mpMetrics) { %>
    @Counted(name = "helloEndpointCount", description = "Count of calls to the hello endpoint")
    @Timed(name = "helloEndpointTime", description = "Time taken to execute the hello endpoint")<% } %><% if (mpFaultTolerance) { %>
    @Timeout(3000) // Timeout after 3 seconds
    @Retry(maxRetries = 3) // Retry the request up to 3 times on failure
    @Fallback(fallbackMethod = "fallbackMethod")<% } %>
    public Response hello(@QueryParam("name")<% if (mpOpenAPI) { %> @Parameter(name = "name", description = "Name to include in the greeting", required = false, example = "John")<% } %> String name) {
        if ((name == null) || name.trim().isEmpty()) {
            name = <% if (mpConfig) { %>defaultName<% } else { %>"world"<% } %>;
        }
        return Response
                .ok(name)
                .build();
    }

    <% if (mpFaultTolerance) { %>public String fallbackMethod() {
        // Fallback logic when the getData method fails or exceeds retries
        return "Fallback data";
    }<% } %>
        
}