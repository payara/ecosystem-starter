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

    private String icon;
    private String title;
    private String longTitle;
    private String homePageDescription;
    private String aboutUsPageDescription;
    private List<String> topBarMenuOptions = new ArrayList<>();

    public ERModel() {
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
