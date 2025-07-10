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
package ${package};

<#if cdi>import ${model.importPrefix}.enterprise.context.Dependent;</#if><#if !cdi>import ${model.importPrefix}.ejb.Stateless;</#if>
<#if named>import ${model.importPrefix}.inject.Named;</#if>
import ${model.importPrefix}.persistence.EntityManager;
import ${model.importPrefix}.inject.Inject;
import ${EntityClass_FQN};

<#if cdi>@Dependent</#if><#if !cdi>@Stateless</#if>
<#if named>@Named("${entityInstance}")</#if>
public class ${EntityRepository} extends ${AbstractRepository}<${EntityClass}, ${EntityPKClass}> {

    @Inject
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ${EntityRepository}() {
        super(${EntityClass}.class);
    }
    
}
