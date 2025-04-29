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

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * CryptographyUtil to generate alpha numeric string.
 * And encrption & decryption of the given input data.
 */
@Component
public class CryptographyUtil {

    private static final char[] RANDOM_ALPHANUMERIC_CHARSET =
      {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
          'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
          'a', 'b', 'c', 'd',
          'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
          'z'};
    private static SecureRandom secureRandom = new SecureRandom();
    private static final int GCM_LENGTH = 8;

    /**
     * Generate random alpha numeric string.
     *
     * @param length the length to determine the random alpha numeric string
     * @return the generated alpha numeric string
     */
    public String getRandomAlphanumericString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM_ALPHANUMERIC_CHARSET[secureRandom.nextInt(RANDOM_ALPHANUMERIC_CHARSET.length)]);
        }
        return sb.toString();
    }

    /**
     * Encrypts the given data using the specified key.
     *
     * @param secretKey the key used for encryption
     * @param inputKey the data to be encrypted
     * @return the encrypted data
     * @throws NoSuchPaddingException if the specified padding scheme is not available
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     * @throws InvalidAlgorithmParameterException if the specified algorithm parameters are invalid
     * @throws InvalidKeyException if the specified key is invalid
     * @throws IllegalBlockSizeException if the block size of the cipher is invalid
     * @throws BadPaddingException if the padding of the cipher is invalid
     */
    public byte[] encrypt(String secretKey, byte[] inputKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        final int Gcm_Tag_Length = 16;
        secretKey = secretKey + "XXXXXXX";
        byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), 
            new GCMParameterSpec(Gcm_Tag_Length * GCM_LENGTH, key));
        return cipher.doFinal(inputKey);
    }

    /**
     * Decrypts the given encrypted data using the specified key.
     *
     * @param secretKey the key used for decryption.
     * @param inputKey the encrypted data to be decrypted
     * @return the decrypted data
     * @throws NoSuchPaddingException if the specified padding scheme is not available
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     * @throws IllegalBlockSizeException if the block size of the cipher is invalid
     * @throws BadPaddingException if the padding of the cipher is invalid
     * @throws InvalidAlgorithmParameterException if the specified algorithm parameters are invalid
     * @throws InvalidKeyException if the specified key is invalid
     */
    public String decrypt(String secretKey, byte[] inputKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException, InvalidKeyException {
        final int Gcm_Tag_Length = 16;
        secretKey = secretKey + "XXXXXXX";
        byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"),
                new GCMParameterSpec(Gcm_Tag_Length * GCM_LENGTH, key));
        return new String(cipher.doFinal(inputKey), StandardCharsets.UTF_8);
    }
}
