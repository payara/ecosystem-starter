/*
 *
 * Copyright (c) 2024 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package fish.payara.starter;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import fish.payara.starter.application.util.MermaidUtil;

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
        return MermaidUtil.filterNoise(erDiagramChat.generateERDiagram(userMessage));
    }

    public String enlargeERDiagramSuggestion(String diagramName, String erDiagram) {
        return MermaidUtil.filterNoise(erDiagramChat.enlargeERDiagram(erDiagram, diagramName));
    }

    public String shrinkERDiagramSuggestion(String diagramName, String erDiagram) {
        return MermaidUtil.filterNoise(erDiagramChat.shrinkERDiagram(erDiagram, diagramName));
    }

    public String updateERDiagramSuggestion(String userRequest, String erDiagram) {
        return MermaidUtil.filterNoise(model.generate(String.format("""
                        You are an API server that modifies the ER diagram as per the user's request '%s'.
                        Ensure that the existing diagram is updated instead of generating a new version.
                        Please update the diagram strictly in Mermaid 'erDiagram' format.
                        Respond only in Mermaid erDiagram format and the response adheres to valid Mermaid syntax.\n\n
                        Existing ER Diagram:\n %s
                       """, userRequest, erDiagram)));
    }
    
    public String addFronEndDetailsToERDiagram(String erDiagram) {
        return erDiagramChat.addFronEndDetailsToERDiagram(erDiagram);
    }

}
