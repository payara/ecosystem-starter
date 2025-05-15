<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="jakarta.faces.facelets"
      xmlns:f="jakarta.faces.core"
      xmlns:h="jakarta.faces.html">
<h:head>
    <title>${entity.getTitle()}</title>
</h:head>
<h:body>
    <h:form>
        <h3>${entity.getTitle()}</h3>
        <p>${entity.getDescription()}</p>
        <h:panelGrid columns="2">
            <#list entity.attributes as attribute>
                <#if !attribute.multi>
                    <h:outputLabel for="${attribute.getName()}" value="${attribute.getStartCaseName()}:" />
                    <h:inputText id="${attribute.getName()}" value="${'#' + '{'+ beanName + '.' + entityNameLowerCase + '.' + attribute.name + '}'}" />
                </#if>
            </#list>
        </h:panelGrid>

        <h:commandButton value="Save" action="${'#' + '{'+ beanName + '.save}'}" />

        <h3>${entity.getTitle()} List</h3>
        <h:dataTable value="${'#' + '{'+ beanName + '.all'+entityNameTitleCasePluralize+'}'}" var="${entityNameLowerCase}" border="1">
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
                <h:commandLink value="Edit" action="${'#' + '{'+ beanName + '.edit(' + entityNameLowerCase + ')}'}" />
                |
                <h:commandLink value="Delete" action="${'#' + '{'+ beanName + '.remove(' + entityNameLowerCase + '.' + pkName + ')}'}"
                               onclick="return confirm('Delete this ${entityNameLowerCase}?');" />
            </h:column>
        </h:dataTable>
    </h:form>
</h:body>
</html>
