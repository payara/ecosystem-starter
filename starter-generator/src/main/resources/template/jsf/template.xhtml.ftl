<#-- 
    DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

    Copyright (c) [2025] Payara Foundation and/or its affiliates. All rights reserved.

    The contents of this file are subject to the terms of either the GNU
    General Public License Version 2 only ("GPL") or the Common Development
    and Distribution License("CDDL") (collectively, the "License").  You
    may not use this file except in compliance with the License.  You can
    obtain a copy of the License at
    https://github.com/payara/Payara/blob/master/LICENSE.txt
    See the License for the specific
    language governing permissions and limitations under the License.

    When distributing the software, include this License Header Notice in each
    file and include the License file at glassfish/legal/LICENSE.txt.

    GPL Classpath Exception:
    The Payara Foundation designates this particular file as subject to the "Classpath"
    exception as provided by the Payara Foundation in the GPL Version 2 section of the License
    file that accompanied this code.

    Modifications:
    If applicable, add the following below the License Header, with the fields
    enclosed by brackets [] replaced by your own identifying information:
    "Portions Copyright [year] [name of copyright owner]"

    Contributor(s):
    If you wish your version of this file to be governed by only the CDDL or
    only the GPL Version 2, indicate your decision by adding "[Contributor]
    elects to include this software in this distribution under the [CDDL or GPL
    Version 2] license."  If you don't indicate a single choice of license, a
    recipient has the option to distribute your version of this file under
    either the CDDL, the GPL Version 2 or to extend the choice of license to
    its licensees as provided above.  However, if you add GPL Version 2 code
    and therefore, elected the GPL Version 2 license, then the option applies
    only if the new code is made subject to such option by the copyright
    holder.
-->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="jakarta.faces.facelets"
      xmlns:f="jakarta.faces.core"
      xmlns:h="jakarta.faces.html">
    <f:view contentType="text/html" encoding="UTF-8">
        <ui:insert name="metadata"></ui:insert>
        <h:head>
            <title>${model.getTitle()}</title>
            <!-- Required meta tags -->
            <meta charset="utf-8"></meta>
            <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"></meta>
            <!-- styles -->
            <link rel="stylesheet"
                  href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
                  crossorigin="anonymous"/>
            <link href="https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@6.6.0/css/fontawesome.min.css"
                  rel="stylesheet"
                  crossorigin="anonymous"/>
            <#noparse><!--<link href="#{request.contextPath}/resources/css/main.css" rel="stylesheet"/>--></#noparse>
            <ui:insert name="headIncludes"></ui:insert>
        </h:head>

        <h:body styleClass="d-flex flex-column h-100">
            <h:panelGroup layout="block" styleClass="header">
                <ui:include src="/WEB-INF/layout/header.xhtml"/>
            </h:panelGroup>

            <h:form id="mainForm">
                <main role="main" class="container-fluid">
                    <div class="row">
                        <nav class="col-md-2 d-none d-md-block bg-light sidebar" id="sidebar">
                            <div class="sidebar-sticky">
                                <ul class="nav flex-column">
                                    <li class="nav-item">
                                        <#noparse>
                                        <h:commandLink action="#{navigationBean.goTo('home')}" styleClass="nav-link #{navigationBean.activePage == 'home' ? 'active' : ''}">
                                            <i class="bi bi-house"></i>
                                            Dashboard
                                            <span class="sr-only">(current)</span>
                                        </h:commandLink>
                                        </#noparse>
                                    </li>
                                    <#list model.entities as entity>
                                        <li class="nav-item">
                                            <h:commandLink action="${'#'}{navigationBean.goTo('${entity.getLowerCaseName()}')}"
                                                styleClass="nav-link ${'#'}{navigationBean.activePage == '${entity.getLowerCaseName()}' ? 'active' : ''}">
                                                 <i class="bi bi-${entity.icon}"></i>
                                                 ${entity.title}
                                             </h:commandLink>
                                        </li>
                                    </#list>
                                </ul>
                                <#noparse>
                                <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
                                    <span>Others</span>
                                    <h:commandLink action="#{navigationBean.goTo('about-us')}" styleClass="d-flex align-items-center text-muted">
                                        <i class="bi bi-plus-circle"></i>
                                    </h:commandLink>
                                </h6>
                                <ul class="nav flex-column mb-2">
                                    <li class="nav-item">
                                        <h:commandLink action="#{navigationBean.goTo('about-us')}" styleClass="nav-link">
                                            <i class="bi bi-info-circle"></i>
                                            About US
                                        </h:commandLink>
                                    </li>
                                </ul>
                                </#noparse>
                            </div>
                        </nav>

                        <section class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4 content" id="content">
                            <ui:insert name="content"/>
                        </section>
                    </div>
                </main>
            </h:form>

            <ui:include src="/WEB-INF/layout/footer.xhtml"/>
            <ui:insert name="bodyIncludes"></ui:insert>
        </h:body>
    </f:view>
</html>