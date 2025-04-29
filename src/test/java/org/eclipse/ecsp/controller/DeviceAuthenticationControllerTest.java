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

package org.eclipse.ecsp.controller;

import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestDataV2;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationResponse;
import org.eclipse.ecsp.auth.lib.rest.model.PreSharedKeyResponse;
import org.eclipse.ecsp.auth.lib.rest.support.ActivationFailException;
import org.eclipse.ecsp.auth.lib.service.DeviceServiceV2;
import org.eclipse.ecsp.auth.springmvc.rest.service.DeviceAuthenticationController;
import org.eclipse.ecsp.common.ApiResponse;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiResourceNotFoundException;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.ACTIVATION_CHECK;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.AUTH_FAILED;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.AUTH_PROVISION_ALIVE;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.AUTH_SUCCESS;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.DEVICE_DETAILS_NOT_FOUND;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.DEVICE_NOT_FOUND;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.GENERAL_ERROR;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.PRESHAREDKEY_VALIDATION_FAILED;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.UNKNOWN_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceAuthenticationController.
 */
public class DeviceAuthenticationControllerTest {

    public static final int STATUS_CODE_200 = 200;
    public static final int STATUS_CODE_412 = 412;
    public static final int STATUS_CODE_400 = 400;
    public static final int STATUS_CODE_404 = 404;
    public static final int STATUS_CODE_500 = 500;

    @InjectMocks
    private DeviceAuthenticationController deviceAuthenticationController;

    @Mock
    private DeviceServiceV2 deviceServiceV2;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void shouldReturnProvisionAliveResponseV3() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setProvisionedAlive(true);

