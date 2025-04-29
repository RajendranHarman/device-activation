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

package org.eclipse.ecsp.util;

import org.eclipse.ecsp.auth.lib.util.DeviceActivationUtil;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for DeviceActivationUtil.
 */
public class DeviceActivationUtilTest {
    // Story 546176, 550679, 554403 - Enabled AAD Authentication method (MAC)
    byte[] associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
    private static final long ID = 123L;
    private static final int VALUE_2 = 2;
    private static final int VALUE_3 = 3;
    private static final int VALUE_4 = 4;
    private static final int VALUE_6 = 6;
    private static final int VALUE_7 = 7;
    private static final int VALUE_8 = 8;

    @Test
    public void generateDeviceIdTest() {
        String prefix = "HU";
        String response = DeviceActivationUtil.generateDeviceId(prefix, ID);
        assertNotNull(response);
    }

    @Test
    public void getPasscodeTest() {
        String response = DeviceActivationUtil.getPasscode();
        assertNotNull(response);
    }

    @Test
    public void decryptTest1() {
        byte[] key = {1, VALUE_2, VALUE_3};
        byte[] encrypted = {1, VALUE_2, VALUE_3};
        /* Story 465232 - Implemented GCM cipher mode to fix the following major Sonar vulnerabilities
         ** The cipher does not provide data integrity
         ** The cipher is susceptible to padding oracle attacks
         */
        // Story 546176, 550679, 554403 - Enabled AAD Authentication method (MAC)
        assertThrows(InvalidKeyException.class, () ->
            DeviceActivationUtil.decrypt(key, encrypted, associatedData));
    }

    @Test
    public void decryptTest2() {
        byte[] key = {'[', 'B', '@', VALUE_4, VALUE_8, 'a', VALUE_2, VALUE_4, VALUE_2, 'c', 'e', 'w', VALUE_3,
            VALUE_6, 'f', 'a'};
        byte[] encrypted = {'[', 'B', '@', 1, 'e', VALUE_4, 'a', VALUE_7, 'd', 'd', VALUE_4, 'w', VALUE_3,
            VALUE_6, 'f', 'a'};
        // Story 546176, 550679, 554403 - Enabled AAD Authentication method (MAC)
        assertThrows(BadPaddingException.class, () ->
            DeviceActivationUtil.decrypt(key, encrypted, associatedData)
        );
    }

    @Test
    public void decryptTest3() {
        byte[] key = {'[', 'B', '@', VALUE_4, VALUE_8, 'a', VALUE_2, VALUE_4, VALUE_2, 'c', 'e', 'w', VALUE_3,
            VALUE_6, 'f', 'a'};
        byte[] encrypted = {'[', 'B', '@', 1, 'e', VALUE_4, 'a', VALUE_7, 'd', 'd', VALUE_4, 'w', VALUE_3,
            VALUE_6, 'f', 'a', 't', VALUE_6, VALUE_7, 'u'};
        // Story 546176, 550679, 554403 - Enabled AAD Authentication method (MAC)
        assertThrows(IllegalBlockSizeException.class, () ->
            DeviceActivationUtil.decrypt(key, encrypted, associatedData)
        );
    }

    @Test
    public void checkQualifierTest1() {
        String vin = "TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0";
        String qualifier =
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=";
        String serialNumber = "523749811223666";
        String secretKey = "r$27T30**";
        // Story 546176, 550679, 554403 - Enabled AAD Authentication method (MAC)
        DeviceActivationUtil.checkQualifier(vin, serialNumber, qualifier, secretKey, "Yes");
    }

    @Test
    public void checkQualifierTest2() {
        String vin = "TEST";
        String qualifier =
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=";
        String serialNumber = "5";
        String secretKey = "r$27T30**";
        // Story 546176, 550679, 554403 - Enabled AAD Authentication method (MAC)
        String aad = null;
        DeviceActivationUtil.checkQualifier(vin, serialNumber, qualifier, secretKey, aad);
    }

    @Test
    public void checkQualifierTest3() {
        String vin = "TEST";
        String qualifier =
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=";
        String serialNumber = "5";
        String secretKey = "r$27T30**";
        // Story 546176, 550679, 554403 - Enabled AAD Authentication method (MAC)
        String aad = "Yes";
        DeviceActivationUtil.checkQualifier(vin, serialNumber, qualifier, secretKey, aad);
    }

    @Test
    public void generateDeviceAssociationCodeTest() {
        String response = DeviceActivationUtil.generateDeviceAssociationCode(ID);
        assertNotNull(response);
    }

    @Test
    public void getRandomAlphanumericStringTest() {
        String response = DeviceActivationUtil.getRandomAlphanumericString(VALUE_8);
        assertNotNull(response);
    }
}