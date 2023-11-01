
import org.apache.commons.io.FileUtils
import groovy.text.SimpleTemplateEngine
import groovy.io.FileType

def build = request.properties["build"].trim()
def _package = request.properties["package"].trim()
def profile = request.properties["profile"].trim()
def jakartaEEVersion = request.properties["jakartaEEVersion"].trim()
def javaVersion = request.properties["javaVersion"].trim()
def platform = request.properties["platform"].trim()
def includeTests = request.properties["includeTests"].trim()
def docker = request.properties["docker"].trim()
def mpConfig = request.properties["mpConfig"].trim()
def mpOpenAPI = request.properties["mpOpenAPI"].trim()
def mpFaultTolerance = request.properties["mpFaultTolerance"].trim()
def mpMetrics = request.properties["mpMetrics"].trim()
def auth = request.properties["auth"].trim()

def outputDirectory = new File(request.getOutputDirectory(), request.getArtifactId())

validateInput(profile, jakartaEEVersion, javaVersion, platform, outputDirectory)
generateSource(build, _package, platform, jakartaEEVersion, includeTests, docker, mpConfig, mpOpenAPI, auth, outputDirectory)
bindEEPackage(jakartaEEVersion, mpConfig, mpOpenAPI, mpFaultTolerance, mpMetrics, auth, outputDirectory)

private void validateInput(String profile, String jakartaEEVersion, String javaVersion, String platform, File outputDirectory) {
    boolean deleteDirectory = true;

    if (!isValidJakartaEEVersion(jakartaEEVersion)) {
        throwAndDelete("Failed, valid Jakarta EE versions are 8, 9, 9.1, and 10", outputDirectory);
    }

    if (!isValidProfile(profile)) {
        throwAndDelete("Failed, valid Jakarta EE profiles are core, web, and full", outputDirectory);
    }

    if (!isValidJavaVersion(javaVersion)) {
        throwAndDelete("Failed, valid Java SE versions are 8, 11, and 17", outputDirectory);
    }

    if (!isValidPlatform(platform)) {
        throwAndDelete("Failed, valid platform values are server and micro", outputDirectory);
    }

    if (profile.equalsIgnoreCase("core") && !jakartaEEVersion.equals("10")) {
        throwAndDelete("Failed, the Core Profile is only supported for Jakarta EE 10", outputDirectory);
    }

    if (!jakartaEEVersion.equals("8") && javaVersion.equals("8")) {
        throwAndDelete("Failed, Payara 6 does not support Java SE 8", outputDirectory);
    }
}

private boolean isValidJakartaEEVersion(String version) {
    return version.equals("8") || version.equals("9") || version.equals("9.1") || version.equals("10");
}

private boolean isValidProfile(String profile) {
    return profile.equalsIgnoreCase("core") || profile.equalsIgnoreCase("web") || profile.equalsIgnoreCase("full");
}

private boolean isValidJavaVersion(String version) {
    return version.equals("8") || version.equals("11") || version.equals("17");
}

private boolean isValidPlatform(String platform) {
    return platform.equalsIgnoreCase("server") || platform.equalsIgnoreCase("micro");
}

private void throwAndDelete(String message, File outputDirectory) {
    FileUtils.forceDelete(outputDirectory);
    throw new RuntimeException(message);
}

private generateSource(build, _package, platform, jakartaEEVersion,
    includeTests, docker, mpConfig, mpOpenAPI,
    auth, File outputDirectory) {

    if (build.equals("maven")) {
        FileUtils.forceDelete(new File(outputDirectory, "build.gradle"))
        FileUtils.forceDelete(new File(outputDirectory, "settings.gradle"))
    } else {
        FileUtils.forceDelete(new File(outputDirectory, "pom.xml"))
    }
    if (platform.equals("micro")) {
        FileUtils.forceDelete(new File(outputDirectory, "src/test/resources"))
    }
    if (!includeTests.equalsIgnoreCase("true")) {
        FileUtils.forceDelete(new File(outputDirectory, "src/test"))
    }
    if (!docker.equalsIgnoreCase("true")) {
        FileUtils.forceDelete(new File(outputDirectory, "Dockerfile"))
    }
    if (!mpConfig.equalsIgnoreCase("true")) {
        FileUtils.forceDelete(new File(outputDirectory, "src/main/resources/META-INF/microprofile-config.properties"))
    }
    if (!mpOpenAPI.equalsIgnoreCase("true")) {
        FileUtils.forceDelete(new File(outputDirectory, "src/main/webapp/swagger.html"))
    }
    
    def packagePath = _package.replaceAll("\\.", "/")

    if (!auth.equals("formAuthDB")) {
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/java/" + packagePath + "/secured/DatabaseSetup.java"))
    }
    
    if (!auth.equals("formAuthLDAP")) {
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/java/" + packagePath + "/secured/LdapSetup.java"))
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/resources/ldap-test.ldif"))
    }

    if (!auth.equals("formAuthDB") && !auth.equals("formAuthLDAP")) {
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/java/" + packagePath + "/secured/ApplicationConfig.java"))
    }
    
    if (!auth.equals("formAuthFileRealm") && !auth.equals("formAuthDB") && !auth.equals("formAuthLDAP")) {
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/java/" + packagePath + "/secured/AdminResource.java"))
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/java/" + packagePath + "/secured/LogoutResource.java"))
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/java/" + packagePath + "/secured/ProtectedResource.java"))
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/java/" + packagePath + "/secured"))
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/webapp/login.xhtml"))
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/webapp/login_error.xhtml"))
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/webapp/admin/admins.xhtml"))
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/webapp/secured/users.xhtml"))
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/webapp/secured"))
        FileUtils.forceDelete(new File(outputDirectory.path + "/src/main/webapp/admin"))
    }
}

private void bindEEPackage(String jakartaEEVersion, String mpConfig, String mpOpenAPI, String mpFaultTolerance, String mpMetrics, String auth, File outputDirectory) {
    def eePackage = (jakartaEEVersion == '8') ? 'javax' : 'jakarta'
    println "Binding EE package: $eePackage"

    def binding = [eePackage: eePackage, \
        'mpConfig': mpConfig, 'mpOpenAPI': mpOpenAPI.toBoolean(), 'mpFaultTolerance': mpFaultTolerance.toBoolean(), 'mpMetrics': mpMetrics.toBoolean(),
        'formAuthFileRealm': auth.equals("formAuthFileRealm"),
        'formAuthDB': auth.equals("formAuthDB"),
        'formAuthLDAP': auth.equals("formAuthLDAP")
    ]
    def engine = new SimpleTemplateEngine()

    outputDirectory.eachFileRecurse(FileType.FILES) { file ->
        if (shouldProcessFile(file)) {
            processFile(file, engine, binding)
        }
    }
}

private boolean shouldProcessFile(File file) {
    !file.name.endsWith("pom.xml") && !file.name.endsWith("arquillian.xml")
}

private void processFile(File file, SimpleTemplateEngine engine, Map binding) {
    file.withReader('UTF-8') { reader ->
        try {
            def template = engine.createTemplate(reader).make(binding)
            file.text = template.toString()
        } catch (ex) {
            println "Error processing ${file.name}: $ex"
        }
    }
}