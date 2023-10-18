package ${package}.secured;

import ${eePackage}.annotation.security.RolesAllowed;
import ${eePackage}.ws.rs.GET;
import ${eePackage}.ws.rs.Path;
import ${eePackage}.ws.rs.core.Response;

/**
 * Simply protected rest resource for role 'admin'.
 *
 */
@Path("admin")
public class AdminResource {
    
    @GET
    @RolesAllowed("admin")
    public Response info() {
        return Response
                .ok("Protected information for users in role 'admin'.")
                .build();
    }
}
