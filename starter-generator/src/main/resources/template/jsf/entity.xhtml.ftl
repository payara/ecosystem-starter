<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:ui="jakarta.faces.facelets"
                xmlns:f="jakarta.faces.core"
                xmlns:h="jakarta.faces.html"
                template="/WEB-INF/layout/template.xhtml">

    <ui:define name="content">
        <h:form>
            <h3>${entity.getTitle()}</h3>
            <p>${entity.getDescription()}</p>
            <h:panelGrid columns="2" columnClasses="label,value" styleClass="table-form">
            <#list entity.attributes as attribute>
                <#if !attribute.multi>
                <h:outputLabel for="${attribute.getName()}" value="${attribute.getStartCaseName()}:" />
                <#if attribute.type == "LocalDateTime">
                <h:inputText id="${attribute.getName()}" 
                            value="${'#' + '{'+ beanName + '.' + entityNameLowerCase + '.' + attribute.name + '}'}" 
                            converter="localDateTimeConverter">
                    <f:passThroughAttribute name="type" value="datetime-local" />
                </h:inputText>
                <#elseif attribute.type == "LocalDate">
                <h:inputText id="${attribute.getName()}" 
                             value="${'#' + '{' + beanName + '.' + entityNameLowerCase + '.' + attribute.name + '}'}" 
                             converter="localDateConverter">
                    <f:passThroughAttribute name="type" value="date" />
                </h:inputText>
                <#else>
                <h:inputText id="${attribute.getName()}" value="${'#' + '{'+ beanName + '.' + entityNameLowerCase + '.' + attribute.name + '}'}" />
                </#if>
                </#if>
            </#list>
            </h:panelGrid>

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
        </h:form>
    </ui:define>
</ui:composition>
