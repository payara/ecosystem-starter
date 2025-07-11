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
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
<#if model.importPrefix == "jakarta" >
                xmlns:pt="http://xmlns.jakarta.org/jsf/passthrough"
                xmlns:ui="jakarta.faces.facelets"
                xmlns:f="jakarta.faces.core"
                xmlns:h="jakarta.faces.html">
 <#else>
                xmlns:pt="http://xmlns.jcp.org/jsf/passthrough"
                xmlns:ui="http://java.sun.com/jsf/facelets"
                xmlns:f="http://java.sun.com/jsf/core"
                xmlns:h="http://java.sun.com/jsf/html">
</#if>

    <nav class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0" role="navigation">
        <h:outputLink styleClass="navbar-brand col-sm-3 col-md-2 me-0 px-3" value="#">
            ${model.getLongTitle()}
        </h:outputLink>

        <h:inputText styleClass="form-control form-control-dark w-100" 
                     pt:placeholder="Search" 
                     pt:aria-label="Search" />

        <ul class="navbar-nav px-3">
            <li class="nav-item text-nowrap">
                <#noparse><h:commandLink action="#{userBean.logout}" styleClass="nav-link" value="Sign out" /></#noparse>
            </li>
        </ul>
    </nav>

</ui:composition>