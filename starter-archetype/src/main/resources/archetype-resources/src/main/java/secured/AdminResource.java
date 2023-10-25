package ${package}.secured;

import ${eePackage}.annotation.security.RolesAllowed;
import ${eePackage}.servlet.http.HttpServletRequest;
import ${eePackage}.ws.rs.GET;
import ${eePackage}.ws.rs.Path;
import ${eePackage}.ws.rs.core.Context;
import ${eePackage}.ws.rs.core.Response;

/**
 * Simply protected rest resource for role 'admin'.
 *
 */
@Path("admin")
@RolesAllowed("admin")
public class AdminResource {

    @Context
    private HttpServletRequest request;

    @GET
    public Response info() {
        String webName = null;
        if (request.getUserPrincipal() != null) {
            webName = request.getUserPrincipal().getName();
        }
        return Response
                .ok("Protected information for user:" + webName
                        + " | web user has role \"user\": " + request.isUserInRole("user")
                        + " | web user has role \"admin\": " + request.isUserInRole("admin"))
                .build();
    }
}

