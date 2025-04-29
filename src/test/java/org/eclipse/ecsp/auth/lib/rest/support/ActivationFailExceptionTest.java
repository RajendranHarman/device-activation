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

package org.eclipse.ecsp.auth.lib.rest.support;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for ActivationFailException.
 */
public class ActivationFailExceptionTest {

    @Test
    public void activationFailExceptionTest1() {
        String message = "Exception Occurred";
        Throwable e = new Throwable();
        ActivationFailException activationFailException = new ActivationFailException(message, e);
        assertEquals(message, activationFailException.getMessage());
        assertEquals(e, activationFailException.getCause());
    }

}