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

package org.eclipse.ecsp.common;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.AUTH_SUCCESS;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.DEVICE_DETAILS_NOT_FOUND;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.GENERAL_ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for WebUtils.
 */
public class WebUtilsTest {
    private static final int STATUS_CODE_200 = 200;
    private static final int STATUS_CODE_404 = 404;
    private static final int STATUS_CODE_500 = 500;

    @Test
    public void getResponseEntitySuccess() {
        ApiResponse<Object> apiResponse = new ApiResponse.Builder<>(AUTH_SUCCESS.getCode(),
            AUTH_SUCCESS.getMessage(),
            HttpStatus.OK).build();
        ResponseEntity<ApiResponse<Object>> responseEntity = WebUtils.getResponseEntity(apiResponse);

        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCode().value());
        assertEquals(AUTH_SUCCESS.getCode(), Objects.requireNonNull(responseEntity.getBody()).getCode());
        assertEquals(AUTH_SUCCESS.getMessage(), responseEntity.getBody().getMessage());
    }

    @Test
    public void getResponseEntityServerError() {
        ApiResponse<Object> apiResponse = new ApiResponse.Builder<>(GENERAL_ERROR.getCode(),
            GENERAL_ERROR.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR).build();
        ResponseEntity<ApiResponse<Object>> responseEntity = WebUtils.getResponseEntity(apiResponse);

        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCode().value());
        assertEquals(GENERAL_ERROR.getCode(), Objects.requireNonNull(responseEntity.getBody()).getCode());
        assertEquals(GENERAL_ERROR.getMessage(), responseEntity.getBody().getMessage());
    }

    @Test
    public void getResponseEntityNotFound() {
        ApiResponse<Object> apiResponse = new ApiResponse.Builder<>(DEVICE_DETAILS_NOT_FOUND.getCode(),
            DEVICE_DETAILS_NOT_FOUND.getMessage(),
            HttpStatus.NOT_FOUND).build();
        ResponseEntity<ApiResponse<Object>> responseEntity = WebUtils.getResponseEntity(apiResponse);

        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_404, responseEntity.getStatusCode().value());
        assertEquals(DEVICE_DETAILS_NOT_FOUND.getCode(), Objects.requireNonNull(responseEntity.getBody()).getCode());
        assertEquals(DEVICE_DETAILS_NOT_FOUND.getMessage(), responseEntity.getBody().getMessage());
    }

    @Test
    public void getResponseEntityWithDataSuccess() {
        ApiResponse<Object> apiResponse = new ApiResponse.Builder<>(AUTH_SUCCESS.getCode(),
            AUTH_SUCCESS.getMessage(),
            HttpStatus.OK).withData("Hello").build();
        ResponseEntity<ApiResponse<Object>> responseEntity = WebUtils.getResponseEntity(apiResponse);

        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCode().value());
        assertEquals(AUTH_SUCCESS.getCode(), Objects.requireNonNull(responseEntity.getBody()).getCode());
        assertEquals(AUTH_SUCCESS.getMessage(), responseEntity.getBody().getMessage());
        assertEquals("Hello", responseEntity.getBody().getData());
    }

    @Test
    public void getResponseEntityWithDataServerError() {
        ApiResponse<Object> apiResponse = new ApiResponse.Builder<>(GENERAL_ERROR.getCode(),
            GENERAL_ERROR.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR).withData("error").build();
        ResponseEntity<ApiResponse<Object>> responseEntity = WebUtils.getResponseEntity(apiResponse);

        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCode().value());
        assertEquals(GENERAL_ERROR.getCode(), Objects.requireNonNull(responseEntity.getBody()).getCode());
        assertEquals(GENERAL_ERROR.getMessage(), responseEntity.getBody().getMessage());
        assertEquals("error", responseEntity.getBody().getData());
    }

    @Test
    public void getResponseEntityWithDataNotFound() {
        ApiResponse<Object> apiResponse = new ApiResponse.Builder<>(DEVICE_DETAILS_NOT_FOUND.getCode(),
            DEVICE_DETAILS_NOT_FOUND.getMessage(),
            HttpStatus.NOT_FOUND).withData("Not found").build();
        ResponseEntity<ApiResponse<Object>> responseEntity = WebUtils.getResponseEntity(apiResponse);

        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_404, responseEntity.getStatusCode().value());
        assertEquals(DEVICE_DETAILS_NOT_FOUND.getCode(), Objects.requireNonNull(responseEntity.getBody()).getCode());
        assertEquals(DEVICE_DETAILS_NOT_FOUND.getMessage(), responseEntity.getBody().getMessage());
        assertEquals("Not found", responseEntity.getBody().getData());
    }
}