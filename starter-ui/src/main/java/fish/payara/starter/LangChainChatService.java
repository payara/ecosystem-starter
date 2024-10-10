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

        return model.generate("""
                        You are an API server that modifies the ER diagram json.
                        1- Add application's bootstrap icon,title, longTitle, website homePageDescription,aboutUsPageDescription(5-10 lines), Top bar menu options(e.g Home, Product, Serices, contact us, about us, ) to show on final website of application to root of json.
                            1(a) -Add Top bar menu options only to root of json
                            1(b) -Add website home-page description,about-us page description(5-10 lines) only to root of json 
                        2- After following property to each Entity 
                           Each must have one primary key PK attribute
                           Add property related to bootstrap icon (e.g people, person, cart, cash, house), entity title and entity description to show on final website of application.
                        3- After following property to Attribute:
                        3(a) Add htmllabel if variable name is not descriptive itself. Add it only if attribute is not itself descriptive.
                            Valid Example:     For string custNumber add property htmllabel=Customer Number
                            Valid Example:     For date dob add property htmllabel=Date of Birth
                            HTML label should not be the same as the attribute name by spliting with space; it should provide more meaning and description.              
                            Invalid Example:    For date appointmentDate dont' add htmllabel=Appointment Date, Not required for appointmentDate as it is self descriptive 
                        3(b) Each entity must have only one label or name attribute. Add a 'display=true' property to the attribute that will serve as the label for the entity.
                            Do not add the 'display=true' property to more than one attribute.
                            Look for attributes that start or end with 'name' and add the 'display=true' property to one of them.
                            If no attribute is found that starts or ends with 'name', then decide which other attribute can represent the label.
                        3(c) Add 'required=true' property if attribute is required for entity.
                             Do not add to more than 50% of attribute in entity.
                             Do not add to Primary key PK.
                        3(d) Add 'tooltip=description of attribute' to attribute to show it on html input UI which will help end user.
                             tooltip must be added to all attributes except FK or PK primary key.    
                        4- Add variable name to relation of JPA Entities with property relationshipVarNameInFirstEntity and relationshipVarNameInSecondEntity.
                        
                        Sample JSON Structure:
                              {
                                "icon": "person",
                                "title": "My Application",
                                "longTitle": "My Jakarta EE Application",
                                "homePageDescription": "",
                                "aboutUsPageDescription": "",
                                "topBarMenuOptions": [],
                                "entities": [
                                  {
                                    "attributes": [
                                      {
                                        "multi": false,
                                        "name": "userId",
                                        "primaryKey": true,
                                        "type": "String"
                                      },
                                      {
                                        "multi": false,
                                        "name": "username",
                                        "primaryKey": false,
                                        "type": "String",
                                        "display": true,
                                        "tooltip": "The name used by the user to log in"
                                      }
                                    ],
                                    "name": "User",
                                    "icon": "people",
                                    "title": "User",
                                    "description": "Represents the users of the application."
                                  },
                                  {
                                    "attributes": [
                                      {
                                        "multi": false,
                                        "name": "commentId",
                                        "primaryKey": true,
                                        "type": "String"
                                      },
                                      {
                                        "multi": false,
                                        "name": "content",
                                        "primaryKey": false,
                                        "type": "String",
                                        "tooltip": "The content of the comment"
                                      }
                                    ],
                                    "name": "Comment",
                                    "icon": "chat",
                                    "title": "Comment",
                                    "description": "Represents comments made on streams."
                                  }
                                ],
                                "relationships": [
                                  {
                                    "firstEntity": "User",
                                    "relationshipLabel": "has",
                                    "relationshipVarNameInFirstEntity": "comments",
                                    "relationshipVarNameInSecondEntity": "user",
                                    "relationshipType": "||--o{",
                                    "secondEntity": "Comment"
                                  }
                                ]
                              }\n\n\n\n
                              
                        Return only the modified json and do not add additional text.\n\n
                        Existing ER Diagram Json which should be updated:\n
                       """ + erDiagram);
    }

}
