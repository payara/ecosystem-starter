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
package fish.payara.starter.test.e2e.pages;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import java.nio.file.Path;

@UsePlaywright
public class StarterPageActions {

    private StarterPageLocators locators; 

    public StarterPageActions(Page page) {
        locators = new StarterPageLocators(page);
    }

    public void confirmGdpr() {
        if (!locators.gdprArea.get().all().isEmpty()) {
            locators.gdprConfirmButton.get().click();
        }
    }

    public void closeGuidePopup() {
        if (!locators.popupGuide.get().all().isEmpty()) {
            locators.popupCloseButton.get().click();
        }
    }

    // Project Description
    public void goToProjectDescription() throws InterruptedException {
        locators.projectDescriptionTitle.get().click();
        Thread.sleep(1000);
        PlaywrightAssertions.assertThat(locators.projectDescriptionTitle.get()).isVisible();
    }

    public void chooseBuild(String value) {
        locators.projectBuild.get().getByText(value).click();
        PlaywrightAssertions.assertThat(locators.projectBuild.get().getByText(value)).isChecked();
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
        locators.jakartaeeBlockTitle.get().click();
        Thread.sleep(1000);
        PlaywrightAssertions.assertThat(locators.jakartaeeBlock.get()).isVisible();
    }

    public void selectJakartaEEVersion(String option, String value) {
        locators.jarkataeeVersion.get().selectOption(option);
        PlaywrightAssertions.assertThat(locators.jarkataeeVersion.get()).hasValue(value);
    }

    public void selectJakartaEEProfile(String value) {
        locators.jakartaeeProfile.get().getByText(value).click();
        PlaywrightAssertions.assertThat(locators.jakartaeeProfile.get().getByText(value)).isChecked();
    }

    public void setJakartaEE(String jakartaEEVersion, String jakartaEEValue, String jakartaEEProfile) throws InterruptedException {
        goToJakartaEESection();
        selectJakartaEEVersion(jakartaEEVersion, jakartaEEValue);
        selectJakartaEEProfile(jakartaEEProfile);
    }

    // Payara Platform
    public void goToPayaraPlatformSection() throws InterruptedException {
        locators.payaraPlatformBlockTitle.get().click();
        Thread.sleep(1000);
        PlaywrightAssertions.assertThat(locators.payaraPlatformBlock.get()).isVisible();
    }

    public void selectPayaraPlatform(String value) {
        locators.payaraPlatform.get().getByText(value).click();
        PlaywrightAssertions.assertThat(locators.payaraPlatform.get().getByText(value)).isChecked();
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
        locators.projectConfigurationBlockTitle.get().click();
        Thread.sleep(1000);
        PlaywrightAssertions.assertThat(locators.projectConfigurationBlock.get()).isVisible();
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
        selectJavaVersion(javaVersion, javaVersionValue);
    }

    // Micro Profile
    public void goToMicroProfileSection() throws InterruptedException {
        locators.microProfileBlockTitle.get().click();
        Thread.sleep(1000);
        PlaywrightAssertions.assertThat(locators.microProfileBlock.get()).isVisible();
    }

    public void selectMicroProfile(String value) {
        if (!value.isEmpty()) {
        locators.microProfileBlock.get().getByText(value).click();
            PlaywrightAssertions.assertThat(locators.microProfileBlock.get().getByText(value)).isChecked();
        }
    }

    public void setMicroProfile(String value) throws InterruptedException {
        goToMicroProfileSection();
        selectMicroProfile(value);
    }

    // Deployment Options
    public void goToDeploymentSection() throws InterruptedException {
        locators.deploymentBlockTitle.get().click();
        Thread.sleep(1000);
        PlaywrightAssertions.assertThat(locators.deploymentBlock.get()).isVisible();
    }

    public void enableDockerDeployment() {
        if (!locators.deploymentDocker.get().isChecked()) {
            locators.deploymentBlock.get().getByText("Docker").click();
        }
        PlaywrightAssertions.assertThat(locators.deploymentDocker.get()).isChecked();
    }

    public void enableQubeDeployment() {
        if (!locators.deploymentQube.get().isChecked()) {
            locators.deploymentBlock.get().getByText("Payara Qube").click();
        }
        PlaywrightAssertions.assertThat(locators.deploymentQube.get()).isChecked();
    }

    public void setDeployment(Boolean docker, Boolean qube) throws InterruptedException {
        goToDeploymentSection();
        if (docker) {
            enableDockerDeployment();
        }
        if (qube) {
            enableQubeDeployment();
        }
    }

    // ER Diagram Designer
    public void goToDiagramSection() throws InterruptedException {
        locators.diagramBlockTitle.get().click();
        Thread.sleep(1000);
        PlaywrightAssertions.assertThat(locators.diagramBlock.get()).isVisible();
    }

    public void selectERDiagram(String value) {
        locators.searchERDiagram.get().selectOption(value);
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
        PlaywrightAssertions.assertThat(locators.diagramSourceText.get()).containsText(value);
    }

    public void checkDiagramGraphContains(String value) {
        PlaywrightAssertions.assertThat(locators.livePreviewGraph.get()).containsText(value);
    }

    public void closeERDiagramPreview() throws InterruptedException {
        locators.closePreview.get().click();
        Thread.sleep(1000);
        PlaywrightAssertions.assertThat(locators.previewDiagramBlock.get()).isHidden();
    }

    // Security Configuration
    public void goToSecuritySection() throws InterruptedException {
        locators.securityBlockTitle.get().click();
        Thread.sleep(1000);
        PlaywrightAssertions.assertThat(locators.securityBlock.get()).isVisible();
    }


    public void setLogin(String value) {
        locators.securityBlock.get().getByText(value).click();
        PlaywrightAssertions.assertThat(locators.securityBlock.get().getByText(value)).isChecked();
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
