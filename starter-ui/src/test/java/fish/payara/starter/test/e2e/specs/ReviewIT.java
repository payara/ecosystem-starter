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
public class ReviewIT {
    // The Review step lists the choices and leads back to each step; the step bar opens any step
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
    void reviewListsTheChoices() {
        starterPage.chooseBuild("Gradle");
        starterPage.setArtifactId("review-app");
        starterPage.goToReview();
        starterPage.checkReviewLine("build", "Gradle");
        starterPage.checkReviewLine("artifactId", "review-app");
        starterPage.checkReviewLine("docker", "No");
    }

    @Test
    void editGoesBackToTheStep() {
        starterPage.goToReview();
        starterPage.editProjectFromReview();
        starterPage.checkStepOpen("step-project");
    }

    @Test
    void stepBarOpensAnyStep() {
        starterPage.openStepFromBar("Step 3 of 4: Features");
        starterPage.checkStepOpen("step-features");
        starterPage.openStepFromBar("Step 1 of 4: Project");
        starterPage.checkStepOpen("step-project");
    }

}
