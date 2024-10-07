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

    ERDiagramPlainChat erDiagramChat;

    @PostConstruct
    void init() {
        erDiagramChat = AiServices.create(ERDiagramPlainChat.class, model);
    }

    public String generateERDiagramSuggestion(String userMessage) {
        return erDiagramChat.generateERDiagram(userMessage);
    }

    public String enlargeERDiagramSuggestion(String diagramName, String erDiagram) {
        return erDiagramChat.enlargeERDiagram(erDiagram, diagramName);
    }

    public String shrinkERDiagramSuggestion(String diagramName, String erDiagram) {
        return erDiagramChat.shrinkERDiagram(erDiagram, diagramName);
    }

}
