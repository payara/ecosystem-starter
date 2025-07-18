/*
 *
 * Copyright (c) 2025 Payara Foundation and/or its affiliates. All rights reserved.
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
package fish.payara.starter.test.e2e.specs;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.UsePlaywright;
import fish.payara.starter.test.e2e.pages.*;
import fish.payara.starter.test.e2e.utils.FileManagement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public class PayaraVersionsIT {

    // Generates a simple application permuting the version of JDK and Payara used (server/micro)
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;
    private static StarterPageActions starterPage;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @BeforeEach
    void openPage() {
        context = browser.newContext();
        page = context.newPage();
        page.setDefaultTimeout(90000);
        page.navigate("http://localhost:8082/payara-starter");
        page.waitForSelector("div.hero", new Page.WaitForSelectorOptions().setTimeout(120000));

        starterPage = new StarterPageActions(page);
        starterPage.confirmGdpr();
    }

    @AfterEach
    void closePage() {
        context.close();
    }
    
    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @Test
    void PayaraServer5EE8Jdk11() throws InterruptedException, IOException {
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "PayaraServer5EE8Jdk11", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 8", "8", "Platform");
        starterPage.setPayaraPlatform("Payara Server", "5.2022.5", "5.2022.5");
        starterPage.closeGuidePopup();
        starterPage.setProjectConfiguration("fish.payara.test", false, "Java SE 11", "11");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("", false, "domain", false, "service", false, "resource", "None", "none");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "PayaraServer5EE8Jdk11.zip"));

        FileManagement.unzip("./target/test-app-maven/PayaraServer5EE8Jdk11.zip", "./target/test-app-maven/PayaraServer5EE8Jdk11");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/PayaraServer5EE8Jdk11/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/PayaraServer5EE8Jdk11/pom.xml"),
                "<maven.compiler.release>11</maven.compiler.release>"));
    }

    @Test
    void PayaraServer6EE9Jdk11() throws InterruptedException, IOException {
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "PayaraServer6EE9Jdk11", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 9", "9", "Platform");
        starterPage.setPayaraPlatform("Payara Server", "6.2025.1", "6.2025.1");
        starterPage.closeGuidePopup();
        starterPage.setProjectConfiguration("fish.payara.test", false, "Java SE 11", "11");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("", false, "domain", false, "service", false, "resource", "None", "none");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "PayaraServer6EE9Jdk11.zip"));

        FileManagement.unzip("./target/test-app-maven/PayaraServer6EE9Jdk11.zip", "./target/test-app-maven/PayaraServer6EE9Jdk11");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/PayaraServer6EE9Jdk11/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/PayaraServer6EE9Jdk11/pom.xml"),
                "<maven.compiler.release>11</maven.compiler.release>"));
    }

    @Test
    void PayaraServer6EE91Jdk11() throws InterruptedException, IOException {
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "PayaraServer6EE91Jdk11", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 9.1", "9.1", "Platform");
        starterPage.setPayaraPlatform("Payara Server", "6.2025.1", "6.2025.1");
        starterPage.closeGuidePopup();
        starterPage.setProjectConfiguration("fish.payara.test", false, "Java SE 11", "11");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("", false, "domain", false, "service", false, "resource", "None", "none");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "PayaraServer6EE91Jdk11.zip"));

        FileManagement.unzip("./target/test-app-maven/PayaraServer6EE91Jdk11.zip", "./target/test-app-maven/PayaraServer6EE91Jdk11");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/PayaraServer6EE91Jdk11/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/PayaraServer6EE91Jdk11/pom.xml"),
                "<maven.compiler.release>11</maven.compiler.release>"));
    }

    @Test
    void PayaraServer6EE10Jdk17() throws InterruptedException, IOException {
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "PayaraServer6EE10Jdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.setPayaraPlatform("Payara Server", "6.2025.1", "6.2025.1");
        starterPage.closeGuidePopup();
        starterPage.setProjectConfiguration("fish.payara.test", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("", false, "domain", false, "service", false, "resource", "None", "none");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "PayaraServer6EE10Jdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/PayaraServer6EE10Jdk17.zip", "./target/test-app-maven/PayaraServer6EE10Jdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/PayaraServer6EE10Jdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/PayaraServer6EE10Jdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void PayaraMicro6EE10Jdk17() throws InterruptedException, IOException {
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "PayaraMicro6EE10Jdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.setPayaraPlatform("Payara Micro", "6.2025.1", "6.2025.1");
        starterPage.closeGuidePopup();
        starterPage.setProjectConfiguration("fish.payara.test", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("", false, "domain", false, "service", false, "resource", "None", "none");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "PayaraMicro6EE10Jdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/PayaraMicro6EE10Jdk17.zip", "./target/test-app-maven/PayaraMicro6EE10Jdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/PayaraMicro6EE10Jdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/PayaraMicro6EE10Jdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void PayaraServerWeb6EE10Jdk17() throws InterruptedException, IOException {
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "PayaraServerWeb6EE10Jdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Web Profile");
        starterPage.setPayaraPlatform("Payara Server", "6.2025.1", "6.2025.1");
        starterPage.closeGuidePopup();
        starterPage.setProjectConfiguration("fish.payara.test", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("", false, "domain", false, "service", false, "resource", "None", "none");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "PayaraServerWeb6EE10Jdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/PayaraServerWeb6EE10Jdk17.zip", "./target/test-app-maven/PayaraServerWeb6EE10Jdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/PayaraServerWeb6EE10Jdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/PayaraServerWeb6EE10Jdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void PayaraServerCore6EE10Jdk17() throws InterruptedException, IOException {
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "PayaraServerCore6EE10Jdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Core Profile");
        starterPage.setPayaraPlatform("Payara Server", "6.2025.1", "6.2025.1");
        starterPage.closeGuidePopup();
        starterPage.setProjectConfiguration("fish.payara.test", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("", false, "domain", false, "service", false, "resource", "None", "none");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "PayaraServerCore6EE10Jdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/PayaraServerCore6EE10Jdk17.zip", "./target/test-app-maven/PayaraServerCore6EE10Jdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/PayaraServerCore6EE10Jdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/PayaraServerCore6EE10Jdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void PayaraMicro6EE10Jdk21() throws InterruptedException, IOException {
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "PayaraMicro6EE10Jdk21", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.setPayaraPlatform("Payara Micro", "6.2025.1", "6.2025.1");
        starterPage.closeGuidePopup();
        starterPage.setProjectConfiguration("fish.payara.test", false, "Java SE 21", "21");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("", false, "domain", false, "service", false, "resource", "None", "none");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "PayaraMicro6EE10Jdk21.zip"));

       FileManagement.unzip("./target/test-app-maven/PayaraMicro6EE10Jdk21.zip", "./target/test-app-maven/PayaraMicro6EE10Jdk21");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/PayaraMicro6EE10Jdk21/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/PayaraMicro6EE10Jdk21/pom.xml"),
                "<maven.compiler.release>21</maven.compiler.release>"));
    }

    @Test
    void PayaraServer7EE11Jdk21() throws InterruptedException, IOException {
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "PayaraMicro6EE10Jdk21", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 11", "11", "Platform");
        starterPage.setPayaraPlatform("Payara Server", "7.2025.1.Alpha2", "7.2025.1.Alpha2");
        starterPage.closeGuidePopup();
        starterPage.setProjectConfiguration("fish.payara.test", false, "Java SE 21", "21");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("", false, "domain", false, "service", false, "resource", "None", "none");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "PayaraServer7EE11Jdk21.zip"));

        FileManagement.unzip("./target/test-app-maven/PayaraServer7EE11Jdk21.zip", "./target/test-app-maven/PayaraServer7EE11Jdk21");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/PayaraServer7EE11Jdk21/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/PayaraServer7EE11Jdk21/pom.xml"),
                "<maven.compiler.release>21</maven.compiler.release>"));
    }
}
