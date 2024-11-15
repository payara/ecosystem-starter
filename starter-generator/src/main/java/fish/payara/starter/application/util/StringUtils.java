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

import java.util.function.Predicate;

public final class StringUtils {

    //(\\s+) space
    //(?<=[a-z])(?=[A-Z]) => eclipseRCPExt -> eclipse / RCPExt
    //(?<=[A-Z])(?=[A-Z][a-z]) => eclipseRCPExt -> eclipse / RCP / Ext
    public final static String NATURAL_TEXT_SPLITTER = "(\\s+)|(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])";

    public static String firstLower(String string) {
        if (isBlank(string)) {
            return EMPTY;
        }
        boolean makeFirstLower = string.length() < 2 || (!Character.isUpperCase(string.charAt(1)));
        return makeFirstLower ? string.substring(0, 1).toLowerCase() + string.substring(1) : string;
    }

    public static String firstUpper(String string) {
        if (isBlank(string)) {
            return EMPTY;
        }
        return string.length() > 1 ? string.substring(0, 1).toUpperCase() + string.substring(1) : string.toUpperCase();
    }

    public static String titleCase(String string) {
        if (isBlank(string)) {
            return EMPTY;
        }
        return string.length() > 1 ? string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase() : string.toUpperCase();
    }

    /**
     * Converts `string` to [start case]
     *
     * startCase('--foo-bar--') => 'Foo Bar' startCase('fooBar') => 'Foo Bar'
     * startCase('__FOO_BAR__') => 'FOO BAR'
     *
     * @param content
     * @return
     *
     */
    public static String startCase(String content) {
        StringBuilder result = new StringBuilder();
        content = content.replaceFirst("[^a-zA-Z0-9]+", EMPTY);
        for (String word : content.replaceAll("[^a-zA-Z0-9]", " ").split(NATURAL_TEXT_SPLITTER)) {
            result.append(firstUpper(word)).append(" ");
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    /**
     * Converts `string` to [snake case] Foo Bar > 'foo_bar', fooBar >
     * 'foo_bar', --FOO-BAR-- > 'foo_bar'
     *
     * @param content
     * @return
     */
    public static String snakeCase(String content) {
        StringBuilder result = new StringBuilder();
        content = content.replaceFirst("[^a-zA-Z0-9]+", EMPTY);
        for (String word : content.replaceAll("[^a-zA-Z0-9]", " ").split(NATURAL_TEXT_SPLITTER)) {
            result.append(word.toLowerCase()).append("_");
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    /**
     * Converts `string` to [kebab case] 'Foo Bar > 'foo-bar', 'fooBar' >
     * 'foo-bar', '__FOO_BAR__' > 'foo-bar'
     *
     * @param content
     * @return
     *
     */
    public static String kebabCase(String content) {
        StringBuilder result = new StringBuilder();
        content = content.replaceFirst("[^a-zA-Z0-9]+", EMPTY);
        for (String word : content.replaceAll("[^a-zA-Z0-9]", " ").split(NATURAL_TEXT_SPLITTER)) {
            result.append(word.toLowerCase()).append("-");
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    /**
     * Removes leading and trailing whitespace or specified characters from
     * `string`.
     *
     * _.trim(' abc ', ' '); // => 'abc'
     *
     * _.trim('_abc_', '_'); // => 'abc'
     *
     * @param content
     * @param trimmer
     * @return
     *
     */
    public static String trim(String content, char trimmer) {
        char value[] = content.toCharArray();
        int len = value.length;
        int st = 0;
        char[] val = value;
        /* avoid getfield opcode */

        while ((st < len) && (val[st] == trimmer)) {
            st++;
        }
        while ((st < len) && (val[len - 1] == trimmer)) {
            len--;
        }
        return ((st > 0) || (len < value.length)) ? content.substring(st, len) : content;
    }

    /**
     * Converts `string` to [camel case]
     *
     * 'Foo Bar > 'fooBar', '--foo-bar--' > 'fooBar', '__FOO_BAR__ > 'fooBar'
     *
     * @param content
     * @return
     */
    public static String camelCase(String content) {
        StringBuilder result = new StringBuilder();
//        content = content.replaceFirst("[^a-zA-Z0-9]+", EMPTY);//issue job-history => jobhistory
        int i = 0;
        for (String word : content.replaceAll("[^a-zA-Z0-9]", " ").split(NATURAL_TEXT_SPLITTER)) {
            word = word.toLowerCase();
            if (i == 0) {
                result.append(word);
            } else {
                result.append(firstUpper(word));
            }
            i++;
        }
        return result.toString();
    }

    /**
     * Convert to natural language BankAccount => Bank Account Bank_Account =>
     * Bank_Account
     *
     * @param input
     * @return
     *
     */
    public static String toNatural(String input) {
        String natural = EMPTY;
        Character lastChar = null;
        for (Character curChar : input.toCharArray()) {
            if (lastChar == null) {
                // First character
                lastChar = Character.toUpperCase(curChar);
                natural = natural + lastChar;

            } else {
                if (Character.isLowerCase(lastChar)
                        && (Character.isUpperCase(curChar)) || Character.isDigit(curChar)) {
                    natural = natural + " " + curChar;
                } else {
                    natural = natural + curChar;
                }
                lastChar = curChar;
            }

        }
        return natural;
    }

    /**
     * Convert to constant BankAccount => BANK_ACCOUNT Bank_Account =>
     * BANK_ACCOUNT
     *
     * @param input
     * @return
     *
     */
    public static String toConstant(String input) {
        String constant = EMPTY;
        Character lastChar = null;
        for (Character curChar : input.toCharArray()) {
            if (lastChar == null) {
                // First character
                lastChar = Character.toUpperCase(curChar);
                constant = constant + lastChar;

            } else {
                if (Character.isLowerCase(lastChar)
                        && (Character.isUpperCase(curChar) || Character.isDigit(curChar))) {
                    constant = constant + '_' + curChar;
                } else {
                    constant = constant + Character.toUpperCase(curChar);
                }
                lastChar = curChar;
            }

        }
        return constant;
    }

    public static String getNext(String name, Predicate<String> checkExist) {
        return getNext(name, checkExist, false);
    }

    public static String getNext(String name, Predicate<String> checkExist, boolean increment) {
        int index = 0;
        String nextName;
        if (increment) {
            nextName = name + ++index;
        } else {
            nextName = name;
        }
        boolean isExist = true;
        while (isExist) {
            if (checkExist.test(nextName)) {
                isExist = true;
                nextName = name + ++index;
            } else {
                return nextName;
            }
        }
        return nextName;
    }

    public static String singularize(String name) {
        return Inflector.getInstance().singularize(name);
    }

    public static final String COLLECTION = "Collection";

    public static String pluralize(String name) {
        if(name.endsWith("s")) {
            return name;
        }
        String pluralName = Inflector.getInstance().pluralize(name);

        if (name.equals(pluralName)) {
            return name + COLLECTION;
        } else {
            return pluralName;
        }
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static boolean compareNonWhitespaces(String v1, String v2) {
        return v1 != null && v2 != null
                && v1.replaceAll("\\s+", "")
                        .equals(v2.replaceAll("\\s+", ""));
    }

    public static final String SPACE = " ";

    public static final String EMPTY = "";

    public static final String LF = "\n";

    public static final String CR = "\r";

    private static final int PAD_LIMIT = 8192;

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        // Step-wise comparison
        final int length = cs1.length();
        for (int i = 0; i < length; i++) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsIgnoreCase(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        return regionMatches(cs1, true, 0, cs2, 0, cs1.length());
    }

    public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        final int len = searchStr.length();
        final int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (regionMatches(str, true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    public static String deleteWhitespace(final String str) {
        if (isEmpty(str)) {
            return str;
        }
        final int sz = str.length();
        final char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }

    public static String trim(final String str) {
        return str == null ? null : str.trim();
    }

    public static String leftPad(final String str, final int size) {
        return leftPad(str, size, ' ');
    }

    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    public static String repeat(final char ch, final int repeat) {
        if (repeat <= 0) {
            return EMPTY;
        }
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
            final CharSequence substring, final int start, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;

        // Extract these first so we detect NPEs the same as the java.lang.String version
        final int srcLen = cs.length() - thisStart;
        final int otherLen = substring.length() - start;

        // Check for invalid parameters
        if (thisStart < 0 || start < 0 || length < 0) {
            return false;
        }

        // Check that the regions are long enough
        if (srcLen < length || otherLen < length) {
            return false;
        }

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!ignoreCase) {
                return false;
            }

            // The same check as in String.regionMatches():
            if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
                    && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }

        return true;
    }

    public static String toCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = true;

        for (char ch : input.toCharArray()) {
            if (ch == '_' || ch == '-') {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                result.append(Character.toUpperCase(ch));
                nextUpperCase = false;
            } else {
                result.append(Character.toLowerCase(ch));
            }
        }
        return result.toString();
    }

}
