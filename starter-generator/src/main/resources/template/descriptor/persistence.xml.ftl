<#-- 
    Copyright 2024 the original author or authors from the Jeddict project (https://jeddict.github.io/).

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.
-->
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<#if model.importPrefix == "jakarta">
<persistence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns="https://jakarta.ee/xml/ns/persistence"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
<#else>
<persistence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd"
             version="2.1">
</#if>
    <persistence-unit name="${appPU}" transaction-type="JTA">
        <jta-data-source>java:comp/DefaultDataSource</jta-data-source>
        <exclude-unlisted-classes>false</exclude-unlisted-classes>
        <properties>
            <property name="${model.importPrefix}.persistence.schema-generation.database.action" value="create"/>
        </properties>
    </persistence-unit>
</persistence>
