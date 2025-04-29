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

package org.eclipse.ecsp.exception.shared;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for ApiPreConditionFailedException.
 */
public class ApiPreConditionFailedExceptionTest {

    @Test
    public void apiPreConditionFailedExceptionTest1() {
        String message = "Exception Occurred";
        String code = "11";
        String errorMessage = "Error Message";
        ApiPreConditionFailedException apiPreConditionFailedException = new ApiPreConditionFailedException(message);
        apiPreConditionFailedException.setCode(code);
        apiPreConditionFailedException.setErrorMessage(errorMessage);
        apiPreConditionFailedException.setMessage(message);
        apiPreConditionFailedException.setThrowable(new Throwable());
        assertEquals(message, apiPreConditionFailedException.getMessage());
        assertEquals(code, apiPreConditionFailedException.getCode());
        assertEquals(errorMessage, apiPreConditionFailedException.generalMessage());
    }

    @Test
    public void apiPreConditionFailedExceptionTest2() {
        String message = "Exception Occurred";
        String code = "code-101";
        ApiPreConditionFailedException apiPreConditionFailedException =
            new ApiPreConditionFailedException(code, message);
        assertEquals(message, apiPreConditionFailedException.getMessage());
        assertEquals(code, apiPreConditionFailedException.getCode());
    }

    @Test
    public void apiPreConditionFailedExceptionTest3() {
        String message = "Exception Occurred";
        String code = "code-101";
        String errorMessage = "Error message";
        ApiPreConditionFailedException apiPreConditionFailedException =
            new ApiPreConditionFailedException(code, message, errorMessage);
        assertEquals(message, apiPreConditionFailedException.getMessage());
        assertEquals(code, apiPreConditionFailedException.getCode());
        assertEquals(errorMessage, apiPreConditionFailedException.generalMessage());
    }

    @Test
    public void apiPreConditionFailedExceptionTest4() {
        String message = "Exception Occurred";
        String code = "code-101";
        String errorMessage = "Error message";
        Throwable e = new Throwable();
        ApiPreConditionFailedException apiPreConditionFailedException =
            new ApiPreConditionFailedException(code, message, errorMessage, e);
        assertEquals(message, apiPreConditionFailedException.getMessage());
        assertEquals(code, apiPreConditionFailedException.getCode());
        assertEquals(errorMessage, apiPreConditionFailedException.generalMessage());
        assertEquals(e, apiPreConditionFailedException.getThrowable());
    }
}
