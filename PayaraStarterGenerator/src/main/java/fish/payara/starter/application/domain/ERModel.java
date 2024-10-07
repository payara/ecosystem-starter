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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ERModel {

    private final List<Entity> entities = new ArrayList<>();
    private final List<Relationship> relationships = new ArrayList<>();
    private List<KeyValue> property = Collections.EMPTY_LIST;

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

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public List<KeyValue> getProperty() {
        return property;
    }

    public void setProperty(List<KeyValue> property) {
        this.property = property;
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
        return getProperty("title", "Jakarta EE Sample");
    }

    public String getLongTitle() {
        return getProperty("long-title", getProperty("title", "EE Sample"));
    }

    public String getDescription() {
        return getProperty("home-page-description", "Unlock the full potential of your application by harnessing the power of Jakarta EE");
    }

    public String getAboutUsDescription() {
        return getProperty("about-us-page-description", "Welcome to our About Us page, where innovation meets reliability with Payara Jakarta EE. As a team passionate about delivering unparalleled solutions, we specialize in harnessing the power of Jakarta EE to create robust, scalable, and secure applications. With a deep understanding of enterprise-grade development, we are committed to crafting tailored solutions that drive business growth and exceed client expectations. Backed by years of experience and a dedication to staying at the forefront of technology, we take pride in our ability to transform ideas into reality, empowering businesses to thrive in the digital landscape. Discover more about our journey, expertise, and the vision that propels us forward.");
    }

    @Override
    public String toString() {
        return "ERModel{" + "\nentities=" + entities + "\n, relationships=" + relationships + '}';
    }

}
