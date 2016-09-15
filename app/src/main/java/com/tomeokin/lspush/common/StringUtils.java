/*
 * Copyright 2016 TomeOkin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tomeokin.lspush.common;

import java.util.Arrays;
import java.util.regex.Pattern;

public class StringUtils {
    private StringUtils() {}

    private static final Pattern BLANK = Pattern.compile("\\s+");

    public static String removeBlank(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        return BLANK.matcher(charSequence.toString().trim()).replaceAll(" ");
    }

    /**
     * Compare two string without considering spaces and case of string
     *
     * @param one one string
     * @param target target string
     * @param start first compare character index
     * @return true equal or false not equal
     */
    public static boolean isEqualWithNature(String one, String target, int start) {
        int length = one.length();
        int targetLength = target.length();
        if (start + targetLength > length) {
            return false;
        }

        int oneOffset = start;
        int index = 0;
        while (index < targetLength) {
            if (!Character.isWhitespace(target.charAt(index))) {
                while (index + oneOffset < length && Character.isWhitespace(one.charAt(index + oneOffset))) {
                    oneOffset++;
                }
                if (index + oneOffset >= length) {
                    return false;
                }
                if (Character.toLowerCase(one.charAt(index + oneOffset)) != Character.toLowerCase(
                    target.charAt(index))) {
                    return false;
                }
            }
            index++;
        }

        return true;
    }

    public static int indexDigest(CharSequence sequence) {
        int length = sequence.length();
        for (int i = 0; i < length; i++) {
            if (Character.isDigit(sequence.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int indexLowerLetter(CharSequence sequence) {
        int length = sequence.length();
        for (int i = 0; i < length; i++) {
            if (Character.isLowerCase(sequence.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int indexUpperLetter(CharSequence sequence) {
        int length = sequence.length();
        for (int i = 0; i < length; i++) {
            if (Character.isUpperCase(sequence.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int indexSpecial(CharSequence special, CharSequence sequence) {
        final char[] chars = special.toString().toCharArray();
        Arrays.sort(chars);
        return indexSpecial(chars, sequence);
    }

    /**
     * @param chars sort characters
     */
    public static int indexSpecial(char[] chars, CharSequence sequence) {
        int length = sequence.length();
        for (int i = 0; i < length; i++) {
            if (Arrays.binarySearch(chars, sequence.charAt(i)) >= 0) {
                return i;
            }
        }
        return -1;
    }
}
