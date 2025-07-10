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
import static fish.payara.starter.application.util.AttributeType.isBoolean;
import static fish.payara.starter.application.util.AttributeType.isPrimitive;
import static fish.payara.starter.application.util.JPAUtil.ALL_RESERVED_KEYWORDS;
import static fish.payara.starter.application.util.JavaUtil.getIntrospectionPrefix;
import static fish.payara.starter.application.util.JavaUtil.getMethodName;
import static fish.payara.starter.application.util.StringUtils.firstLower;
import static fish.payara.starter.application.util.StringUtils.firstUpper;
import static fish.payara.starter.application.util.StringUtils.kebabCase;
import static fish.payara.starter.application.util.StringUtils.pluralize;
import static fish.payara.starter.application.util.StringUtils.singularize;
import static fish.payara.starter.application.util.StringUtils.startCase;
import static fish.payara.starter.application.util.StringUtils.titleCase;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

public class CRUDAppGenerator {

    private final String _package;
    private final String domainLayer;
    private final String repositoryLayer;
    private final String controllerLayer;
    private final ERModel model;
    private static final boolean IS_LOCAL = false; // Change this flag for local vs. production
    private static final String CONVERTER = "converter";

    public CRUDAppGenerator(ERModel model, String _package, String domainLayer, String repositoryLayer, String controllerLayer) {
        this._package = _package;
        this.model = model;
        if (domainLayer == null || domainLayer.trim().isEmpty()) {
            domainLayer = "domain";
        }
        if (repositoryLayer == null || repositoryLayer.trim().isEmpty()) {
            repositoryLayer = "service";
        }
        if (controllerLayer == null || controllerLayer.trim().isEmpty()) {
            controllerLayer = "resource";
        }
        this.domainLayer = domainLayer;
        this.repositoryLayer = repositoryLayer;
        this.controllerLayer = controllerLayer;
    }

    public static void main(String[] args) {
        String mermaidCode = """
                             erDiagram
                                 DEPARTMENT ||--o{ IT_EMPLOYEE : belongs_to
                                 IT_EMPLOYEE {
                                     int employeeID PK
                                     string name
                                     string position
                                     datetime hireDate
                                 }
                                 DEPARTMENT {
                                     int departmentID PK
                                     string name
                                     string location				
                                 }
                                 MANAGER ||--|| DEPARTMENT : managesSys
                                 MANAGER {
                                     int managerID PK
                                     string name
                                 }
                             
                             """;

        ERDiagramParser parser = new ERDiagramParser();
        ERModel erModel = parser.parse(mermaidCode);

        String _package = "fish.payara.example";
        String domainLayer = "domain";
        String repositoryLayer = "service";
        String controllerLayer = "resource";
        CRUDAppGenerator generator = new CRUDAppGenerator(erModel, _package, domainLayer, repositoryLayer, controllerLayer);
        try {
            generator.generate(new File("D:\\HelloWorld"), true, true, true, "jsf"); // Output directory
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generate(File projectDir, boolean generateJPA, boolean generateRepository, boolean generateController, String generateWeb) throws IOException {

        File java = getDir(projectDir, "src/main/java");
        File resources = getDir(projectDir, "src/main/resources");
        File metainf = getDir(resources, "META-INF");
        File webapp = getDir(projectDir, "src/main/webapp");
        File webinf = getDir(webapp, "WEB-INF");
        if (generateJPA) {
            for (Entity entity : model.getEntities()) {
                generateJPAClass(_package, model, entity, java);
            }
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("model", model);
            dataModel.put("appPU", model.getTitle("app").replace(" ", "") + "PU");
            generate("template/descriptor", "persistence.xml.ftl", "persistence.xml", dataModel, metainf);

            if (generateRepository) {
                for (Entity entity : model.getEntities()) {
                    generateEntityRepository(_package, entity, java);
                }
                generate("template/descriptor", "beans.xml.ftl", "beans.xml", dataModel, webinf);
                generateRepositoryBase(dataModel, _package, java);
                if (generateController) {
                    if ("jsf".equals(generateWeb.toLowerCase())) {
                        for (Entity entity : model.getEntities()) {
                            generateEntityBean(_package, entity, java);
                            generateEntityConverter(_package, entity, java);
                        }
                        for (Entity entity : model.getEntities()) {
                            generateJSFFrontend(model, entity, webapp);
                        }
                        generateBackendUtils(_package, java);
                        generateJSFFrontendBase(model, webapp);
                    } else if ("html".equals(generateWeb.toLowerCase())) {
                        for (Entity entity : model.getEntities()) {
                            generateEntityController(_package, entity, java);
                        }
                        generateRestBase(dataModel, _package, java);
                        for (Entity entity : model.getEntities()) {
                            generateHTMLFrontend(model, entity, webapp);
                        }
                        generateHTMLFrontendBase(model, webapp);
                    }
                }
            }
        }
    }

    private void generateJSFFrontendBase(ERModel model, File webapp) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("model", model);
        generate("template/jsf", "home.xhtml.ftl", "home.xhtml", dataModel, webapp);
        generate("template/jsf", "about-us.xhtml.ftl", "about-us.xhtml", dataModel, webapp);

        File layoutDir = new File(webapp, "WEB-INF/layout");
        if (!layoutDir.exists()) {
            layoutDir.mkdirs();
        }
        generate("template/jsf", "template.xhtml.ftl", "template.xhtml", dataModel, layoutDir);
        generate("template/jsf", "header.xhtml.ftl", "header.xhtml", dataModel, layoutDir);
        generate("template/jsf", "footer.xhtml.ftl", "footer.xhtml", dataModel, layoutDir);
    }

    private void generateHTMLFrontendBase(ERModel model, File outputDir) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("model", model);
        generate("template/html", "app.html.ftl", "main.html", dataModel, outputDir);
        generate("template/html", "home.html.ftl", "home.html", dataModel, outputDir);
        generate("template/html", "about-us.html.ftl", "about-us.html", dataModel, outputDir);
    }

