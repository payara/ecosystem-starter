/*
 *
 * Copyright (c) 2023-2024 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package fish.payara.starter.resources;

import jakarta.json.bind.annotation.JsonbProperty;

/**
 *
 * @author Gaurav Gupta
 */
public class ApplicationConfiguration {

    public static final String BUILD = "build";
    public static final String GROUP_ID = "groupId";
    public static final String ARTIFACT_ID = "artifactId";
    public static final String VERSION = "version";
    public static final String PACKAGE = "package";
    public static final String JAKARTA_EE_VERSION = "jakartaEEVersion";
    public static final String PROFILE = "profile";
    public static final String JAVA_VERSION = "javaVersion";
    public static final String PLATFORM = "platform";
    public static final String INCLUDE_TESTS = "includeTests";
    public static final String DOCKER = "docker";
    public static final String PAYARA_QUBE = "qube";
    public static final String MP_CONFIG = "mpConfig";
    public static final String MP_OPEN_API = "mpOpenAPI";
    public static final String MP_FAULT_TOLERANCE = "mpFaultTolerance";
    public static final String MP_METRICS = "mpMetrics";
    public static final String PAYARA_VERSION = "payaraVersion";
    public static final String AUTO_BIND_HTTP = "autoBindHttp";
    public static final String ADD_CONCURRENT_API = "addConcurrentApi";
    public static final String ADD_RESOURCE_API = "addResourceApi";
    public static final String ADD_JBATCH_API = "addJBatchApi";
    public static final String ADD_MICROPROFILE_API = "addMicroprofileApi";
    public static final String ADD_JCACHE = "addJcache";
    public static final String ADD_PAYARA_API = "addPayaraApi";
    public static final String DEPLOY_WAR = "deployWar";
    public static final String CONTEXT_ROOT = "contextRoot";
    public static final String AUTH = "auth";
    public static final String ER_DIAGRAM = "erDiagram";
    public static final String ER_DIAGRAM_NAME = "erDiagramName";
    public static final String REST_SUBPACKAGE = "restSubpackage";
    public static final String GENERATE_WEB = "generateWeb";

    public static final String PAYARA_VERSION_6_2023_11 = "6.2023.11";

    @JsonbProperty(BUILD)
    private String build = "maven";

    @JsonbProperty(GROUP_ID)
    private String groupId = "fish.payara";

    @JsonbProperty(ARTIFACT_ID)
    private String artifactId = "hello-world";

    @JsonbProperty(VERSION)
    private String version = "0.1-SNAPSHOT";

    @JsonbProperty(PACKAGE)
    private String packageName = "fish.payara";

    @JsonbProperty(JAKARTA_EE_VERSION)
    private String jakartaEEVersion = "10";

    @JsonbProperty(PROFILE)
    private String profile = "full";

    @JsonbProperty(JAVA_VERSION)
    private int javaVersion = 17;

    @JsonbProperty(PLATFORM)
    private String platform = "none";

    @JsonbProperty(INCLUDE_TESTS)
    private boolean includeTests = false;

    @JsonbProperty(DOCKER)
    private boolean docker = false;

    @JsonbProperty(PAYARA_QUBE)
    private boolean qube = false;

    @JsonbProperty(MP_CONFIG)
    private boolean mpConfig = false;

    @JsonbProperty(MP_OPEN_API)
    private boolean mpOpenAPI = false;

    @JsonbProperty(MP_FAULT_TOLERANCE)
    private boolean mpFaultTolerance = false;

    @JsonbProperty(MP_METRICS)
    private boolean mpMetrics = false;

    @JsonbProperty(PAYARA_VERSION)
    private String payaraVersion;
    
    @JsonbProperty(AUTO_BIND_HTTP)
    private boolean autoBindHttp = true;

    @JsonbProperty(ADD_CONCURRENT_API)
    private boolean addConcurrentApi = false;

    @JsonbProperty(ADD_RESOURCE_API)
    private boolean addResourceApi = false;

    @JsonbProperty(ADD_JBATCH_API)
    private boolean addJBatchApi = false;

    @JsonbProperty(ADD_MICROPROFILE_API)
    private boolean addMicroprofileApi = true;

    @JsonbProperty(ADD_JCACHE)
    private boolean addJcache = false;

    @JsonbProperty(ADD_PAYARA_API)
    private boolean addPayaraApi = true;

    @JsonbProperty(DEPLOY_WAR)
    private boolean deployWar = true;

    @JsonbProperty(CONTEXT_ROOT)
    private String contextRoot = "/";

    @JsonbProperty(AUTH)
    private String auth = "none";

    @JsonbProperty(ER_DIAGRAM)
    private String erDiagram = null;

    @JsonbProperty(ER_DIAGRAM_NAME)
    private String erDiagramName = null;

    private boolean generateJPA = true;
    private boolean generateRepository = true;
    private boolean generateRest = true;
    private String generateWeb;
    private String jpaSubpackage = "domain";
    private String repositorySubpackage = "service";
    private String restSubpackage = "resource";

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getJakartaEEVersion() {
        return jakartaEEVersion;
    }

