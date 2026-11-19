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

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.function.Supplier;

public class StarterPageLocators {

    private Page page;

    public StarterPageLocators(Page page) {
        this.page = page;
    }

    // Common Elements
    public final Supplier<Locator> gdprArea = () -> page.locator("#hs-eu-cookie-confirmation-inner");
    public final Supplier<Locator> gdprConfirmButton = () -> gdprArea.get().locator("#hs-eu-confirmation-button");
    public final Supplier<Locator> gdprDeclineButton = () -> gdprArea.get().locator("#hs-eu-decline-button");
    public final Supplier<Locator> introduction = () -> page.locator(".hero");
    public final Supplier<Locator> footer = () -> page.locator(".form__row--footer");
    public final Supplier<Locator> generateButton = () -> footer.get().locator("button[type='submit']");

    // The wizard shows one step at a time (Project, Platform, Features, Review); Back and Next are
    // shared by all steps. The Features step groups its options in sections.
    public final Supplier<Locator> steps = () -> page.locator("fieldset.form-steps__step");
    public final Supplier<Locator> nextButton = () -> footer.get().locator("button.form__step__button");
    public final Supplier<Locator> backButton = () -> footer.get().locator("button.form__step__button-back");
    public final Supplier<Locator> dockedStepBar = () -> page.locator(".page__header nav.wizard-progress");

    // Project Description
    public final Supplier<Locator> projectDescriptionBlock = () -> page.locator("fieldset#step-project");
    public final Supplier<Locator> projectDescriptionTitle = () -> projectDescriptionBlock.get().locator("div.form-steps__title");
    public final Supplier<Locator> projectBuild = () -> projectDescriptionBlock.get();
    public final Supplier<Locator> mavenRadio = () -> projectDescriptionBlock.get().locator("input#maven");
    public final Supplier<Locator> gradleRadio = () -> projectDescriptionBlock.get().locator("input#gradle");
    public final Supplier<Locator> groupIdInputBox = () -> projectDescriptionBlock.get().locator("#groupId");
    public final Supplier<Locator> artifactIdInputBox = () -> projectDescriptionBlock.get().locator("#artifactId");
    public final Supplier<Locator> versionInputBox = () -> projectDescriptionBlock.get().locator("#version");

    // Jakarta EE (Platform step)
    public final Supplier<Locator> jakartaeeBlock = () -> page.locator("fieldset#step-platform");
    public final Supplier<Locator> jakartaeeBlockTitle = () -> jakartaeeBlock.get().locator("div.form-steps__title");
    public final Supplier<Locator> jarkataeeVersion = () -> jakartaeeBlock.get().locator("select#jakartaEEVersion");
    public final Supplier<Locator> jakartaeeProfile = () -> jakartaeeBlock.get();

    // Payara Platform (Platform step)
    public final Supplier<Locator> payaraPlatformBlock = () -> page.locator("fieldset#step-platform");
    public final Supplier<Locator> payaraPlatformBlockTitle = () -> payaraPlatformBlock.get().locator("div.form-steps__title");
    public final Supplier<Locator> payaraPlatform = () -> payaraPlatformBlock.get();
    public final Supplier<Locator> payaraPlatformVersion = () -> payaraPlatformBlock.get().locator("select#payaraVersion");

    // Project Configuration: the package and tests are in the Project step, the Java version in Platform
    public final Supplier<Locator> projectConfigurationBlock = () -> page.locator("fieldset#step-project");
    public final Supplier<Locator> projectConfigurationBlockTitle = () -> projectConfigurationBlock.get().locator(
            "div.form-steps__title");
    public final Supplier<Locator> projectPackage = () -> projectConfigurationBlock.get().locator("input#package");
    public final Supplier<Locator> projectIncludeTestsLabel = () -> projectConfigurationBlock.get().locator(
            "label[for='includeTests']");
    public final Supplier<Locator> projectIncludeTests = () -> projectConfigurationBlock.get().locator("input#includeTests");
    public final Supplier<Locator> projectJavaVersion = () -> page.locator("fieldset#step-platform select#javaVersion");

    // MicroProfile (Features step)
    public final Supplier<Locator> microProfileBlock = () -> page.locator("fieldset#section-microprofile");
    public final Supplier<Locator> microProfileBlockTitle = () -> microProfileBlock.get().locator(".form-section__title");
    public final Supplier<Locator> microProfileOptions = () -> microProfileBlock.get();

    // Deployment Options (Features step)
    public final Supplier<Locator> deploymentBlock = () -> page.locator("fieldset#section-deployment");
    public final Supplier<Locator> deploymentBlockTitle = () -> deploymentBlock.get().locator(".form-section__title");
    public final Supplier<Locator> deploymentDocker = () -> deploymentBlock.get().locator("input#docker");
    public final Supplier<Locator> deploymentDockerLabel = () -> deploymentBlock.get().locator("label[for='docker']");

