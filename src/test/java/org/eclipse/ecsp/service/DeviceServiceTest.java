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

package org.eclipse.ecsp.service;

import org.eclipse.ecsp.common.config.EnvConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.ecsp.auth.lib.config.AuthProperty;
import org.eclipse.ecsp.auth.lib.dao.DeviceFactoryData;
import org.eclipse.ecsp.auth.lib.dao.DeviceFactoryDataDao;
import org.eclipse.ecsp.auth.lib.dao.DeviceInfoSharedDao;
import org.eclipse.ecsp.auth.lib.obsever.DefaultDeviceStateChangeObservable;
import org.eclipse.ecsp.auth.lib.obsever.DeviceStateActivation;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationResponse;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationStateRequest;
import org.eclipse.ecsp.auth.lib.rest.model.DeactivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.support.ActivationFailException;
import org.eclipse.ecsp.auth.lib.rest.support.DeactivationFailException;
import org.eclipse.ecsp.auth.lib.rest.support.SpringAuthTokenGenerator;
import org.eclipse.ecsp.auth.lib.service.DeviceService;
import org.eclipse.ecsp.auth.lib.validate.DeviceValidatorDefaultImpl;
import org.eclipse.ecsp.auth.lib.validate.DeviceValidatorFactory;
import org.eclipse.ecsp.services.device.dao.DeviceDao;
import org.eclipse.ecsp.services.device.model.Device;
import org.eclipse.ecsp.services.deviceactivation.dao.DeviceActivationStateDao;
import org.eclipse.ecsp.services.factorydata.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryData;
import org.eclipse.ecsp.services.shared.dao.HcpInfoDao;
import org.eclipse.ecsp.services.shared.db.HcpInfo;
import org.eclipse.ecsp.springauth.client.rest.SpringAuthRestClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.naming.directory.InvalidAttributeValueException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceService.
 */
@Slf4j
public class DeviceServiceTest {

