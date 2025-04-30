package fish.payara.starter.test.e2e.specs;

import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.junit.UsePlaywright;
import fish.payara.starter.test.e2e.pages.*;
import java.nio.file.Paths;
import org.junit.jupiter.api.*;

@UsePlaywright
public class GenerationAppIT {
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;
    private static StarterPageActions starterPage;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void openPage() {
        context = browser.newContext();
        page = context.newPage();
        page.navigate("http://localhost:8080/payara-starter");
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
    void shouldGenerateSimpleApp() throws InterruptedException {
        assertThat(page).hasTitle("Generate Payara Application");
        starterPage.setProjectDescription("Gradle", "fish.payara.playwright.test", "PlaywrightTest", "1.0");
        starterPage.setJakartaEE("Jakarta EE 9.1", "9.1", "Web Profile");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Micro", "6.2025.1", "6.2025.1");
        starterPage.setProjectConfiguration("fish.payara.e2e", true, "Java SE 17", "17");
        starterPage.setMicroProfile("Full MP");
        starterPage.setDeployment(true, false);
        starterPage.setERDiagram("", true, "domain.test", false, "service.test", false, "resource", true);
        starterPage.setSecurity("Form Authentication - File Realm");
        starterPage.generate(page, Paths.get(".", "PlaywrightTest.zip"));
    }

    @Test
    void shouldModifyAppWithERDiagram() throws InterruptedException {
        starterPage.setProjectDescription("Maven", "fish.payara.playwright.test", "InventorySystemTest", "1.0-SNAPSHOT");
        starterPage.setJakartaEE("Jakarta EE 10", "10", "Web Profile");
        starterPage.closeGuidePopup();
        starterPage.setPayaraPlatform("Payara Server", "6.2025.4", "6.2025.4");
        starterPage.setProjectConfiguration("fish.payara.e2e", false, "Java SE 21", "21");
        starterPage.setMicroProfile("MicroProfile Metrics");
        starterPage.setDeployment(false, false);
        starterPage.setERDiagram("Inventory System", true, "domain.test", false, "service.test", false, "resource", true);
        starterPage.openERDiagramPreview();
        starterPage.checkDiagramCodeContains("PRODUCT ||--o{ INVENTORY : contains");
        starterPage.checkDiagramGraphContains("INVENTORY");
        starterPage.closeERDiagramPreview();
        starterPage.setSecurity("None");
        starterPage.generate(page, Paths.get(".", "InventorySystemTest.zip"));
    }
}
