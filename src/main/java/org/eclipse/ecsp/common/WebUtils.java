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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for web-related operations.
 */
public final class WebUtils {

    /**
     * Forbidden instantiation.
     */
    private WebUtils() {

    }

    /**
     * Returns the ResponseEntity for the given ApiResponse.
     *
     * @param apiResponse the ApiResponse object
     * @param <D> denotes the type of data in the ApiResponse
     * @return the ResponseEntity containing the ApiResponse and HttpStatus
     */
    public static <D> ResponseEntity<ApiResponse<D>> getResponseEntity(ApiResponse<D> apiResponse) {
        HttpStatus httpStatus = apiResponse.getStatusCode();
        apiResponse.setStatusCode(null);
        return new ResponseEntity<>(apiResponse, httpStatus);
    }

}
