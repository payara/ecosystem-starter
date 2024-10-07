/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fish.payara.starter;

//import fish.payara.ai.GptService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;

@Path("/er-diagram")
public class ERDiagramResource {

    @Inject
    private LangChainChatService langChainChatService;

    @GET
    @Path("/generate")
    public String generateERDiagram(@QueryParam("request") String request) {
        if (request != null && !request.isBlank()) {
            return langChainChatService.generateERDiagramSuggestion(request);
        } else {
            throw new BadRequestException("Please provide the request");
        }
    }

       @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(value = "/enlarge")
    public String enlargeERDiagramSize(@QueryParam(value = "name") String diagramName, String erDiagram) {
        try {
        if (erDiagram != null && !erDiagram.isBlank()
                && diagramName != null && !diagramName.isBlank()) {
            return langChainChatService.enlargeERDiagramSuggestion(diagramName, erDiagram);
        } else {
            throw new BadRequestException("Please provide the diagram");
        }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

       @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/shrink")
    public String shrinkERDiagramSize(@QueryParam("name") String diagramName, String erDiagram) {
        if (erDiagram != null && !erDiagram.isBlank()
                && diagramName != null && !diagramName.isBlank()) {
            return langChainChatService.shrinkERDiagramSuggestion(diagramName, erDiagram);
        } else {
            throw new BadRequestException("Please provide the diagram");
        }
    }

}
