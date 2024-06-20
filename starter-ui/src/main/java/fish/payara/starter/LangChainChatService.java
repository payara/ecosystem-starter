package fish.payara.starter;


import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

@ApplicationScoped
public class LangChainChatService {

    @Inject
    OpenAiChatModel model;

    ERDiagramChat erDiagramChat;

    @PostConstruct
    void init() {
        erDiagramChat = AiServices.create(ERDiagramChat.class, model);
    }

    public String generateERDiagramSuggestion(String userMessage) {
        return erDiagramChat.generateERDiagram(userMessage);
    }

}
