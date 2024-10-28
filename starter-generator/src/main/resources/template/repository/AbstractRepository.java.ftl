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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import ${model.importPrefix}.persistence.EntityManager;
import ${model.importPrefix}.persistence.NoResultException;
import ${model.importPrefix}.persistence.Query;
import ${model.importPrefix}.persistence.TypedQuery;
import ${model.importPrefix}.persistence.criteria.CriteriaQuery;
import ${model.importPrefix}.persistence.criteria.Root;
<#if cdi>import ${model.importPrefix}.transaction.Transactional;
import static ${model.importPrefix}.transaction.Transactional.TxType.REQUIRED;
import static ${model.importPrefix}.transaction.Transactional.TxType.SUPPORTS;</#if>

<#if cdi>@Transactional(SUPPORTS)</#if>
public abstract class ${AbstractRepository}<E,P> {

    private final Class<E> entityClass;

    public ${AbstractRepository}(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    <#if cdi>@Transactional(REQUIRED)</#if>
    public void create(E entity) {
        getEntityManager().persist(entity);
    }

    <#if cdi>@Transactional(REQUIRED)</#if>
    public E edit(E entity) {
        return getEntityManager().merge(entity);
    }

    <#if cdi>@Transactional(REQUIRED)</#if>
    public void remove(E entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public P getIdentifier(E entity) {
        return (P)getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
    }

    public E find(P id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<E> findAll() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<E> findRange(int startPosition, int size) {
        return findRange(startPosition, size, null);
    }
    
    public List<E> findRange(int startPosition, int size, String entityGraph) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(size);
        q.setFirstResult(startPosition);
        if (entityGraph != null) {
            q.setHint("${model.importPrefix}.persistence.loadgraph", getEntityManager().getEntityGraph(entityGraph));
        }
        return q.getResultList();
    }

    public int count() {
        CriteriaQuery criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery();
        Root<E> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(getEntityManager().getCriteriaBuilder().count(root));
        Query query = getEntityManager().createQuery(criteriaQuery);
        return ((Long) query.getSingleResult()).intValue();
    }

    public Optional<E> findSingleByNamedQuery(String namedQueryName) {
        return findOrEmpty(() -> getEntityManager().createNamedQuery(namedQueryName, entityClass).getSingleResult());
    }

    public Optional<E> findSingleByNamedQuery(String namedQueryName, Map<String, Object> parameters) {
        return findSingleByNamedQuery(namedQueryName, null, parameters);
    }

    public Optional<E> findSingleByNamedQuery(String namedQueryName, String entityGraph, Map<String, Object> parameters) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        TypedQuery<E> query = getEntityManager().createNamedQuery(namedQueryName, entityClass);
        rawParameters.forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));
        if(entityGraph != null){
            query.setHint("${model.importPrefix}.persistence.loadgraph", getEntityManager().getEntityGraph(entityGraph));
        }
        return findOrEmpty(query::getSingleResult);
    }

    public List<E> findByNamedQuery(String namedQueryName) {
        return findByNamedQuery(namedQueryName, -1);
    }

    public List<E> findByNamedQuery(String namedQueryName, Map<String, Object> parameters) {
        return findByNamedQuery(namedQueryName, parameters, -1);
    }

    public List<E> findByNamedQuery(String namedQueryName, int resultLimit) {
        return findByNamedQuery(namedQueryName, Collections.EMPTY_MAP, resultLimit);
    }

    public List<E> findByNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = getEntityManager().createNamedQuery(namedQueryName);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        rawParameters.forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));
        return query.getResultList();
    }

    public static <E> Optional<E> findOrEmpty(final DaoRetriever<E> retriever) {
        try {
            return Optional.of(retriever.retrieve());
        } catch (NoResultException ex) {
            //log
        }
        return Optional.empty();
    }

    @FunctionalInterface
    public interface DaoRetriever<E> {

        E retrieve() throws NoResultException;
    }

}