        Mockito.doReturn(activationResponse).when(deviceServiceV2).activateDevice(activationRequestData, "v3");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV3(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
        assertEquals(AUTH_PROVISION_ALIVE.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldReturnAuthFailedResponseV3() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doReturn(null).when(deviceServiceV2).activateDevice(activationRequestData, "v3");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV3(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
        assertEquals(AUTH_FAILED.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldReturnSuccessResponseV3() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setPasscode("24828rh2yr2");
        activationResponse.setDeviceId("HUEUE6283681");

        Mockito.doReturn(activationResponse).when(deviceServiceV2).activateDevice(activationRequestData, "v3");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV3(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(AUTH_SUCCESS.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldThrowApiValidationFailedExceptionV3() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(new ApiValidationFailedException(UNKNOWN_ERROR.getCode(), UNKNOWN_ERROR.getMessage(),
            UNKNOWN_ERROR.getGeneralMessage())).when(deviceServiceV2).activateDevice(activationRequestData, "v3");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV3(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCodeValue());
        assertEquals(UNKNOWN_ERROR.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldThrowApiResourceNotFoundExceptionV3() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(
                new ApiResourceNotFoundException(DEVICE_DETAILS_NOT_FOUND.getCode(),
                    DEVICE_DETAILS_NOT_FOUND.getMessage(),
                    DEVICE_DETAILS_NOT_FOUND.getGeneralMessage())).when(deviceServiceV2)
            .activateDevice(activationRequestData, "v3");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV3(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_404, responseEntity.getStatusCodeValue());
        assertEquals(DEVICE_DETAILS_NOT_FOUND.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldThrowApiPreConditionFailedExceptionV3() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(new ApiPreConditionFailedException(ACTIVATION_CHECK.getCode(), ACTIVATION_CHECK.getMessage(),
            ACTIVATION_CHECK.getGeneralMessage())).when(deviceServiceV2).activateDevice(activationRequestData, "v3");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV3(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
        assertEquals(ACTIVATION_CHECK.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldThrowApiTechnicalExceptionV3() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(new ApiTechnicalException(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
            GENERAL_ERROR.getGeneralMessage())).when(deviceServiceV2).activateDevice(activationRequestData, "v3");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV3(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
        assertEquals(GENERAL_ERROR.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldThrowExceptionV3() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(new ActivationFailException("Error occurred")).when(deviceServiceV2)
            .activateDevice(activationRequestData, "v3");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV3(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(GENERAL_ERROR.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldReturnProvisionAliveResponseV4() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setProvisionedAlive(true);

        Mockito.doReturn(activationResponse).when(deviceServiceV2).activateDevice(activationRequestData, "v4");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV4(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
        assertEquals(AUTH_PROVISION_ALIVE.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldReturnAuthFailedResponseV4() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doReturn(null).when(deviceServiceV2).activateDevice(activationRequestData, "v4");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV4(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
        assertEquals(AUTH_FAILED.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldReturnSuccessResponseV4() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setPasscode("24828rh2yr2");
        activationResponse.setDeviceId("HUEUE6283681");

        Mockito.doReturn(activationResponse).when(deviceServiceV2).activateDevice(activationRequestData, "v4");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV4(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(AUTH_SUCCESS.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldThrowApiValidationFailedExceptionV4() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(new ApiValidationFailedException(UNKNOWN_ERROR.getCode(), UNKNOWN_ERROR.getMessage(),
            UNKNOWN_ERROR.getGeneralMessage())).when(deviceServiceV2).activateDevice(activationRequestData, "v4");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV4(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCodeValue());
        assertEquals(UNKNOWN_ERROR.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldThrowApiResourceNotFoundExceptionV4() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(
                new ApiResourceNotFoundException(DEVICE_DETAILS_NOT_FOUND.getCode(),
                    DEVICE_DETAILS_NOT_FOUND.getMessage(),
                    DEVICE_DETAILS_NOT_FOUND.getGeneralMessage())).when(deviceServiceV2)
            .activateDevice(activationRequestData, "v4");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV4(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_404, responseEntity.getStatusCodeValue());
        assertEquals(DEVICE_DETAILS_NOT_FOUND.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldThrowApiPreConditionFailedExceptionV4() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(new ApiPreConditionFailedException(ACTIVATION_CHECK.getCode(), ACTIVATION_CHECK.getMessage(),
            ACTIVATION_CHECK.getGeneralMessage())).when(deviceServiceV2).activateDevice(activationRequestData, "v4");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV4(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
        assertEquals(ACTIVATION_CHECK.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldThrowApiTechnicalExceptionV4() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(new ApiTechnicalException(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
            GENERAL_ERROR.getGeneralMessage())).when(deviceServiceV2).activateDevice(activationRequestData, "v4");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV4(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
        assertEquals(GENERAL_ERROR.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldThrowExceptionV4() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(new ActivationFailException("Error occurred")).when(deviceServiceV2)
            .activateDevice(activationRequestData, "v4");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV4(activationRequestData);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(GENERAL_ERROR.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldReturnNullResponseV5() {
        ActivationRequestDataV2 activationRequestDataV2 = new ActivationRequestDataV2(
            "49d5dab90474bcba5c346d14ceff15f4f0063700", "pOYc76IOlwf4EDsgrN7hINUyd02tD/ADzkpnwIP6gbs=");
        Mockito.doReturn(null).when(deviceServiceV2).activateDevice(activationRequestDataV2, "v5");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV5(activationRequestDataV2);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(AUTH_FAILED.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldReturnSuccessResponseV5() {
        ActivationRequestDataV2 activationRequestDataV2 = new ActivationRequestDataV2(
            "49d5dab90474bcba5c346d14ceff15f4f0063700", "pOYc76IOlwf4EDsgrN7hINUyd02tD/ADzkpnwIP6gbs=");
        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setPasscode(
            "WKUbNR7gC5caIcJSUDGxjYxxuKiCEkMylysh3ZMEUaDhrhjbugSA3dYhwBLb9Pb31729855921801");
        activationResponse.setDeviceId("HU1MM8RZ5BZER1");

        Mockito.doReturn(activationResponse).when(deviceServiceV2).activateDevice(activationRequestDataV2, "v5");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV5(activationRequestDataV2);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(AUTH_SUCCESS.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldReturnApiPreConditionFailedExceptionV5() {
        ActivationRequestDataV2 activationRequestDataV2 = new ActivationRequestDataV2(
            "49d5dab90474bcba5c346d14ceff15f4f0063700", "pOYc76IOlwf4EDsgrN7hINUyd02tD/ADzkpnwIP6gbs=");
        Mockito.doThrow(new ApiPreConditionFailedException(PRESHAREDKEY_VALIDATION_FAILED.getCode(),
            PRESHAREDKEY_VALIDATION_FAILED.getMessage(), PRESHAREDKEY_VALIDATION_FAILED.getGeneralMessage()))
            .when(deviceServiceV2).activateDevice(activationRequestDataV2, "v5");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV5(activationRequestDataV2);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
        assertEquals(PRESHAREDKEY_VALIDATION_FAILED.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldReturnApiResourceNotFoundExceptionV5() {
        ActivationRequestDataV2 activationRequestDataV2 = new ActivationRequestDataV2(
            "49d5dab90474bcba5c346d14ceff15f4f0063700", "pOYc76IOlwf4EDsgrN7hINUyd02tD/ADzkpnwIP6gbs=");
        Mockito.doThrow(new ApiResourceNotFoundException(DEVICE_NOT_FOUND.getCode(), DEVICE_NOT_FOUND.getMessage(),
            DEVICE_NOT_FOUND.getGeneralMessage())).when(deviceServiceV2).activateDevice(activationRequestDataV2, "v5");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV5(activationRequestDataV2);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_404, responseEntity.getStatusCodeValue());
        assertEquals(DEVICE_NOT_FOUND.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldReturnExceptionV5() {
        ActivationRequestDataV2 activationRequestDataV2 = new ActivationRequestDataV2(
            "49d5dab90474bcba5c346d14ceff15f4f0063700", "pOYc76IOlwf4EDsgrN7hINUyd02tD/ADzkpnwIP6gbs=");
        Mockito.doThrow(new NullPointerException("key is null in vault")).when(deviceServiceV2)
            .activateDevice(activationRequestDataV2, "v5");
        ResponseEntity<?> responseEntity = deviceAuthenticationController.activateV5(activationRequestDataV2);
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCodeValue());
        assertEquals(GENERAL_ERROR.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void getPreSharedKeyTest() {
        PreSharedKeyResponse keyResponse = new PreSharedKeyResponse();
        keyResponse.setPreSharedKey("GPHzwNC1qJIrOE0T");
        keyResponse.setEncryptedPreSharedKey("t4Iz9JR2hGzFbCYRkP+4GzRHHgf4aOUVhMiB9TxD/LQ=");

        Mockito.doReturn(keyResponse).when(deviceServiceV2).getPresharedKey();
        ResponseEntity<?> responseEntity = deviceAuthenticationController.getPresharedKey();
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(AUTH_SUCCESS.getCode(), actualApiResponse.getCode());
    }

    @Test
    public void shouldThrowExceptionOnGetPreSharedKey() {
        Mockito.doThrow(new ApiTechnicalException(" Exception while creating preSharedKey "))
            .when(deviceServiceV2).getPresharedKey();
        ResponseEntity<?> responseEntity = deviceAuthenticationController.getPresharedKey();
        ApiResponse<Object> actualApiResponse = (ApiResponse) responseEntity.getBody();
        assertNotNull(actualApiResponse);
        assertEquals(GENERAL_ERROR.getCode(), actualApiResponse.getCode());
    }
}