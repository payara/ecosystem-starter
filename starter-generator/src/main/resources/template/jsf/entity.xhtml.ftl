<ui:composition xmlns="http://www.w3.org/1999/xhtml"
<#if model.importPrefix == "jakarta" >
                xmlns:ui="jakarta.faces.facelets"
                xmlns:f="jakarta.faces.core"
                xmlns:h="jakarta.faces.html"
 <#else>
                xmlns:ui="http://java.sun.com/jsf/facelets"
                xmlns:f="http://java.sun.com/jsf/core"
                xmlns:h="http://java.sun.com/jsf/html"
</#if>
                template="/WEB-INF/layout/template.xhtml">

    <ui:define name="content">
        <h3>${entity.getTitle()}</h3>
        <p>${entity.getDescription()}</p>
        <h:panelGrid columns="2" columnClasses="label,value" styleClass="table-form">
        <#list entity.attributes as attribute>
        <#if !(attribute.primaryKey && attribute.type != "String") && !attribute.multi>
            <#assign attrId = attribute.getName()>
            <#assign attrValue = beanName + '.' + entityNameLowerCase + '.' + attribute.name>

            <h:outputLabel for="${attrId}" value="${attribute.getStartCaseName()}:" />
            <#if model.getEntity(attribute.type)??>
            <h:selectOneMenu id="${attrId}"
                             value="${'#{' + attrValue + '}'}"
                             converter="${attribute.converter}">
                <f:selectItem itemLabel="Select ${attribute.titleCaseName}" noSelectionOption="true" />
                <f:selectItems value="${'#{' + attribute.bean + '.all' + attribute.pluralType + '}'}"
                               var="${attrId}"
                               itemValue="${'#{' + attrId + '}'}"
                               itemLabel="${'#{' + attrId + '}'}" />
            </h:selectOneMenu>
            <#elseif attribute.type == "LocalDateTime" || attribute.type == "LocalDate">
            <h:inputText id="${attrId}" 
                         value="${'#{' + attrValue + '}'}" 
                         converter="${attribute.converter}">
                <f:passThroughAttribute name="type" value="${(attribute.type == 'LocalDateTime')?string('datetime-local', 'date')}" />
            </h:inputText>
            <#elseif attribute.isNumber()>
            <h:inputText id="${attrId}" value="${'#{' + attrValue + '}'}" >
                <f:passThroughAttribute name="type" value="number" />
            </h:inputText>
            <#else>
            <h:inputText id="${attrId}" value="${'#{' + attrValue + '}'}" />
            </#if>
        </#if>
        </#list>
        </h:panelGrid>
        <#list entity.attributes as attribute>
        <#if attribute.primaryKey && attribute.type != "String">
        <h:inputHidden id="${attribute.getName()}" 
                       value="${'#' + '{'+ beanName + '.' + entityNameLowerCase + '.' + attribute.name + '}'}" />
        </#if>
        </#list>

        <h:commandButton value="Save" action="${'#' + '{'+ beanName + '.save}'}" styleClass="btn btn-primary mt-3" />

        <h3 class="mt-5">${entity.getTitle()} List</h3>
        <h:dataTable value="${'#' + '{'+ beanName + '.all'+entityNameTitleCasePluralize+'}'}" var="${entityNameLowerCase}" border="1" styleClass="table table-striped table-bordered">
        <#list entity.attributes as attribute>
            <#if !attribute.multi>
            <h:column>
                <f:facet name="header">${attribute.getStartCaseName()}</f:facet>
                ${'#' + '{' + entityNameLowerCase + '.' + attribute.name + '}'}
            </h:column>
            </#if>
        </#list>
            <h:column>
                <f:facet name="header">Actions</f:facet>
                <h:commandLink value="Edit" action="${'#' + '{'+ beanName + '.edit(' + entityNameLowerCase + ')}'}" styleClass="btn btn-link p-0" />
                |
                <h:commandLink value="Delete" action="${'#' + '{'+ beanName + '.remove(' + entityNameLowerCase + '.' + pkName + ')}'}"
                               onclick="return confirm('Delete this ${entityNameLowerCase}?');" 
                               styleClass="btn btn-link p-0 text-danger" />
            </h:column>
        </h:dataTable>
    </ui:define>
</ui:composition>
