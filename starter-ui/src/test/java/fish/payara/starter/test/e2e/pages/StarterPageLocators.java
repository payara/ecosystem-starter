package fish.payara.starter.test.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;

@UsePlaywright
public class StarterPageLocators {

    private Page page;
    
    public StarterPageLocators(Page page) {
        this.page = page;
    }
    
    // Common Elements
    public final Locator introduction = page.locator(".hero");
    public final Locator popupGuide = page.locator("[data-hs-container-type='SLIDE_IN']");
    public final Locator popupCloseButton = popupGuide.locator("#interactive-close-button");
    public final Locator popupViewGuideButton = popupGuide.locator(".interactive-button");
    public final Locator footer = page.locator(".form__row--footer");
    public final Locator generateButton = footer.locator(".button");
    
    // Project Description
    public final Locator projectDescriptionBlock = page.locator("fieldset[id='stb0-st0']");
    public final Locator mavenRadio = page.locator("label[for='maven']");
    public final Locator gradleRadio = page.locator("label[for='gradle']");
    public final Locator groupIdInputBox = page.locator("#groupId");
    public final Locator artifactIdInputBox = page.locator("#artifactId");
    public final Locator versionInputBox = page.locator("#version");
    public final Locator nextButtonToJakartaee = page.locator("#stb0-st0").locator("a[href='#stb0-st1']");
    public final Locator jakartaeeVersionLabel = page.locator("label[for='jakartaEEVersion']");

    // Jakarta EE
    
    // Payara Platform
    
    // Project Configuration
    
    // MicroProfile
    
    // Deployment Options
    
    // ER Diagram Designer
    
    // Security Configuration
    
}
