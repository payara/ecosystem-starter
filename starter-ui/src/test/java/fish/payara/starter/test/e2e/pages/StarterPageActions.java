/*
 *
 * Copyright (c) 2025-2026 Payara Foundation and/or its affiliates. All rights reserved.
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
package fish.payara.starter.test.e2e.pages;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import java.nio.file.Path;
import java.util.regex.Pattern;

@UsePlaywright
public class StarterPageActions {

    private StarterPageLocators locators; 
    private Page page;

    public StarterPageActions(Page page) {
        this.page = page;
        locators = new StarterPageLocators(page);
    }

    // The wizard shows one step at a time: Back or Next until the step holding the block (a step,
    // a section of one, or a field) is the open one.
    private void goToStep(Locator block) {
        String indexOf = "el => [...document.querySelectorAll('fieldset.form-steps__step')]"
                + ".indexOf(el.closest('fieldset.form-steps__step'))";
        int target = ((Number) block.evaluate(indexOf)).intValue();
        for (int i = 0; i < locators.steps.get().count(); i++) {
            int open = ((Number) page.evaluate("() => [...document.querySelectorAll('fieldset.form-steps__step')]"
                    + ".findIndex(step => step.classList.contains('form-steps__step--active'))")).intValue();
            if (open == target) {
                break;
            }
            (open < target ? locators.nextButton : locators.backButton).get().click();
        }
        PlaywrightAssertions.assertThat(block).isVisible();
    }

    // A radio or checkbox by the exact text of its label (help tooltips repeat option names). The
    // inputs themselves are visually hidden, so the label's text is clicked; Playwright follows it
    // to the input for isChecked().
    private Locator option(Locator scope, String text) {
        return scope.locator("label").getByText(text, new Locator.GetByTextOptions().setExact(true));
    }

    // Steps
    public void next() {
        locators.nextButton.get().click();
    }

    public void checkStepOpen(String id) {
        PlaywrightAssertions.assertThat(page.locator("fieldset#" + id)).hasClass(
                Pattern.compile("\\bform-steps__step--active\\b"));
    }

    // The step bar has a button per step, named after it ("Step 3 of 4: Features"). Once the form is
    // scrolled to, the bar docks under the page header and the form's own copy hides, so the form is
    // scrolled to first and the docked bar is clicked.
    public void openStepFromBar(String name) {
        page.locator("#appForm").scrollIntoViewIfNeeded();
        PlaywrightAssertions.assertThat(locators.dockedStepBar.get()).isVisible();
        locators.dockedStepBar.get().getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(name).setExact(true)).click();
    }

    // Field checks: once a field has been left, a problem marks it invalid and shows a message after it.
    public void leaveField(String id) {
        page.locator("#" + id).blur();
    }

    public void checkFieldProblem(String id, String message) {
        PlaywrightAssertions.assertThat(page.locator("#" + id)).hasAttribute("aria-invalid", "true");
        PlaywrightAssertions.assertThat(page.locator("#" + id + "-problem")).hasText(message);
    }

    public void checkFieldFocused(String id) {
        PlaywrightAssertions.assertThat(page.locator("#" + id)).isFocused();
    }

    public void checkFieldValue(String id, String value) {
        PlaywrightAssertions.assertThat(page.locator("#" + id)).hasValue(value);
    }

    public void checkFieldChecked(String id) {
        PlaywrightAssertions.assertThat(page.locator("#" + id)).isChecked();
    }

    // Dependent fields: a choice can turn other fields off (or back on) and say why in a notice.
    public void checkFieldDisabled(String id) {
        PlaywrightAssertions.assertThat(page.locator("#" + id)).isDisabled();
    }

    public void checkFieldEnabled(String id) {
        PlaywrightAssertions.assertThat(page.locator("#" + id)).isEnabled();
    }

    public void checkFieldNotChecked(String id) {
        PlaywrightAssertions.assertThat(page.locator("#" + id)).isChecked(
                new LocatorAssertions.IsCheckedOptions().setChecked(false));
    }

    // A notice (or hint) shows or hides by itself; the step holding it has to be the open one to see it.
    public void checkNotice(String id, String text) {
        PlaywrightAssertions.assertThat(page.locator("#" + id)).isVisible();
        PlaywrightAssertions.assertThat(page.locator("#" + id)).containsText(text);
    }

    public void checkNoticeHidden(String id) {
        PlaywrightAssertions.assertThat(page.locator("#" + id)).isHidden();
    }

    public void checkSelectOptions(String id, String... labels) {
        PlaywrightAssertions.assertThat(page.locator("select#" + id + " option")).hasText(labels);
    }

    public void confirmGdpr() {
        if (!locators.gdprArea.get().all().isEmpty()) {
            locators.gdprConfirmButton.get().click();
        }
    }

    // Project step
    public void goToProject() {
        goToStep(locators.projectBlock.get());
        PlaywrightAssertions.assertThat(locators.projectBlockTitle.get()).isVisible();
    }

    public void chooseBuild(String value) {
        option(locators.projectBlock.get(), value).click();
        PlaywrightAssertions.assertThat(option(locators.projectBlock.get(), value)).isChecked();
    }

    public void setGroupId(String value) {
        locators.groupIdInputBox.get().clear();
        locators.groupIdInputBox.get().fill(value);
        PlaywrightAssertions.assertThat(locators.groupIdInputBox.get()).hasValue(value);
    }

    public void setArtifactId(String value) {
        locators.artifactIdInputBox.get().clear();
        locators.artifactIdInputBox.get().fill(value);
        PlaywrightAssertions.assertThat(locators.artifactIdInputBox.get()).hasValue(value);
    }

    public void setVersion(String value) {
        locators.versionInputBox.get().clear();
        locators.versionInputBox.get().fill(value);
        PlaywrightAssertions.assertThat(locators.versionInputBox.get()).hasValue(value);
    }

    public void setPackage(String value) {
        locators.projectPackage.get().clear();
        locators.projectPackage.get().fill(value);
        PlaywrightAssertions.assertThat(locators.projectPackage.get()).hasValue(value);
    }

    public void includeTests(Boolean value) {
        if (value) {
            locators.projectIncludeTestsLabel.get().click();
            PlaywrightAssertions.assertThat(locators.projectIncludeTests.get()).isChecked();
        } else {
            PlaywrightAssertions.assertThat(locators.projectIncludeTests.get()).isChecked(
                    new LocatorAssertions.IsCheckedOptions().setChecked(false));
        }
    }

    public void setProject(String build, String groupId, String artifactId, String version, String packageValue,
            Boolean includeTests) {
        goToProject();
        chooseBuild(build);
        setGroupId(groupId);
        setArtifactId(artifactId);
        setVersion(version);
        setPackage(packageValue);
        includeTests(includeTests);
    }

    // Platform step
    public void goToPlatform() {
        goToStep(locators.platformBlock.get());
        PlaywrightAssertions.assertThat(locators.platformBlockTitle.get()).isVisible();
    }

    public void selectJakartaEEVersion(String option, String value) {
        locators.jakartaeeVersion.get().selectOption(option);
        PlaywrightAssertions.assertThat(locators.jakartaeeVersion.get()).hasValue(value);
    }

    public void selectJakartaEEProfile(String value) {
        option(locators.platformBlock.get(), value).click();
        PlaywrightAssertions.assertThat(option(locators.platformBlock.get(), value)).isChecked();
    }

    public void selectPayaraPlatform(String value) {
        option(locators.platformBlock.get(), value).click();
        PlaywrightAssertions.assertThat(option(locators.platformBlock.get(), value)).isChecked();
    }

    public void selectPayaraPlatformVersion(String option, String value) {
        locators.payaraPlatformVersion.get().selectOption(option);
        PlaywrightAssertions.assertThat(locators.payaraPlatformVersion.get()).hasValue(value);
    }

    public void selectJavaVersion(String option, String value) {
        locators.projectJavaVersion.get().selectOption(option);
        PlaywrightAssertions.assertThat(locators.projectJavaVersion.get()).hasValue(value);
    }

    // Each choice narrows the next ones, so they are made in the page's order.
    public void setPlatform(String jakartaEEVersion, String jakartaEEValue, String jakartaEEProfile, String platform,
            String platformVersion, String platformValue, String javaVersion, String javaValue) {
        goToPlatform();
        selectJakartaEEVersion(jakartaEEVersion, jakartaEEValue);
        selectJakartaEEProfile(jakartaEEProfile);
        selectPayaraPlatform(platform);
        selectPayaraPlatformVersion(platformVersion, platformValue);
        selectJavaVersion(javaVersion, javaValue);
    }

    public void setJakartaEE(String jakartaEEVersion, String jakartaEEValue, String jakartaEEProfile) {
        goToPlatform();
        selectJakartaEEVersion(jakartaEEVersion, jakartaEEValue);
        selectJakartaEEProfile(jakartaEEProfile);
    }

    // Micro Profile
    public void goToMicroProfileSection() throws InterruptedException {
        goToStep(locators.microProfileBlock.get());
        PlaywrightAssertions.assertThat(locators.microProfileBlockTitle.get()).isVisible();
    }

    public void selectMicroProfile(String value) {
        if (!value.isEmpty()) {
            option(locators.microProfileBlock.get(), value).click();
            PlaywrightAssertions.assertThat(option(locators.microProfileBlock.get(), value)).isChecked();
        }
    }

    public void setMicroProfile(String value) throws InterruptedException {
        goToMicroProfileSection();
        selectMicroProfile(value);
    }

    // Security Configuration
    public void goToSecuritySection() throws InterruptedException {
        goToStep(locators.securityBlock.get());
        PlaywrightAssertions.assertThat(locators.securityBlockTitle.get()).isVisible();
    }


    public void setLogin(String value) {
        option(locators.securityBlock.get(), value).click();
        PlaywrightAssertions.assertThat(option(locators.securityBlock.get(), value)).isChecked();
    }


    public void setSecurity(String login) throws InterruptedException {
        goToSecuritySection();
        setLogin(login);
    }

    // Deployment Options
    public void goToDeploymentSection() throws InterruptedException {
        goToStep(locators.deploymentBlock.get());
        PlaywrightAssertions.assertThat(locators.deploymentBlockTitle.get()).isVisible();
    }

    public void enableDockerDeployment() {
        if (!locators.deploymentDocker.get().isChecked()) {
            locators.deploymentDockerLabel.get().click();
        }
        PlaywrightAssertions.assertThat(locators.deploymentDocker.get()).isChecked();
    }

    public void disableDockerDeployment() {
        if (locators.deploymentDocker.get().isChecked()) {
            locators.deploymentDockerLabel.get().click();
        }
        PlaywrightAssertions.assertThat(locators.deploymentDocker.get()).isChecked(
                new LocatorAssertions.IsCheckedOptions().setChecked(false));
    }

    public void setDeployment(Boolean docker) throws InterruptedException {
        goToDeploymentSection();
        if (docker) {
            enableDockerDeployment();
        } else {
            disableDockerDeployment();
        }
    }

    // ER Diagram Designer
    public void goToDiagramSection() throws InterruptedException {
        goToStep(locators.diagramBlock.get());
        PlaywrightAssertions.assertThat(locators.diagramBlockTitle.get()).isVisible();
    }

    public void skipERDiagramSection() throws InterruptedException {
        goToDiagramSection();
        locators.nextButton.get().click();
    }

    // Picked through the search field; the native select behind it holds the value.
    public void selectERDiagram(String value) {
        if (!value.isEmpty()) {
            locators.searchERDiagramInput.get().click();
            locators.searchERDiagramInput.get().fill(value);
            locators.diagramBlock.get().getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setName(value).setExact(true)).click();
        }
        PlaywrightAssertions.assertThat(locators.searchERDiagram.get()).hasValue(value);
    }

    public void enableGenerateJPA() {
        if (!locators.generateJPA.get().isChecked()) {
            locators.generateJPA.get().click();
        }
        PlaywrightAssertions.assertThat(locators.generateJPA.get()).isChecked();
    }

    public void setJpaPackage(String value) {
        locators.jpaSubPackage.get().clear();
        locators.jpaSubPackage.get().fill(value);
        PlaywrightAssertions.assertThat(locators.jpaSubPackage.get()).hasValue(value);
    }

    public void enableGenerateRepository() {
        if (!locators.generateRepository.get().isChecked()) {
            locators.generateRepository.get().click();
        }
        PlaywrightAssertions.assertThat(locators.generateRepository.get()).isChecked();
    }

    public void setRepoPackage(String value) {
        locators.repositorySubpackage.get().clear();
        locators.repositorySubpackage.get().fill(value);
        PlaywrightAssertions.assertThat(locators.repositorySubpackage.get()).hasValue(value);
    }

    public void enableGenerateRest() {

        if (!locators.generateRest.get().isChecked()) {
            locators.generateRest.get().click();
        }
        PlaywrightAssertions.assertThat(locators.generateRest.get()).isChecked();
    }

    public void setRestPackage(String value) {
        locators.restSubpackage.get().clear();
        locators.restSubpackage.get().fill(value);
        PlaywrightAssertions.assertThat(locators.restSubpackage.get()).hasValue(value);
    }

    public void selectGenerateWeb(String text, String value) {
        locators.generateWeb.get().selectOption(text);
        PlaywrightAssertions.assertThat(locators.generateWeb.get()).hasValue(value);
    }

    public void setERDiagram(String ERDiagram, Boolean generateJPA, String jpaPackage, Boolean generateRepository,
            String repoPackage, Boolean generateRest, String restPackage, String webText, String webValue)
            throws InterruptedException {
        goToDiagramSection();
        selectERDiagram(ERDiagram);
        if (generateJPA) {
            enableGenerateJPA();
        }
        setJpaPackage(jpaPackage);
        if (generateRepository) {
            enableGenerateRepository();
        }
        setRepoPackage(repoPackage);
        if (generateRest) {
            enableGenerateRest();
        }
        setRestPackage(restPackage);
        selectGenerateWeb(webText, webValue);
    }

    // Diagram Builder & Live Preview
    public void openERDiagramPreview() throws InterruptedException {
        locators.diagramPreviewButton.get().click();
        Thread.sleep(1000);
        PlaywrightAssertions.assertThat(locators.previewDiagramBlock.get()).isVisible();
    }

    public void checkDiagramCodeContains(String value) {
        locators.codeTab.get().click();
        PlaywrightAssertions.assertThat(locators.diagramSourceText.get()).containsText(value);
    }

    public void checkDiagramGraphContains(String value) {
        locators.previewTab.get().click();
        PlaywrightAssertions.assertThat(locators.livePreviewGraph.get()).containsText(value);
    }

    public void closeERDiagramPreview() throws InterruptedException {
        locators.closePreview.get().click();
        Thread.sleep(1000);
        PlaywrightAssertions.assertThat(locators.previewDiagramBlock.get()).isHidden();
    }


    // Review
    public void goToReview() {
        goToStep(locators.reviewBlock.get());
    }

    // A line of the review, by the field (or radio group) it shows.
    public void checkReviewLine(String key, String text) {
        PlaywrightAssertions.assertThat(locators.reviewBlock.get().locator("dd[data-summary='" + key + "']")).hasText(text);
    }

    public void editProjectFromReview() {
        locators.editProjectButton.get().click();
    }

    public void editPlatformFromReview() {
        locators.editPlatformButton.get().click();
    }

    public void editFeaturesFromReview() {
        locators.editFeaturesButton.get().click();
    }

    // Download project and the result panel
    public void clickDownload() {
        locators.generateButton.get().click();
    }

    // Waits for the file itself (path()): WebKit won't close the context while a download is unfinished.
    public void download() {
        page.waitForDownload(new Page.WaitForDownloadOptions().setTimeout(120000), this::clickDownload).path();
    }

    public void checkProjectReady(String filename) {
        PlaywrightAssertions.assertThat(locators.resultReady.get()).isVisible();
        PlaywrightAssertions.assertThat(locators.resultReadyFile.get()).hasText(filename);
    }

    public void checkProjectFailed() {
        PlaywrightAssertions.assertThat(locators.resultFailed.get()).isVisible();
    }

    public void tryAgain() {
        page.waitForDownload(new Page.WaitForDownloadOptions().setTimeout(120000),
                () -> locators.tryAgainButton.get().click()).path();
    }

    public void startOver() {
        locators.startOverButton.get().click();
        PlaywrightAssertions.assertThat(locators.resultBlock.get()).isHidden();
    }

    // The generator answers with a server error until restoreGeneration().
    public void failGeneration() {
        page.route("**/resources/starter", route -> route.fulfill(new Route.FulfillOptions().setStatus(500)));
    }

    public void restoreGeneration() {
        page.unroute("**/resources/starter");
    }

    public void generate(Page page, Path filepath) {
        Download download = page.waitForDownload(new Page.WaitForDownloadOptions().setTimeout(120000), () -> {
            locators.generateButton.get().click();
        });
        download.saveAs(filepath);
    }

}
