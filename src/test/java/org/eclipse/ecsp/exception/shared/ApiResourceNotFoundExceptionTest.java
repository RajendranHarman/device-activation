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
 * Test class for ApiResourceNotFoundException.
 */
public class ApiResourceNotFoundExceptionTest {

    @Test
    public void apiResourceNotFoundExceptionTest1() {
        String message = "Exception Occurred";
        String code = "11";
        String errorMessage = "Error Message";
        ApiResourceNotFoundException apiResourceNotFoundException = new ApiResourceNotFoundException(message);
        apiResourceNotFoundException.setMessage(message);
        apiResourceNotFoundException.setCode(code);
        apiResourceNotFoundException.setErrorMessage(errorMessage);
        apiResourceNotFoundException.setThrowable(new Throwable());
        assertEquals(message, apiResourceNotFoundException.getMessage());
        assertEquals(code, apiResourceNotFoundException.getCode());
        assertEquals(errorMessage, apiResourceNotFoundException.getErrorMessage());
    }

    @Test
    public void apiResourceNotFoundExceptionTest2() {
        String message = "Exception Occurred";
        String code = "code-101";
        ApiResourceNotFoundException apiResourceNotFoundException = new ApiResourceNotFoundException(code, message);
        assertEquals(message, apiResourceNotFoundException.getMessage());
        assertEquals(code, apiResourceNotFoundException.getCode());
    }

    @Test
    public void apiResourceNotFoundExceptionTest3() {
        String message = "Exception Occurred";
        String code = "code-101";
        String errorMessage = "Error message";
        ApiResourceNotFoundException apiResourceNotFoundException =
            new ApiResourceNotFoundException(code, message, errorMessage);
        assertEquals(message, apiResourceNotFoundException.getMessage());
        assertEquals(code, apiResourceNotFoundException.getCode());
        assertEquals(errorMessage, apiResourceNotFoundException.getErrorMessage());
    }

    @Test
    public void apiResourceNotFoundExceptionTest4() {
        String message = "Exception Occurred";
        String code = "code-101";
        String errorMessage = "Error message";
        Throwable e = new Throwable();
        ApiResourceNotFoundException apiResourceNotFoundException =
            new ApiResourceNotFoundException(code, message, errorMessage, e);
        assertEquals(message, apiResourceNotFoundException.getMessage());
        assertEquals(code, apiResourceNotFoundException.getCode());
        assertEquals(errorMessage, apiResourceNotFoundException.getErrorMessage());
        assertEquals(e, apiResourceNotFoundException.getThrowable());
    }
}
