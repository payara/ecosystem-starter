package ${package}.secured;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import ${eePackage}.annotation.PostConstruct;
import ${eePackage}.annotation.PreDestroy;
import ${eePackage}.annotation.Resource;
import ${eePackage}.ejb.Singleton;
import ${eePackage}.ejb.Startup;
import ${eePackage}.inject.Inject;
import javax.sql.DataSource;
import ${eePackage}.security.enterprise.identitystore.Pbkdf2PasswordHash;

@Singleton
@Startup
public class DatabaseSetup {

    @Resource
    private DataSource dataSource; // default datasource

    @Inject
    private Pbkdf2PasswordHash passwordHash;
    
    @Inject
    private ApplicationConfig applicationConfig;

    @PostConstruct
    public void init() {

        passwordHash.initialize(applicationConfig.getHashAlgorithmParameterMap());

        executeUpdate(dataSource, "DROP TABLE IF EXISTS caller");
        executeUpdate(dataSource, "DROP TABLE IF EXISTS caller_groups");

        executeUpdate(dataSource, "CREATE TABLE IF NOT EXISTS caller(name VARCHAR(64) PRIMARY KEY, password VARCHAR(255))");
        executeUpdate(dataSource, "CREATE TABLE IF NOT EXISTS caller_groups(caller_name VARCHAR(64), group_name VARCHAR(64))");

        executeUpdate(dataSource, "INSERT INTO caller VALUES('myuser', '" + passwordHash.generate("secret".toCharArray()) + "')");
        executeUpdate(dataSource, "INSERT INTO caller VALUES('myadmin', '" + passwordHash.generate("secret".toCharArray()) + "')");

        executeUpdate(dataSource, "INSERT INTO caller_groups VALUES('myadmin', 'admin')");
        executeUpdate(dataSource, "INSERT INTO caller_groups VALUES('myadmin', 'user')");

        executeUpdate(dataSource, "INSERT INTO caller_groups VALUES('myuser', 'user')");
    }

    @PreDestroy
    public void destroy() {
    	try {
    		executeUpdate(dataSource, "DROP TABLE IF EXISTS caller");
    		executeUpdate(dataSource, "DROP TABLE IF EXISTS caller_groups");
    	} catch (Exception e) {
    		// silently ignore
    	}
    }

    private void executeUpdate(DataSource dataSource, String query) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
           throw new IllegalStateException(e);
        }
    }

}
