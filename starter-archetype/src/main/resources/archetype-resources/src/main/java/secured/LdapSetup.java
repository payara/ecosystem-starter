package ${package}.secured;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFReader;
import ${eePackage}.annotation.PostConstruct;
import ${eePackage}.annotation.PreDestroy;
import ${eePackage}.ejb.Singleton;
import ${eePackage}.ejb.Startup;

/**
 * Starts up the embedded Unboundid LDAP server on port 33389 and loads a test directory from resources ldap-test.ldif
 *
 */
@Startup
@Singleton
public class LdapSetup {
    
    private InMemoryDirectoryServer directoryServer;

    @PostConstruct
    public void init() {
        try {
            InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig("dc=net");
            config.setListenerConfigs(
                new InMemoryListenerConfig("myListener", null, 33389, null, null, null));

            directoryServer = new InMemoryDirectoryServer(config);
            
            directoryServer.importFromLDIF(true, 
                new LDIFReader(this.getClass().getResourceAsStream("/ldap-test.ldif")));

            directoryServer.startListening();
        } catch (LDAPException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @PreDestroy
    public void destroy() {
        directoryServer.shutDown(true);
    }
    
}
