/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fish.payara.starter.resources;

import jakarta.json.bind.annotation.JsonbProperty;

/**
 *
 * @author Gaurav Gupta
 */
public class ApplicationConfiguration {

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

    @JsonbProperty(GROUP_ID)
    private String groupId = "fish.payara";

    @JsonbProperty(ARTIFACT_ID)
    private String artifactId = "hello-world";

    @JsonbProperty(VERSION)
    private String version = "0.1-SNAPSHOT";

    @JsonbProperty(PACKAGE)
    private String packageName = "fish.payara";

    @JsonbProperty(JAKARTA_EE_VERSION)
    private int jakartaEEVersion = 10;

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

    public int getJakartaEEVersion() {
        return jakartaEEVersion;
    }

    public void setJakartaEEVersion(int jakartaEEVersion) {
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
        return addPayaraApi;
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

}
