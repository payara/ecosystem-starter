/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
