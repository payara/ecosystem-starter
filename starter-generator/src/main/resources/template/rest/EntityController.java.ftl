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

import ${EntityClass_FQN};
import ${EntityRepository_FQN};
<#list entity.attributes as attribute>
    <#if model.getEntity(attribute.type)??>
        <#if attribute.multi>
        <#else>
import ${EntityRepository_package}.${attribute.getType()}${EntityRepositorySuffix};
        </#if>
    <#else>
    </#if>
</#list>
import ${model.importPrefix}.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import ${model.importPrefix}.ws.rs.Consumes;
import ${model.importPrefix}.ws.rs.DELETE;
import ${model.importPrefix}.ws.rs.GET;
import ${model.importPrefix}.ws.rs.POST;
import ${model.importPrefix}.ws.rs.PUT;
import ${model.importPrefix}.ws.rs.Path;
import ${model.importPrefix}.ws.rs.PathParam;
import ${model.importPrefix}.ws.rs.Produces;
import ${model.importPrefix}.ws.rs.core.MediaType;
import ${model.importPrefix}.ws.rs.core.Response;<#if pagination != "no">
import ${model.importPrefix}.ws.rs.QueryParam;
import ${model.importPrefix}.ws.rs.core.Response.ResponseBuilder;
import ${Page_FQN};
import ${PaginationUtil_FQN};</#if><#if metrics>
import org.eclipse.microprofile.metrics.annotation.Timed;</#if>
import org.eclipse.microprofile.faulttolerance.Timeout;<#if openAPI>
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;</#if>

/**
 * REST controller for managing ${EntityClass}.
 */
@Path("/api/${entityApiUrl}")
public class ${controllerClass} {

    private static final Logger LOG = Logger.getLogger(${controllerClass}.class.getName());

    @Inject
    private ${EntityRepository} ${entityRepository};

    <#list entity.attributes as attribute>
        <#if model.getEntity(attribute.type)??>
            <#if attribute.multi>
            <#else>
    @Inject
    private ${attribute.getType()}${EntityRepositorySuffix} ${attribute.name}${EntityRepositorySuffix};
            </#if>
        <#else>
        </#if>
    </#list>

    private static final String ENTITY_NAME = "${entityTranslationKey}";

