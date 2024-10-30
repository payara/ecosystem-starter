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

/**
 *
 * @author Gaurav Gupta
 */
import static fish.payara.starter.application.domain.Constant.aboutUsPageDescription_default;
import static fish.payara.starter.application.domain.Constant.homePageDescription_default;
import static fish.payara.starter.application.domain.Constant.icon_default;
import static fish.payara.starter.application.domain.Constant.longTitle_default;
import static fish.payara.starter.application.domain.Constant.title_default;
import java.util.ArrayList;
import java.util.List;
import jakarta.json.bind.annotation.JsonbTransient;
import java.util.LinkedHashSet;
import java.util.Set;

public class ERModel {

    private List<Entity> entities = new ArrayList<>();
    private Set<Relationship> relationships = new LinkedHashSet<>();
    private String importPrefix = "jakarta";
    private String icon;
    private String title;
    private String longTitle;
    private String homePageDescription;
    private String aboutUsPageDescription;
    private List<String> topBarMenuOptions = new ArrayList<>();

    public ERModel() {
    }

    public String getImportPrefix() {
        return importPrefix;
    }

    public void setImportPrefix(String importPrefix) {
        this.importPrefix = importPrefix;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public Entity getEntity(String entityName) {
        for (Entity entity : entities) {
            if (entity.getName().equals(entityName)) {
                return entity;
            }
        }
        return null;
    }

    public void addRelationship(Relationship relationship) {
        relationships.add(relationship);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Set<Relationship> getRelationships() {
        return relationships;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public void setRelationships(Set<Relationship> relationships) {
        this.relationships = relationships;
    }

    @JsonbTransient
    public String getIcon() {
        return icon == null ? icon_default : icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @JsonbTransient
    public String getTitle() {
        return title == null ? title_default : title;
    }

    @JsonbTransient
    public String getTitle(String defaultValue) {
        return title != null ? title : defaultValue;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonbTransient
    public String getLongTitle() {
        return longTitle == null ? longTitle_default : longTitle;
    }

    public void setLongTitle(String longTitle) {
        this.longTitle = longTitle;
    }

    @JsonbTransient
    public String getHomePageDescription() {
        return homePageDescription == null ? homePageDescription_default : homePageDescription;
    }

    public void setHomePageDescription(String homePageDescription) {
        this.homePageDescription = homePageDescription;
    }

    @JsonbTransient
    public String getAboutUsPageDescription() {
        return aboutUsPageDescription == null ? aboutUsPageDescription_default : aboutUsPageDescription;
    }

    public void setAboutUsPageDescription(String aboutUsPageDescription) {
        this.aboutUsPageDescription = aboutUsPageDescription;
    }

    public List<String> getTopBarMenuOptions() {
        return topBarMenuOptions;
    }

    public void setTopBarMenuOptions(List<String> topBarMenuOptions) {
        this.topBarMenuOptions = topBarMenuOptions;
    }

    @Override
    public String toString() {
        return "ERModel{" + "\nentities=" + entities + "\n, relationships=" + relationships + '}';
    }

}
