/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fish.payara.starter;

//import fish.payara.ai.GptService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("/er-diagram")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
public class ERDiagramResource {
//
//    @Inject
//    private GptService gptService;

    @Inject
    private LangChainChatService langChainChatService;

    @GET
    @Path("/generate")
    public String generateRecipeSuggestion(@QueryParam("ingredients") String recipeRequest) {
//        try {
//            Thread.sleep(10000);
        if (recipeRequest != null && !recipeRequest.isBlank()) {
            return langChainChatService.generateERDiagramSuggestion(recipeRequest);
        } else {
            throw new BadRequestException("Please provide a list of ingredients");
        }
//        
//        return "erDiagram\n" +
//"    HOSPITAL ||--o{ BILLING : generates\n" +
//"    HOSPITAL {\n" +
//"        string hospitalName\n" +
//"        string hospitalAddress\n" +
//"    }\n" +
//"    BILLING {\n" +
//"        string billId PK\n" +
//"        float amount\n" +
//"        date billingDate\n" +
//"    }\n" +
//"    PATIENT ||--o{ MEDICINE-RECORD : has\n" +
//"    PATIENT {\n" +
//"        string patientId PK\n" +
//"        string patientName\n" +
//"        string disease\n" +
//"    }\n" +
//"    MEDICINE-RECORD {\n" +
//"        string recordId PK\n" +
//"        string medicineName\n" +
//"        int quantity\n" +
//"        date startDate\n" +
//"        date endDate\n" +
//"    }\n" +
//"    PATIENT ||--o{ BILLING : pays"
//        } catch (InterruptedException ex) {
//            Logger.getLogger(ERDiagramResource.class.getName()).log(Level.SEVERE, null, ex);
//        }
                
//             return   """
//erDiagram
//    CUSTOMER ||--o{ ORDER : places
//    CUSTOMER {
//        string name
//        string custNumber
//        string sector
//    }
//    ORDER ||--|{ SECURITY-SERVICE : contains
//    ORDER {
//        int orderNumber
//        string deliveryAddress
//    }
//    SECURITY-SERVICE {
//        string serviceCode
//        string serviceName
//        int quantity
//        float pricePerService
//    }
//    %%{ 
//                      asds
//                       }%%
//""";
        }
        
    }