    // ER Diagram Designer (Features step)
    public final Supplier<Locator> diagramBlock = () -> page.locator("fieldset#section-er-diagram");
    public final Supplier<Locator> diagramBlockTitle = () -> diagramBlock.get().locator(".form-section__title");
    // The native select holds the value; the page shows a search field with suggestions over it.
    public final Supplier<Locator> searchERDiagram = () -> diagramBlock.get().locator("select#mermaidErDiagramList");
    public final Supplier<Locator> searchERDiagramInput = () -> diagramBlock.get().locator("input[role='combobox']");
    public final Supplier<Locator> diagramPreviewButton = () -> diagramBlock.get().locator("button.button");
    public final Supplier<Locator> generateJPA = () -> diagramBlock.get().locator("label[for='generateJpa']");
    public final Supplier<Locator> jpaSubPackage = () -> diagramBlock.get().locator("input#jpaSubpackage");
    public final Supplier<Locator> generateRepository = () -> diagramBlock.get().locator("label[for='generateRepository']");
    public final Supplier<Locator> repositorySubpackage = () -> diagramBlock.get().locator("input#repositorySubpackage");
    public final Supplier<Locator> generateRest = () -> diagramBlock.get().locator("label[for='generateRest']");
    public final Supplier<Locator> restSubpackage = () -> diagramBlock.get().locator("input#restSubpackage");
    public final Supplier<Locator> generateWeb = () -> diagramBlock.get().locator("select#generateWeb");

    // Security Configuration (Features step)
    public final Supplier<Locator> securityBlock = () -> page.locator("fieldset#section-security");
    public final Supplier<Locator> securityBlockTitle = () -> securityBlock.get().locator(".form-section__title");

    // Review step
    public final Supplier<Locator> reviewBlock = () -> page.locator("fieldset#step-review");
    public final Supplier<Locator> editProjectButton = () -> reviewBlock.get().locator("button[data-summary-edit='step-project']");

    // Result panel: takes the step's place while the project is generated, then says how it went
    public final Supplier<Locator> resultBlock = () -> page.locator("section#formResult");
    public final Supplier<Locator> resultReady = () -> resultBlock.get().locator("[data-result='generated']");
    public final Supplier<Locator> resultReadyFile = () -> resultReady.get().locator(".form-result__file");
    public final Supplier<Locator> startOverButton = () -> resultReady.get().locator("button[type='reset']");
    public final Supplier<Locator> resultFailed = () -> resultBlock.get().locator("[data-result='failed']");
    public final Supplier<Locator> tryAgainButton = () -> resultFailed.get().locator("button[data-result-action='retry']");

    // ER Diagram Builder (dialog)
    public final Supplier<Locator> previewDiagramBlock = () -> page.locator("dialog#previewModal");
    public final Supplier<Locator> diagramSourceSelect = () -> previewDiagramBlock.get().locator("input[role='combobox']");
    public final Supplier<Locator> codeTab = () -> previewDiagramBlock.get().locator("button#erTabCode");
    public final Supplier<Locator> previewTab = () -> previewDiagramBlock.get().locator("button#erTabPreview");
    public final Supplier<Locator> diagramSource = () -> previewDiagramBlock.get().locator("div#erPanelCode");
    public final Supplier<Locator> diagramSourceText = () -> diagramSource.get().locator("div.CodeMirror");
    public final Supplier<Locator> diagramSourcePrompt = () -> previewDiagramBlock.get().locator("textarea#queryBox");
    public final Supplier<Locator> livePreview = () -> previewDiagramBlock.get().locator("div#erPanelPreview");
    public final Supplier<Locator> livePreviewGraph = () -> livePreview.get().locator("div#livePreview svg");
    public final Supplier<Locator> previousERDiagram = () -> previewDiagramBlock.get().locator("button#prevERDiagram");
    public final Supplier<Locator> nextERDiagram = () -> previewDiagramBlock.get().locator("button#nextERDiagram");
    public final Supplier<Locator> enlargeERDiagram = () -> previewDiagramBlock.get().locator("button#enlargeERDiagram");
    public final Supplier<Locator> shrinkERDiagram = () -> previewDiagramBlock.get().locator("button#shrinkERDiagram");
    public final Supplier<Locator> zoomInDiagram = () -> previewDiagramBlock.get().locator("button#erZoomIn");
    public final Supplier<Locator> zoomOutDiagram = () -> previewDiagramBlock.get().locator("button#erZoomOut");
    public final Supplier<Locator> fitDiagram = () -> previewDiagramBlock.get().locator("button#erZoomFit");
    public final Supplier<Locator> closePreview = () -> previewDiagramBlock.get().locator("button.close");
}
