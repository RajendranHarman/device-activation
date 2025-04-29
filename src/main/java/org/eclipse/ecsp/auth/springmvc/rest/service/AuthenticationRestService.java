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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequest;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationResponse;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationStateRequest;
import org.eclipse.ecsp.auth.lib.rest.model.DeactivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.support.ActivationFailException;
import org.eclipse.ecsp.auth.lib.rest.support.DeactivationFailException;
import org.eclipse.ecsp.auth.lib.service.DeviceService;
import org.eclipse.ecsp.auth.springmvc.rest.support.RestResponse;
import org.eclipse.ecsp.common.ErrorUtils;
import org.eclipse.ecsp.services.shared.rest.support.SimpleResponseMessage;
import org.eclipse.ecsp.services.shared.util.HealthCheckConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.directory.InvalidAttributeValueException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.GENERAL_ERROR;

/**
 * This class represents the REST service for authentication.
 * It provides endpoints for health check, device activation, login, and other related operations.
 */
@Controller
public class AuthenticationRestService {
    private static final int LOG_CONFIGURATION_RELOAD_TIME_DELAY = 30000;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationRestService.class);
    private static final String USER_ID = "user-id";
    private static final String SUCCESSFUL = "successful";
    private static final String REQUEST_REGEX = "[\r\n]";

    private final DeviceService deviceService;

    @Autowired
    public AuthenticationRestService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * Performs a health check to verify the database connection.
     *
     * @return A ResponseEntity containing the status of the health check.
     */
    @GetMapping(value = "/health")
    @ResponseBody
    @Hidden
    public ResponseEntity<String> healthCheck() {
        String status = "Database Connection : ";
        try {
            int statusCode = deviceService.healthCheck();
            if (statusCode == HealthCheckConstants.DB_CONN_SUCCESS_CODE) {
                status = status + HealthCheckConstants.OK;
            } else {
                return RestResponse
                    .internalServerError("Error with health check - Can't connect with database");
            }

        } catch (Exception e) {
            return RestResponse
                .internalServerError("Error with health check - Can't connect with database");

        }
        return RestResponse.ok(status);
    }

    /**
     * Activates a device.
     *
     * <p>This method is used to activate a device based on the provided activation request.
     *
     * @param activationRequest The activation request containing the necessary information for device activation.
     * @return A ResponseEntity object representing the HTTP response.
     */
    @PostMapping(value = "/device/activate", produces = "application/json")
    @ResponseBody
    @Hidden
    public ResponseEntity<String> activate(@RequestBody ActivationRequest activationRequest) {
        // 2.33 Release - Removing redundant null check to resolve RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE Sonar
        // code smell
        String requestData = activationRequest.toString().replaceAll(REQUEST_REGEX, "");
        LOGGER.info("Invoking activate method for ActivationRequest: {}", requestData);
        return RestResponse.badRequest("V1 version is not supported");
    }

    /**
     * Activates a device using the provided activation request data.
     *
     * @param activationRequestData The activation request data.
     * @return The response entity containing the activation response.
     */
    @PostMapping(value = "/v2/device/activate/", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "POST /v2/device/activate/", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<String> activateV2(@Valid @RequestBody ActivationRequestData activationRequestData) {
        ActivationResponse activationResponse;
        // 2.33 Release - Removing redundant null check to resolve RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE Sonar
        // code smell
        String requestData = activationRequestData.toString().replaceAll(REQUEST_REGEX, "");
        LOGGER.info("## Activation Request Data - START: {}", requestData);
        try {
            activationResponse = deviceService.activateDevice(activationRequestData);
            if (activationResponse == null) {
                return RestResponse.getJsonResponseEntity(HttpStatus.PRECONDITION_FAILED,
                    new SimpleResponseMessage("Activation Failed: Duplicate activation request"));
            } else if (activationResponse.isProvisionedAlive()) {
                LOGGER.warn("Device will be in PROVISIONED_ALIVE state. Reason: Association was not performed");
                return RestResponse.getJsonResponseEntity(HttpStatus.PRECONDITION_FAILED,
                    new SimpleResponseMessage(
                        "Device will be in PROVISIONED_ALIVE state. Reason: Association was not performed"));
            }
        } catch (ActivationFailException e) {
            LOGGER.error(
                "Exception has occurred while activating device, IMEI: {}, serialNumber: {}, error msg: {}, error: {}",
                activationRequestData.getImei(), activationRequestData.getSerialNumber(), e.getMessage(), e);
            return RestResponse.getJsonResponseEntity(HttpStatus.PRECONDITION_FAILED,
                new SimpleResponseMessage(e.getMessage()));
        } catch (Exception e) {
            Map<Object, Object> errorMap = new LinkedHashMap<>();
            errorMap.put("serialNUmber", activationRequestData.getSerialNumber());
            errorMap.put("vin", activationRequestData.getVin());
            errorMap.put("qualifier", activationRequestData.getQualifier());
            errorMap.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
            LOGGER.error("{}", ErrorUtils.buildError("## Error has occurred while activating device", e, errorMap));
            return RestResponse.getJsonResponseEntity(HttpStatus.PRECONDITION_FAILED,
                new SimpleResponseMessage(e.getMessage()));
        }
        return RestResponse.getJsonResponseEntity(HttpStatus.OK, activationResponse);
    }

    /**
     * Decrypts the device information.
     *
     * @param request the HttpServletRequest object containing the request information
     * @return a ResponseEntity object with the decrypted device information
     */
    @PostMapping(value = "/device/decrypt", produces = "application/json")
    @ResponseBody
    @Hidden
    public ResponseEntity<String> decrypt(HttpServletRequest request) {
        LOGGER.info("Invoking decrypt method for Request: {}", request);
        return RestResponse.badRequest("API not supported");
    }

    /**
     * Handles the login request for device activation.
     *
     * @param activationResponse The activation response object containing the necessary data.
     * @return A ResponseEntity with a JSON string indicating that the API is not supported.
     */
    @PostMapping(value = "/device/login", produces = "application/json")
    @ResponseBody
    @Hidden
    public ResponseEntity<String> loginV1(
        @RequestBody ActivationResponse activationResponse) {
        // 2.33 Release - Removing redundant null check to resolve RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE Sonar
        // code smell
        String requestData = activationResponse.toString().replaceAll(REQUEST_REGEX, "");
        LOGGER.info("Invoking v1 login method for ActivationResponse: {}", requestData);
        return RestResponse.badRequest("API not supported");
    }

    /**
     * Maps Harman IDs for VINs.
     *
     * <p>This method maps Harman IDs for VINs based on the provided temporary group ID.
     *
     * @param tempGroupId The temporary group ID used for mapping Harman IDs.
     * @return A ResponseEntity containing a JSON response indicating the success of the mapping operation.
     */
    @GetMapping(value = "/device/vinlist/upload/{tempGroupId}", produces = "application/json")
    @ResponseBody
    @Hidden
    public ResponseEntity<String> mapHarmanIdsForVins(@PathVariable("tempGroupId") long tempGroupId) {
        boolean isSuccess = deviceService.mapHarmanIdsForVins(tempGroupId);
        return RestResponse.getJsonResponseEntity(HttpStatus.OK, isSuccess);
    }

    /**
     * Sets the device ready to activate.
     *
     * @param readyToActivateRequest The request object containing the activation state.
     * @param request                The HTTP servlet request.
     * @return The response entity with the result of the operation.
     */
    @PostMapping(value = "/device/setReadyToActivate", produces = "application/json")
    @Operation(summary = "POST /device/setReadyToActivate", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<String> setReadyToActivate(@RequestBody ActivationStateRequest readyToActivateRequest,
                                                     HttpServletRequest request) {
        String requestData = readyToActivateRequest.toString().replaceAll(REQUEST_REGEX, "");
        LOGGER.info("setReadyToActivate - {}", requestData);
        try {
            String userId = request.getHeader("HCP-User");
            LOGGER.info("setReadyToActivate requested by the user Id : {}",
                (userId == null) ? null : userId.replaceAll(REQUEST_REGEX, ""));
            deviceService.setReadyToActivate(readyToActivateRequest, userId);
            return RestResponse.getJsonResponseEntity(HttpStatus.OK, new SimpleResponseMessage(SUCCESSFUL));
        } catch (Exception e) {
            LOGGER.error("setReadyToActivate threw exception: ", e);
            return RestResponse.internalServerError("Not successfull");
        }
    }

    /**
     * Deactivates a device.
     *
     * @param readyToActivateRequest The request object containing the activation state.
     * @param request                The HTTP servlet request.
     * @return The response entity containing the result of the deactivation.
     */
    @PostMapping(value = "/device/deactivate", produces = "application/json")
    @Hidden
    public ResponseEntity<String> deactivate(@RequestBody ActivationStateRequest readyToActivateRequest,
                                             HttpServletRequest request) {
        String requestData = readyToActivateRequest.toString().replaceAll(REQUEST_REGEX, "");
        LOGGER.info("deactivate - {}", requestData);
        try {
            String userId = request.getHeader("HCP-User");
            LOGGER.info("deactivate requested by the user Id : {}",
                (userId == null) ? null : userId.replaceAll(REQUEST_REGEX, ""));
            deviceService.deactivate(readyToActivateRequest.getSerialNumber(), userId);
            return RestResponse.getJsonResponseEntity(HttpStatus.OK, new SimpleResponseMessage(SUCCESSFUL));
        } catch (Exception e) {
            LOGGER.error("deactivate threw exception: ", e);
            return RestResponse.internalServerError("Not successfull");
        }
    }

    /**
     * Deactivates a device.
     *
     * @param deactivationRequestData The deactivation request data.
     * @param userId                  The user ID.
     * @return The response entity containing the result of the deactivation.
     */
    @PostMapping(value = "/v2/device/deactivate", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "POST /v2/device/deactivate", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<String> deviceDeactivate(@Valid @RequestBody DeactivationRequestData deactivationRequestData,
                                                   @RequestHeader(USER_ID) String userId) {
        LOGGER.info("## deviceDeactivate - START factory Id: {}, user Id: {}",
            (deactivationRequestData.getFactoryId() == null) ? null :
                deactivationRequestData.getFactoryId().replaceAll(REQUEST_REGEX, ""),
            (userId == null) ? null : userId.replaceAll(REQUEST_REGEX, ""));
        try {
            deviceService.deactivateAccount(deactivationRequestData, userId);
        } catch (InvalidAttributeValueException exception) {
            if (deactivationRequestData.getFactoryId() != null) {
                LOGGER.error("Exception occurred while deactivating device for factoryId:{} error: {}",
                    deactivationRequestData.getFactoryId(), exception.getMessage());
            } else {
                LOGGER.error("Exception occurred while deactivating device with error: {}", exception.getMessage());
            }
            return RestResponse.deactivationFailed(exception.getMessage());
        } catch (DeactivationFailException exception) {
            if (deactivationRequestData.getFactoryId() != null) {
                LOGGER.error("Exception occurred while deactivating device for factoryId:{}  error: {}",
                    deactivationRequestData.getFactoryId(), exception.getMessage());
            } else {
                LOGGER.error("Exception occurred while deactivating device with error: {}", exception.getMessage());
            }
            return RestResponse.deactivationFailed(exception.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error has occurred while deactivating device, error: ", e);
        }
        LOGGER.info("Deactivated device for the factoryId {}", deactivationRequestData.getFactoryId());
        return RestResponse.getJsonResponseEntity(HttpStatus.OK, new SimpleResponseMessage(SUCCESSFUL));
    }
}