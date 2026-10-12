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
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import java.nio.file.Path;
import java.util.regex.Pattern;

@UsePlaywright
public class StarterPageActions {

    private StarterPageLocators locators; 
    private Page page;

    public StarterPageActions(Page page) {
        this.page = page;
        locators = new StarterPageLocators(page);
        hideMarketingPopups();
    }

    // HubSpot's marketing popups (live content that changes over time) anchor to the bottom of the
    // viewport, over the wizard's Back / Next row. The tests exercise the Starter form, so they are
    // hidden; the cookie banner stays, for confirmGdpr().
    private void hideMarketingPopups() {
        page.addStyleTag(new Page.AddStyleTagOptions().setContent(
                "[id^='hs-web-interactives'], [data-hs-container-type] { display: none !important; }"));
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

    // A radio or checkbox by the exact text of its label (help tooltips repeat option names).
    private Locator option(Locator scope, String text) {
        return scope.locator("label").filter(new Locator.FilterOptions().setHasText(exactText(text)));
    }

    // Whole-text match. Playwright runs the pattern as a JavaScript RegExp, which has no \\Q...\\E
    // quoting (Pattern.quote), so special characters are escaped one by one.
    private static Pattern exactText(String text) {
        return Pattern.compile("^\\s*" + text.replaceAll("[.*+?^${}()|\\[\\]\\\\]", "\\\\$0") + "\\s*$");
    }

    public void confirmGdpr() {
        if (!locators.gdprArea.get().all().isEmpty()) {
            locators.gdprConfirmButton.get().click();
        }
    }

    public void closeGuidePopup() {
        if (locators.popupGuide.get().isVisible()) {
            locators.popupCloseButton.get().click();
        }
    }

    // Project Description
    public void goToProjectDescription() throws InterruptedException {
        goToStep(locators.projectDescriptionBlock.get());
        PlaywrightAssertions.assertThat(locators.projectDescriptionTitle.get()).isVisible();
    }

    public void chooseBuild(String value) {
        option(locators.projectBuild.get(), value).click();
        PlaywrightAssertions.assertThat(option(locators.projectBuild.get(), value)).isChecked();
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

    public void setProjectDescription(String build, String groupId, String artifactId, String version) throws InterruptedException {
        goToProjectDescription();
        chooseBuild(build);
        setGroupId(groupId);
        setArtifactId(artifactId);
        setVersion(version);
    }

    // Jakarta EE
    public void goToJakartaEESection() throws InterruptedException {
        goToStep(locators.jakartaeeBlock.get());
        PlaywrightAssertions.assertThat(locators.jakartaeeBlockTitle.get()).isVisible();
    }

    public void selectJakartaEEVersion(String option, String value) {
        locators.jarkataeeVersion.get().selectOption(option);
        PlaywrightAssertions.assertThat(locators.jarkataeeVersion.get()).hasValue(value);
    }

    public void selectJakartaEEProfile(String value) {
        option(locators.jakartaeeProfile.get(), value).click();
        PlaywrightAssertions.assertThat(option(locators.jakartaeeProfile.get(), value)).isChecked();
    }

    public void setJakartaEE(String jakartaEEVersion, String jakartaEEValue, String jakartaEEProfile) throws InterruptedException {
        goToJakartaEESection();
        selectJakartaEEVersion(jakartaEEVersion, jakartaEEValue);
        selectJakartaEEProfile(jakartaEEProfile);
    }

    // Payara Platform
    public void goToPayaraPlatformSection() throws InterruptedException {
        goToStep(locators.payaraPlatformBlock.get());
        PlaywrightAssertions.assertThat(locators.payaraPlatformBlockTitle.get()).isVisible();
    }

    public void selectPayaraPlatform(String value) {
        option(locators.payaraPlatform.get(), value).click();
        PlaywrightAssertions.assertThat(option(locators.payaraPlatform.get(), value)).isChecked();
    }

    public void selectPayaraPlatformVersion(String option, String value) {
        locators.payaraPlatformVersion.get().selectOption(option);
        PlaywrightAssertions.assertThat(locators.payaraPlatformVersion.get()).hasValue(value);
    }

    public void setPayaraPlatform(String platform, String platformVersion, String platformValue) throws InterruptedException {
        goToPayaraPlatformSection();
        selectPayaraPlatform(platform);
        selectPayaraPlatformVersion(platformVersion, platformValue);
    }

    // Project Configuration
    public void goToProjectConfigurationSection() throws InterruptedException {
        goToStep(locators.projectConfigurationBlock.get());
        PlaywrightAssertions.assertThat(locators.projectConfigurationBlockTitle.get()).isVisible();
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

    public void selectJavaVersion(String option, String value) {
        locators.projectJavaVersion.get().selectOption(option);
        PlaywrightAssertions.assertThat(locators.projectJavaVersion.get()).hasValue(value);
    }

    public void setProjectConfiguration(String packageValue, Boolean includeTests, String javaVersion, String javaVersionValue)
            throws InterruptedException {
        goToProjectConfigurationSection();
        setPackage(packageValue);
        includeTests(includeTests);
        goToStep(locators.projectJavaVersion.get());
        selectJavaVersion(javaVersion, javaVersionValue);
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
        locators.nextButtonToSecurity.get().click();
    }

    // Picked through the search field; the native select behind it holds the value.
    public void selectERDiagram(String value) {
        if (!value.isEmpty()) {
            locators.searchERDiagramInput.get().click();
            locators.searchERDiagramInput.get().fill(value);
            locators.searchERDiagramOptions.get().filter(new Locator.FilterOptions().setHasText(exactText(value))).click();
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


    public void generate(Page page, Path filepath) {
        Download download = page.waitForDownload(new Page.WaitForDownloadOptions().setTimeout(120000), () -> {
            locators.generateButton.get().click();
        });
        download.saveAs(filepath);
    }

}
