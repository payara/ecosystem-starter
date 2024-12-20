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
package fish.payara.starter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.io.IOException;
import java.time.Instant;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

/**
 * JAX-RS Filter for rate limiting based on IP address. Blocks requests if the
 * client exceeds the configured API call limit within the specified time
 * window.
 *
 * Author: Gaurav Gupta
 */
@Provider
@fish.payara.starter.RateLimited
@Priority(Priorities.AUTHORIZATION)
public class IpRateLimitFilter implements ContainerRequestFilter {

    private static final int SC_TOO_MANY_REQUESTS = 429;
    private final int limit;
    private final long timeWindow;
    private final ConcurrentHashMap<String, Deque<Long>> ipAccessLogs = new ConcurrentHashMap<>();

    @Resource
    private ManagedScheduledExecutorService scheduler;

    public IpRateLimitFilter() {
        Config config = ConfigProvider.getConfig();
        this.limit = config.getOptionalValue("rate.limit", Integer.class).orElse(100); // Default to 100
        this.timeWindow = config.getOptionalValue("rate.time.window", Long.class).orElse(3600_000L); // Default to 1 hour
    }

    /**
     * Start the cleanup task periodically when the application is deployed.
     */
    @PostConstruct
    public void startCleanupTask() {
        scheduler.scheduleAtFixedRate(this::cleanOldEntries, 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String clientIp = getClientIp(requestContext);

        if (clientIp == null || clientIp.isEmpty()) {
            requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Unable to determine client IP")
                    .build());
            return;
        }

        if (isIpBlocked(clientIp)) {
            requestContext.abortWith(Response.status(SC_TOO_MANY_REQUESTS)
                    .entity("Rate limit exceeded")
                    .build());
            return;
        }

        logApiCall(clientIp);
    }

    private boolean isIpBlocked(String clientIp) {
        Deque<Long> timestamps = ipAccessLogs.getOrDefault(clientIp, new ConcurrentLinkedDeque<>());
        long now = Instant.now().toEpochMilli();
        while (!timestamps.isEmpty() && now - timestamps.peekFirst() > timeWindow) {
            timestamps.pollFirst();
        }
        return timestamps.size() >= limit;
    }

    private void logApiCall(String clientIp) {
        long now = Instant.now().toEpochMilli();
        ipAccessLogs.computeIfAbsent(clientIp, key -> new ConcurrentLinkedDeque<>()).addLast(now);
    }

    private String getClientIp(ContainerRequestContext requestContext) {
        String ip = requestContext.getHeaderString("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = requestContext.getUriInfo().getRequestUri().getHost();
        }
        return ip;
    }

    /**
     * Scheduled cleanup to remove old IP access logs entries periodically.
     */
    private void cleanOldEntries() {
        long now = Instant.now().toEpochMilli();

        Iterator<Map.Entry<String, Deque<Long>>> iterator = ipAccessLogs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Deque<Long>> entry = iterator.next();
            Deque<Long> timestamps = entry.getValue();

            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > timeWindow) {
                timestamps.pollFirst();
            }

            // Remove the entry if it has no recent timestamps left
            if (timestamps.isEmpty()) {
                iterator.remove();
            }
        }
    }

}
