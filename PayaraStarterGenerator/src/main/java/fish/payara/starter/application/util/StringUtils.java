/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fish.payara.starter.application.util;

/**
 *
 * @author Gaurav Gupta
 */
public class StringUtils {

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
