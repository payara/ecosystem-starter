package ${package}.secured;

import ${eePackage}.annotation.security.RolesAllowed;
import ${eePackage}.ws.rs.GET;
import ${eePackage}.ws.rs.Path;
import ${eePackage}.ws.rs.core.Response;

/**
 * Simply protected rest resource for role 'user'.
 *
 */
@Path("protected")
public class ProtectedResource {
    
    @GET
    @RolesAllowed("user")
    public Response info() {
        return Response
                .ok("Protected information for users in role 'user'.")
                .build();
    }
}
