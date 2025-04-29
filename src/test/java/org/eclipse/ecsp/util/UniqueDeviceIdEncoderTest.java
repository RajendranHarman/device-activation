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

import org.eclipse.ecsp.auth.lib.util.UniqueDeviceIdEncoder;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for UniqueDeviceIdEncoder.
 */
public class UniqueDeviceIdEncoderTest {
    private static final long DEVICE_ID = 12345L;
    private static final int MIN_LENGTH = 2;
    private static final int EXPECTED_RESULT = 421876;

    @InjectMocks
    private UniqueDeviceIdEncoder uniqueDeviceIdEncoder;

    @Test
    public void encodeTest() {
        assertNotNull(uniqueDeviceIdEncoder.encode(DEVICE_ID, MIN_LENGTH));
    }

    @Test
    public void decodeTest() {
        String encodedString = "JAVA";
        assertEquals(EXPECTED_RESULT, uniqueDeviceIdEncoder.decode(encodedString));
    }
}