/*
 *  *******************************************************************************
 *  Copyright (c) 2023-24 Harman International
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  *******************************************************************************
 */

package org.eclipse.ecsp.auth.lib.util;

import org.apache.commons.lang3.StringUtils;

/**
 * The UniqueDeviceIdEncoder class provides methods to encode and decode unique device IDs.
 */
public class UniqueDeviceIdEncoder {
    //'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'T', 'U', 'V', 'W', 'X', 'Y', '3', '4',
    // '6', '7', '8', '9'
    private static final char[] CHARSET_BASE = {'P', 'B', 'V', 'D', 'F', 'G',
        '7', 'H', 'K', '8', 'M', 'N', 'E', 'Q', '6', 'R', 'C', 'T', '9',
        'U', 'W', 'X', 'Y', '3', 'J', '4'};
    private static final char NULL_CHAR_REPRESENTATION = 'A';
    private static final int RADIX = CHARSET_BASE.length;

    /**
     * The `UniqueDeviceIdEncoder` class is a utility class that provides methods for encoding unique device IDs.
     * It is responsible for encoding the unique device ID using a specific algorithm.
     * This class cannot be instantiated as it only contains static methods.
     */
    private UniqueDeviceIdEncoder() {

    }

    /**
     * Encodes the decimal device ID into a string representation.
     *
     * @param decimalDeviceId the unique device ID in the base 10 representation
     * @param minimumLength   the minimum length of the encoded string
     * @return the encoded string representation of the device ID
     */
    public static String encode(long decimalDeviceId, int minimumLength) {
        char[] charSet = CHARSET_BASE;
        int radix = charSet.length;
        StringBuilder inverseConvertedStr = new StringBuilder();

        while (decimalDeviceId >= radix) {
            int remainder = (int) (decimalDeviceId % radix);
            inverseConvertedStr.append(charSet[remainder]);
            decimalDeviceId = decimalDeviceId / radix;
        }
        inverseConvertedStr.append(charSet[(int) decimalDeviceId]);
        return pad(inverseConvertedStr.reverse().toString(), minimumLength);

    }

    /**
     * Decodes the encoded string into a decimal device ID.
     *
     * @param encodedString the encoded string
     * @return the decoded number in the base 10 representation
     */
    public static long decode(String encodedString) {
        char[] encodedChars = encodedString.toCharArray();
        long decodedDecimal = 0;
        int position = 0;

        for (int i = encodedChars.length - 1; i >= 0; i--) {
            decodedDecimal += (Math.pow(RADIX, position) * findIndex(encodedChars[i],
                CHARSET_BASE));
            position++;
        }
        return decodedDecimal;
    }

    /**
     * Pads the given encoded string with a specified minimum length.
     *
     * @param encodedStr The encoded string to be padded.
     * @param minLength The minimum length of the padded string.
     * @return The padded string.
     */
    private static String pad(String encodedStr, int minLenght) {
        return (encodedStr.length() >= minLenght) ? encodedStr : StringUtils
            .repeat(NULL_CHAR_REPRESENTATION,
                minLenght - encodedStr.length())
            + encodedStr;
    }

    /**
     * Finds the index of a specified character in a character array.
     *
     * @param toFind the character to find
     * @param charSet the character array to search in
     * @return the index of the character in the array, or 0 if not found
     */
    private static int findIndex(char toFind, char[] charSet) {
        for (int i = 0; i < charSet.length; i++) {
            if (charSet[i] == toFind) {
                return i;
            }
        }
        return 0;
    }

}
