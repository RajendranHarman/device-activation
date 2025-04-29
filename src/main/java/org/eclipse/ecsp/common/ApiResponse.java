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

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

/**
 * Represents an API response.
 *
 * @param <D> the type of data in the response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<D> {
    private String code;
    private String message;
    private D data;
    private HttpStatus statusCode;

    /**
     * Constructs a new ApiResponse object.
     *
     * @param builder The builder object used to construct the ApiResponse.
     */
    private ApiResponse(Builder<D> builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.data = builder.data;
        this.statusCode = builder.statusCode;
    }

    /**
     * Gets the code of the API response.
     *
     * @return The code of the API response.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the message of the API response.
     *
     * @return The message of the API response.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the data of the API response.
     *
     * @return The data of the API response.
     */
    public D getData() {
        return data;
    }

    /**
     * Gets the status code of the API response.
     *
     * @return The status code of the API response.
     */
    public HttpStatus getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the status code of the API response.
     *
     * @param statusCode The status code to set.
     */
    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Builder class for constructing ApiResponse objects.
     *
     * @param <D> The type of the data in the API response.
     */
    public static class Builder<D> {
        private String code;
        private String message;
        private D data;
        private HttpStatus statusCode;

        /**
         * Constructs a new Builder object with the specified code, message, and status code.
         *
         * @param code       The code of the API response.
         * @param message    The message of the API response.
         * @param statusCode The status code of the API response.
         */
        public Builder(String code, String message, HttpStatus statusCode) {
            this.code = code;
            this.message = message;
            this.statusCode = statusCode;
        }

        /**
         * Sets the data of the API response.
         *
         * @param data The data to set.
         * @return The Builder object.
         */
        public Builder<D> withData(D data) {
            this.data = data;
            return this;
        }

        /**
         * Builds and returns a new ApiResponse object.
         *
         * @return The constructed ApiResponse object.
         */
        public ApiResponse<D> build() {
            return new ApiResponse<>(this);
        }
    }
}
