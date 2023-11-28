/*
 *
 * Copyright (c) 2023 Payara Foundation and/or its affiliates. All rights reserved.
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
package fish.payara.starter.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Path("version")
public class VersionResource {

    private static final long REFRESH_INTERVAL_MILLIS = TimeUnit.DAYS.toMillis(1);
    private static final String VERSION_PATTERN = "<version>([^<]+)</version>";
    private static final String VERSION_METADATA_URL = "https://repo1.maven.org/maven2/fish/payara/distributions/payara/maven-metadata.xml";

    private List<String> cachedVersions;
    private long lastFetchedTime = 0;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getVersions(@QueryParam("jakartaEEVersion") String jakartaEEVersion) {
        if (cachedVersions == null || needsRefresh(REFRESH_INTERVAL_MILLIS)) {
            String versionData = fetchVersionData(VERSION_METADATA_URL);
            List<String> versions = extractVersions(versionData)
                    .stream()
                    .filter(version -> !version.contains("Alpha") && !version.contains("Beta"))
                    .sorted(Collections.reverseOrder())
                    .collect(Collectors.toList());
            lastFetchedTime = System.currentTimeMillis();
            cachedVersions = versions;
        }
        return filterVersionsByJakartaEE(cachedVersions, jakartaEEVersion);
    }

    // Add a method to filter versions based on Jakarta EE version
    private List<String> filterVersionsByJakartaEE(List<String> versions, String jakartaEEVersion) {
        List<String> filteredVersions = new ArrayList<>();

        for (String version : versions) {
            // Check if the version starts with "5.x" when jakartaEEVersion is "8"
            if (jakartaEEVersion.equals("8")) {
                if (version.startsWith("5.")) {
                    filteredVersions.add(version);
                }
            } else {
                // Otherwise, include only "6.x" versions
                if (version.startsWith("6.")) {
                    filteredVersions.add(version);
                }
            }
        }

        return filteredVersions;
    }

    public boolean needsRefresh(long refreshIntervalMillis) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastFetchedTime) >= refreshIntervalMillis;
    }

    private static String fetchVersionData(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder content;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }
            connection.disconnect();

            return content.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private static List<String> extractVersions(String data) {
        List<String> versions = new ArrayList<>();
        Pattern pattern = Pattern.compile(VERSION_PATTERN);
        Matcher matcher = pattern.matcher(data);

        while (matcher.find()) {
            versions.add(matcher.group(1));
        }

        return versions;
    }
}
