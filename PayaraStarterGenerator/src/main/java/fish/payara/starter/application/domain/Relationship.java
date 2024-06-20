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

import java.util.List;

/**
 *
 * @author Gaurav Gupta
 */
public class Relationship {

    private final String firstEntity;
    private final String secondEntity;
    private final String relationshipType;
    private final String relationshipLabel;
    private final List<KeyValue> property;

    public Relationship(String firstEntity, String secondEntity, String relationshipType, String relationshipLabel, List<KeyValue> property) {
        this.firstEntity = firstEntity;
        this.secondEntity = secondEntity;
        this.relationshipType = relationshipType;
        this.relationshipLabel = relationshipLabel;
        this.property = property;
    }

    public String getFirstEntity() {
        return firstEntity;
    }

    public String getSecondEntity() {
        return secondEntity;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public String getRelationshipLabel() {
        return relationshipLabel;
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

    @Override
    public String toString() {
        return "Relationship{" + "firstEntity=" + firstEntity + ", secondEntity=" + secondEntity + ", relationshipType=" + relationshipType + ", relationshipLabel=" + relationshipLabel + '}';
    }

}
