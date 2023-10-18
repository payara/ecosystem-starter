package ${package}.secured;

import ${eePackage}.servlet.ServletException;
import ${eePackage}.servlet.http.HttpServletRequest;
import ${eePackage}.servlet.http.HttpSession;
import ${eePackage}.ws.rs.GET;
import ${eePackage}.ws.rs.Path;
import ${eePackage}.ws.rs.core.Context;
import ${eePackage}.ws.rs.core.Response;

/**
 * Simply protected rest resource for role 'user'.
 *
 */
@Path("logout")
public class LogoutResource {

    @Context
    private HttpServletRequest request;

    @GET
    public Response logout() {
        HttpSession session = request.getSession(false); // Don't create a new session if it doesn't exist
        if (session != null) {
            try {
                request.logout(); // Log out the user
                session.invalidate(); // Invalidate the session
                return Response.ok("Logout successful").build();
            } catch (ServletException e) {
                // Handle any exception that may occur during logout
                return Response.serverError().entity("Logout failed").build();
            }
        } else {
            return Response.ok("No active session").build();
        }
    }
}
