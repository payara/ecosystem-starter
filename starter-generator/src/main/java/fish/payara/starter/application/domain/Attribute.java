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
import static fish.payara.starter.application.util.AttributeType.LOCAL_DATE;
import static fish.payara.starter.application.util.AttributeType.LOCAL_DATE_TIME;
import static fish.payara.starter.application.util.StringHelper.pluralize;
import static fish.payara.starter.application.util.StringHelper.startCase;
import static fish.payara.starter.application.util.StringHelper.titleCase;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gaurav Gupta
 */
@JsonbTypeSerializer(AttributeSerializer.class)
public class Attribute {

    private String name;
    private String type;
    private boolean primaryKey;
    private boolean multi;
    private boolean required;
    private String tooltip;
    private Boolean display;
    private String htmlLabel;

    public Attribute() {
    }

    public Attribute(String name, String type, boolean isPrimaryKey) {
        this.name = name;
        this.type = type;
        this.primaryKey = isPrimaryKey;
    }

    public Attribute(String name, boolean multi, String relation) {
        this.name = name;
        this.type = relation;
        this.multi = multi;
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
        return pluralize(titleCase(name));
    }

    public String getType() {
        return type;
    }

    @JsonbTransient
    public boolean isNumber() {
        return AttributeType.isNumber(type);
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    @JsonbTransient
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isMulti() {
        return multi;
    }

    @JsonbTransient
    public String getToolTipText() {
        return tooltip;
    }

    @JsonbTransient
    public boolean isToolTip() {
        return getToolTipText() != null && !getToolTipText().trim().isEmpty();
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @JsonbTransient
    public String getHtmlLabel() {
        return htmlLabel;
    }

    public void setHtmlLabel(String htmllabel) {
        this.htmlLabel = htmllabel;
    }

    @JsonbTransient
    public Boolean isDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    @JsonbTransient
    public List<String> getImports() {
        List<String> _import = new ArrayList<>();
        if (type.equals("LocalDate")) {
            _import.add(LOCAL_DATE);
        } else if (type.equals("LocalDateTime")) {
            _import.add(LOCAL_DATE_TIME);
        }
        return _import;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrimaryKey(boolean isPrimaryKey) {
        this.primaryKey = isPrimaryKey;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    @Override
    public String toString() {
        return "\n\t\tAttribute{name=" + name + ", type=" + type + ", isPrimaryKey=" + primaryKey + '}';
    }

}
