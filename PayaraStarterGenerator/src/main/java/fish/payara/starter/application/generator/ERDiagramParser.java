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
package fish.payara.starter.application.generator;

import fish.payara.starter.application.domain.Entity;
import fish.payara.starter.application.domain.Relationship;
import fish.payara.starter.application.domain.ERModel;
import fish.payara.starter.application.domain.Attribute;
import fish.payara.starter.application.domain.KeyValue;
import static fish.payara.starter.application.util.AttributeType.LOCAL_DATE;
import static fish.payara.starter.application.util.AttributeType.LOCAL_DATE_TIME;
import static fish.payara.starter.application.util.AttributeType.getWrapperType;
import static fish.payara.starter.application.util.StringHelper.titleCase;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class ERDiagramParser {

    private final static Pattern RELATIONSHIP_PATTERN = Pattern.compile("^(\\w+)\\s*(\\|\\|--o\\{)\\s*(\\w+)\\s*:\\s*(.+?)\\s*%%\\{(.+?)\\}%%");
    private final static Pattern ENTITY_PATTERN = Pattern.compile("^(\\w+)\\s*\\{\\s*%%\\{(.+?)\\}%%");
    private final static Pattern ATTRIBUTE_PATTERN = Pattern.compile("^\\s*(\\w+)\\s+(\\w+)(?:\\s+(PK|FK))?\\s*%%\\{(.+?)\\}%%");

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
                    String metadata = matcher.group(5).trim();
                    erModel.addRelationship(new Relationship(titleCase(firstEntity), titleCase(secondEntity), relationshipType, relationshipDetail, parseKeyValuePairs(metadata)));
                }
            } else if (ENTITY_PATTERN.matcher(line).find()) {
                Matcher matcher = ENTITY_PATTERN.matcher(line);
                if (matcher.find()) {
                    String entityName = matcher.group(1);
                    String entityMetadata = matcher.group(2).trim();
                    Entity entity = new Entity(titleCase(entityName), parseKeyValuePairs(entityMetadata));
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
                            String attrMetadata = attrMatcher.group(4).trim();
                            if (!isForeignKey) {
                                type = getWrapperType(mapToJavaType(type));
                                Attribute attribute = new Attribute(name, type, isPrimaryKey, parseKeyValuePairs(attrMetadata));
                                if(type.equals("LocalDate")) {
                                    attribute.addImport(LOCAL_DATE);
                                } else if(type.equals("LocalDateTime")) {
                                    attribute.addImport(LOCAL_DATE_TIME);
                                }
                                entity.addAttribute(attribute);
                            }
                        }
                    }
                    erModel.addEntity(entity);
                }
            } else if (line.startsWith("%%{") && line.endsWith("}%%")) {
                String globalMetadata = line.substring(3, line.length() - 3).trim();
                erModel.setProperty(parseKeyValuePairs(globalMetadata));
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
    private List<KeyValue> parseKeyValuePairs(String metadata) {
        List<KeyValue> keyValues = new ArrayList<>();
        Pattern keyValuePattern = Pattern.compile("([\\w-]+)\\[(.+?)\\]");
        Matcher matcher = keyValuePattern.matcher(metadata);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            keyValues.add(new KeyValue(key, value));
        }
        return keyValues;
    }
}
