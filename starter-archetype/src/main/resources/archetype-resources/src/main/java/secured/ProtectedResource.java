package ${package}.secured;

import ${eePackage}.annotation.security.RolesAllowed;
import ${eePackage}.inject.Inject;
import ${eePackage}.security.enterprise.SecurityContext;
import ${eePackage}.ws.rs.GET;
import ${eePackage}.ws.rs.Path;
import ${eePackage}.ws.rs.core.Response;

/**
 * Simply protected rest resource for role 'user'.
 *
 */
@Path("protected")
@RolesAllowed("user")
public class ProtectedResource {

    @Inject
    private SecurityContext securityContext;

    @GET
    public Response info() {
        String webName = null;
        if (securityContext.getCallerPrincipal() != null) {
            webName = securityContext.getCallerPrincipal().getName();
        }
        return Response
                .ok("Protected information for user:" + webName
                        + " | web user has role \"user\": " + securityContext.isCallerInRole("user")
                        + " | web user has role \"admin\": " + securityContext.isCallerInRole("admin"))
                .build();
    }
}
