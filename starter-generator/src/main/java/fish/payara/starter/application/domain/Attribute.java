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

import fish.payara.starter.application.util.AttributeType;
import static fish.payara.starter.application.util.AttributeType.LOCAL_DATE;
import static fish.payara.starter.application.util.AttributeType.LOCAL_DATE_TIME;
import static fish.payara.starter.application.util.StringUtils.firstLower;
import static fish.payara.starter.application.util.StringUtils.firstUpper;
import static fish.payara.starter.application.util.StringUtils.kebabCase;
import static fish.payara.starter.application.util.StringUtils.pluralize;
import static fish.payara.starter.application.util.StringUtils.startCase;
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
    private boolean display;
    private String htmlLabel;

    /** Default constructor for Attribute. */
    public Attribute() {
    }

    /**
     * Constructor for Attribute with specified name, type, and primary key status.
     *
     * @param name the name of the attribute
     * @param type the type of the attribute
     * @param isPrimaryKey indicates if this attribute is a primary key
     */
    public Attribute(String name, String type, boolean isPrimaryKey) {
        this.name = name;
        this.type = type;
        this.primaryKey = isPrimaryKey;
    }

    /**
     * Constructor for Attribute with specified name, multi status, and relation type.
     *
     * @param name the name of the attribute
     * @param multi indicates if the attribute can have multiple values
     * @param relation the type of relation for the attribute
     */
    public Attribute(String name, boolean multi, String relation) {
        this.name = name;
        this.type = relation;
        this.multi = multi;
    }
   
    /** 
     * Returns the name of the attribute.
     * 
     * @return the name of the attribute
     */
    public String getName() {
        return name;
    }

    /** 
     * Returns the name of the attribute in start case format.
     * 
     * @return the start case name
     */
    @JsonbTransient
    public String getStartCaseName() {
        return startCase(name);
    }

    /** 
     * Returns the name of the attribute in title case format.
     * 
     * @return the title case name
     */
    @JsonbTransient
    public String getTitleCaseName() {
        return firstUpper(name);
    }

    /**
     * Returns the name of the attribute in plural case format.
     *
     * @return the plural case name
     */
    @JsonbTransient
    public String getPluralName() {
        return pluralize(firstUpper(name));
    }

    /** 
     * Returns the type of the attribute.
     * 
     * @return the type of the attribute
     */
    public String getType() {
        return type;
    }

    /** 
     * Checks if the attribute is a number type.
     * 
     * @return true if the attribute type is a number, false otherwise
     */
    @JsonbTransient
    public boolean isNumber() {
        return AttributeType.isNumber(type);
    }

    /** 
     * Checks if this attribute is a primary key.
     * 
     * @return true if this attribute is a primary key, false otherwise
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /** 
     * Checks if this attribute is required.
     * 
     * @return true if this attribute is required, false otherwise
     */
    @JsonbTransient
    public boolean isRequired() {
        return required;
    }

    /** 
     * Sets whether this attribute is required.
     * 
     * @param required true if this attribute is required, false otherwise
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /** 
     * Checks if this attribute can have multiple values.
     * 
     * @return true if this attribute can have multiple values, false otherwise
     */
    public boolean isMulti() {
        return multi;
    }

    /** 
     * Returns the tooltip text of the attribute.
     * 
     * @return the tooltip text
     */
    @JsonbTransient
    public String getToolTipText() {
        return tooltip;
    }

    /** 
     * Checks if the attribute has a tooltip.
     * 
     * @return true if the attribute has a tooltip, false otherwise
     */
    @JsonbTransient
    public boolean isToolTip() {
        return getToolTipText() != null && !getToolTipText().trim().isEmpty();
    }

    /** 
     * Sets the tooltip text for the attribute.
     * 
     * @param tooltip the tooltip text to set
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /** 
     * Returns the HTML label associated with the attribute.
     * 
     * @return the HTML label
     */
    @JsonbTransient
    public String getHtmlLabel() {
        return htmlLabel;
    }

    /** 
     * Sets the HTML label for the attribute.
     * 
     * @param htmllabel the HTML label to set
     */
    public void setHtmlLabel(String htmllabel) {
        this.htmlLabel = htmllabel;
    }

    /** 
     * Checks if this attribute should be displayed.
     * 
     * @return true if the attribute is to be displayed, false otherwise
     */
    @JsonbTransient
    public boolean isDisplay() {
        return display;
    }

    /** 
     * Sets the display status for the attribute.
     * 
     * @param display true if the attribute should be displayed, false otherwise
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /** 
     * Returns a list of imports required for the attribute type.
     * 
     * @return a list of import strings
     */
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

    /** 
     * Sets the name of the attribute.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /** 
     * Sets the type of the attribute.
     * 
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /** 
     * Sets the primary key status for the attribute.
     * 
     * @param isPrimaryKey true if this attribute is a primary key, false otherwise
     */
    public void setPrimaryKey(boolean isPrimaryKey) {
        this.primaryKey = isPrimaryKey;
    }

    /** 
     * Sets whether this attribute can have multiple values.
     * 
     * @param multi true if this attribute can have multiple values, false otherwise
     */
    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public String getApiUrl() {
        return kebabCase(firstLower(type));
    }
    
    public String getConverter() {
        return firstLower(type) + "Converter";
    }
    
    public String getBean() {
        return firstLower(type) + "Bean";
    }
    
    public String getPluralType() {
        return pluralize(type);
    }

    @Override
    public String toString() {
        return "\n\t\tAttribute{name=" + name + ", type=" + type + ", isPrimaryKey=" + primaryKey + '}';
    }

}