    public static final String HCP_AUTH_QUALIFIER_SECRET_KEY = "hcp_auth_qualifier_secret_key";
    private static final String RESPONSE1 = "{\n"
        + "  \"client_name\": \"HC34703\",\n"
        + "  \"client_secret\": \"8725372\"\n"
        + "}";
    public static final long ID = 12345L;
    @InjectMocks
    private DeviceService deviceService;
    @Mock
    private EnvConfig<AuthProperty> envConfig;
    @Mock
    private DeviceInfoSharedDao deviceInfoDao;
    @Mock
    private HcpInfoDao hcpInfoDao;
    @Mock
    private HcpInfo hcpInfo;
    @Mock
    private DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;
    @Mock
    private DeviceInfoFactoryData deviceInfoFactoryData;
    @Mock
    private DeviceFactoryDataDao factoryDataDao;
    @Mock
    private DeviceActivationStateDao deviceActivationStateDao;
    @Mock
    private SpringAuthRestClient springAuthRestClient;
    @Mock
    private DeviceDao deviceDao;
    @Mock
    private DeviceValidatorFactory deviceValidatorFactory;
    @Mock
    private SpringAuthTokenGenerator springAuthTokenGenerator;
    @Mock
    private DefaultDeviceStateChangeObservable deviceStateChangeObservable;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void preActivateVinsTest() {
        List<String> vins = new ArrayList<>();
        vins.add("TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0");
        vins.add("TESTVIN_Make:SECOND_Model:300_Year:2013_Type:Car_0");
        vins.add("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        Mockito.doReturn(vins).when(hcpInfoDao).getVinsToPreactivate(Mockito.anyLong());
        deviceService.preActivateVins(1L);
        assertEquals(vins, hcpInfoDao.getVinsToPreactivate(Mockito.anyLong()));
    }

    @Test
    public void mapHarmanIdsForVinsTest() {
        Mockito.doReturn(1).when(hcpInfoDao).mapHarmanIdsForVins(ID);
        Mockito.doReturn(1L).when(hcpInfoDao).getTempGroupSize(ID);
        boolean actualResponse = deviceService.mapHarmanIdsForVins(ID);
        assertTrue(actualResponse);
    }

    @Test
    public void setReadyToActivateTest() {
        DeviceService deviceServicee = new DeviceService();
        DeviceService deviceService1 = Mockito.spy(deviceServicee);
        ActivationStateRequest activationStateRequest = new ActivationStateRequest();
        activationStateRequest.setSerialNumber("12345");
        Mockito.doNothing().when(deviceService1).deactivate(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(deviceActivationStateDao).insert(Mockito.any());
        deviceService.setReadyToActivate(activationStateRequest, "Test");
        assertNotNull(deviceService1);
    }

    @Test
    public void deactivateTest() {
        Mockito.doReturn(1).when(deviceDao).deactivate(Mockito.anyString());
        List<Long> activeRecordIdList = new ArrayList<>();
        activeRecordIdList.add(1L);
        activeRecordIdList.add(ID);
        Mockito.doReturn(activeRecordIdList).when(deviceActivationStateDao).findActiveDevices(Mockito.anyString());
        deviceService.deactivate("1234", "user123");
        assertEquals(activeRecordIdList, deviceActivationStateDao.findActiveDevices(Mockito.anyString()));
    }

    @Test
    public void getDeviceInfoFactoryDataTest() {
        LinkedHashMap<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("serial_number", "12345");
        Mockito.when(factoryDataDao.find(Mockito.any(Map.class))).thenReturn(Mockito.any(DeviceFactoryData.class));
        DeviceFactoryData deviceFactoryData = deviceService.getDeviceInfoFactoryData(orderedMap);
        assertNull(deviceFactoryData);
    }

    @Test
    public void healthCheckTest() {
        Mockito.when(deviceDao.healthCheck()).thenReturn(1);
        int actualResponse = deviceService.healthCheck();
        assertEquals(1, actualResponse);
    }

    @Test
    public void shouldThrowDeactivationFailExceptionForInvalidFactoryId() throws InvalidAttributeValueException {
        DeactivationRequestData deactivationRequestData = new DeactivationRequestData();
        deactivationRequestData.setFactoryId("abcd");
        boolean actualResponse = false;
        try {
            deviceService.deactivateAccount(deactivationRequestData, "test");
        } catch (DeactivationFailException e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowDeactivationFailExceptionForDeviceDetailsNotPresent() throws InvalidAttributeValueException {
        DeactivationRequestData deactivationRequestData = new DeactivationRequestData();
        deactivationRequestData.setFactoryId("12345");
        boolean actualResponse = false;
        Mockito.when(deviceInfoFactoryDataDao.findByFactoryId(Mockito.anyLong())).thenReturn(null);
        try {
            deviceService.deactivateAccount(deactivationRequestData, "test");
        } catch (DeactivationFailException e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void deactivateAccountNoActiveDeviceFoundTest() throws InvalidAttributeValueException {
        DeactivationRequestData deactivationRequestData = new DeactivationRequestData();
        deactivationRequestData.setFactoryId("12345");
        boolean actualResponse = false;
        deviceInfoFactoryData.setId(1L);
        deviceInfoFactoryData.setSerialNumber("12345");
        deviceInfoFactoryData.setImei("234");
        deviceInfoFactoryData.setDeviceType("dongle");
        Mockito.when(deviceInfoFactoryDataDao.findByFactoryId(Mockito.anyLong())).thenReturn(deviceInfoFactoryData);
        deviceService.deactivateAccount(deactivationRequestData, "test");
        assertEquals(deviceInfoFactoryData, deviceInfoFactoryDataDao.findByFactoryId(Mockito.anyLong()));
    }

    @Test
    public void shouldThrowActivationFailExceptionForNullActivationRequestData() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData = null;
        boolean actualResponse = false;
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(false);
        try {
            ActivationResponse activationResponse = deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForNullVinInActivationRequestData() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin(null);
        activationRequestData.setSerialNumber("12345");
        boolean actualResponse = false;
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(false);
        try {
            ActivationResponse activationResponse = deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForBlankAad() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setAad(" ");
        boolean actualResponse = false;
        try {
            ActivationResponse activationResponse = deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForInvalidAad() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setAad("abc");
        boolean actualResponse = false;
        try {
            ActivationResponse activationResponse = deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForDeviceDetailsNotInInventory() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setAad("no");
        boolean actualResponse = false;
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(false);
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(null);
        try {
            ActivationResponse activationResponse = deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForEmptyImeiSerialNumberBssid() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setAad("yes");

        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        boolean actualResponse = false;
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(false);
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(deviceFactoryData);
        try {
            deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
            log.error(e.getMessage());
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForBlankConfiguredValidationProperty() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("12121");
        activationRequestData.setBssid("212121");
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setImei("12121");
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setBssid("212121");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setPackageSerialNumber("MX3234");
        deviceFactoryData.setImsi("345343");
        boolean actualResponse = false;
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(false);
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(deviceFactoryData);
        Mockito.when(envConfig.getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION)).thenReturn("");
        Mockito.when(envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT)).thenReturn("OEM1");
        try {
            deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
            log.error(e.getMessage());
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForBlankOemEnvironment() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("12121");
        activationRequestData.setBssid("212121");
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setImei("12121");
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setBssid("212121");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setPackageSerialNumber("MX3234");
        deviceFactoryData.setImsi("345343");
        boolean actualResponse = false;
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(false);
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(deviceFactoryData);
        Mockito.when(envConfig.getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION)).thenReturn("ENABLE");
        Mockito.when(envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT)).thenReturn("");
        try {
            deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
            log.error(e.getMessage());
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForInvalidDevice() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("12121");
        activationRequestData.setBssid("212121");
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setImei("12121");
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setBssid("212121");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setPackageSerialNumber("MX3234");
        deviceFactoryData.setImsi("345343");
        boolean actualResponse = false;
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(false);
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(deviceFactoryData);
        Mockito.when(envConfig.getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION)).thenReturn("ENABLE");
        Mockito.when(envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT)).thenReturn("OEM1");
        DeviceValidatorFactory.OemEnvironment environment = DeviceValidatorFactory.OemEnvironment.OEM1;
        Mockito.when(deviceValidatorFactory.getInstance(environment)).thenReturn(null);
        try {
            deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
            log.error(e.getMessage());
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForValidDeviceVinDisabled() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("12121");
        activationRequestData.setBssid("212121");
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setImei("12121");
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setBssid("212121");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setPackageSerialNumber("MX3234");
        deviceFactoryData.setImsi("345343");
        boolean actualResponse = false;
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(false);
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(deviceFactoryData);
        Mockito.when(envConfig.getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION)).thenReturn("ENABLE");
        Mockito.when(envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT)).thenReturn("OEM1");
        DeviceValidatorFactory.OemEnvironment environment = DeviceValidatorFactory.OemEnvironment.OEM1;
        DeviceValidatorDefaultImpl oemDeviceValidator = new DeviceValidatorDefaultImpl();
        Mockito.when(deviceValidatorFactory.getInstance(environment)).thenReturn(oemDeviceValidator);
        try {
            deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
            log.error(e.getMessage());
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForEmptyVin() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("12121");
        activationRequestData.setBssid("212121");
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setImei("12121");
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setBssid("212121");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setPackageSerialNumber("MX3234");
        deviceFactoryData.setImsi("345343");
        boolean actualResponse = false;
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(true);
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(deviceFactoryData);
        Mockito.when(envConfig.getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION)).thenReturn("ENABLE");
        Mockito.when(envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT)).thenReturn("OEM1");
        DeviceValidatorFactory.OemEnvironment environment = DeviceValidatorFactory.OemEnvironment.OEM1;
        DeviceValidatorDefaultImpl oemDeviceValidator = new DeviceValidatorDefaultImpl();
        Mockito.when(deviceValidatorFactory.getInstance(environment)).thenReturn(oemDeviceValidator);
        try {
            deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
            log.error(e.getMessage());
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForInvalidTransactionStatus() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("12121");
        activationRequestData.setBssid("212121");
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setImei("12121");
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setBssid("212121");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setPackageSerialNumber("MX3234");
        deviceFactoryData.setImsi("345343");
        boolean actualResponse = false;
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(true);
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(deviceFactoryData);
        Mockito.when(envConfig.getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION)).thenReturn("ENABLE");
        Mockito.when(envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT)).thenReturn("OEM1");
        DeviceValidatorFactory.OemEnvironment environment = DeviceValidatorFactory.OemEnvironment.OEM1;
        DeviceValidatorDefaultImpl oemDeviceValidator = new DeviceValidatorDefaultImpl();
        Mockito.when(deviceValidatorFactory.getInstance(environment)).thenReturn(oemDeviceValidator);
        Mockito.when(deviceActivationStateDao.getAssociatedVin(deviceFactoryData.getSerialNumber()))
            .thenReturn("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        try {
            deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
            log.error(e.getMessage());
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForSecretsVaultEnabled() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("12121");
        activationRequestData.setBssid("212121");
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setImei("12121");
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setBssid("212121");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setPackageSerialNumber("MX3234");
        deviceFactoryData.setImsi("345343");
        boolean actualResponse = false;
        String secretKey = "r$27T30@*";
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(true);
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(deviceFactoryData);
        Mockito.when(envConfig.getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION)).thenReturn("ENABLE");
        Mockito.when(envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT)).thenReturn("OEM1");
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        DeviceValidatorFactory.OemEnvironment environment = DeviceValidatorFactory.OemEnvironment.OEM1;
        DeviceValidatorDefaultImpl oemDeviceValidator = new DeviceValidatorDefaultImpl();
        Mockito.when(deviceValidatorFactory.getInstance(environment)).thenReturn(oemDeviceValidator);
        Mockito.when(deviceActivationStateDao.getAssociatedVin(deviceFactoryData.getSerialNumber()))
            .thenReturn("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        Mockito.when(deviceActivationStateDao.getSimTransactionStatus(deviceFactoryData.getSerialNumber()))
            .thenReturn("Completed");
        try {
            deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
            log.error(e.getMessage());
        }
        assertTrue(actualResponse);
    }

    @Test
    public void shouldThrowActivationFailExceptionForSecretsVaultDisabled() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("12121");
        activationRequestData.setBssid("212121");
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");

        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setImei("12121");
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setBssid("212121");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setPackageSerialNumber("MX3234");
        deviceFactoryData.setImsi("345343");
        boolean actualResponse = false;
        Mockito.when(envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG)).thenReturn(true);
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(deviceFactoryData);
        Mockito.when(envConfig.getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION)).thenReturn("ENABLE");
        Mockito.when(envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT)).thenReturn("OEM1");
        DeviceValidatorFactory.OemEnvironment environment = DeviceValidatorFactory.OemEnvironment.OEM1;
        DeviceValidatorDefaultImpl oemDeviceValidator = new DeviceValidatorDefaultImpl();
        Mockito.when(deviceValidatorFactory.getInstance(environment)).thenReturn(oemDeviceValidator);
        Mockito.when(deviceActivationStateDao.getAssociatedVin(deviceFactoryData.getSerialNumber()))
            .thenReturn("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        Mockito.when(deviceActivationStateDao.getSimTransactionStatus(deviceFactoryData.getSerialNumber()))
            .thenReturn("Completed");
        Mockito.when(envConfig.getBooleanValue(AuthProperty.SECRET_VAULT_ENABLE_FLG)).thenReturn(false);
        String secretKey = "r$27T30@*";
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        try {
            deviceService.activateDevice(activationRequestData);
        } catch (ActivationFailException e) {
            actualResponse = true;
            log.error(e.getMessage());
        }
        assertTrue(actualResponse);
    }

    @Test
    public void updateAllActivationTablesTest() {
        DeviceFactoryData difd = new DeviceFactoryData();
        difd.setId(1L);
        difd.setImei("123");
        difd.setSerialNumber("12345");
        difd.setDeviceType("dongle");
        difd.setFaulty(false);
        difd.setStolen(false);
        difd.setIccid("26482468");
        difd.setSsid("1234112");
        difd.setBssid("84284");
        difd.setState("PROVISIONED");
        difd.setImsi("24663923");
        difd.setModel("MX6424");
        difd.setPlatformVersion("v1");
        difd.setManufacturingDate("2020-05-01");
        difd.setMsisdn("342223");
        difd.setImsi("2434121");
        difd.setRecordDate("2020-05-01");
        difd.setCreatedDate("2020-05-01");
        difd.setFactoryAdmin("xyz");
        difd.setPackageSerialNumber("HT23233N3");

        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setDeviceType("dongle");
        Device newDevice = new Device();
        Mockito.doReturn(1).when(deviceDao).insert(newDevice, false);
        deviceService.updateAllActivationTables(newDevice, difd, activationRequestData);
        Mockito.verify(deviceDao, times(1)).insert(newDevice, false);
    }

    @Test
    public void activateAccountFromSpringAuthPassTestForV2Activation() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:SECOND_Model:200_Year:2013_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "YzZNBBmWCV3tQs0WsmHlVsVKTcxsU7xrtUU1gp7Iy/XqWHr2OlFYjm4nJwDr6jb4yNFCt2i6xpRtaNLv3OSX"
                + "Uit9gDu5bIdMY6ugUr2qS7E=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setDeviceType("dongle");
        log.info("Activation Request Data: " + activationRequestData);
        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setId(1L);
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setImei("123");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setFaulty(false);
        deviceFactoryData.setStolen(false);
        deviceFactoryData.setState("READY_TO_ACTIVATE");
        deviceFactoryData.setBssid("84284");
        deviceFactoryData.setIccid("26482468");
        deviceFactoryData.setImsi("24662482");
        deviceFactoryData.setModel("MX6424");
        deviceFactoryData.setPlatformVersion("v1");
        DeviceStateActivation deviceStateActivation = new DeviceStateActivation();
        deviceStateActivation.setDeviceType("Dongle");
        
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(deviceFactoryData);
        Mockito.when(envConfig.getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION)).thenReturn("ENABLE");
        Mockito.when(envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT)).thenReturn("OEM1");
        DeviceValidatorFactory.OemEnvironment environment = DeviceValidatorFactory.OemEnvironment.OEM1;
        DeviceValidatorDefaultImpl oemDeviceValidator = new DeviceValidatorDefaultImpl();
        Mockito.when(deviceValidatorFactory.getInstance(environment)).thenReturn(oemDeviceValidator);
        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Mockito.doReturn(false).when(envConfig).getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        String secretKey = "HarmanAct";
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        Mockito.doReturn("dummytoken").when(springAuthTokenGenerator).fetchSpringAuthToken();
        Mockito.doNothing().when(springAuthRestClient).createRegisteredClient("dummytoken", "HC34703",
            "498jd328e", "Dongle");
        Mockito.doNothing().when(deviceStateChangeObservable).newDeviceActivated(deviceStateActivation);
        deviceService.activateDevice(activationRequestData);
    }

    @Test
    public void activateAccountFromSpringAuthPassTestForV2ReActivation() throws InvalidAttributeValueException {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:SECOND_Model:200_Year:2013_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "YzZNBBmWCV3tQs0WsmHlVsVKTcxsU7xrtUU1gp7Iy/XqWHr2OlFYjm4nJwDr6jb4yNFCt2i6xpRtaNLv3OSX"
                + "Uit9gDu5bIdMY6ugUr2qS7E=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setDeviceType("dongle");
        log.info("Re Activation Request Data: " + activationRequestData);
        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setId(1L);
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setImei("123");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setFaulty(false);
        deviceFactoryData.setStolen(false);
        deviceFactoryData.setState("ACTIVE");
        deviceFactoryData.setBssid("84284");
        deviceFactoryData.setIccid("26482468");
        deviceFactoryData.setImsi("24662482");
        deviceFactoryData.setModel("MX6424");
        deviceFactoryData.setPlatformVersion("v1");
        List<Device> deviceList = new LinkedList<>();
        Device device = new Device();
        device.setHarmanId("HC34703");
        device.setPasscode("8725372");
        deviceList.add(device);
        
        Mockito.when(envConfig.getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION)).thenReturn("ENABLE");
        Mockito.when(envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT)).thenReturn("OEM1");
        DeviceValidatorFactory.OemEnvironment environment = DeviceValidatorFactory.OemEnvironment.OEM1;
        DeviceValidatorDefaultImpl oemDeviceValidator = new DeviceValidatorDefaultImpl();
        Mockito.when(deviceValidatorFactory.getInstance(environment)).thenReturn(oemDeviceValidator);
        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Mockito.doReturn(deviceList).when(deviceDao).findActiveDevice(1L);
        Mockito.doReturn(false).when(envConfig).getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        String secretKey = "HarmanAct";
        Map testMap = new LinkedHashMap<>();
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        Mockito.doReturn("dummytoken").when(springAuthTokenGenerator).fetchSpringAuthToken();
        Mockito.doReturn(testMap).when(springAuthRestClient).deleteRegisteredClient("dummytoken", "HC34703");
        Mockito.doNothing().when(springAuthRestClient)
            .updateRegisteredClient("dummytoken", "HC34703", "8725372", "Dongle", "approved");
        ;
        deviceService.activateDevice(activationRequestData);
    }

    @Test
    public void reactivationNullDeviceList() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:SECOND_Model:200_Year:2013_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "YzZNBBmWCV3tQs0WsmHlVsVKTcxsU7xrtUU1gp7Iy/XqWHr2OlFYjm4nJwDr6jb4yNFCt2i6xpRtaNLv3OSX"
                + "Uit9gDu5bIdMY6ugUr2qS7E=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setDeviceType("dongle");
        log.info("Re Activation Request Data: " + activationRequestData);
        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setId(1L);
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setImei("123");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setFaulty(false);
        deviceFactoryData.setStolen(false);
        deviceFactoryData.setState("ACTIVE");
        deviceFactoryData.setBssid("84284");
        deviceFactoryData.setIccid("26482468");
        deviceFactoryData.setImsi("24662482");
        deviceFactoryData.setModel("MX6424");
        deviceFactoryData.setPlatformVersion("v1");
        String secretKey = "HarmanAct";
        Mockito.when(factoryDataDao.find(Mockito.any())).thenReturn(deviceFactoryData);
        Mockito.when(envConfig.getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION)).thenReturn("ENABLE");
        Mockito.when(envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT)).thenReturn("OEM1");
        DeviceValidatorFactory.OemEnvironment environment = DeviceValidatorFactory.OemEnvironment.OEM1;
        DeviceValidatorDefaultImpl oemDeviceValidator = new DeviceValidatorDefaultImpl();
        Mockito.when(deviceValidatorFactory.getInstance(environment)).thenReturn(oemDeviceValidator);
        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Mockito.doReturn(false).when(envConfig).getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        Mockito.doReturn(null).when(deviceDao).findActiveDevice(1L);
        assertThrows(ActivationFailException.class, () -> {
            deviceService.activateDevice(activationRequestData);
        });
    }
}