package fish.payara.starter.test.e2e.specs;

import fish.payara.starter.test.e2e.pages.*;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.junit.UsePlaywright;
import java.nio.file.Paths;
import org.junit.jupiter.api.*;

@UsePlaywright
public class GenerationAppIT {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    
    private static final String groupId = "fish.payara.playwrighttest";
    private static final String artifactId = "PlaywrightTest";
    private static final String version = "1.0";

    @BeforeAll
    static void launchBrowser() {
      playwright = Playwright.create();
      browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }
    
    @AfterAll
    static void closeBrowser(){
        playwright.close();
    }
    
    @BeforeEach
    void createPage(){
        context = browser.newContext();
        page = context.newPage();
        page.setDefaultTimeout(60000);
        page.navigate("http://localhost:8080/payara-starter");
        page.waitForSelector("div.hero", new Page.WaitForSelectorOptions().setTimeout(120000));
    }
    
    @AfterEach
    void closePage(){
        context.close();
    }
    
    @Test
    void shouldGenerateSimpleApp() throws InterruptedException {
        assertThat(page).hasTitle("Generate Payara Application");
        StarterPageActions starterPage = new StarterPageActions(page);
        starterPage.chooseBuild("Gradle");  
        PlaywrightAssertions.assertThat(starterPage.getGradleCheckbox()).isChecked();
        PlaywrightAssertions.assertThat(starterPage.getMavenCheckbox()).not().isChecked();

        starterPage.fillGroupId(groupId);
        starterPage.fillArtifactId(artifactId);
        starterPage.fillVersion(version);

        PlaywrightAssertions.assertThat(starterPage.getGroupId()).hasValue(groupId);
        PlaywrightAssertions.assertThat(starterPage.getArtifactId()).hasValue(artifactId);
        PlaywrightAssertions.assertThat(starterPage.getVersion()).hasValue(version);

        Thread.sleep(1000);

        starterPage.getNextButtonToJakartaEE().click();

        PlaywrightAssertions.assertThat(starterPage.getJakartaEEVersion()).isVisible();
        Thread.sleep(6000);
    }
}
