package fish.payara.starter.test.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;

@UsePlaywright
public class StarterPageActions {

    private final Page page;

    private final StarterPageLocators locators; 

    public StarterPageActions(Page page) {
        this.page = page;
        this.locators = new StarterPageLocators(this.page);
    }

    public void chooseBuild(String value) throws InterruptedException {

        switch (value) {
            case "Maven":
                getMavenCheckbox().click();
                break;
            case "Gradle":
                getGradleCheckbox().click();
                break;
            default:
                throw new IllegalArgumentException("Invalid value: " + value);
        }
    }
    
    
    public void fillGroupId(String value) throws InterruptedException {
        getGroupId().clear();
        getGroupId().fill(value);
    }

    public void fillArtifactId (String value) throws InterruptedException {
        getArtifactId().clear();
        getArtifactId().fill(value);
    }

    public void fillVersion (String value) throws InterruptedException {
        getVersion().clear();
        getVersion().fill(value);
    }

    public Locator getGradleCheckbox() {
        return locators.gradleRadio;
    }

    public Locator getMavenCheckbox() {
        return locators.mavenRadio;
    }

    public Locator getGroupId() {
        return locators.groupIdInputBox;
    }

    public Locator getArtifactId() {
        return locators.artifactIdInputBox;
    }

    public Locator getVersion() {
        return locators.versionInputBox;
    }

    public Locator getNextButtonToJakartaEE() {
        return locators.nextButtonToJakartaee;
    }

    public Locator getJakartaEEVersion() {
        return locators.jakartaeeVersionLabel;
    }
}
