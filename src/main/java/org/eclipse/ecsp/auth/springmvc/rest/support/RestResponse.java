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

package org.eclipse.ecsp.auth.springmvc.rest.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.ecsp.auth.lib.rest.model.ErrorResponseRest;
import org.eclipse.ecsp.auth.lib.rest.model.ErrorRest;
import org.eclipse.ecsp.common.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * The `RestResponse` class represents a response from a RESTful API.
 * It provides methods to set and retrieve the response data.
 */
public class RestResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestResponse.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * The `RestResponse` class represents a response from a RESTful API.
     * It provides methods to set and retrieve the response data.
     */
    private RestResponse() {
    }

    /**
     * Creates a CREATED response with the specified resource and resource URL.
     *
     * @param resource    the resource to be included in the response
     * @param resourceUrl the URL of the resource
     * @param <T>         the type of the resource
     * @return the CREATED response with the specified resource and resource URL
     */
    public static <T> ResponseEntity<T> created(T resource, String resourceUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonConstants.CONTENT_LOCATION, resourceUrl);
        return new ResponseEntity<>(resource, headers, HttpStatus.CREATED);
    }

    /**
     * Creates an OK response with the specified resource.
     *
     * @param resource the resource to be included in the response
     * @param <T>      the type of the resource
     * @return the OK response with the specified resource
     */
    public static <T> ResponseEntity<T> ok(T resource) {
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * Creates a JSON response entity with the specified status and resource.
     *
     * @param status   the HTTP status of the response
     * @param resource the resource to be included in the response
     * @return the JSON response entity with the specified status and resource
     */
    public static ResponseEntity<String> getJsonResponseEntity(HttpStatus status, Object resource) {
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(resource);
        } catch (JsonProcessingException e) {
            LOGGER.error("Exception thrown while creating JSON response :: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(status).body(jsonString);
    }

    /**
     * Creates an error response entity with the specified status, message, and reference.
     *
     * @param status    the HTTP status of the response
     * @param message   the error message
     * @param reference the error reference
     * @return the error response entity with the specified status, message, and reference
     */
    private static ResponseEntity<String> createErrorResponse(HttpStatus status, String message, String reference) {
        ErrorRest error = new ErrorRest();
        error.setMessage(message);
        error.setReference(reference);
        ErrorResponseRest errorResponse = new ErrorResponseRest();
        errorResponse.setError(error);
        return getJsonResponseEntity(status, errorResponse);
    }

    /**
     * Creates a NOT_FOUND response with the specified message.
     *
     * @param message the error message
     * @return the NOT_FOUND response with the specified message
     */
    public static ResponseEntity<String> notFound(String message) {
        return createErrorResponse(HttpStatus.NOT_FOUND, message, null);
    }

    /**
     * Creates a METHOD_NOT_ALLOWED response.
     *
     * @return the METHOD_NOT_ALLOWED response
     */
    public static ResponseEntity<String> methodNotAllowed() {
        return createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method is not allowed for this resource", null);
    }

    /**
     * Creates an UNAUTHORIZED response for login failure.
     *
     * @return the UNAUTHORIZED response for login failure
     */
    public static ResponseEntity<String> loginFail() {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid Credentials", null);
    }

    /**
     * Creates a PRECONDITION_FAILED response with the specified message.
     *
     * @param message the error message
     * @return the PRECONDITION_FAILED response with the specified message
     */
    public static ResponseEntity<String> activationFailed(String message) {
        return createErrorResponse(HttpStatus.PRECONDITION_FAILED, message, null);
    }

    /**
     * Creates an INTERNAL_SERVER_ERROR response with the specified message.
     *
     * @param message the error message
     * @return the INTERNAL_SERVER_ERROR response with the specified message
     */
    public static ResponseEntity<String> internalServerError(String message) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    /**
     * Creates an UNAUTHORIZED response with the specified string.
     *
     * @param string the error string
     * @return the UNAUTHORIZED response with the specified string
     */
    public static ResponseEntity<String> authorizationFailed(String string) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, string, null);
    }

    /**
     * Creates a BAD_REQUEST response with the specified string.
     *
     * @param string the error string
     * @return the BAD_REQUEST response with the specified string
     */
    public static ResponseEntity<String> badRequest(String string) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, string, null);
    }

    /**
     * Creates a BAD_REQUEST response for deactivation failure with the specified message.
     *
     * @param message the error message
     * @return the BAD_REQUEST response for deactivation failure with the specified message
     */
    public static ResponseEntity<String> deactivationFailed(String message) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, message, null);
    }

    /* Test */

}
