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

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

import ${EntityClass_FQN};
import ${EntityRepository_FQN};

@Named("${beanName}")
@ViewScoped
public class ${beanClass} implements Serializable {

    @Inject
    private transient ${EntityRepository} ${entityRepository};

    private ${EntityClass} ${entityInstance} = new ${EntityClass}();

    public ${EntityClass} get${EntityClass}() {
        return ${entityInstance};
    }

    public List<${EntityClass}> getAll${EntityClassPlural}() {
        return ${entityRepository}.findAll();
    }

    public String create() {
      
        return null;
    }
    public String save() {
        if (${entityInstance}.${pkGetter}() == null) {
             ${entityRepository}.create(${entityInstance});
        } else {
             ${entityRepository}.edit(${entityInstance});
        }
        ${entityInstance} = new ${EntityClass}(); // reset
        return null;
    }

    public String remove(${pkType} ${pkName}) {
        ${entityRepository}.remove(${entityRepository}.find(${pkName}));
        return null;
    }

    public String edit(${EntityClass} p) {
        this.${entityInstance} = p;
        return null;
    }

}