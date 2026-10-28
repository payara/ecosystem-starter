/*
 *
 * Copyright (c) 2026 Payara Foundation and/or its affiliates. All rights reserved.
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
import org.junit.jupiter.api.*;

@UsePlaywright
public class DependentFieldsIT {
    // Fields that change with an earlier choice: Gradle turns off Include Tests, Core Profile turns off
    // Security and the ER Diagram Designer, and Jakarta EE 8 or 9 turns off Core Profile.
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

    private static final String TESTS_NOTICE = "Include Tests is not available with Gradle build system.";
    private static final String CORE_NOTICE =
            "Core Profile has no security or ER Diagram Designer, so both are turned off in Features.";
    private static final String SECURITY_NOTICE = "Security configuration is not available with Jakarta EE Core Profile.";
    private static final String ER_NOTICE = "ER Diagram Designer is not available with Jakarta EE Core Profile.";
    private static final String CORE_HINT = "Needs Jakarta EE 10 or later";

    // Build

    @Test
    void gradleTurnsOffIncludeTests() throws InterruptedException {
        starterPage.goToProject();
        starterPage.checkFieldEnabled("includeTests");
        starterPage.includeTests(true);

        starterPage.chooseBuild("Gradle");
        starterPage.checkFieldDisabled("includeTests");
        starterPage.checkFieldNotChecked("includeTests");
        starterPage.checkNotice("includeTestsNotice", TESTS_NOTICE);
    }

    @Test
    void backToMavenTurnsIncludeTestsOn() throws InterruptedException {
        starterPage.goToProject();
        starterPage.chooseBuild("Gradle");
        starterPage.checkFieldDisabled("includeTests");

        starterPage.chooseBuild("Maven");
        starterPage.checkFieldEnabled("includeTests");
        starterPage.checkNoticeHidden("includeTestsNotice");
        // What was deselected for Gradle stays off until the user picks it again
        starterPage.checkFieldNotChecked("includeTests");
    }

    // Jakarta EE Profile

    @Test
    void coreProfileTurnsOffSecurity() throws InterruptedException {
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Core Profile");
        starterPage.checkNotice("coreProfileNotice", CORE_NOTICE);

        starterPage.goToSecuritySection();
        starterPage.checkNotice("securityNotice", SECURITY_NOTICE);
        starterPage.checkFieldDisabled("formAuthFileRealm");
        starterPage.checkFieldDisabled("formAuthDB");
        starterPage.checkFieldDisabled("formAuthLDAP");
        starterPage.checkFieldChecked("noneAuth");
    }

    @Test
    void coreProfileTurnsOffERDiagramDesigner() throws InterruptedException {
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Core Profile");

        starterPage.goToDiagramSection();
        starterPage.checkNotice("erDiagramNotice", ER_NOTICE);
        for (String id : new String[] {"mermaidErDiagramList", "generateJpa", "jpaSubpackage", "generateRepository",
                "repositorySubpackage", "generateRest", "restSubpackage", "generateWeb", "erBuilderOpen"}) {
            starterPage.checkFieldDisabled(id);
        }
        starterPage.checkFieldNotChecked("generateJpa");
        starterPage.checkFieldNotChecked("generateRepository");
        starterPage.checkFieldNotChecked("generateRest");
        starterPage.checkFieldValue("jpaSubpackage", "");
        starterPage.checkFieldValue("repositorySubpackage", "");
        starterPage.checkFieldValue("restSubpackage", "");
        starterPage.checkFieldValue("generateWeb", "none");
    }

    @Test
    void coreProfileClearsWhatWasChosenInSecurityAndERDiagram() throws InterruptedException {
        starterPage.setSecurity("Form Authentication - Database");
        starterPage.setERDiagram("Library Management System", true, "domain", true, "service", true, "resource",
                "Jakarta Faces", "jsf");

        starterPage.setJakartaEE("Jakarta EE 10", "10", "Core Profile");

        starterPage.goToSecuritySection();
        starterPage.checkFieldChecked("noneAuth");
        starterPage.checkFieldNotChecked("formAuthDB");
        starterPage.goToDiagramSection();
        starterPage.checkFieldValue("mermaidErDiagramList", "");
        starterPage.checkFieldNotChecked("generateJpa");
    }

    @Test
    void leavingCoreProfileTurnsSecurityAndERDiagramBackOn() throws InterruptedException {
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Core Profile");
        starterPage.selectJakartaEEProfile("Web Profile");
        starterPage.checkNoticeHidden("coreProfileNotice");

        starterPage.goToSecuritySection();
        starterPage.checkNoticeHidden("securityNotice");
        starterPage.checkFieldEnabled("formAuthFileRealm");
        starterPage.checkFieldEnabled("formAuthDB");
        starterPage.checkFieldEnabled("formAuthLDAP");

        starterPage.goToDiagramSection();
        starterPage.checkNoticeHidden("erDiagramNotice");
        starterPage.checkFieldEnabled("mermaidErDiagramList");
        starterPage.checkFieldEnabled("erBuilderOpen");
        starterPage.checkFieldEnabled("generateWeb");
        // The defaults come back with the controls
        starterPage.checkFieldChecked("generateJpa");
        starterPage.checkFieldChecked("generateRepository");
        starterPage.checkFieldChecked("generateRest");
        starterPage.checkFieldValue("jpaSubpackage", "domain");
        starterPage.checkFieldValue("repositorySubpackage", "service");
        starterPage.checkFieldValue("restSubpackage", "resource");
        starterPage.checkFieldValue("generateWeb", "jsf");
    }

    // Jakarta EE version

    @Test
    void jakartaEE8And9TurnOffCoreProfile() throws InterruptedException {
        starterPage.goToPlatform();
        starterPage.selectJakartaEEVersion("Jakarta EE 8", "8");
        starterPage.checkFieldDisabled("core");
        starterPage.checkNotice("coreProfileHint", CORE_HINT);

        starterPage.selectJakartaEEVersion("Jakarta EE 9.1", "9.1");
        starterPage.checkFieldDisabled("core");

        starterPage.selectJakartaEEVersion("Jakarta EE 10", "10");
        starterPage.checkFieldEnabled("core");
        starterPage.checkNoticeHidden("coreProfileHint");
    }

    @Test
    void olderJakartaEEChangesCoreProfileToWebProfile() throws InterruptedException {
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Core Profile");
        starterPage.goToSecuritySection();
        starterPage.checkFieldDisabled("formAuthDB");

        starterPage.goToPlatform();
        starterPage.selectJakartaEEVersion("Jakarta EE 8", "8");
        starterPage.checkFieldDisabled("core");
        starterPage.checkFieldNotChecked("core");
        starterPage.checkFieldChecked("web");
        starterPage.checkNoticeHidden("coreProfileNotice");

        // Web Profile has security and the designer, so Core Profile's changes to them are undone
        starterPage.goToSecuritySection();
        starterPage.checkFieldEnabled("formAuthDB");
        starterPage.checkNoticeHidden("securityNotice");
        starterPage.goToDiagramSection();
        starterPage.checkFieldEnabled("mermaidErDiagramList");
        starterPage.checkNoticeHidden("erDiagramNotice");
    }

    @Test
    void jakartaEEVersionChangesJavaVersions() throws InterruptedException {
        starterPage.goToPlatform();
        starterPage.selectJakartaEEVersion("Jakarta EE 8", "8");
        starterPage.checkSelectOptions("javaVersion", "Java SE 11", "Java SE 8");

        starterPage.selectJakartaEEVersion("Jakarta EE 10", "10");
        starterPage.selectPayaraPlatformVersion("6.2025.1", "6.2025.1");
        starterPage.checkSelectOptions("javaVersion", "Java SE 21", "Java SE 17", "Java SE 11");
    }
}