    /**
     * POST : Create a new ${entityInstance}.
     *
     * @param ${instanceName} the ${instanceName} to create
     * @return the Response with status 201 (Created) and with body the
     * new ${instanceName}, or with status 400 (Bad Request) if the ${entityInstance} has already
     * an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "create a new ${entityInstance}", description = "Create a new ${entityInstance}")
    @APIResponse(responseCode = "201", description = "Created")
    @APIResponse(responseCode = "400", description = "Bad Request")</#if>
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create${EntityClass}(${instanceType} ${instanceName}) throws URISyntaxException {
        LOG.log(Level.FINE, "REST request to save ${EntityClass} : {}", ${instanceName});
    <#list entity.attributes as attribute>
        <#if model.getEntity(attribute.type)??>
            <#if attribute.multi>
            <#else>
        if (${instanceName}.get${attribute.getTitleCaseName()}() != null && ${instanceName}.get${attribute.getTitleCaseName()}().get${model.getEntity(attribute.type).getPrimaryKeyFirstUpperName()}() != null) {
            ${instanceName}.set${attribute.getTitleCaseName()}(${attribute.name}${EntityRepositorySuffix}.find(${instanceName}.get${attribute.getTitleCaseName()}().get${model.getEntity(attribute.type).getPrimaryKeyFirstUpperName()}()));
        } else {
            ${instanceName}.set${attribute.getTitleCaseName()}(null);
        }
            </#if>
        <#else>
        </#if>
    </#list>
        ${entityRepository}.create(${instanceName});
        return HeaderUtil.createEntityCreationAlert(Response.created(new URI("/${applicationPath}/api/${entityApiUrl}/" + ${instanceName}.${pkGetter}())),
                ENTITY_NAME, <#if isPKPrimitive>String.valueOf(${instanceName}.${pkGetter}())<#elseif pkType == "String">${instanceName}.${pkGetter}()<#else>${instanceName}.${pkGetter}().toString()</#if>)
                .entity(${instanceName}).build();
    }

    /**
     * PUT : Updates an existing ${entityInstance}.
     *
     * @param ${instanceName} the ${instanceName} to update
     * @return the Response with status 200 (OK) and with body the updated ${instanceName},
     * or with status 400 (Bad Request) if the ${instanceName} is not valid,
     * or with status 500 (Internal Server Error) if the ${instanceName} couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "update ${entityInstance}", description = "Updates an existing ${entityInstance}")
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "400", description = "Bad Request")
    @APIResponse(responseCode = "500", description = "Internal Server Error")</#if>
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update${EntityClass}(${instanceType} ${instanceName}) throws URISyntaxException {
        LOG.log(Level.FINE, "REST request to update ${EntityClass} : {}", ${instanceName});
    <#list entity.attributes as attribute>
        <#if model.getEntity(attribute.type)??>
            <#if attribute.multi>
            <#else>
        if (${instanceName}.get${attribute.getTitleCaseName()}() != null && ${instanceName}.get${attribute.getTitleCaseName()}().get${model.getEntity(attribute.type).getPrimaryKeyFirstUpperName()}() != null) {
            ${instanceName}.set${attribute.getTitleCaseName()}(${attribute.name}${EntityRepositorySuffix}.find(${instanceName}.get${attribute.getTitleCaseName()}().get${model.getEntity(attribute.type).getPrimaryKeyFirstUpperName()}()));
        } else {
            ${instanceName}.set${attribute.getTitleCaseName()}(null);
        }
            </#if>
        <#else>
        </#if>
    </#list>
        ${entityRepository}.edit(${instanceName});
        return HeaderUtil.createEntityUpdateAlert(Response.ok(), ENTITY_NAME, <#if isPKPrimitive>String.valueOf(${instanceName}.${pkGetter}())<#else>${instanceName}.${pkGetter}().toString()</#if>)
                .entity(${instanceName}).build();
    }

    /**
     * GET : get all the ${entityInstancePlural}.
     <#if pagination!= "no">* @param page the pagination information
     * @param size the pagination size information
     <#elseif fieldsContainNoOwnerOneToOne>* @param filter the filter of the request</#if>
     * @return the Response with status 200 (OK) and the list of ${entityInstancePlural} in body
     <#if pagination!= "no">* @throws URISyntaxException if there is an error to generate the pagination HTTP headers</#if>
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "get all the ${entityInstancePlural}")
    @APIResponse(responseCode = "200", description = "OK")</#if>
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timeout
    <#if pagination == "no">
    public List<${instanceType}> getAll${EntityClassPlural}() {
        LOG.log(Level.FINE, "REST request to get all ${EntityClassPlural}");
        List<${EntityClass}> ${entityInstancePlural} = ${entityRepository}.findAll();
        return ${entityInstancePlural};
    }
    <#else>
    public Response getAll${EntityClassPlural}(@QueryParam("page") int page, @QueryParam("size") int size) throws URISyntaxException {
        LOG.log(Level.FINE, "REST request to get all ${EntityClassPlural}");
        List<${EntityClass}> ${entityInstancePlural} = ${entityRepository}.findRange(page * size, size);
        ResponseBuilder builder = Response.ok(${entityInstancePlural});
        PaginationUtil.generatePaginationHttpHeaders(builder, new Page(page, size, ${entityRepository}.count()), "/${applicationPath}/api/${entityApiUrl}");
        return builder.build();
    }
    </#if>

    /**
     * GET /:${pkName} : get the "${pkName}" ${entityInstance}.
     *
     * @param ${pkName} the ${pkName} of the ${instanceName} to retrieve
     * @return the Response with status 200 (OK) and with body the ${instanceName}, or with status 404 (Not Found)
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "get the ${entityInstance}")
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "404", description = "Not Found")</#if>
    @GET
    @Path("/{${pkName}}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get${EntityClass}(@PathParam("${pkName}") ${pkType} ${pkName}) {
        LOG.log(Level.FINE, "REST request to get ${EntityClass} : {}", ${pkName});
        ${instanceType} ${instanceName} = ${entityRepository}.find(${pkName});
        return Optional.ofNullable(${instanceName})
                .map(res -> Response.status(Response.Status.OK).entity(${instanceName}).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * DELETE /:${pkName} : remove the "${pkName}" ${entityInstance}.
     * 
     * @param ${pkName} the ${pkName} of the ${instanceName} to delete
     * @return the Response with status 200 (OK)
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "remove the ${entityInstance}" )
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "404", description = "Not Found")</#if>
    @DELETE
    @Path("/{${pkName}}")
    public Response remove${EntityClass}(@PathParam("${pkName}") ${pkType} ${pkName}) {
        LOG.log(Level.FINE, "REST request to delete ${EntityClass} : {}", ${pkName});
        ${entityRepository}.remove(${entityRepository}.find(${pkName}));
        return HeaderUtil.createEntityDeletionAlert(Response.ok(), ENTITY_NAME, <#if isPKPrimitive>String.valueOf(${pkName})<#else>${pkName}.toString()</#if>).build();
    }

}
