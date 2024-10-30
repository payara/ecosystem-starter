/**
 * Copyright 2024 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
package fish.payara.starter.application.generator;

import fish.payara.starter.application.domain.Entity;
import fish.payara.starter.application.domain.Relationship;
import fish.payara.starter.application.domain.ERModel;
import fish.payara.starter.application.domain.Attribute;
import static fish.payara.starter.application.util.AttributeType.getWrapperType;
import static fish.payara.starter.application.util.StringHelper.titleCase;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ERDiagramParser {

 private final static Pattern RELATIONSHIP_PATTERN = Pattern.compile("^(\\w+)\\s*(\\|\\|--o\\{)\\s*(\\w+)\\s*:\\s*(.+?)\\s*$");
private final static Pattern ENTITY_PATTERN = Pattern.compile("^(\\w+)\\s*\\{\\s*(?:%%\\{(.+?)\\}%%)?");
    private final static Pattern ATTRIBUTE_PATTERN = Pattern.compile("^\\s*(\\w+)\\s+(\\w+)(?:\\s+(PK|FK))?(?:\\s*%%\\{(.+?)\\}%%)?");

    public ERModel parse(String mermaidCode) {
        ERModel erModel = new ERModel();
        String[] lines = mermaidCode.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("erDiagram")) {
                // .. skip
            } else if (RELATIONSHIP_PATTERN.matcher(line).find()) {
                Matcher matcher = RELATIONSHIP_PATTERN.matcher(line);
                if (matcher.find()) {
                    String firstEntity = matcher.group(1);
                    String relationshipType = matcher.group(2);
                    String secondEntity = matcher.group(3);
                    String relationshipDetail = matcher.group(4).trim();
                    erModel.addRelationship(new Relationship(titleCase(firstEntity), titleCase(secondEntity), relationshipType, relationshipDetail));
                }
            } else if (ENTITY_PATTERN.matcher(line).find()) {
                Matcher matcher = ENTITY_PATTERN.matcher(line);
                if (matcher.find()) {
                    String entityName = matcher.group(1);
                    
                    Entity entity = erModel.getEntity(titleCase(entityName));
                    if(entity == null) {
                        entity = new Entity(titleCase(entityName));
                    }
                    while (i < lines.length - 1) {
                        line = lines[++i].trim();
                        if (line.equals("}")) {
                            break;
                        }
                        Matcher attrMatcher = ATTRIBUTE_PATTERN.matcher(line);
                        if (attrMatcher.find()) {
                            String type = attrMatcher.group(1);
                            String name = attrMatcher.group(2);
                            String keyType = attrMatcher.group(3);
                            boolean isPrimaryKey = "PK".equals(keyType);
                            boolean isForeignKey = "FK".equals(keyType);
                            if (!isForeignKey && entity.findAttributeByName(name) == null) {
                                type = getWrapperType(mapToJavaType(type));
                                Attribute attribute = new Attribute(name, type, isPrimaryKey);
                                entity.addAttribute(attribute);
                            }
                        }
                    }
                    erModel.addEntity(entity);
                }
//            } else if (line.startsWith("%%{") && line.endsWith("}%%")) {
//                String globalMetadata = line.substring(3, line.length() - 3).trim();
//                erModel.setProperty(parseKeyValuePairs(globalMetadata));
            }
        }
        return erModel;
    }
    private String mapToJavaType(String type) {
        switch (type) {
            case "string":
                return "String";
            case "int":
                return "int";
            case "float":
                return "float";
            case "date":
                return "LocalDate";
            case "datetime":
                return "LocalDateTime";
            default:
                return "String";
        }
    }
//    private List<KeyValue> parseKeyValuePairs(String metadata) {
//        List<KeyValue> keyValues = new ArrayList<>();
//        if(metadata != null) {
//        Pattern keyValuePattern = Pattern.compile("([\\w-]+)\\[(.+?)\\]");
//        Matcher matcher = keyValuePattern.matcher(metadata.trim());
//        while (matcher.find()) {
//            String key = matcher.group(1);
//            String value = matcher.group(2);
//            keyValues.add(new KeyValue(key, value));
//        }
//        }
//        return keyValues;
//    }
}
