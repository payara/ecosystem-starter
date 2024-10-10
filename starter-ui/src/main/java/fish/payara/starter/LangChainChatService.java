package fish.payara.starter;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

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

    public String updateERDiagramSuggestion(String userRequest, String erDiagram) {
        return model.generate(String.format("""
                        You are an API server that modifies the ER diagram as per the user's request '%s'.
                        Ensure that the existing diagram is updated instead of generating a new version.
                        Please update the diagram strictly in Mermaid 'erDiagram' format.
                        Respond only in Mermaid erDiagram format and the response adheres to valid Mermaid syntax.\n\n
                        Existing ER Diagram:\n %s
                       """, userRequest, erDiagram));
    }
    
    public String addFronEndDetailsToERDiagram(String erDiagram) {
        return erDiagramChat.addFronEndDetailsToERDiagram(erDiagram);
    }

}
