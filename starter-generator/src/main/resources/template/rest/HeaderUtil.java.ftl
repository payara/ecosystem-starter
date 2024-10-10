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

import jakarta.ws.rs.core.Response.ResponseBuilder;

/**
 * Utility class for HTTP headers creation.
 *
 */
public class HeaderUtil {

    public static ResponseBuilder createAlert(ResponseBuilder builder, String message, String param) {
        builder.header("X-app-alert", message);
        builder.header("X-app-params", param);
        return builder;
    }

    public static ResponseBuilder createEntityCreationAlert(ResponseBuilder builder, String entityName, String param) {
        return createAlert(builder, "${frontendAppName}." + entityName + ".created", param);
    }

    public static ResponseBuilder createEntityUpdateAlert(ResponseBuilder builder, String entityName, String param) {
        return createAlert(builder, "${frontendAppName}." + entityName + ".updated", param);
    }

    public static ResponseBuilder createEntityDeletionAlert(ResponseBuilder builder, String entityName, String param) {
        return createAlert(builder, "${frontendAppName}." + entityName + ".deleted", param);
    }

    public static ResponseBuilder createFailureAlert(ResponseBuilder builder, String entityName, String errorKey, String defaultMessage) {
        builder.header("X-app-error", "error." + errorKey);
        builder.header("X-app-params", entityName);
        return builder;
    }
}
