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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.ecsp.auth.lib.rest.model.ErrorRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.List;

/**
 * This class handles exceptions that occur in the controllers of the application.
 * It implements the HandlerExceptionResolver interface to provide a custom exception handling mechanism.
 */
public class ControllerExceptionHandler implements HandlerExceptionResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    private static final String INVALID_FACTORY_ID =
        "Device details is not present in the Factory data for the passed factoryID";

    /**
     * Resolves exceptions that occur during request handling in the controller.
     * This method creates a ModelAndView object with an ErrorRest object and sets the appropriate HTTP status code
     * based on the exception type.
     * If the exception is a Spring HttpConverterError, the error message is set to "Unable to parse request" and the
     * HTTP status code is set to 400 (Bad Request).
     * If the exception is an AccessDeniedException, the error message is set to "User is NOT AUTHORIZED to perform
     * this operation" and the HTTP status code is set to 403 (Forbidden).
     * If the exception is an IllegalArgumentException and the message contains "INVALID_FACTORY_ID", the error message
     * is set to "INVALID_FACTORY_ID" and the HTTP status code is set to 404 (Not Found).
     * For any other exception, the error message is set to "Internal Server Error" and the HTTP status code is set to
     * 500 (Internal Server Error).
     * The error object is added to the ModelAndView object and the response status is set accordingly.
     * Finally, the method logs the error message and returns the ModelAndView object.
     *
     * @param request  the HttpServletRequest object representing the current request
     * @param response the HttpServletResponse object representing the current response
     * @param handler  the Object representing the handler for the request
     * @param ex       the Exception that occurred during request handling
     * @return a ModelAndView object containing the error information
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        ErrorRest error = new ErrorRest();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        StackTraceElement[] stackTrace = ex.getStackTrace();

        if (isSpringHttpConverterError(stackTrace)) {
            error.setMessage("Unable to parse request (" + ex + ")");
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof AccessDeniedException) {
            error.setMessage("User is NOT AUTHORIZED to perform this operation");
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (ex instanceof IllegalArgumentException) {
            if (ex.getMessage().contains(INVALID_FACTORY_ID)) {
                error.setMessage(INVALID_FACTORY_ID);
                httpStatus = HttpStatus.NOT_FOUND;
            }
        } else if (ex instanceof MethodArgumentNotValidException) {
            if (ex.getMessage() != null) {
                error.setMessage(getErrorsMap((MethodArgumentNotValidException) ex));
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                error.setMessage("Internal Server Error (" + ex + ")");
            }
        } else {
            error.setMessage("Internal Server Error (" + ex + ")");
        }
        mv.addObject("error", error);
        response.setStatus(httpStatus.value());
        LOG.error("HttpStatus( {} ) error: {}", httpStatus, error.getMessage());
        return mv;
    }

    /**
     * Checks if the given stack trace contains any element from the Spring HTTP converter class.
     *
     * @param stackTrace the stack trace to check
     * @return true if the stack trace contains an element from the Spring HTTP converter class, false otherwise
     */
    private boolean isSpringHttpConverterError(StackTraceElement[] stackTrace) {
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("org.springframework.http.converter.AbstractHttpMessageConverter".equals(
                stackTraceElement.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the first error message from the given MethodArgumentNotValidException.
     *
     * @param ex The MethodArgumentNotValidException containing the validation errors.
     * @return The first error message as a String.
     */
    private String getErrorsMap(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).toList();
        return errors.get(0);
    }

}
