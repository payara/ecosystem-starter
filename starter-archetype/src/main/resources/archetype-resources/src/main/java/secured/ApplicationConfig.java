package ${package}.secured;

import ${eePackage}.annotation.security.DeclareRoles;
import ${eePackage}.enterprise.context.ApplicationScoped;
import ${eePackage}.inject.Named;
import ${eePackage}.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import ${eePackage}.security.enterprise.authentication.mechanism.http.LoginToContinue;
import ${eePackage}.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import ${eePackage}.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.HashMap;
import java.util.Map;

@DatabaseIdentityStoreDefinition(
    callerQuery = "#{'select password from caller where name = ?'}",
    groupsQuery = "select group_name from caller_groups where caller_name = ?",
    hashAlgorithm = Pbkdf2PasswordHash.class,
    priorityExpression = "#{'${100}'}",
    hashAlgorithmParameters = {
        "${'${applicationConfig.hashAlgorithmParameters}'}"
    }
)
@FormAuthenticationMechanismDefinition(
    loginToContinue = @LoginToContinue(
        loginPage="/login.xhtml",
        errorPage="/login_error.xhtml"
    )
)
@DeclareRoles({ "user", "admin" })
@ApplicationScoped
@Named
public class ApplicationConfig {

    public String[] getHashAlgorithmParameters() {
        return getHashAlgorithmParameterMap().entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new);
    }

    public Map<String, String> getHashAlgorithmParameterMap() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("Pbkdf2PasswordHash.Iterations", "3072");
        parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512");
        parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "64");
        return parameters;
    }

}
