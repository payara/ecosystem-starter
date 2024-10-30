package fish.payara.starter.application.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MermaidUtil {

    public static String filterNoise(String mermaidString) {
        mermaidString = mermaidString.replaceAll("\\s*//.*", "");
        StringBuilder sb = new StringBuilder();
        for (String input : mermaidString.split("\n")) {
            sb.append(convertAttribute(input)).append('\n');
        }
        return sb.toString();
    }

    private static String convertAttribute(String input) {
        // Regex to match the pattern
        String regex = "^\\s+([a-zA-Z]+)\\s+([a-zA-Z]+)\\s+([a-zA-Z]+)\\s*(PK|FK)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String dataType = matcher.group(1);
            String var1 = matcher.group(2);
            String var2 = matcher.group(3);
            if(var2.equals("PK") || var2.equals("FK")) {
                return input;
            }
            String constraint = matcher.group(4) != null ? " " + matcher.group(4) : "";

            // Convert variable names to camelCase
            String camelCaseName = toCamelCase(var1, var2);

            // Format the output
            return "        "+ String.format("\t\t%s %s%s", dataType, camelCaseName, constraint).trim();
        }
        return input; // return the original input if it doesn't match
    }

    private static String toCamelCase(String var1, String var2) {
        // Convert var1 to lower case and var2 to capitalized
        return var1.toLowerCase() + var2.substring(0, 1).toUpperCase() + var2.substring(1).toLowerCase();
    }
}
