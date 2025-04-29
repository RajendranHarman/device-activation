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

import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequest;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationResponse;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationStateRequest;
import org.eclipse.ecsp.auth.lib.rest.model.DeactivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.support.ActivationFailException;
import org.eclipse.ecsp.auth.lib.rest.support.DeactivationFailException;
import org.eclipse.ecsp.auth.lib.service.DeviceService;
import org.eclipse.ecsp.auth.springmvc.rest.service.AuthenticationRestService;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import javax.naming.directory.InvalidAttributeValueException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for AuthenticationRestService.
 */
public class AuthenticationRestServiceTest {

    public static final int STATUS_CODE_200 = 200;
    public static final int STATUS_CODE_412 = 412;
    public static final int STATUS_CODE_400 = 400;
    public static final int STATUS_CODE_500 = 500;
    public static final int RETURN_VALUE = 2;

    @InjectMocks
    private AuthenticationRestService authenticationRestService;

    @Mock
    private DeviceService deviceService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void shouldReturnSuccessForHealthCheck() {
        Mockito.doReturn(1).when(deviceService).healthCheck();
        ResponseEntity<String> responseEntity = authenticationRestService.healthCheck();
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldReturnInternalServerErrorForHealthCheck() {
        Mockito.doReturn(RETURN_VALUE).when(deviceService).healthCheck();
        ResponseEntity<String> responseEntity = authenticationRestService.healthCheck();
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldThrowExceptionForHealthCheck() {
        Mockito.doThrow(new ApiValidationFailedException("Error Occurred")).when(deviceService).healthCheck();
        ResponseEntity<String> responseEntity = authenticationRestService.healthCheck();
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldReturnVersionNotSupportedMessage() {
        ActivationRequest activationRequest = new ActivationRequest();
        ResponseEntity<String> responseEntity = authenticationRestService.activate(activationRequest);
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCode().value());
        assertEquals("{\"error\":{\"message\":\"V1 version is not supported\"}}", responseEntity.getBody());
    }

    @Test
    public void activateEmptyCheck() {
        ActivationRequest mockRequest = mock(ActivationRequest.class);
        Mockito.doReturn("").when(mockRequest).toString();
        ResponseEntity<String> responseEntity = authenticationRestService.activate(mockRequest);
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCode().value());
        assertEquals("{\"error\":{\"message\":\"V1 version is not supported\"}}", responseEntity.getBody());
    }

    @Test
    public void decryptTest() {
        ResponseEntity<String> responseEntity = authenticationRestService.decrypt(httpServletRequest);
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCode().value());
        assertEquals("{\"error\":{\"message\":\"API not supported\"}}", responseEntity.getBody());
    }

