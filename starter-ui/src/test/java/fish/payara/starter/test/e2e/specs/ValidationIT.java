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
public class ValidationIT {
    // Field checks: a value the generator can't use is flagged at its field, and the form isn't sent
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

    private static final String NAME_PROBLEM =
            "Use only letters, digits, dots (.), hyphens (-) and underscores (_), with no spaces.";
    private static final String RESERVED_NEW_PROBLEM =
            "“new” is a reserved word in Java, so it can’t be part of a package name.";

    @Test
    void groupIdWithSpaceStopsNext() {
        starterPage.setGroupId("fish payara");
        starterPage.leaveField("groupId");
        starterPage.checkFieldProblem("groupId", NAME_PROBLEM);

        starterPage.next();
        starterPage.checkStepOpen("step-project");
        starterPage.checkFieldFocused("groupId");

        starterPage.setGroupId("fish.payara");
        starterPage.next();
        starterPage.checkStepOpen("step-platform");
    }

    @Test
    void reservedWordInPackageIsFlagged() {
        starterPage.setPackage("com.new.app");
        starterPage.leaveField("package");
        starterPage.checkFieldProblem("package", RESERVED_NEW_PROBLEM);
    }

    @Test
    void downloadOpensTheStepWithAProblem() {
        starterPage.setPackage("com.new.app");
        starterPage.openStepFromBar("Step 4 of 4: Review");
        starterPage.checkStepOpen("step-review");
        starterPage.checkReviewLine("package", "com.new.app Needs a fix");

        starterPage.clickDownload();
        starterPage.checkStepOpen("step-project");
        starterPage.checkFieldProblem("package", RESERVED_NEW_PROBLEM);
        starterPage.checkFieldFocused("package");
    }

}
