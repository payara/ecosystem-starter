/**
 * Copyright 2013-2022 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 *
 * Copyright (c) 2024 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package fish.payara.starter.application.util;

import javax.lang.model.element.Modifier;

public class Constants {

    public static final String WEB_INF = "WEB-INF";
    public static final String META_INF = "META-INF";
    public static final String LOGGER = "java.util.logging.Logger";

    public static final String RESOURCE_SUFFIX = "Resource";

    public static final String XML_TRANSIENT_ANNOTATION = "XmlTransient";
    public static final String XML_ROOT_ELEMENT_ANNOTATION = "XmlRootElement";
    public static final String XML_ELEMENT_ANNOTATION = "XmlElement";
    public static final String XML_ATTRIBUTE_ANNOTATION = "XmlAttribute";
    public static final String URI_TYPE = "java.net.URI";
    public static final String VOID = "void";

    public static final String COLLECTION = "Collection";
    public static final String COLLECTION_TYPE = "java.util.Collection";
    public static final String COLLECTIONS_TYPE = "java.util.Collections";
    public static final String LIST_TYPE = "java.util.List";
    public static final String SET_TYPE = "java.util.Set";
    public static final String ARRAY_LIST_TYPE = "java.util.ArrayList";
    public static final String HASH_SET_TYPE = "java.util.HashSet";

    public static final Modifier[] PUBLIC = new Modifier[]{Modifier.PUBLIC};
    public static final Modifier[] PRIVATE = new Modifier[]{Modifier.PRIVATE};
    public static final Modifier[] PUBLIC_STATIC_FINAL = new Modifier[]{Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL};

    public static final String LANG_PACKAGE = "java.lang";
    public static final String JAVA_EXT = "java";
    public static final String JAVA_EXT_SUFFIX = ".java";

}
