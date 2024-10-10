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
package fish.payara.starter.application.domain;

import static fish.payara.starter.application.util.StringHelper.firstUpper;
import static fish.payara.starter.application.util.StringHelper.pluralize;
import static fish.payara.starter.application.util.StringHelper.startCase;
import static fish.payara.starter.application.util.StringHelper.titleCase;
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
    public String getStartCaseName() {
        return startCase(name);
    }

    @JsonbTransient
    public String getLowerCaseName() {
        return name.toLowerCase();
    }

    @JsonbTransient
    public String getTitleCaseName() {
        return titleCase(name);
    }

    @JsonbTransient
    public String getLowerCasePluralizeName() {
        return pluralize(name.toLowerCase());
    }

    @JsonbTransient
    public String getTitleCasePluralizeName() {
        return pluralize(getTitle());
    }

    public List<Attribute> getAttributes() {
        return attributes;
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
        String displayName = attributes.stream().filter(a -> a.isDisplay() != null && a.isDisplay()).map(a -> a.getName()).findFirst().orElse(null);
        if(displayName == null) {
            displayName = attributes.stream().filter(a -> !a.isPrimaryKey()).map(a -> a.getName()).findFirst().orElse(null);

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