    @Test
    public void shouldReturnApiNotSupportedMessage() {
        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setPasscode("24828rh2yr2");
        activationResponse.setDeviceId("HUEUE6283681");
        ResponseEntity<String> responseEntity = authenticationRestService.loginV1(activationResponse);
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCode().value());
        assertEquals("{\"error\":{\"message\":\"API not supported\"}}", responseEntity.getBody());
    }

    //    @Test
    //    public void loginV1NullCheck() {
    //        ActivationResponse mockRequest = mock(ActivationResponse.class);
    //        Mockito.doReturn(null).when(mockRequest).toString();
    //        ResponseEntity<String> responseEntity = authenticationRESTService.loginV1(mockRequest);
    //        assertEquals(STATUS_CODE_400, responseEntity.getStatusCode().value());
    //        assertEquals("{\"error\":{\"message\":\"API not supported\"}}", responseEntity.getBody());
    //    }

    @Test
    public void shouldReturnSuccessForMappingHarmanIdsToVin() {
        Mockito.doReturn(true).when(deviceService).mapHarmanIdsForVins(Mockito.anyLong());
        ResponseEntity<String> responseEntity = authenticationRestService.mapHarmanIdsForVins(Mockito.anyLong());
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldReturnSuccessForSettingStateToActivate() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        mockRequest.setAttribute("HCP-User", "Test");
        ActivationStateRequest activationStateRequest = new ActivationStateRequest();
        activationStateRequest.setSerialNumber("Test123");
        Mockito.doReturn("User123").when(mockRequest).getHeader("HCP-User");
        Mockito.doNothing().when(deviceService).setReadyToActivate(Mockito.any(), Mockito.anyString());
        ResponseEntity<String> responseEntity =
            authenticationRestService.setReadyToActivate(activationStateRequest, mockRequest);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldThrowExceptionForSettingStateToActivate() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        mockRequest.setAttribute("HCP-User", "Test");
        ActivationStateRequest activationStateRequest = new ActivationStateRequest();
        activationStateRequest.setSerialNumber("Test123");
        Mockito.doReturn("User123").when(mockRequest).getHeader("HCP-User");
        Mockito.doThrow(new ApiValidationFailedException("Error Occurred")).when(deviceService)
            .setReadyToActivate(Mockito.any(), Mockito.anyString());
        ResponseEntity<String> responseEntity =
            authenticationRestService.setReadyToActivate(activationStateRequest, mockRequest);
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCode().value());
    }

    //    @Test
    //    public void setReadyToActivate_NullActivationStateRequestCheck() {
    //        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    //        ActivationStateRequest activationStateRequest = mock(ActivationStateRequest.class);
    //        Mockito.doReturn(null).when(activationStateRequest).toString();
    //        Mockito.doThrow(new ApiValidationFailedException("Error Occurred")).
    //        when(deviceService).setReadyToActivate(Mockito.any(), Mockito.any());
    //        ResponseEntity<String> responseEntity =
    //        authenticationRESTService.setReadyToActivate(activationStateRequest, mockRequest);
    //        assertEquals(STATUS_CODE_500, responseEntity.getStatusCode().value());
    //    }

    @Test
    public void shouldDeactivateTheDevice() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        mockRequest.setAttribute("HCP-User", "Test");
        ActivationStateRequest activationStateRequest = new ActivationStateRequest();
        activationStateRequest.setSerialNumber("Test123");
        Mockito.doReturn("User123").when(mockRequest).getHeader("HCP-User");
        Mockito.doNothing().when(deviceService).deactivate(Mockito.any(), Mockito.anyString());
        ResponseEntity<String> responseEntity =
            authenticationRestService.deactivate(activationStateRequest, mockRequest);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldThrowExceptionWhileDeactivatingTheDevice() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        mockRequest.setAttribute("HCP-User", "Test");
        ActivationStateRequest activationStateRequest = new ActivationStateRequest();
        activationStateRequest.setSerialNumber("Test123");
        Mockito.doReturn("User123").when(mockRequest).getHeader("HCP-User");
        Mockito.doThrow(new ApiValidationFailedException("Error Occurred")).when(deviceService)
            .deactivate(Mockito.any(), Mockito.anyString());
        ResponseEntity<String> responseEntity =
            authenticationRestService.deactivate(activationStateRequest, mockRequest);
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCode().value());
    }

    //    @Test
    //    public void deactivate_NullActivationStateRequestCheck() {
    //        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    //        ActivationStateRequest activationStateRequest = mock(ActivationStateRequest.class);
    //        Mockito.doReturn(null).when(activationStateRequest).toString();
    //        Mockito.doThrow(new ApiValidationFailedException("Error Occurred")).
    //        when(deviceService).deactivate(Mockito.any(), Mockito.any());
    //        ResponseEntity<String> responseEntity =
    //        authenticationRESTService.deactivate(activationStateRequest, mockRequest);
    //        assertEquals(STATUS_CODE_500, responseEntity.getStatusCode().value());
    //    }

    @Test
    public void shouldSuccessfullyDeactivateTheDeviceV2() throws InvalidAttributeValueException {
        DeactivationRequestData deactivationRequestData = new DeactivationRequestData();
        deactivationRequestData.setFactoryId("Test123");
        Mockito.doNothing().when(deviceService).deactivateAccount(Mockito.any(), Mockito.anyString());
        ResponseEntity<String> responseEntity =
            authenticationRestService.deviceDeactivate(deactivationRequestData, "Test");
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldThrowInvalidAttributeValueExceptionDeactivateV2Test1() throws InvalidAttributeValueException {
        DeactivationRequestData deactivationRequestData = new DeactivationRequestData();
        deactivationRequestData.setFactoryId("Test123");

        Mockito.doThrow(new InvalidAttributeValueException()).when(deviceService)
            .deactivateAccount(deactivationRequestData, "Test");
        ResponseEntity<String> responseEntity =
            authenticationRestService.deviceDeactivate(deactivationRequestData, "Test");
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldThrowInvalidAttributeValueExceptionDeactivateV2Test2() throws InvalidAttributeValueException {
        DeactivationRequestData deactivationRequestData = new DeactivationRequestData();

        Mockito.doThrow(new InvalidAttributeValueException()).when(deviceService)
            .deactivateAccount(deactivationRequestData, "Test");
        ResponseEntity<String> responseEntity =
            authenticationRestService.deviceDeactivate(deactivationRequestData, "Test");
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldThrowDeactivationFailException1() throws InvalidAttributeValueException {
        DeactivationRequestData deactivationRequestData = new DeactivationRequestData();
        deactivationRequestData.setFactoryId("Test123");

        Mockito.doThrow(new DeactivationFailException("Deactivation Failed")).when(deviceService)
            .deactivateAccount(deactivationRequestData, "Test");
        ResponseEntity<String> responseEntity =
            authenticationRestService.deviceDeactivate(deactivationRequestData, "Test");
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldThrowDeactivationFailException2() throws InvalidAttributeValueException {
        DeactivationRequestData deactivationRequestData = new DeactivationRequestData();

        Mockito.doThrow(new DeactivationFailException("Deactivation Failed")).when(deviceService)
            .deactivateAccount(deactivationRequestData, "Test");
        ResponseEntity<String> responseEntity =
            authenticationRestService.deviceDeactivate(deactivationRequestData, "Test");
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldThrowDeactivationFailException3() throws InvalidAttributeValueException {
        DeactivationRequestData deactivationRequestData = new DeactivationRequestData();
        deactivationRequestData.setFactoryId("Test123");
        Mockito.doThrow(new DeactivationFailException("Deactivation Failed")).when(deviceService)
            .deactivateAccount(deactivationRequestData, "Test");
        ResponseEntity<String> responseEntity =
            authenticationRestService.deviceDeactivate(deactivationRequestData, null);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCode().value());
    }

    @Test
    public void shouldReturnSuccessfulActivationResponseV2() {
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

        Mockito.doReturn(activationResponse).when(deviceService).activateDevice(activationRequestData);
        ResponseEntity<?> responseEntity = authenticationRestService.activateV2(activationRequestData);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void shouldReturnProvisionAliveResponseV2() {
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

        Mockito.doReturn(activationResponse).when(deviceService).activateDevice(activationRequestData);
        ResponseEntity<?> responseEntity = authenticationRestService.activateV2(activationRequestData);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
    }

    @Test
    public void shouldReturnAuthFailedResponseV2() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doReturn(null).when(deviceService).activateDevice(activationRequestData);
        ResponseEntity<?> responseEntity = authenticationRestService.activateV2(activationRequestData);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
    }

    @Test
    public void shouldHandleActivationFailExceptionV2() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(new ActivationFailException("Not sufficient data is provided to activate")).when(deviceService)
            .activateDevice(activationRequestData);
        ResponseEntity<?> responseEntity = authenticationRestService.activateV2(activationRequestData);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
    }

    @Test
    public void shouldHandleExceptionV2() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        Mockito.doThrow(new ApiValidationFailedException("Not sufficient data is provided to activate"))
            .when(deviceService).activateDevice(activationRequestData);
        ResponseEntity<?> responseEntity = authenticationRestService.activateV2(activationRequestData);
        assertNotNull(responseEntity);
        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
    }

    //    @Test
    //    public void activateNullTest() {
    //        ActivationRequestData activationRequestData = mock(ActivationRequestData.class);
    //        Mockito.doReturn(null).when(activationRequestData).toString();
    //        Mockito.doThrow(new ActivationFailException("Not sufficient data is provided to activate")).
    //        when(deviceService).activateDevice(activationRequestData);
    //        ResponseEntity<?> responseEntity = authenticationRESTService.activate(activationRequestData);
    //        assertEquals(STATUS_CODE_412, responseEntity.getStatusCodeValue());
    //    }
}