    public void setJakartaEEVersion(String jakartaEEVersion) {
        this.jakartaEEVersion = jakartaEEVersion;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(int javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPayaraVersion() {
        return payaraVersion;
    }

    public void setPayaraVersion(String payaraVersion) {
        this.payaraVersion = payaraVersion;
    }

    public boolean isAutoBindHttp() {
        return autoBindHttp;
    }

    public void setAutoBindHttp(boolean autoBindHttp) {
        this.autoBindHttp = autoBindHttp;
    }

    public boolean isAddConcurrentApi() {
        return addConcurrentApi;
    }

    public void setAddConcurrentApi(boolean addConcurrentApi) {
        this.addConcurrentApi = addConcurrentApi;
    }

    public boolean isAddResourceApi() {
        return addResourceApi;
    }

    public void setAddResourceApi(boolean addResourceApi) {
        this.addResourceApi = addResourceApi;
    }

    public boolean isAddJBatchApi() {
        return addJBatchApi;
    }

    public void setAddJBatchApi(boolean addJBatchApi) {
        this.addJBatchApi = addJBatchApi;
    }

    public boolean isAddMicroprofileApi() {
        return addMicroprofileApi;
    }

    public void setAddMicroprofileApi(boolean addMicroprofileApi) {
        this.addMicroprofileApi = addMicroprofileApi;
    }

    public boolean isAddJcache() {
        return addJcache;
    }

    public void setAddJcache(boolean addJcache) {
        this.addJcache = addJcache;
    }

    public boolean isAddPayaraApi() {
        if (addPayaraApi) {
            return compareVersions(payaraVersion, PAYARA_VERSION_6_2023_11) > 0;
        } else {
            return false;
        }
    }

    public void setAddPayaraApi(boolean addPayaraApi) {
        this.addPayaraApi = addPayaraApi;
    }

    public boolean isDeployWar() {
        return deployWar;
    }

    public void setDeployWar(boolean deployWar) {
        this.deployWar = deployWar;
    }

    public String getContextRoot() {
        return contextRoot;
    }

    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
    }

    public boolean isIncludeTests() {
        return includeTests;
    }

    public void setIncludeTests(boolean includeTests) {
        this.includeTests = includeTests;
    }

    public boolean isDocker() {
        return docker;
    }

    public void setDocker(boolean docker) {
        this.docker = docker;
    }

    public boolean isQube() {
        return qube;
    }

    public void setQube(boolean qube) {
        this.qube = qube;
    }

    public boolean isMpConfig() {
        return mpConfig;
    }

    public void setMpConfig(boolean mpConfig) {
        this.mpConfig = mpConfig;
    }

    public boolean isMpOpenAPI() {
        return mpOpenAPI;
    }

    public void setMpOpenAPI(boolean mpOpenAPI) {
        this.mpOpenAPI = mpOpenAPI;
    }

    public boolean isMpFaultTolerance() {
        return mpFaultTolerance;
    }

    public void setMpFaultTolerance(boolean mpFaultTolerance) {
        this.mpFaultTolerance = mpFaultTolerance;
    }

    public boolean isMpMetrics() {
        return mpMetrics;
    }

    public void setMpMetrics(boolean mpMetrics) {
        this.mpMetrics = mpMetrics;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getErDiagram() {
        return erDiagram;
    }

    public void setErDiagram(String erDiagram) {
        this.erDiagram = erDiagram;
    }

    public String getErDiagramName() {
        return erDiagramName;
    }

    public void setErDiagramName(String erDiagramName) {
        this.erDiagramName = erDiagramName;
    }

    public boolean isGenerateJPA() {
        return generateJPA;
    }

    public void setGenerateJPA(boolean generateJPA) {
        this.generateJPA = generateJPA;
    }

    public boolean isGenerateRepository() {
        return generateRepository;
    }

    public void setGenerateRepository(boolean generateRepository) {
        this.generateRepository = generateRepository;
    }

    public boolean isGenerateRest() {
        return generateRest;
    }

    public void setGenerateRest(boolean generateRest) {
        this.generateRest = generateRest;
    }

    public String getGenerateWeb() {
        return generateWeb;
    }

    public void setGenerateWeb(String generateWeb) {
        this.generateWeb = generateWeb;
    }

    public String getJpaSubpackage() {
        return jpaSubpackage;
    }

    public void setJpaSubpackage(String jpaSubpackage) {
        this.jpaSubpackage = jpaSubpackage;
    }

    public String getRepositorySubpackage() {
        return repositorySubpackage;
    }

    public void setRepositorySubpackage(String repositorySubpackage) {
        this.repositorySubpackage = repositorySubpackage;
    }

    public String getRestSubpackage() {
        return restSubpackage;
    }

    public void setRestSubpackage(String restSubpackage) {
        this.restSubpackage = restSubpackage;
    }
    
    public static int compareVersions(String version1, String version2) {
        String[] v1Components = version1.split("\\.");
        String[] v2Components = version2.split("\\.");

        int minLength = Math.min(v1Components.length, v2Components.length);

        for (int i = 0; i < minLength; i++) {
            int v1Part = Integer.parseInt(v1Components[i]);
            int v2Part = Integer.parseInt(v2Components[i]);

            if (v1Part != v2Part) {
                return Integer.compare(v1Part, v2Part);
            }
        }

        return Integer.compare(v1Components.length, v2Components.length);
    }

}
