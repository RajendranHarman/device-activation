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
 * Test class for ApiTechnicalException.
 */
public class ApiTechnicalExceptionTest {

    @Test
    public void apiTechnicalExceptionTest1() {
        String message = "Exception Occurred";
        String code = "11";
        String errorMessage = "Error Message";
        ApiTechnicalException apiTechnicalException = new ApiTechnicalException(message);
        apiTechnicalException.setCode(code);
        apiTechnicalException.setErrorMessage(errorMessage);
        apiTechnicalException.setMessage(message);
        apiTechnicalException.setThrowable(new Throwable());
        assertEquals(message, apiTechnicalException.getMessage());
        assertEquals(code, apiTechnicalException.getCode());
        assertEquals(errorMessage, apiTechnicalException.getErrorMessage());
    }

    @Test
    public void apiTechnicalExceptionTest2() {
        String message = "Exception Occurred";
        String code = "code-101";
        ApiTechnicalException apiTechnicalException = new ApiTechnicalException(code, message);
        assertEquals(message, apiTechnicalException.getMessage());
        assertEquals(code, apiTechnicalException.getCode());
    }

    @Test
    public void apiTechnicalExceptionTest3() {
        String message = "Exception Occurred";
        String code = "code-101";
        String errorMessage = "Error message";
        ApiTechnicalException apiTechnicalException = new ApiTechnicalException(code, message, errorMessage);
        assertEquals(message, apiTechnicalException.getMessage());
        assertEquals(code, apiTechnicalException.getCode());
        assertEquals(errorMessage, apiTechnicalException.getErrorMessage());
    }

    @Test
    public void apiTechnicalExceptionTest4() {
        String message = "Exception Occurred";
        String code = "code-101";
        String errorMessage = "Error message";
        Throwable e = new Throwable();
        ApiTechnicalException apiTechnicalException = new ApiTechnicalException(code, message, errorMessage, e);
        assertEquals(message, apiTechnicalException.getMessage());
        assertEquals(code, apiTechnicalException.getCode());
        assertEquals(errorMessage, apiTechnicalException.getErrorMessage());
        assertEquals(e, apiTechnicalException.getThrowable());
    }
}
