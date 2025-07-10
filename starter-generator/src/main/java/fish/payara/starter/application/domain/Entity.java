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
package fish.payara.starter.application.domain;

import static fish.payara.starter.application.util.StringUtils.firstUpper;
import static fish.payara.starter.application.util.StringUtils.startCase;
import static fish.payara.starter.application.util.StringUtils.titleCase;
import jakarta.json.bind.annotation.JsonbTransient;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gaurav Gupta
 */
public class Entity {

    private String name;
    private List<Attribute> attributes = new ArrayList<>();
    private String icon = "circle";
    private String title;
    private String description = "";

    public Entity() {
    }
    
    public Entity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @JsonbTransient
    public String getClassName() {
        String[] parts = name.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    @JsonbTransient
    public String getStartCaseName() {
        return startCase(name);
    }

    @JsonbTransient
    public String getLowerCaseName() {
        return getClassName().toLowerCase();
    }

    @JsonbTransient
    public String getTitleCaseName() {
        return titleCase(getClassName());
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
    
    public Attribute findAttributeByName(String name) {
        return attributes.stream()
                .filter(attribute -> attribute.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @JsonbTransient
    public String getPrimaryKeyType() {
        return attributes.stream().filter(a -> a.isPrimaryKey()).map(a -> a.getType()).findFirst().orElse("Long");
    }

    @JsonbTransient
    public String getPrimaryKeyName() {
        return attributes.stream().filter(a -> a.isPrimaryKey()).map(a -> a.getName()).findFirst().orElse(null);
    }

    @JsonbTransient
    public String getPrimaryKeyFirstUpperName() {
        return firstUpper(getPrimaryKeyName());
    }
    
    @JsonbTransient
    public String getDisplayName() {
        String displayName = attributes.stream()
                .filter(a -> a.isDisplay())
                .map(a -> a.getName()).findFirst().orElse(null);
        if(displayName == null) {
            displayName = attributes.stream()
                    .filter(a -> !a.isPrimaryKey())
                    .map(a -> a.getName()).findFirst().orElse(null);
        }
        if(displayName == null) {
            displayName = attributes.stream()
                    .map(a -> a.getName()).findFirst().orElse(null);
        }
        return displayName;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    @JsonbTransient
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @JsonbTransient
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonbTransient
    public String getTitle() {
        return getTitle(titleCase(name));
    }
    
    @JsonbTransient
    public String getTitle(String defaultValue) {
        return title != null ? title : defaultValue;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "\n\tEntity{" + "name=" + name + ", attributes=" + attributes + '}';
    }

}
