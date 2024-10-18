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

import jakarta.json.bind.annotation.JsonbTransient;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Gaurav Gupta
 */
public class Relationship {

    private String firstEntity;
    private String secondEntity;
    private String relationshipType;
    private String relationshipLabel;
    private String relationshipVarNameInFirstEntity;
    private String relationshipVarNameInSecondEntity;

    public Relationship() {
    }

    public Relationship(String firstEntity, String secondEntity, String relationshipType, String relationshipLabel) {
        this.firstEntity = firstEntity;
        this.secondEntity = secondEntity;
        this.relationshipType = relationshipType;
        this.relationshipLabel = relationshipLabel;
    }

    public String getFirstEntity() {
        return firstEntity;
    }

    public String getSecondEntity() {
        return secondEntity;
    }

    @JsonbTransient
    public String getFirstEntityClass() {
        return getClassName(firstEntity);
    }

    @JsonbTransient
    public String getSecondEntityClass() {
        return getClassName(secondEntity);
    }

    @JsonbTransient
    public String getClassName(String name) {
        String[] parts = name.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public String getRelationshipLabel() {
        return relationshipLabel;
    }

    public String getRelationshipVarNameInFirstEntity() {
        return relationshipVarNameInFirstEntity;
    }

    public void setRelationshipVarNameInFirstEntity(String relationshipVarNameInFirstEntity) {
        this.relationshipVarNameInFirstEntity = relationshipVarNameInFirstEntity;
    }

    public String getRelationshipVarNameInSecondEntity() {
        return relationshipVarNameInSecondEntity;
    }

    public void setRelationshipVarNameInSecondEntity(String relationshipVarNameInSecondEntity) {
        this.relationshipVarNameInSecondEntity = relationshipVarNameInSecondEntity;
    }

    public void setFirstEntity(String firstEntity) {
        this.firstEntity = firstEntity;
    }

    public void setSecondEntity(String secondEntity) {
        this.secondEntity = secondEntity;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public void setRelationshipLabel(String relationshipLabel) {
        this.relationshipLabel = relationshipLabel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstEntity, secondEntity, relationshipType, relationshipLabel);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Relationship other = (Relationship)obj;
        if (!Objects.equals(this.firstEntity, other.firstEntity)) {
            return false;
        }
        if (!Objects.equals(this.secondEntity, other.secondEntity)) {
            return false;
        }
        if (!Objects.equals(this.relationshipType, other.relationshipType)) {
            return false;
        }
        return Objects.equals(this.relationshipLabel, other.relationshipLabel);
    }

    @Override
    public String toString() {
        return "Relationship{" + "firstEntity=" + firstEntity + ", secondEntity=" + secondEntity + ", relationshipType=" + relationshipType + ", relationshipLabel=" + relationshipLabel + '}';
    }

}
