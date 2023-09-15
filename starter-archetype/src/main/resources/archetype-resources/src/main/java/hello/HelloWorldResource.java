package ${package}.hello;

import ${eePackage}.ws.rs.GET;
import ${eePackage}.ws.rs.Path;
import ${eePackage}.ws.rs.QueryParam;
import ${eePackage}.ws.rs.core.Response;

@Path("hello")
public class HelloWorldResource {

    @GET
    public Response hello(@QueryParam("name") String name) {
        if ((name == null) || name.trim().isEmpty()) {
            name = "world";
        }
        return Response
                .ok(name)
                .build();
    }
        
}