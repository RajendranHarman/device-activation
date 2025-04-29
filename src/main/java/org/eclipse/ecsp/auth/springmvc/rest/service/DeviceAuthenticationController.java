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

package org.eclipse.ecsp.auth.springmvc.rest.service;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestDataV2;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationResponse;
import org.eclipse.ecsp.auth.lib.rest.model.PreSharedKeyResponse;
import org.eclipse.ecsp.auth.lib.service.DeviceServiceV2;
import org.eclipse.ecsp.common.ApiResponse;
import org.eclipse.ecsp.common.ErrorUtils;
import org.eclipse.ecsp.common.WebUtils;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiResourceNotFoundException;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.AUTH_FAILED;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.AUTH_PROVISION_ALIVE;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.AUTH_SUCCESS;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.GENERAL_ERROR;

/**
 * This class is a controller for device authentication.
 * It handles the activation of devices and exposes REST endpoints for device activation.
 */
@RestController
public class DeviceAuthenticationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAuthenticationController.class);
    private static final String SERIAL_NUMBER_ERR_MSG =
        "## Error has occurred while performing device activation. serialNumber: {}, ErrMsg: {} ";
    private static final String DEF_ACTIVATION_ERR_MSG = "## Error has occurred while activating device";
    private final DeviceServiceV2 deviceServiceV2;

    @Autowired
    public DeviceAuthenticationController(DeviceServiceV2 deviceServiceV2) {
        this.deviceServiceV2 = deviceServiceV2;
    }

    /**
     * Activates a device using the provided activation request data.
     *
     * @param requestData The activation request data containing the serial number, VIN, and qualifier.
     * @return A ResponseEntity containing the API response.
     */
    @PostMapping(value = "/v3/device/activate/", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "POST /v3/device/activate/", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<ApiResponse<Object>> activateV3(@Valid @RequestBody ActivationRequestData requestData) {
        String serialNumber = requestData.getSerialNumber();
        String vin = requestData.getVin();
        String qualifier = requestData.getQualifier();
        String requestBody = requestData.toString().replaceAll("[\r\n]", "");
        LOGGER.info("## activate device - START  requestData: {}", requestBody);
        ApiResponse<Object> apiResponse;
        try {
            //Call device activation
            ActivationResponse activationResponse = deviceServiceV2.activateDevice(requestData, "v3");
            if (activationResponse == null) {
                apiResponse = new ApiResponse.Builder<>(AUTH_FAILED.getCode(), AUTH_FAILED.getMessage(),
                    HttpStatus.PRECONDITION_FAILED).build();
            } else if (activationResponse.isProvisionedAlive()) {
                LOGGER.debug("## Device activation completed successfully without association.");
                apiResponse =
                    new ApiResponse.Builder<>(AUTH_PROVISION_ALIVE.getCode(), AUTH_PROVISION_ALIVE.getMessage(),
                            HttpStatus.PRECONDITION_FAILED).withData(activationResponse).build();
            } else {
                LOGGER.debug("## Device activation completed successfully with association.");
                apiResponse =
                    new ApiResponse.Builder<>(AUTH_SUCCESS.getCode(), AUTH_SUCCESS.getMessage(),
                            HttpStatus.OK).withData(activationResponse).build();
            }
        } catch (ApiValidationFailedException e) {
            LOGGER.error("## Error has occurred while performing device activation. serialNumber: {}, ErrMsg: {}",
                serialNumber,
                e.getErrorMessage());
            apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.BAD_REQUEST).build();
        } catch (ApiResourceNotFoundException e) {
            LOGGER.error(SERIAL_NUMBER_ERR_MSG, serialNumber, e.getErrorMessage());
            apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.NOT_FOUND).build();
        } catch (ApiPreConditionFailedException e) {
            LOGGER.error(SERIAL_NUMBER_ERR_MSG, serialNumber, e.generalMessage());
            apiResponse =
                new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.PRECONDITION_FAILED).build();
        } catch (ApiTechnicalException e) {
            LOGGER.error("{}",
                ErrorUtils.buildError(DEF_ACTIVATION_ERR_MSG, e, getErrorMap(serialNumber, vin, qualifier)));
            apiResponse =
                new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.PRECONDITION_FAILED).build();
        } catch (Exception e) {
            LOGGER.error("{}",
                ErrorUtils.buildError(DEF_ACTIVATION_ERR_MSG, e, getErrorMap(serialNumber, vin, qualifier)));
            apiResponse = new ApiResponse.Builder<>(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        LOGGER.debug("## activate Device Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Activates a device using the v4 API.
     *
     * @param requestData The activation request data.
     * @return The response entity containing the API response.
     */
    @PostMapping(value = "/v4/device/activate/", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "POST /v4/device/activate/", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<ApiResponse<Object>> activateV4(@Valid @RequestBody ActivationRequestData requestData) {
        String serialNumber = requestData.getSerialNumber();
        String vin = requestData.getVin();
        String qualifier = requestData.getQualifier();
        String requestBody = requestData.toString().replaceAll("[\r\n]", "");
        LOGGER.info("## activate device - START  requestData: {}", requestBody);
        ApiResponse<Object> apiResponse;
        try {
            //Call device activation
            ActivationResponse activationResponse = deviceServiceV2.activateDevice(requestData, "v4");
            if (activationResponse == null) {
                apiResponse = new ApiResponse.Builder<>(AUTH_FAILED.getCode(), AUTH_FAILED.getMessage(),
                        HttpStatus.PRECONDITION_FAILED).build();
            } else if (activationResponse.isProvisionedAlive()) {
                LOGGER.debug("## Device activation completed successfully without association.");
                apiResponse =
                    new ApiResponse.Builder<>(AUTH_PROVISION_ALIVE.getCode(), AUTH_PROVISION_ALIVE.getMessage(),
                            HttpStatus.PRECONDITION_FAILED).withData(activationResponse).build();
            } else {
                LOGGER.debug("## Device activation completed successfully with association.");
                apiResponse =
                    new ApiResponse.Builder<>(AUTH_SUCCESS.getCode(), AUTH_SUCCESS.getMessage(),
                            HttpStatus.OK).withData(activationResponse).build();
            }
        } catch (ApiValidationFailedException e) {
            LOGGER.error("## Error has occurred while performing device activation. serialNumber: {}, ErrMsg: {}",
                serialNumber,
                e.getErrorMessage());
            apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.BAD_REQUEST).build();
        } catch (ApiResourceNotFoundException e) {
            LOGGER.error(SERIAL_NUMBER_ERR_MSG, serialNumber, e.getErrorMessage());
            apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.NOT_FOUND).build();
        } catch (ApiPreConditionFailedException e) {
            LOGGER.error(SERIAL_NUMBER_ERR_MSG, serialNumber, e.generalMessage());
            apiResponse =
                new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.PRECONDITION_FAILED).build();
        } catch (ApiTechnicalException e) {
            LOGGER.error("{}",
                ErrorUtils.buildError(DEF_ACTIVATION_ERR_MSG, e, getErrorMap(serialNumber, vin, qualifier)));
            apiResponse =
                new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.PRECONDITION_FAILED).build();
        } catch (Exception e) {
            LOGGER.error("{}",
                ErrorUtils.buildError(DEF_ACTIVATION_ERR_MSG, e, getErrorMap(serialNumber, vin, qualifier)));
            apiResponse = new ApiResponse.Builder<>(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        LOGGER.debug("## activate Device Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Activates a device using the v5 API.
     *
     * @param requestData The activation request data.
     * @return The response entity containing the API response.
     */
    @PostMapping(value = "/v5/device/activate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Hidden
    public ResponseEntity<ApiResponse<Object>> activateV5(@RequestBody ActivationRequestDataV2 requestData) {
        String jitActId = requestData.getJitActId();
        LOGGER.info("## activate device - START  with requestData: {}", requestData);
        ApiResponse<Object> apiResponse;
        try {
            ActivationResponse activationResponse = deviceServiceV2.activateDevice(requestData, "v5");
            if (activationResponse == null) {
                apiResponse = new ApiResponse.Builder<>(AUTH_FAILED.getCode(), AUTH_FAILED.getMessage(),
                        HttpStatus.PRECONDITION_FAILED).build();
            } else {
                LOGGER.debug("## Device activation completed successfully.");
                apiResponse =
                    new ApiResponse.Builder<>(AUTH_SUCCESS.getCode(), AUTH_SUCCESS.getMessage(),
                            HttpStatus.OK).withData(activationResponse).build();
            }
        } catch (ApiPreConditionFailedException e) {
            LOGGER.error("API pre condition failed for {} with exception : {}",
                jitActId, e.getMessage());
            apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(),
                    HttpStatus.PRECONDITION_FAILED).build();
        } catch (ApiResourceNotFoundException e) {
            LOGGER.error("API resource not found for {} with exception : {}",
                jitActId, e.getMessage());
            apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(),
                    HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error(DEF_ACTIVATION_ERR_MSG + " {} with error : {}", jitActId, e.getMessage());
            apiResponse = new ApiResponse.Builder<>(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        LOGGER.debug("## activate Device v5 Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Api to return newly generated pre-shared key.
     *
     * @return apiResponse
     */
    @GetMapping(value = "/GeneratePreSharedKey", produces = MediaType.APPLICATION_JSON_VALUE)
    @Hidden
    public ResponseEntity<ApiResponse<Object>> getPresharedKey() {
        LOGGER.debug("## get preSharedKey Controller - START");
        ApiResponse<Object> apiResponse;
        try {
            PreSharedKeyResponse preSharedKeyResponse = deviceServiceV2.getPresharedKey();
            LOGGER.info("PreSharedKeyResponse :: {}", preSharedKeyResponse);
            apiResponse =
                new ApiResponse.Builder<>(AUTH_SUCCESS.getCode(), AUTH_SUCCESS.getMessage(),
                        HttpStatus.OK).withData(preSharedKeyResponse).build();
        } catch (Exception e) {
            LOGGER.error("exception in preSharedKey Controller :: {}", e.getMessage());
            apiResponse = new ApiResponse.Builder<>(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        LOGGER.debug("## get preSharedKey Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Returns a map containing error information for the given serial number, VIN, and qualifier.
     *
     * @param serialNumber The serial number of the device.
     * @param vin The vehicle identification number.
     * @param qualifier The qualifier for the error.
     * @return A map containing the error information.
     */
    public Map<Object, Object> getErrorMap(String serialNumber, String vin, String qualifier) {
        Map<Object, Object> errorMap = new LinkedHashMap<>();
        errorMap.put("serialNUmber", serialNumber);
        errorMap.put("vin", vin);
        errorMap.put("qualifier", qualifier);
        errorMap.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
        return errorMap;
    }
}