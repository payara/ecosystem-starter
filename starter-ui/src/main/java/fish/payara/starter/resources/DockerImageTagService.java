/*
 *
 * Copyright (c) 2026 Payara Foundation and/or its affiliates. All rights reserved.
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

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service that checks Docker Hub tag availability for Payara images.
 * Results are cached to avoid repeated network calls.
 */
@ApplicationScoped
public class DockerImageTagService {

    private static final Logger LOGGER = Logger.getLogger(DockerImageTagService.class.getName());
    private static final String DOCKER_HUB_TAG_URL = "https://hub.docker.com/v2/repositories/payara/micro/tags/%s";
    private static final long CACHE_TTL_MILLIS = TimeUnit.HOURS.toMillis(24);

    private final Map<String, CachedResult> tagCache = new ConcurrentHashMap<>();

    /**
     * Checks whether a JDK-specific Docker image tag exists for the given
     * Payara version and Java version (e.g. {@code 6.2024.9-jdk11}).
     *
     * @param payaraVersion the Payara version string (e.g. {@code 6.2024.9})
     * @param javaVersion   the Java version (e.g. {@code 11}, {@code 17}, {@code 21})
     * @return {@code true} if the tag exists on Docker Hub, {@code false} otherwise
     */
    public boolean isJdkTagAvailable(String payaraVersion, int javaVersion) {
        String tag = payaraVersion + "-jdk" + javaVersion;
        CachedResult cached = tagCache.get(tag);
        if (cached != null && !cached.isExpired()) {
            return cached.exists;
        }
        boolean exists = fetchTagExists(tag);
        tagCache.put(tag, new CachedResult(exists));
        return exists;
    }

    private boolean fetchTagExists(String tag) {
        String urlStr = String.format(DOCKER_HUB_TAG_URL, tag);
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            boolean exists = responseCode == HttpURLConnection.HTTP_OK;
            LOGGER.log(Level.FINE, "Docker Hub tag {0} exists: {1}", new Object[]{tag, exists});
            return exists;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to check Docker Hub tag availability for {0}, assuming tag exists: {1}",
                    new Object[]{tag, e.getMessage()});
            return true;
        }
    }

    private static class CachedResult {

        final boolean exists;
        final long timestamp;

        CachedResult(boolean exists) {
            this.exists = exists;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MILLIS;
        }
    }
}
