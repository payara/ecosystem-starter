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

public class CRUDAppGenerator {
    
    private final String _package;
    private final String domainLayer;
    private final String repositoryLayer;
    private final String controllerLayer;
    private final ERModel model;
    private static final boolean IS_LOCAL = false; // Change this flag for local vs. production

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
            generator.generate(new File("D:\\HelloWorld"), true, true, true, true); // Output directory
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generate(File projectDir, boolean generateJPA, boolean generateRepository, boolean generateController, boolean generateWeb) throws IOException {

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
                    for (Entity entity : model.getEntities()) {
                        generateEntityController(_package, entity, java);
                    }
                    generateRestBase(dataModel, _package, java);
                    if (generateWeb) {
                        for (Entity entity : model.getEntities()) {
                            generateFrontend(model, entity, webapp);
                        }
                        generateFrontendBase(model, webapp);
                    }
                }
            }
        }
    }

    private void generateFrontendBase(ERModel model, File outputDir) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("model", model);
        generate("template/html", "app.html.ftl", "main.html", dataModel, outputDir);
        generate("template/html", "home.html.ftl", "home.html", dataModel, outputDir);
        generate("template/html", "about-us.html.ftl", "about-us.html", dataModel, outputDir);
    }

    private void generateFrontend(ERModel model, Entity entity, File outputDir) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("model", model);
        dataModel.put("entity", entity);
        dataModel.put("entityNameLowerCase", entity.getLowerCaseName());
        dataModel.put("entityNameTitleCase", titleCase(entity.getClassName()));
        dataModel.put("entityNameTitleCasePluralize", pluralize(titleCase(entity.getClassName())));
        dataModel.put("entityNameLowerCasePluralize", pluralize(entity.getClassName().toLowerCase()));
        String entityInstance = firstLower(entity.getClassName());
        String entityNameSpinalCased = kebabCase(entityInstance);
        dataModel.put("entityApiUrl", entityNameSpinalCased);
        generate("template/html", "entity.html.ftl", dataModel.get("entityNameLowerCase") + ".html", dataModel, outputDir);
    }

    private void generateEntityController(String _package, Entity entity, File outputDir) {
        // Configure FreeMarker
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        try {

            if (IS_LOCAL) {
                // Local development - load templates from the file system
                cfg.setDirectoryForTemplateLoading(new File("src/main/resources/template/rest"));
            } else {
                // Production - load templates from the classpath
                cfg.setClassLoaderForTemplateLoading(
                        Thread.currentThread().getContextClassLoader(),
                        "template/rest"
                );
            }
            cfg.setDefaultEncoding("UTF-8");

            // Load the template
            Template template = cfg.getTemplate("EntityController.java.ftl");

            // Create the data model
            String repositoryPackage = _package + "." + repositoryLayer;
            String controllerPackage = _package + "." + controllerLayer;

            String entityInstance = firstLower(entity.getClassName());
            String entityNameSpinalCased = kebabCase(entityInstance);
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("model", model);
            dataModel.put("package", controllerPackage);
            dataModel.put("entity", entity);
            dataModel.put("EntityClass", entity.getClassName());
            dataModel.put("EntityClassPlural", pluralize(firstUpper(entity.getClassName())));
            dataModel.put("EntityClass_FQN", _package + "."+ domainLayer+"." + entity.getClassName());
            dataModel.put("entityInstance", entityInstance);
            dataModel.put("entityInstancePlural", pluralize(entityInstance));
            dataModel.put("entityTranslationKey", entityInstance);

            String repositoryFileName = entity.getClassName() + firstUpper(repositoryLayer);
            String controllerFileName = entity.getClassName() + firstUpper(controllerLayer);

            dataModel.put("controllerClass", controllerFileName);
            dataModel.put("controllerClassHumanized", startCase(controllerFileName));
            dataModel.put("entityApiUrl", entityNameSpinalCased);

            dataModel.put("EntityRepository", repositoryFileName);
            dataModel.put("entityRepository", firstLower(repositoryFileName));
            dataModel.put("EntityRepository_FQN", repositoryPackage + "." + repositoryFileName);
            dataModel.put("EntityRepository_package", repositoryPackage);
            dataModel.put("EntityRepositorySuffix", firstUpper(repositoryLayer));

            boolean dto = false;
            dataModel.put("instanceType", dto ? entity.getClassName() + "DTO" : entity.getClassName());
            dataModel.put("instanceName", dto ? entityInstance + "DTO" : entityInstance);

            dataModel.put("pagination", "no");
            dataModel.put("fieldsContainNoOwnerOneToOne", false);
            dataModel.put("metrics", false);
            dataModel.put("openAPI", false);
            dataModel.put("applicationPath", "resources");

            String pkName = entity.getPrimaryKeyName();
            String pkType = entity.getPrimaryKeyType();
            dataModel.put("pkName", firstLower(pkName));
            dataModel.put("pkGetter", getMethodName(getIntrospectionPrefix(isBoolean(pkType)), pkName));
            dataModel.put("pkSetter", getMethodName("set", pkName));
            dataModel.put("pkType", pkType);
            dataModel.put("isPKPrimitive", isPrimitive(pkType));

            // Output file path
            File outputPackageDir = new File(outputDir, controllerPackage.replace(".", File.separator));
            if (!outputPackageDir.exists()) {
                outputPackageDir.mkdirs();
            }
            File outputFile = new File(outputPackageDir, dataModel.get("controllerClass") + ".java");

            // Write the generated file
            try (FileWriter writer = new FileWriter(outputFile)) {
                template.process(dataModel, writer);
            }

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private void generateEntityRepository(String _package, Entity entity, File outputDir) {
        // Configure FreeMarker
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        try {
            if (IS_LOCAL) {
                // Local development - load templates from the file system
                cfg.setDirectoryForTemplateLoading(new File("src/main/resources/template/repository"));
            } else {
                // Production - load templates from the classpath
                cfg.setClassLoaderForTemplateLoading(
                        Thread.currentThread().getContextClassLoader(),
                        "template/repository"
                );
            }
            cfg.setDefaultEncoding("UTF-8");

            // Load the template
            Template template = cfg.getTemplate("EntityRepository.java.ftl");

            // Create the data model
            String repositoryPackage = _package + "." + repositoryLayer;
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("model", model);
            dataModel.put("package", repositoryPackage);
            dataModel.put("cdi", true);
            dataModel.put("named", false);
            dataModel.put("entityInstance", "exampleEntityRepository");
            dataModel.put("EntityClass", entity.getClassName());
            dataModel.put("EntityRepository", entity.getClassName()+ firstUpper(repositoryLayer));
            dataModel.put("EntityClass_FQN", _package + "."+domainLayer+"." + entity.getClassName());
            dataModel.put("EntityPKClass", entity.getPrimaryKeyType());
            dataModel.put("EntityPKClass_FQN", "");
            dataModel.put("AbstractRepository", "Abstract" + firstUpper(repositoryLayer));
            dataModel.put("AbstractRepository_FQN", _package + "." + repositoryLayer + "." + "Abstract" + firstUpper(repositoryLayer));

            // Output file path
            File outputPackageDir = new File(outputDir, repositoryPackage.replace(".", File.separator));
            if (!outputPackageDir.exists()) {
                outputPackageDir.mkdirs();
            }
            File outputFile = new File(outputPackageDir, dataModel.get("EntityRepository") + ".java");

            // Write the generated file
            try (FileWriter writer = new FileWriter(outputFile)) {
                template.process(dataModel, writer);
            }

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
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        try {
            if (IS_LOCAL) {
                // Local development - load templates from the file system
                cfg.setDirectoryForTemplateLoading(new File("src/main/resources/" + templatePath));
            } else {
                // Production - load templates from the classpath
                cfg.setClassLoaderForTemplateLoading(
                        Thread.currentThread().getContextClassLoader(),
                        templatePath
                );
            }
            cfg.setDefaultEncoding("UTF-8");

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
        
        Attribute attribute;
        if (isFirstEntity) {
            switch (relationshipType) {
                case "||--||": // Exactly one to exactly one
                case "||--o|": // Exactly one to zero or one
                case "|o--||": // Zero or one to exactly one
                    attribute = new Attribute(firstEntityVar, false, secondEntity);
                    entity.getAttributes().add(attribute);
                    sbfunc.append("    public ").append(attribute.getType()).append(" get").append(attribute.getTitleCaseName()).append("() {\n");
                    sbfunc.append("        return ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    sbfunc.append("    public void set").append(attribute.getTitleCaseName()).append("(").append(attribute.getType()).append(" ").append(attribute.getName()).append(") {\n");
                    sbfunc.append("        this.").append(attribute.getName()).append(" = ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    sb.append("    @OneToOne(mappedBy = \"").append(secondEntityVar).append("\")\n");
                    sb.append("    private ").append(attribute.getType()).append(" ").append(attribute.getName()).append(";\n");
                    break;
                case "||--|{": // Exactly one to one or more
                case "||--o{": // Exactly one to zero or more
                    attribute = new Attribute(firstEntityVars, true, secondEntity);
                    entity.getAttributes().add(attribute);
                    sbfunc.append("    public List<").append(attribute.getType()).append("> get").append(attribute.getTitleCaseName()).append("() {\n");
                    sbfunc.append("        return ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    sbfunc.append("    public void set").append(attribute.getTitleCaseName()).append("(List<").append(attribute.getType()).append("> ").append(attribute.getName()).append(") {\n");
                    sbfunc.append("        this.").append(attribute.getName()).append(" = ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    _imports.add(model.getImportPrefix() + ".json.bind.annotation.JsonbTransient");
                    _imports.add("java.util.List");
                    sb.append("    @JsonbTransient\n");
                    sb.append("    @OneToMany(mappedBy = \"").append(secondEntityVar).append("\")\n");
                    sb.append("    private List<").append(attribute.getType()).append("> ").append(attribute.getName()).append(";\n");
                    break;
                case "}|--||": // One or more to exactly one
                case "}o--||": // Zero or more to exactly one
                    attribute = new Attribute(firstEntityVar, false, secondEntity);
                    entity.getAttributes().add(attribute);
                    sbfunc.append("    public ").append(attribute.getType()).append(" get").append(attribute.getTitleCaseName()).append("() {\n");
                    sbfunc.append("        return ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    sbfunc.append("    public void set").append(attribute.getTitleCaseName()).append("(").append(attribute.getType()).append(" ").append(attribute.getName()).append(") {\n");
                    sbfunc.append("        this.").append(attribute.getName()).append(" = ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    sb.append("    @ManyToOne\n");
                    sb.append("    private ").append(attribute.getType()).append(" ").append(attribute.getName()).append(";\n");
                    break;
                case "}o--o{": // Zero or more to zero or more
                case "}|--o{": // One or more to zero or more
                case "}o--|{": // Zero or more to one or more
                case "}|--|{": // One or more to one or more
                    attribute = new Attribute(firstEntityVars, true, secondEntity);
                    entity.getAttributes().add(attribute);
                    sbfunc.append("    public List<").append(attribute.getType()).append("> get").append(attribute.getTitleCaseName()).append("() {\n");
                    sbfunc.append("        return ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    sbfunc.append("    public void set").append(attribute.getTitleCaseName()).append("(List<").append(attribute.getType()).append("> ").append(attribute.getName()).append(") {\n");
                    sbfunc.append("        this.").append(attribute.getName()).append(" = ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    _imports.add(model.getImportPrefix() + ".json.bind.annotation.JsonbTransient");
                    _imports.add("java.util.List");
                    sb.append("    @JsonbTransient\n");
                    sb.append("    @ManyToMany(mappedBy = \"").append(secondEntityVars).append("\")\n");
                    sb.append("    private List<").append(attribute.getType()).append("> ").append(attribute.getName()).append(";\n");
                    break;
            }
        } else {
            switch (relationshipType) {
                case "||--||": // Exactly one to exactly one
                case "||--o|": // Exactly one to zero or one
                case "|o--||": // Zero or one to exactly one
                    attribute = new Attribute(secondEntityVar, false, firstEntity);
                    entity.getAttributes().add(attribute);
                    sbfunc.append("    public ").append(attribute.getType()).append(" get").append(attribute.getTitleCaseName()).append("() {\n");
                    sbfunc.append("        return ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    sbfunc.append("    public void set").append(attribute.getTitleCaseName()).append("(").append(attribute.getType()).append(" ").append(attribute.getName()).append(") {\n");
                    sbfunc.append("        this.").append(attribute.getName()).append(" = ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    _imports.add(model.getImportPrefix() + ".json.bind.annotation.JsonbTransient");
                    sb.append("    @JsonbTransient\n");
                    sb.append("    @OneToOne\n");
                    sb.append("    @JoinColumn(name = \"").append(attribute.getName()).append("_id\")\n");
                    sb.append("    private ").append(attribute.getType()).append(" ").append(attribute.getName()).append(";\n");
                    break;
                case "||--|{": // Exactly one to one or more
                case "||--o{": // Exactly one to zero or more
                    attribute = new Attribute(secondEntityVar, false, firstEntity);
                    entity.getAttributes().add(attribute);
                    sbfunc.append("    public ").append(attribute.getType()).append(" get").append(attribute.getTitleCaseName()).append("() {\n");
                    sbfunc.append("        return ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    sbfunc.append("    public void set").append(attribute.getTitleCaseName()).append("(").append(attribute.getType()).append(" ").append(attribute.getName()).append(") {\n");
                    sbfunc.append("        this.").append(attribute.getName()).append(" = ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    sb.append("    @ManyToOne\n");
                    sb.append("    @JoinColumn(name = \"").append(attribute.getName()).append("_id\")\n");
                    sb.append("    private ").append(attribute.getType()).append(" ").append(attribute.getName()).append(";\n");
                    break;
//                case "}|--||": // One or more to exactly one
//                case "}o--||": // Zero or more to exactly one
//                    sb.append("    @OneToMany\n");
//                    sb.append("    @JoinColumn(name = \"").append(attribute.getName()).append("_id\")\n");
//                    sb.append("    private ").append(attribute.getType()).append(" ").append(attribute.getName()).append(";\n");
//                    break;
                case "}o--o{": // Zero or more to zero or more
                case "}|--o{": // One or more to zero or more
                case "}o--|{": // Zero or more to one or more
                case "}|--|{": // One or more to one or more
                    attribute = new Attribute(secondEntityVars, true, firstEntity);
                    entity.getAttributes().add(attribute);
                    sbfunc.append("    public List<").append(attribute.getType()).append("> get").append(attribute.getTitleCaseName()).append("() {\n");
                    sbfunc.append("        return ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    sbfunc.append("    public void set").append(attribute.getTitleCaseName()).append("(List<").append(attribute.getType()).append("> ").append(attribute.getName()).append(") {\n");
                    sbfunc.append("        this.").append(attribute.getName()).append(" = ").append(attribute.getName()).append(";\n");
                    sbfunc.append("    }\n\n");
                    _imports.add(model.getImportPrefix() + ".json.bind.annotation.JsonbTransient");
                    _imports.add("java.util.List");
                    sb.append("    @JsonbTransient\n");
                    sb.append("    @ManyToMany\n");
                    sb.append("    @JoinColumn(name = \"").append(secondEntityVar).append("_id\")\n");
                    sb.append("    private List<").append(attribute.getType()).append("> ").append(attribute.getName()).append(";\n");
                    break;
            }
        }
    }

    private File getDir(String parent, String path) {
        File outputDir = new File(parent, path);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        return outputDir;
    }

    private File getDir(File parent, String path) {
        File outputDir = new File(parent, path);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        return outputDir;
    }

}
