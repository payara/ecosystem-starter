package fish.payara.starter;

import java.time.Duration;

import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import com.theokanning.openai.service.OpenAiService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import static java.time.Duration.ofSeconds;

@ApplicationScoped
public class OpenAIFactory {

	@Inject
	@ConfigProperty(name = "OPEN_API_KEY")
	String apiKey;
	@Inject
	@ConfigProperty(name = "gpt.model")
	String gptModel;


	@Inject
	@ConfigProperty(name = "model.temperature")
	Double temperature;
	@Inject
	@ConfigProperty(name = "openai.timeout")
	int apiTimeout;

	@Produces
	@Singleton
	public OpenAiService produceService() {
		return new OpenAiService(apiKey,
				Duration.ofSeconds(apiTimeout));
	}

	@Produces
	@Singleton
	public OpenAiChatModel produceModel() {
		return OpenAiChatModel.builder()
				.apiKey(apiKey)
				// .responseFormat("json_object")
				.modelName(gptModel)
				.temperature(temperature)
				.timeout(ofSeconds(60))
				.logRequests(true)
				.logResponses(true)
				.build();
	}

}
