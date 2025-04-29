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
 * Test class for ApiValidationFailedException.
 */
public class ApiValidationFailedExceptionTest {

    @Test
    public void apiValidationFailedExceptionTest1() {
        String message = "Exception Occurred";
        String code = "11";
        String errorMessage = "Error Message";
        ApiValidationFailedException apiValidationFailedException = new ApiValidationFailedException(message);
        apiValidationFailedException.setCode(code);
        apiValidationFailedException.setErrorMessage(errorMessage);
        apiValidationFailedException.setMessage(message);
        apiValidationFailedException.setThrowable(new Throwable());
        assertEquals(message, apiValidationFailedException.getMessage());
        assertEquals(code, apiValidationFailedException.getCode());
        assertEquals(errorMessage, apiValidationFailedException.getErrorMessage());
    }

    @Test
    public void apiValidationFailedExceptionTest2() {
        String message = "Exception Occurred";
        String code = "code-101";
        ApiValidationFailedException apiValidationFailedException = new ApiValidationFailedException(code, message);
        assertEquals(message, apiValidationFailedException.getMessage());
        assertEquals(code, apiValidationFailedException.getCode());
    }

    @Test
    public void apiValidationFailedExceptionTest3() {
        String message = "Exception Occurred";
        String code = "code-101";
        String errorMessage = "Error message";
        ApiValidationFailedException apiValidationFailedException =
            new ApiValidationFailedException(code, message, errorMessage);
        assertEquals(message, apiValidationFailedException.getMessage());
        assertEquals(code, apiValidationFailedException.getCode());
        assertEquals(errorMessage, apiValidationFailedException.getErrorMessage());
    }

    @Test
    public void apiValidationFailedExceptionTest4() {
        String message = "Exception Occurred";
        String code = "code-101";
        String errorMessage = "Error message";
        Throwable e = new Throwable();
        ApiValidationFailedException apiValidationFailedException =
            new ApiValidationFailedException(code, message, errorMessage, e);
        assertEquals(message, apiValidationFailedException.getMessage());
        assertEquals(code, apiValidationFailedException.getCode());
        assertEquals(errorMessage, apiValidationFailedException.getErrorMessage());
        assertEquals(e, apiValidationFailedException.getThrowable());
    }
}
