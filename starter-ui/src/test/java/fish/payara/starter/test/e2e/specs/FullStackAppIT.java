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
public class FullStackAppIT {

    // Generates full stack applications including tests
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
    void productCatalogJdk8() throws InterruptedException, IOException {
        // Build with Maven, EE8 Web profile, jdk8, Payara Server 5, Full MP, ER Diagram = Product Catalog, tests, Security DB
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "productCatalogJdk8", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 8", "8", "Web Profile");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "5.2022.5", "5.2022.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", true, "Java SE 8", "8");
        starterPage.setMicroProfile("Full MP");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Product Catalog", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("Form Authentication - Database");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "productCatalogJdk8.zip"));

        FileManagement.unzip("./target/test-app-maven/productCatalogJdk8.zip", "./target/test-app-maven/productCatalogJdk8");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/productCatalogJdk8/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/productCatalogJdk8/pom.xml"),
                "<maven.compiler.target>1.8</maven.compiler.target>"));
    }

    /*@Test
    // Disabled - gradle miss the ldap library - FISH-11339
    void salesTrackingJdk8() throws InterruptedException, IOException {
        // Build with Gradle, EE8 Full, jdk8, Payara Micro 5, Full MP, ER Diagram = Sales Tracking, no test, Security LDAP
        starterPage.setProjectDescription("Gradle", "fish.payara.playwright.test", "salesTrackingJdk8", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 8", "8", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Micro", "5.2022.5", "5.2022.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", true, "Java SE 8", "8");
        starterPage.setMicroProfile("Full MP");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Sales Tracking", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("Form Authentication - LDAP");
        starterPage.generate(page, Paths.get("./target/test-app-gradle", "salesTrackingJdk8.zip"));

        FileManagement.unzip("./target/test-app-gradle/salesTrackingJdk8.zip", "./target/test-app-gradle/salesTrackingJdk8");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-gradle/salesTrackingJdk8/build.gradle")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-gradle/salesTrackingJdk8/build.gradle"),
                "sourceCompatibility = 1.8"));
    }*/

    @Test
    void inventorySystemJdk11() throws InterruptedException, IOException {
        // Build with Maven, EE8, jdk11, Web Profile, Payara Server 5, MP Metrics, ER Diagram = Inventory System, tests
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "inventorySystemJdk11", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 8", "8", "Web Profile");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "5.2022.5", "5.2022.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", true, "Java SE 11", "11");
        starterPage.setMicroProfile("MicroProfile Metrics");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Inventory System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.openERDiagramPreview();
        starterPage.checkDiagramCodeContains("PRODUCT ||--o{ INVENTORY : contains");
        starterPage.checkDiagramGraphContains("INVENTORY");
        starterPage.closeERDiagramPreview();
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "inventorySystemJdk11.zip"));

        FileManagement.unzip("./target/test-app-maven/inventorySystemJdk11.zip", "./target/test-app-maven/inventorySystemJdk11");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/inventorySystemJdk11/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/inventorySystemJdk11/pom.xml"),
                "<maven.compiler.release>11</maven.compiler.release>"));
    }

    @Test
    void flightReservationSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Web Profile, jdk17, Payara Micro 6, MP Metrics + Fault Tolerance, Payara Cloud, ER Diagram = Flight Reservation System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "flightReservationSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Web Profile");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", true, "Java SE 17", "17");
        starterPage.setMicroProfile("MicroProfile Metrics");
        starterPage.setMicroProfile("MicroProfile Fault Tolerance");
        starterPage.setDeployment(false, true);
        starterPage.setERDiagram("Flight Reservation System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.openERDiagramPreview();
        starterPage.checkDiagramCodeContains("FLIGHT ||--o{ RESERVATION : ");
        starterPage.checkDiagramGraphContains("PASSENGER");
        starterPage.closeERDiagramPreview();
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "flightReservationSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/flightReservationSystemJdk17.zip", "./target/test-app-maven/flightReservationSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/flightReservationSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/flightReservationSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    // test the different templates
    @Test
    void auctionManagementSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Auction Management System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "auctionManagementSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Auction Management System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "auctionManagementSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/auctionManagementSystemJdk17.zip", "./target/test-app-maven/auctionManagementSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/auctionManagementSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/auctionManagementSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void customerRelationshipManagementJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Customer Relationship Management, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "customerRelationshipManagementJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Customer Relationship Management", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "customerRelationshipManagementJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/customerRelationshipManagementJdk17.zip", "./target/test-app-maven/customerRelationshipManagementJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/customerRelationshipManagementJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/customerRelationshipManagementJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void employeeManagementJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Employee Management, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "employeeManagementJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Employee Management", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "employeeManagementJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/employeeManagementJdk17.zip", "./target/test-app-maven/employeeManagementJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/employeeManagementJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/employeeManagementJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void energyManagementSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Energy Management System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "energyManagementSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Energy Management System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "energyManagementSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/energyManagementSystemJdk17.zip", "./target/test-app-maven/energyManagementSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/energyManagementSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/energyManagementSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void eventManagementJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Event Management, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "eventManagementJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Event Management", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "eventManagementJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/eventManagementJdk17.zip", "./target/test-app-maven/eventManagementJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/eventManagementJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/eventManagementJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void healthcareSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Healthcare System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "healthcareSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Healthcare System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "healthcareSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/healthcareSystemJdk17.zip", "./target/test-app-maven/healthcareSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/healthcareSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/healthcareSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void insuranceClaimManagementSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Insurance Claim Management System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "insuranceClaimManagementSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Insurance Claim Management System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "insuranceClaimManagementSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/insuranceClaimManagementSystemJdk17.zip", "./target/test-app-maven/insuranceClaimManagementSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/insuranceClaimManagementSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/insuranceClaimManagementSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void inventorySystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Inventory System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "inventorySystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Inventory System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "inventorySystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/inventorySystemJdk17.zip", "./target/test-app-maven/inventorySystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/inventorySystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/inventorySystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void laboratoryInformationManagementSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Laboratory Information Management System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "laboratoryInformationManagementSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Laboratory Information Management System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "laboratoryInformationManagementSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/laboratoryInformationManagementSystemJdk17.zip", "./target/test-app-maven/laboratoryInformationManagementSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/laboratoryInformationManagementSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/laboratoryInformationManagementSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void lawFirmManagementSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Law Firm Management System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "lawFirmManagementSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Law Firm Management System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "lawFirmManagementSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/lawFirmManagementSystemJdk17.zip", "./target/test-app-maven/lawFirmManagementSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/lawFirmManagementSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/lawFirmManagementSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void legalCaseManagementSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Legal Case Management System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "legalCaseManagementSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Legal Case Management System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "legalCaseManagementSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/legalCaseManagementSystemJdk17.zip", "./target/test-app-maven/legalCaseManagementSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/legalCaseManagementSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/legalCaseManagementSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void libraryManagementSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Library Management System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "libraryManagementSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Library Management System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "libraryManagementSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/libraryManagementSystemJdk17.zip", "./target/test-app-maven/libraryManagementSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/libraryManagementSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/libraryManagementSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void membershipManagementSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Membership Management System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "membershipManagementSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Membership Management System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "membershipManagementSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/membershipManagementSystemJdk17.zip", "./target/test-app-maven/membershipManagementSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/membershipManagementSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/membershipManagementSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void musicStreamingPlatformJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Music Streaming Platform, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "musicStreamingPlatformJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Music Streaming Platform", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "musicStreamingPlatformJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/musicStreamingPlatformJdk17.zip", "./target/test-app-maven/musicStreamingPlatformJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/musicStreamingPlatformJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/musicStreamingPlatformJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void propertyManagementSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Property Management System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "propertyManagementSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Property Management System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "propertyManagementSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/propertyManagementSystemJdk17.zip", "./target/test-app-maven/propertyManagementSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/propertyManagementSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/propertyManagementSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void recruitmentManagementSystemJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Recruitment Management System, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "recruitmentManagementSystemJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Recruitment Management System", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "recruitmentManagementSystemJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/recruitmentManagementSystemJdk17.zip", "./target/test-app-maven/recruitmentManagementSystemJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/recruitmentManagementSystemJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/recruitmentManagementSystemJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

    @Test
    void salesTrackingJdk17() throws InterruptedException, IOException {
        // Build with Maven, EE10 Full profile, jdk17, Payara Micro 6, no MP, ER Diagram = Sales Tracking, include tests, no security
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "salesTrackingJdk17", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Platform");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.5", "6.2025.5");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 17", "17");
        starterPage.setMicroProfile("");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Sales Tracking", true, "domain", true, "service", true, "resource", "HTML", "html");
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get("./target/test-app-maven", "salesTrackingJdk17.zip"));

        FileManagement.unzip("./target/test-app-maven/salesTrackingJdk17.zip", "./target/test-app-maven/salesTrackingJdk17");
        assertTrue(FileManagement.checkFilePresence(new File("./target/test-app-maven/salesTrackingJdk17/pom.xml")));
        assertTrue(FileManagement.checkFileContains(new File("./target/test-app-maven/salesTrackingJdk17/pom.xml"),
                "<maven.compiler.release>17</maven.compiler.release>"));
    }

}
