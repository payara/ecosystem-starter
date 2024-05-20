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

import fish.payara.starter.application.util.AttributeType;
import static fish.payara.starter.application.util.StringHelper.pluralize;
import static fish.payara.starter.application.util.StringHelper.startCase;
import static fish.payara.starter.application.util.StringHelper.titleCase;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gaurav Gupta
 */
public class Attribute {

    private final String name;
    private final String type;
    private boolean isPrimaryKey;
    private boolean required;
    private List<String> _import = new ArrayList<>();
    private Entity relation;
    private boolean multi;

    private final List<KeyValue> property;

    public Attribute(String name, String type, boolean isPrimaryKey, List<KeyValue> property) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
        this.property = property;
    }

    public Attribute(String name, Entity relation, boolean multi, List<KeyValue> property) {
        this.name = name;
        this.type = relation.getName();
        this.relation = relation;
        this.multi = multi;
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
        return pluralize(titleCase(name));
    }

    public String getType() {
        return type;
    }

    public boolean isNumber() {
        return AttributeType.isNumber(type);
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isMulti() {
        return multi;
    }

    public Entity getRelation() {
        return relation;
    }

    public List<KeyValue> getProperty() {
        return property;
    }

    public String getProperty(String key) {
        for (KeyValue keyValue : property) {
            if (keyValue.getKey().equals(key)) {
                return keyValue.getValue();
            }
        }
        return null;
    }

    public List<String> getImports() {
        return _import;
    }

    public boolean addImport(String e) {
        return _import.add(e);
    }

    public boolean removeImport(String o) {
        return _import.remove(o);
    }

    @Override
    public String toString() {
        return "\n\t\tAttribute{name=" + name + ", type=" + type + ", isPrimaryKey=" + isPrimaryKey + ", property=" + property + '}';
    }

}
