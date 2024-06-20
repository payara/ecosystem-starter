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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gaurav Gupta
 */
public class Entity {

    public String name;
    private final List<Attribute> attributes = new ArrayList<>();
    private final List<KeyValue> property;

    public Entity(String name, List<KeyValue> property) {
        this.name = name;
        this.property = property;
    }

    public String getName() {
        return name;
    }

    public String getStartCaseName() {
        return startCase(name);
    }

    public String getLowerCaseName() {
        return name.toLowerCase();
    }

    public String getTitleCaseName() {
        return titleCase(name);
    }

    public String getLowerCasePluralizeName() {
        return pluralize(name.toLowerCase());
    }

    public String getTitleCasePluralizeName() {
        return getProperty("title", pluralize(titleCase(name)));
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public String getPrimaryKeyType() {
        return attributes.stream().filter(a -> a.isPrimaryKey()).map(a -> a.getType()).findFirst().orElse("Long");
    }

    public String getPrimaryKeyName() {
        return attributes.stream().filter(a -> a.isPrimaryKey()).map(a -> a.getName()).findFirst().orElse(null);
    }

    public String getPrimaryKeyFirstUpperName() {
        return firstUpper(getPrimaryKeyName());
    }

    public String getDisplayName() {
        String displayName = attributes.stream().filter(a -> Boolean.valueOf(a.getProperty("display"))).map(a -> a.getName()).findFirst().orElse(null);
        if(displayName == null) {
            displayName = attributes.stream().filter(a -> !a.isPrimaryKey()).map(a -> a.getName()).findFirst().orElse(null);

        }
        return displayName;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public List<KeyValue> getProperty() {
        return property;
    }

    public KeyValue getProperty(String key) {
        for (KeyValue keyValue : property) {
            if (keyValue.getKey().equals(key)) {
                return keyValue;
            }
        }
        return null;
    }

    public String getProperty(String key, String defaultValue) {
        for (KeyValue keyValue : property) {
            if (keyValue.getKey().equals(key)) {
                return keyValue.getValue() == null ? defaultValue : keyValue.getValue();
            }
        }
        return defaultValue;
    }

    public String getIcon() {
        return getProperty("icon", "circle");
    }

    public String getTitle() {
        return getProperty("title", titleCase(name));
    }

    public String getDescription() {
        return getProperty("description", "");
    }

    @Override
    public String toString() {
        return "\n\tEntity{" + "name=" + name + ", attributes=" + attributes + ", property=\n\t\t" + property + '}';
    }

}
