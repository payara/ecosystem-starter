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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MermaidUtil {

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("^\\s+([a-zA-Z]+)\\s+([a-zA-Z]+)\\s+([a-zA-Z]+)\\s*(PK|FK)?$");
    private static final Pattern HYPHEN_TO_UNDERSCORE_PATTERN = Pattern.compile("\\b([a-zA-Z]+)-([a-zA-Z]+)\\b");
    private static final Pattern MERMAID_RELATIONSHIP_PATTERN = Pattern.compile("^\\s*(\\w+)\\s+([|o}]{1,2}--[|o{]{1,2})\\s*(\\w+)\\s*:\\s*(.+)$");

    public static String filterNoise(String mermaidString) {
        mermaidString = mermaidString.replaceAll("\\s*//.*", "");
        return mermaidString.lines()
                .map(MermaidUtil::convertAttributeSplit)
                .map(MermaidUtil::convertHyphenToUnderscore)
                .map(MermaidUtil::convertMermaidRelationship)
                .collect(Collectors.joining("\n"));
    }

    private static String convertAttributeSplit(String input) {
        Matcher matcher = ATTRIBUTE_PATTERN.matcher(input);

        if (matcher.find()) {
            String dataType = matcher.group(1);
            String var1 = matcher.group(2);
            String var2 = matcher.group(3);
            if (var2.equals("PK") || var2.equals("FK")) {
                return input;
            }
            String constraint = matcher.group(4) != null ? " " + matcher.group(4) : "";
            String camelCaseName = toCamelCase(var1, var2);

            // Format the output
            return "        " + String.format("%s %s%s", dataType, camelCaseName, constraint).trim();
        }
        return input;
    }

    private static String convertHyphenToUnderscore(String input) {
        Matcher matcher = HYPHEN_TO_UNDERSCORE_PATTERN.matcher(input);
        StringBuffer processedInput = new StringBuffer();

        while (matcher.find()) {
            String hyphenatedWord = matcher.group();
            String converted = hyphenatedWord.replace("-", "_");
            matcher.appendReplacement(processedInput, converted);
        }
        matcher.appendTail(processedInput);
        return processedInput.toString();
    }

    public static String convertMermaidRelationship(String input) {
        Matcher matcher = MERMAID_RELATIONSHIP_PATTERN.matcher(input);

        if (matcher.find()) {
            String leftValue = matcher.group(1);
            String relationshipSymbol = matcher.group(2);
            String rightValue = matcher.group(3);
            String meaning = matcher.group(4);

            String camelCaseMeaning = toCamelCase(meaning);

            return "    " + String.format("%s %s %s : %s", leftValue, relationshipSymbol, rightValue, camelCaseMeaning);
        }
        return input;
    }

    private static String toCamelCase(String phrase) {
        String[] words = phrase.split(" ");
        StringBuilder camelCasePhrase = new StringBuilder(words[0].toLowerCase());

        for (int i = 1; i < words.length; i++) {
            camelCasePhrase.append(words[i].substring(0, 1).toUpperCase())
                    .append(words[i].substring(1).toLowerCase());
        }

        return camelCasePhrase.toString();
    }

    private static String toCamelCase(String var1, String var2) {
        return var1.toLowerCase() + Character.toUpperCase(var2.charAt(0)) + var2.substring(1).toLowerCase();
    }
}
