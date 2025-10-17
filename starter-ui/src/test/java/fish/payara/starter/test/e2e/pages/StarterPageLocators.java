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
    public final Supplier<Locator> popupGuide = () -> page.locator("[data-hs-container-type='SLIDE_IN']");
    public final Supplier<Locator> popupCloseButton = () -> popupGuide.get().locator("#interactive-close-button");
    public final Supplier<Locator> popupViewGuideButton = () -> popupGuide.get().locator(".interactive-button");
    public final Supplier<Locator> footer = () -> page.locator(".form__row--footer");
    public final Supplier<Locator> generateButton = () -> footer.get().locator(".button");

    // Project Description
    public final Supplier<Locator> projectDescriptionBlock = () -> page.locator("fieldset#stb0-st0");
    public final Supplier<Locator> projectDescriptionTitle = () -> projectDescriptionBlock.get().locator(
            "div.legend a[href='#stb0-st0']");
    public final Supplier<Locator> projectBuild = () -> projectDescriptionBlock.get();
    public final Supplier<Locator> mavenRadio = () -> projectDescriptionBlock.get().locator("label[for='maven']");
    public final Supplier<Locator> gradleRadio = () -> projectDescriptionBlock.get().locator("label[for='gradle']");
    public final Supplier<Locator> groupIdInputBox = () -> projectDescriptionBlock.get().locator("#groupId");
    public final Supplier<Locator> artifactIdInputBox = () -> projectDescriptionBlock.get().locator("#artifactId");
    public final Supplier<Locator> versionInputBox = () -> projectDescriptionBlock.get().locator("#version");
    public final Supplier<Locator> nextButtonToJakartaee = () -> projectDescriptionBlock.get().locator(
            "a[href='#stb0-st1']");

    // Jakarta EE
    public final Supplier<Locator> jakartaeeBlock = () -> page.locator("fieldset#stb0-st1");
    public final Supplier<Locator> jakartaeeBlockTitle = () -> jakartaeeBlock.get().locator("div.legend a[href='#stb0-st1']");
    public final Supplier<Locator> jarkataeeVersion = () -> jakartaeeBlock.get().locator("select#jakartaEEVersion");
    public final Supplier<Locator> jakartaeeProfile = () -> jakartaeeBlock.get().locator("label.form__radbox-label");
    public final Supplier<Locator> previousButtonToPayaraPlatform = () -> jakartaeeBlock.get().locator("a[href='#stb0-st0']");
    public final Supplier<Locator> nextButtonToPayaraPlatform = () -> jakartaeeBlock.get().locator("a[href='#stb0-st2']");

    // Payara Platform
    public final Supplier<Locator> payaraPlatformBlock = () -> page.locator("fieldset#stb0-st2");
    public final Supplier<Locator> payaraPlatformBlockTitle = () -> payaraPlatformBlock.get().locator(
            "div.legend a[href='#stb0-st2']");
    public final Supplier<Locator> payaraPlatform = () -> payaraPlatformBlock.get();
    public final Supplier<Locator> payaraPlatformVersion = () -> payaraPlatformBlock.get().locator("select#payaraVersion");
    public final Supplier<Locator> previousButtonToProjectConfiguration = () -> payaraPlatformBlock.get().locator(
            "a[href='#stb0-st1']");
    public final Supplier<Locator> nextButtonToProjectConfiguration = () -> payaraPlatformBlock.get().locator(
            "a[href='#stb0-st3']");

    // Project Configuration
    public final Supplier<Locator> projectConfigurationBlock = () -> page.locator("fieldset#stb0-st3");
    public final Supplier<Locator> projectConfigurationBlockTitle = () -> projectConfigurationBlock.get().locator(
            "div.legend a[href='#stb0-st3']");
    public final Supplier<Locator> projectPackage = () -> projectConfigurationBlock.get().locator("input#package");
    public final Supplier<Locator> projectIncludeTestsLabel = () -> projectConfigurationBlock.get().locator(
            "label[for='includeTests']");
    public final Supplier<Locator> projectIncludeTests = () -> projectConfigurationBlock.get().locator("input#includeTests");
    public final Supplier<Locator> projectJavaVersion = () -> projectConfigurationBlock.get().locator("select#javaVersion");
    public final Supplier<Locator> previousButtonToMicroProfile = () -> projectConfigurationBlock.get().locator(
            "a[href='#stb0-st2']");
    public final Supplier<Locator> nextButtonToMicroProfile = () -> projectConfigurationBlock.get().locator(
            "a[href='#stb0-st4']");

    // MicroProfile
    public final Supplier<Locator> microProfileBlock = () -> page.locator("fieldset#stb0-st4");
    public final Supplier<Locator> microProfileBlockTitle = () -> microProfileBlock.get().
            locator("div.legend a[href='#stb0-st4']");
    public final Supplier<Locator> microProfileOptions = () -> microProfileBlock.get();
    public final Supplier<Locator> previousButtonToDeployment = () -> microProfileBlock.get().locator("a[href='#stb0-st3']");
    public final Supplier<Locator> nextButtonToDeployment = () -> microProfileBlock.get().locator("a[href='#stb0-st5']");

    // Deployment Options
    public final Supplier<Locator> deploymentBlock = () -> page.locator("fieldset#stb0-st5");
    public final Supplier<Locator> deploymentBlockTitle = () -> deploymentBlock.get().locator("div.legend a[href='#stb0-st5']");
    public final Supplier<Locator> deploymentDocker = () -> deploymentBlock.get().locator("input#docker");
    public final Supplier<Locator> deploymentQube = () -> deploymentBlock.get().locator("input#qube");
    public final Supplier<Locator> previousButtonToDiagram = () -> deploymentBlock.get().locator("a[href='#stb0-st4']");
    public final Supplier<Locator> nextButtonToDiagram = () -> deploymentBlock.get().locator("a[href='#stb0-st6']");

    // ER Diagram Designer
    public final Supplier<Locator> diagramBlock = () -> page.locator("fieldset#stb0-st6");
    public final Supplier<Locator> diagramBlockTitle = () -> diagramBlock.get().locator("div.legend a[href='#stb0-st6']");
    public final Supplier<Locator> searchERDiagram = () -> diagramBlock.get().locator("select#mermaidErDiagramList");
    public final Supplier<Locator> diagramPreviewButton = () -> diagramBlock.get().locator("button.button");
    public final Supplier<Locator> generateJPA = () -> diagramBlock.get().locator("label[for='generateJpa']");
    public final Supplier<Locator> jpaSubPackage = () -> diagramBlock.get().locator("input#jpaSubpackage");
    public final Supplier<Locator> generateRepository = () -> diagramBlock.get().locator("input#generateRepository");
    public final Supplier<Locator> repositorySubpackage = () -> diagramBlock.get().locator("input#repositorySubpackage");
    public final Supplier<Locator> generateRest = () -> diagramBlock.get().locator("input#generateRest");
    public final Supplier<Locator> restSubpackage = () -> diagramBlock.get().locator("input#restSubpackage");
    public final Supplier<Locator> generateWeb = () -> diagramBlock.get().locator("select#generateWeb");
    public final Supplier<Locator> previousButtonToSecurity = () -> diagramBlock.get().locator("a[href='#stb0-st5']");
    public final Supplier<Locator> nextButtonToSecurity = () -> diagramBlock.get().locator("a[href='#stb0-st7']");

    // Security Configuration
    public final Supplier<Locator> securityBlock = () -> page.locator("fieldset#stb0-st7");
    public final Supplier<Locator> securityBlockTitle = () -> securityBlock.get().locator("div.legend a[href='#stb0-st7']");

    // Diagram Builder & Live Preview
    public final Supplier<Locator> previewDiagramBlock = () -> page.locator("div#previewModal");
    public final Supplier<Locator> diagramSource = () -> previewDiagramBlock.get().locator("div#diagramSource");
    public final Supplier<Locator> diagramSourceSelect = () -> diagramSource.get().locator("input#mermaidErDiagramListPreview");
    public final Supplier<Locator> diagramSourceText = () -> diagramSource.get().locator("div.CodeMirror");
    public final Supplier<Locator> diagramSourcePrompt = () -> diagramSource.get().locator("input#queryBox");
    public final Supplier<Locator> livePreview = () -> previewDiagramBlock.get().locator("div#diagramView");
    public final Supplier<Locator> livePreviewGraph = () -> livePreview.get().locator("svg#graphDiv");
    public final Supplier<Locator> previousERDiagram = () -> livePreview.get().locator("button#prevERDiagram");
    public final Supplier<Locator> nextERDiagram = () -> livePreview.get().locator("button#nextERDiagram");
    public final Supplier<Locator> enlargeERDiagram = () -> livePreview.get().locator("button#enlargeERDiagram");
    public final Supplier<Locator> shrinkERDiagram = () -> livePreview.get().locator("button#shrinkERDiagram");
    public final Supplier<Locator> fullscreenDiagram = () -> livePreview.get().locator("button#fullscreenDiagram");
    public final Supplier<Locator> closePreview = () -> previewDiagramBlock.get().locator("button.close");
}
