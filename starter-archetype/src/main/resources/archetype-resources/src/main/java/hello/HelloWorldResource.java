package ${package}.hello;

import ${eePackage}.ws.rs.GET;
import ${eePackage}.ws.rs.Path;
import ${eePackage}.ws.rs.QueryParam;
import ${eePackage}.ws.rs.core.Response;<% if (mpOpenAPI) { %>
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;<% } %>

@Path("hello")
public class HelloWorldResource {

    @GET<% if (mpOpenAPI) { %>
    @Operation(summary = "Get a personalized greeting")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Successful operation"),
        @APIResponse(responseCode = "400", description = "Invalid input")
    })<% } %>
    public Response hello(@QueryParam("name")<% if (mpOpenAPI) { %> @Parameter(name = "name", description = "Name to include in the greeting", required = false, example = "John")<% } %> String name) {
        if ((name == null) || name.trim().isEmpty()) {
            name = "world";
        }
        return Response
                .ok(name)
                .build();
    }
        
}