    private void generateHTMLFrontend(ERModel model, Entity entity, File outputDir) {
        Map<String, Object> dataModel = createEntityDataModel(model, entity, _package, domainLayer, repositoryLayer);
        generate("template/html", "entity.html.ftl", dataModel.get("entityNameLowerCase") + ".html", dataModel, outputDir);
    }

    private void generateJSFFrontend(ERModel model, Entity entity, File outputDir) {
        Map<String, Object> dataModel = createEntityDataModel(model, entity, _package, domainLayer, repositoryLayer);
        generate("template/jsf", "entity.xhtml.ftl", dataModel.get("entityNameLowerCase") + ".xhtml", dataModel, outputDir);
    }

    private void generateBackendUtils(String _package, File outputDir) {
        try {
            Configuration cfg = createFreemarkerConfiguration("template/jsf");

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("package", _package + "." + CONVERTER);
            processTemplateToFile(cfg, "LocalDateConverter.java.ftl", dataModel, outputDir, "LocalDateConverter.java");
            processTemplateToFile(cfg, "LocalDateTimeConverter.java.ftl", dataModel, outputDir, "LocalDateTimeConverter.java");

            dataModel.put("package", _package + "." + controllerLayer);
            String navigationBean = "NavigationBean.java.ftl";
            processTemplateToFile(cfg, navigationBean, dataModel, outputDir, navigationBean.replace(".ftl", ""));
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private void generateEntityBean(String _package, Entity entity, File outputDir) {
        try {
            Configuration cfg = createFreemarkerConfiguration("template/jsf");
            String beanPackage = _package + "." + controllerLayer;
            Map<String, Object> dataModel = createEntityDataModel(model, entity, _package, domainLayer, repositoryLayer);
            dataModel.put("package", beanPackage);
            processTemplateToFile(cfg, "EntityBean.java.ftl", dataModel, outputDir, dataModel.get("beanClass") + ".java");
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private void generateEntityConverter(String _package, Entity entity, File outputDir) {
        try {
            Configuration cfg = createFreemarkerConfiguration("template/jsf");
            String converterPackage = _package + "." + CONVERTER;
            Map<String, Object> dataModel = createEntityDataModel(model, entity, _package, domainLayer, repositoryLayer);
            dataModel.put("package", converterPackage);
            dataModel.put("entityConverterClass", entity.getClassName() + "Converter");
            dataModel.put("entityConverterName", firstLower(entity.getClassName()) + "Converter");
            processTemplateToFile(cfg, "EntityConverter.java.ftl", dataModel, outputDir, dataModel.get("entityConverterClass") + ".java");
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private void generateEntityController(String _package, Entity entity, File outputDir) {
        try {
            Configuration cfg = createFreemarkerConfiguration("template/rest");

            String controllerPackage = _package + "." + controllerLayer;
            Map<String, Object> dataModel = createEntityDataModel(model, entity, _package, domainLayer, repositoryLayer);
            dataModel.put("model", model);
            dataModel.put("package", controllerPackage);

            String controllerFileName = entity.getClassName() + firstUpper(controllerLayer);
            dataModel.put("controllerClass", controllerFileName);
            dataModel.put("controllerClassHumanized", startCase(controllerFileName));
            dataModel.put("pagination", "no");
            dataModel.put("fieldsContainNoOwnerOneToOne", false);
            dataModel.put("metrics", false);
            dataModel.put("openAPI", false);
            dataModel.put("applicationPath", "resources");

            processTemplateToFile(cfg, "EntityController.java.ftl", dataModel, outputDir, dataModel.get("controllerClass") + ".java");
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private void generateEntityRepository(String _package, Entity entity, File outputDir) {
        try {
            Configuration cfg = createFreemarkerConfiguration("template/repository");

            String repositoryPackage = _package + "." + repositoryLayer;
            Map<String, Object> dataModel = createEntityDataModel(model, entity, _package, domainLayer, repositoryLayer);
            dataModel.put("model", model);
            dataModel.put("package", repositoryPackage);
            dataModel.put("cdi", true);
            dataModel.put("named", false);
            dataModel.put("AbstractRepository", "Abstract" + firstUpper(repositoryLayer));
            dataModel.put("AbstractRepository_FQN", _package + "." + repositoryLayer + "." + "Abstract" + firstUpper(repositoryLayer));

            processTemplateToFile(cfg, "EntityRepository.java.ftl", dataModel, outputDir, dataModel.get("EntityRepository") + ".java");
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private void generateRestBase(Map<String, Object> dataModel, String _package, File outputDir) {

        dataModel.put("package", _package + "." + controllerLayer);
        generate("template/rest", "RestConfiguration.java.ftl", "RestConfiguration.java", dataModel, outputDir);

        dataModel.put("frontendAppName", "appName");
        generate("template/rest", "HeaderUtil.java.ftl", "HeaderUtil.java", dataModel, outputDir);
    }

    private void generateRepositoryBase(Map<String, Object> dataModel, String _package, File outputDir) {

        dataModel.put("package", _package + "." + repositoryLayer + ".producer");
        generate("template/service/producer", "EntityManagerProducer.java.ftl", "EntityManagerProducer.java", dataModel, outputDir);

        dataModel.put("package", _package + "." + repositoryLayer);
        dataModel.put("cdi", true);
        dataModel.put("AbstractRepository", "Abstract" + firstUpper(repositoryLayer));
        generate("template/repository", "AbstractRepository.java.ftl", "Abstract" + firstUpper(repositoryLayer) + ".java", dataModel, outputDir);
    }

    private void generate(String templatePath, String templateName, String outputFileName, Map<String, Object> dataModel, File outputDir) {
        // Configure FreeMarker
        try {
            Configuration cfg = createFreemarkerConfiguration(templatePath);

            // Load the template
            Template template = cfg.getTemplate(templateName);

            // Output file path
            File outputPackageDir = outputDir;
            if (dataModel.get("package") != null) {
                outputPackageDir = new File(outputDir, ((String) dataModel.get("package")).replace(".", File.separator));
                if (!outputPackageDir.exists()) {
                    outputPackageDir.mkdirs();
                }
            }

            File outputFile = new File(outputPackageDir, outputFileName);

            // Write the generated file
            try (FileWriter writer = new FileWriter(outputFile)) {
                template.process(dataModel, writer);
            }

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private String escapeReservedKeyword(String name) {
        return ALL_RESERVED_KEYWORDS.contains(name.toUpperCase()) ? "\\\"" + name + "\\\"" : name;
    }

    private void generateJPAClass(String _package, ERModel model, Entity entity, File outputDir) throws IOException {
        Set<Relationship> relationships = model.getRelationships();
        String className = entity.getClassName();
        StringBuilder sbHeader = new StringBuilder();
        StringBuilder sbfunc = new StringBuilder();
        String entityPackage = _package + "." + domainLayer;
        sbHeader.append("package ").append(entityPackage).append(";\n\n");
        sbHeader.append("import ").append(model.getImportPrefix()).append(".persistence.*;\n");
        sbHeader.append("import java.util.Objects;\n\n");

        StringBuilder sbBody = new StringBuilder();
        // Generate named queries
        sbBody.append(generateNamedQueries(entity));
        String entityName = entity.getClassName();
        String escapedEntityName = escapeReservedKeyword(entityName);
        if (entityName.length() < escapedEntityName.length()) {
            sbBody.append("@Table(name = \"").append(escapedEntityName).append("\")\n");
        }
        sbBody.append("@Entity\n");
        sbBody.append("public class ").append(className).append(" {\n\n");

        Set<String> _imports = new HashSet<>();
        for (Attribute attribute : entity.getAttributes()) {
            if (attribute.isPrimaryKey()) {
                sbBody.append("    @Id\n");
                sbBody.append("    @GeneratedValue(strategy = GenerationType.AUTO)\n");
            }
            String attributeName = attribute.getName();
            String escapedAttributeName = escapeReservedKeyword(attributeName);
            if (attributeName.length() < escapedAttributeName.length()) {
                sbBody.append("    @Column(name = \"").append(escapedAttributeName).append("\")\n");
            }
            sbBody.append("    private ").append(attribute.getType()).append(" ").append(attribute.getName()).append(";\n\n");
            _imports.addAll(attribute.getImports());
        }

        sbfunc.append("\n    // Getters and setters\n\n");
        for (Attribute attribute : entity.getAttributes()) {
            String type = attribute.getType();
            String name = attribute.getName();
            String capitalized = name.substring(0, 1).toUpperCase() + name.substring(1);
            sbfunc.append("    public ").append(type).append(" get").append(capitalized).append("() {\n");
            sbfunc.append("        return ").append(name).append(";\n");
            sbfunc.append("    }\n\n");
            sbfunc.append("    public void set").append(capitalized).append("(").append(type).append(" ").append(name).append(") {\n");
            sbfunc.append("        this.").append(name).append(" = ").append(name).append(";\n");
            sbfunc.append("    }\n\n");
        }

        for (Relationship relationship : relationships) {
            if (relationship.getFirstEntityClass().equals(className)) {
                appendRelationship(sbBody, sbfunc, _imports, model, entity, relationship, true);
            } else if (relationship.getSecondEntityClass().equals(className)) {
                appendRelationship(sbBody, sbfunc, _imports, model, entity, relationship, false);
            }
        }

        Attribute primaryKey = null;
        for (Attribute attr : entity.getAttributes()) {
            if (attr.isPrimaryKey()) {
                primaryKey = attr;
                break;
            }
        }
        if (primaryKey != null) {
            String pkName = primaryKey.getName();

            sbfunc.append("    @Override\n");
            sbfunc.append("    public int hashCode() {\n");
            sbfunc.append("        int hash = 3;\n");
            sbfunc.append("        hash = 97 * hash + Objects.hashCode(this.").append(pkName).append(");\n");
            sbfunc.append("        return hash;\n");
            sbfunc.append("    }\n\n");

            sbfunc.append("    @Override\n");
            sbfunc.append("    public boolean equals(Object obj) {\n");
            sbfunc.append("        if (this == obj) {\n");
            sbfunc.append("            return true;\n");
            sbfunc.append("        }\n");
            sbfunc.append("        if (obj == null) {\n");
            sbfunc.append("            return false;\n");
            sbfunc.append("        }\n");
            sbfunc.append("        if (getClass() != obj.getClass()) {\n");
            sbfunc.append("            return false;\n");
            sbfunc.append("        }\n");
            sbfunc.append("        final ").append(className).append(" other = (").append(className).append(") obj;\n");
            sbfunc.append("        return Objects.equals(this.").append(pkName).append(", other.").append(pkName).append(");\n");
            sbfunc.append("    }\n\n");
        }

        Attribute displayNameAttr = null;
        for (Attribute attr : entity.getAttributes()) {
            if (attr.isDisplay()) {
                displayNameAttr = attr;
                break;
            }
        }
        sbfunc.append("    @Override\n");
        sbfunc.append("    public String toString() {\n");
        if (displayNameAttr != null) {
            sbfunc.append("        return String.valueOf(").append(displayNameAttr.getName()).append(");\n");
        } else {
            sbfunc.append("        return String.valueOf(").append(primaryKey.getName()).append(");\n");
        }
        sbfunc.append("    }\n\n");

        for (String _import : _imports) {
            sbHeader.append("import ").append(_import).append(";\n");
        }
        sbHeader.append("\n");
        sbHeader.append(sbBody);
        sbHeader.append(sbfunc);
        sbHeader.append("}\n");

        File outputPackageDir = new File(outputDir, entityPackage.replace(".", File.separator));
        if (!outputPackageDir.exists()) {
            outputPackageDir.mkdirs();
        }
        File outputFile = new File(outputPackageDir, className + ".java");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(sbHeader.toString());
        }
    }

    private String generateNamedQueries(Entity entity) {
        StringBuilder sb = new StringBuilder();
        sb.append("@NamedQueries({\n");

        for (Attribute attribute : entity.getAttributes()) {
            String capitalized = attribute.getName().substring(0, 1).toUpperCase() + attribute.getName().substring(1);
            String queryName = entity.getClassName() + ".findBy" + capitalized;
            String queryString = "SELECT e FROM " + entity.getClassName() + " e WHERE e." + attribute.getName() + " = :" + attribute.getName();
            sb.append("    @NamedQuery(name = \"").append(queryName).append("\", query = \"").append(queryString).append("\"),\n");
        }

        // Remove the last comma
        sb.setLength(sb.length() - 2);

        sb.append("\n})\n");
        return sb.toString();
    }

    private void appendRelationship(StringBuilder sb, StringBuilder sbfunc, Set<String> _imports, ERModel model, Entity entity, Relationship relationship, boolean isFirstEntity) {
        String relationshipType = relationship.getRelationshipType();
        String firstEntity = relationship.getFirstEntityClass();
        String secondEntity = relationship.getSecondEntityClass();

        String firstEntityVar = relationship.getRelationshipVarNameInFirstEntity() != null ? relationship.getRelationshipVarNameInFirstEntity() : secondEntity.toLowerCase();
        String firstEntityVars = pluralize(firstEntityVar);
        firstEntityVar = singularize(firstEntityVar);

        String secondEntityVar = relationship.getRelationshipVarNameInSecondEntity() != null ? relationship.getRelationshipVarNameInSecondEntity() : firstEntity.toLowerCase();
        String secondEntityVars = pluralize(secondEntityVar);
        secondEntityVar = singularize(secondEntityVar);

        if (isFirstEntity) {
            switch (relationshipType) {
                case "||--||": // OneToOne bidirectional
                case "||--o|":
                case "|o--||": {
                    Attribute attribute = new Attribute(firstEntityVar, false, secondEntity);
                    entity.getAttributes().add(attribute);
                    appendAttribute(sbfunc, sb, attribute, "    @OneToOne(mappedBy = \"" + secondEntityVar + "\")", _imports, false);
                    break;
                }
                case "||--|{": // OneToMany
                case "||--o{": {
                    Attribute attribute = new Attribute(firstEntityVars, true, secondEntity);
                    entity.getAttributes().add(attribute);
                    _imports.add(model.getImportPrefix() + ".json.bind.annotation.JsonbTransient");
                    _imports.add("java.util.List");
                    appendAttribute(sbfunc, sb, attribute, "    @JsonbTransient\n    @OneToMany(mappedBy = \"" + secondEntityVar + "\")", _imports, true);
                    break;
                }
                case "}|--||": // ManyToOne
                case "}o--||": {
                    Attribute attribute = new Attribute(firstEntityVar, false, secondEntity);
                    entity.getAttributes().add(attribute);
                    appendAttribute(sbfunc, sb, attribute, "    @ManyToOne", _imports, false);
                    break;
                }
                case "}o--o{": // ManyToMany
                case "}|--o{":
                case "}o--|{":
                case "}|--|{": {
                    Attribute attribute = new Attribute(firstEntityVars, true, secondEntity);
                    entity.getAttributes().add(attribute);
                    _imports.add(model.getImportPrefix() + ".json.bind.annotation.JsonbTransient");
                    _imports.add("java.util.List");
                    appendAttribute(sbfunc, sb, attribute, "    @JsonbTransient\n    @ManyToMany(mappedBy = \"" + secondEntityVars + "\")", _imports, true);
                    break;
                }
            }
        } else {
            switch (relationshipType) {
                case "||--||":
                case "||--o|":
                case "|o--||": {
                    Attribute attribute = new Attribute(secondEntityVar, false, firstEntity);
                    entity.getAttributes().add(attribute);
                    _imports.add(model.getImportPrefix() + ".json.bind.annotation.JsonbTransient");
                    String joinColumn = "    @JoinColumn(name = \"" + attribute.getName() + "_id\")";
                    appendAttribute(sbfunc, sb, attribute, "    @JsonbTransient\n    @OneToOne\n" + joinColumn, _imports, false);
                    break;
                }
                case "||--|{":
                case "||--o{": {
                    Attribute attribute = new Attribute(secondEntityVar, false, firstEntity);
                    entity.getAttributes().add(attribute);
                    String joinColumn = "    @JoinColumn(name = \"" + attribute.getName() + "_id\")";
                    appendAttribute(sbfunc, sb, attribute, "    @ManyToOne\n" + joinColumn, _imports, false);
                    break;
                }
                case "}o--o{":
                case "}|--o{":
                case "}o--|{":
                case "}|--|{": {
                    Attribute attribute = new Attribute(secondEntityVars, true, firstEntity);
                    entity.getAttributes().add(attribute);
                    _imports.add(model.getImportPrefix() + ".json.bind.annotation.JsonbTransient");
                    _imports.add("java.util.List");
                    String joinColumn = "    @JoinColumn(name = \"" + secondEntityVar + "_id\")";
                    appendAttribute(sbfunc, sb, attribute, "    @JsonbTransient\n    @ManyToMany\n" + joinColumn, _imports, true);
                    break;
                }
            }
        }
    }

    private void appendAttribute(StringBuilder sbfunc, StringBuilder sb, Attribute attribute, String annotations, Set<String> _imports, boolean isCollection) {
        // Append annotations and declaration
        sb.append(annotations).append("\n");
        sb.append("    private ");
        if (isCollection) {
            sb.append("List<").append(attribute.getType()).append(">");
        } else {
            sb.append(attribute.getType());
        }
        sb.append(" ").append(attribute.getName()).append(";\n\n");

        // Append getter
        sbfunc.append("    public ");
        if (isCollection) {
            sbfunc.append("List<").append(attribute.getType()).append(">");
        } else {
            sbfunc.append(attribute.getType());
        }
        sbfunc.append(" get").append(attribute.getTitleCaseName()).append("() {\n");
        sbfunc.append("        return ").append(attribute.getName()).append(";\n");
        sbfunc.append("    }\n\n");

        // Append setter
        sbfunc.append("    public void set").append(attribute.getTitleCaseName()).append("(");
        if (isCollection) {
            sbfunc.append("List<").append(attribute.getType()).append(">");
        } else {
            sbfunc.append(attribute.getType());
        }
        sbfunc.append(" ").append(attribute.getName()).append(") {\n");
        sbfunc.append("        this.").append(attribute.getName()).append(" = ").append(attribute.getName()).append(";\n");
        sbfunc.append("    }\n\n");
    }

    private File getDir(File parent, String path) {
        File outputDir = new File(parent, path);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        return outputDir;
    }

    private Configuration createFreemarkerConfiguration(String templatePath) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        if (IS_LOCAL) {
            cfg.setDirectoryForTemplateLoading(new File("src/main/resources/" + templatePath));
        } else {
            cfg.setClassLoaderForTemplateLoading(
                    Thread.currentThread().getContextClassLoader(),
                    templatePath
            );
        }
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }

    private void processTemplateToFile(Configuration cfg, String templateName, Map<String, Object> dataModel, File outputDir, String outputFileName) throws IOException, TemplateException {
        Template template = cfg.getTemplate(templateName);

        File outputPackageDir = outputDir;
        if (dataModel.get("package") != null) {
            outputPackageDir = new File(outputDir, ((String) dataModel.get("package")).replace(".", File.separator));
            if (!outputPackageDir.exists()) {
                outputPackageDir.mkdirs();
            }
        }

        File outputFile = new File(outputPackageDir, outputFileName);

        try (FileWriter writer = new FileWriter(outputFile)) {
            template.process(dataModel, writer);
        }
    }

    private Map<String, Object> createEntityDataModel(ERModel model, Entity entity, String _package, String domainLayer, String repositoryLayer) {
        Map<String, Object> dataModel = new HashMap<>();
        String entityInstance = firstLower(entity.getClassName());

        String domainPackage = _package + "." + domainLayer;
        dataModel.put("EntityClass", entity.getClassName());
        dataModel.put("EntityClass_FQN", domainPackage + "." + entity.getClassName());
        dataModel.put("EntityClassPlural", pluralize(firstUpper(entity.getClassName())));
        dataModel.put("entityInstance", entityInstance);
        dataModel.put("entityInstancePlural", pluralize(entityInstance));
        dataModel.put("entity", entity);
        dataModel.put("entityTranslationKey", entityInstance);
        dataModel.put("instanceType", entity.getClassName());
        dataModel.put("instanceName", entityInstance);

        dataModel.put("model", model);
        dataModel.put("entityNameLowerCase", entity.getLowerCaseName());
        dataModel.put("entityNameTitleCase", titleCase(entity.getClassName()));
        dataModel.put("entityNameTitleCasePluralize", pluralize(titleCase(entity.getClassName())));
        dataModel.put("entityNameLowerCasePluralize", pluralize(entity.getClassName().toLowerCase()));
        String entityNameSpinalCased = kebabCase(entityInstance);
        dataModel.put("entityApiUrl", entityNameSpinalCased);

        String repositoryFileName = entity.getClassName() + firstUpper(repositoryLayer);
        String repositoryPackage = _package + "." + repositoryLayer;
        dataModel.put("EntityRepository", repositoryFileName);
        dataModel.put("entityRepository", firstLower(repositoryFileName));
        dataModel.put("EntityRepository_FQN", repositoryPackage + "." + repositoryFileName);
        dataModel.put("EntityRepository_package", repositoryPackage);
        dataModel.put("EntityRepositorySuffix", firstUpper(repositoryLayer));

        dataModel.put("beanName", entityInstance + "Bean");
        dataModel.put("beanClass", entity.getClassName() + "Bean");

        String pkName = entity.getPrimaryKeyName();
        String pkType = entity.getPrimaryKeyType();
        dataModel.put("pkName", firstLower(pkName));
        dataModel.put("pkGetter", getMethodName(getIntrospectionPrefix(isBoolean(pkType)), pkName));
        dataModel.put("pkSetter", getMethodName("set", pkName));
        dataModel.put("pkType", pkType);
        dataModel.put("EntityPKClass", entity.getPrimaryKeyType());
        dataModel.put("isPKPrimitive", isPrimitive(pkType));

        return dataModel;
    }

}
