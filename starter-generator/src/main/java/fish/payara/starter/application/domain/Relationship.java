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
    public String toString() {
        return "Relationship{" + "firstEntity=" + firstEntity + ", secondEntity=" + secondEntity + ", relationshipType=" + relationshipType + ", relationshipLabel=" + relationshipLabel + '}';
    }

}
