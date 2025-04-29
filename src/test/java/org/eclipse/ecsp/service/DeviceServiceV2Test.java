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
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.auth.lib.config.AuthProperty;
import org.eclipse.ecsp.auth.lib.dao.DeviceFactoryData;
import org.eclipse.ecsp.auth.lib.dao.DeviceFactoryDataDao;
import org.eclipse.ecsp.auth.lib.dao.DeviceInfoSharedDao;
import org.eclipse.ecsp.auth.lib.obsever.DefaultDeviceStateChangeObservable;
import org.eclipse.ecsp.auth.lib.obsever.DeviceStateActivation;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestDataV2;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationResponse;
import org.eclipse.ecsp.auth.lib.rest.model.PreSharedKeyResponse;
import org.eclipse.ecsp.auth.lib.rest.support.SpringAuthTokenGenerator;
import org.eclipse.ecsp.auth.lib.service.AssociationService;
import org.eclipse.ecsp.auth.lib.service.DeviceServiceV2;
import org.eclipse.ecsp.auth.lib.util.CryptographyUtil;
import org.eclipse.ecsp.auth.lib.util.DeviceActivationUtil;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiResourceNotFoundException;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.eclipse.ecsp.services.device.dao.DeviceDao;
import org.eclipse.ecsp.services.device.model.Device;
import org.eclipse.ecsp.services.deviceactivation.dao.DeviceActivationDao;
import org.eclipse.ecsp.services.deviceactivation.dao.DeviceActivationStateDao;
import org.eclipse.ecsp.services.deviceactivation.model.DeviceActivation;
import org.eclipse.ecsp.services.factorydata.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.services.shared.dao.HcpInfoDao;
import org.eclipse.ecsp.springauth.client.rest.SpringAuthRestClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import javax.naming.directory.InvalidAttributeValueException;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceServiceV2.
 */
@Slf4j
public class DeviceServiceV2Test {

    public static final String SERIAL_NUMBER = "serial_number";
    public static final String HCP_AUTH_QUALIFIER_SECRET_KEY = "hcp_auth_qualifier_secret_key";
    private static final String RESPONSE1 = "{\n"
        + "  \"client_name\": \"HC34703\",\n"
        + "  \"client_secret\": \"8725372\"\n"
        + "}";
    public static final long ID = 12345L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServiceV2Test.class);
    private static final int ALPHA_NUM_LENGTH = 16;

    @InjectMocks
    private DeviceServiceV2 deviceServiceV2;

    @Mock
    private SpringAuthTokenGenerator springAuthTokenGenerator;

    @Mock
    private DeviceDao deviceDao;

    @Mock
    private HcpInfoDao hcpInfoDao;

    @Mock
    private DeviceActivationUtil deviceActivationUtil;

    @Mock
    private DeviceInfoSharedDao deviceInfoDao;

    @Mock
    private DeviceFactoryDataDao factoryDataDao;

    @Mock
    private DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;

    @Mock
    private EnvConfig<AuthProperty> envConfig;

    @Mock
    private DeviceActivationStateDao deviceActivationStateDao;

    @Mock
    private AssociationService associationService;

    @Mock
    private SpringAuthRestClient springAuthRestClient;

    @Mock
    private DefaultDeviceStateChangeObservable deviceStateChangeObservable;

    @Mock
    private DeviceActivationDao deviceActivationDao;

    @Mock
    private CryptographyUtil cryptographyUtil;

    @Before
    public void beforeEach() {
        initMocks(this);
        ReflectionTestUtils.setField(deviceServiceV2, "allowedTypes", new String[]{"dongle", "tcu", "hu", "dashcam"});
    }

    @Test
    public void activateDeviceNullRequestDataTest() {
        ActivationRequestData activationRequestData = null;
        LOGGER.info("Activation Request Data: {}", activationRequestData);
        String version = "v4";
        assertThrows(ApiValidationFailedException.class, () -> {
            deviceServiceV2.activateDevice(activationRequestData, version);
        });
    }

    @Test
    public void activateDeviceInvalidRequestDataTest() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin(" ");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(" ");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setDeviceType("ObdDongle");
        LOGGER.info("Activation Request Data: {}", activationRequestData);
        String version = "v4";
        assertThrows(ApiValidationFailedException.class, () -> {
            deviceServiceV2.activateDevice(activationRequestData, version);
        });
    }

    @Test
    public void activateDeviceBlankAad() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setAad(StringUtils.EMPTY);
        LOGGER.info("Activation Request Data: {}", activationRequestData);
        String version = "v4";
        assertThrows(ApiValidationFailedException.class, () -> {
            deviceServiceV2.activateDevice(activationRequestData, version);
        });
    }

    @Test
    public void activateDeviceInvalidAad() {
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
        LOGGER.info("Activation Request Data: {}", activationRequestData);
        String version = "v4";
        assertThrows(ApiValidationFailedException.class, () -> {
            deviceServiceV2.activateDevice(activationRequestData, version);
        });
    }

    @Test
    public void activateDeviceNullDeviceTypeTest() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setAad("NO");
        LOGGER.info("Activation Request Data: {}", activationRequestData);
        String version = "v4";
        assertThrows(ApiValidationFailedException.class, () -> {
            deviceServiceV2.activateDevice(activationRequestData, version);
        });
    }

    @Test
    public void activateDeviceInvalidDeviceTypeTest() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setDeviceType("xyz");
        activationRequestData.setAad("yES");
        LOGGER.info("Activation Request Data: {}", activationRequestData);
        Assert.assertNotNull(activationRequestData);
        String version = "v4";
        try {
            deviceServiceV2.activateDevice(activationRequestData, version);
        } catch (ApiValidationFailedException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void activateDeviceNullFactoryDataTest() {
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
        LOGGER.info("Activation Request Data: {}", activationRequestData);
        Assert.assertNotNull(activationRequestData);
        String version = "v4";
        try {
            deviceServiceV2.activateDevice(activationRequestData, version);
        } catch (ApiResourceNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void activateDeviceThrowsApiTechnicalExceptionTest() {
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
        LOGGER.info("Activation Request Data: {}", activationRequestData);
        String version = "v4";
        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setId(1L);
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setImei("123");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setFaulty(false);
        deviceFactoryData.setStolen(false);
        deviceFactoryData.setState("PROVISIONED");
        deviceFactoryData.setBssid("84284");
        deviceFactoryData.setIccid("26482468");
        deviceFactoryData.setImsi("24662482");
        deviceFactoryData.setModel("MX6424");
        deviceFactoryData.setPlatformVersion("v1");

        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Assert.assertNotNull(activationRequestData);
        try {
            deviceServiceV2.activateDevice(activationRequestData, version);
        } catch (ApiTechnicalException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void activateDeviceVinDisabledTest() {
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
        LOGGER.info("Activation Request Data: {}", activationRequestData);
        String version = "v4";
        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setId(1L);
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setImei("123");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setFaulty(false);
        deviceFactoryData.setStolen(false);
        deviceFactoryData.setState("PROVISIONED");
        deviceFactoryData.setBssid("84284");
        deviceFactoryData.setIccid("26482468");
        deviceFactoryData.setImsi("24662482");
        deviceFactoryData.setModel("MX6424");
        deviceFactoryData.setPlatformVersion("v1");

        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Mockito.doReturn(false).when(envConfig).getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        Assert.assertNotNull(activationRequestData);
        try {
            deviceServiceV2.activateDevice(activationRequestData, version);
        } catch (ApiTechnicalException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void activateDeviceEmptyAssociatedVinTest() {
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
        LOGGER.info("Activation Request Data: " + activationRequestData);
        String version = "v4";
        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setId(1L);
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setImei("123");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setFaulty(false);
        deviceFactoryData.setStolen(false);
        deviceFactoryData.setState("PROVISIONED");
        deviceFactoryData.setBssid("84284");
        deviceFactoryData.setIccid("26482468");
        deviceFactoryData.setImsi("24662482");
        deviceFactoryData.setModel("MX6424");
        deviceFactoryData.setPlatformVersion("v1");

        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Mockito.doReturn(true).when(envConfig).getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        Assert.assertNotNull(activationRequestData);

        try {
            deviceServiceV2.activateDevice(activationRequestData, version);
        } catch (ApiPreConditionFailedException e) {
            LOGGER.error(e.generalMessage());
        }
    }

    @Test
    public void activateDeviceEmptyTransactionIdTest() {
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
        LOGGER.info("Activation Request Data: " + activationRequestData);
        String version = "v4";
        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setId(1L);
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setImei("123");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setFaulty(false);
        deviceFactoryData.setStolen(false);
        deviceFactoryData.setState("PROVISIONED");
        deviceFactoryData.setBssid("84284");
        deviceFactoryData.setIccid("26482468");
        deviceFactoryData.setImsi("24662482");
        deviceFactoryData.setModel("MX6424");
        deviceFactoryData.setPlatformVersion("v1");

        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Mockito.doReturn(true).when(envConfig).getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        Mockito.doReturn("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0").when(deviceActivationStateDao)
            .getAssociatedVin(Mockito.any());
        Assert.assertNotNull(activationRequestData);

        try {
            deviceServiceV2.activateDevice(activationRequestData, version);
        } catch (ApiPreConditionFailedException e) {
            LOGGER.error(e.generalMessage());
        }
    }

    @Test
    public void activateDeviceRandomNumberGenerationFailTest1() {
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
        LOGGER.info("Activation Request Data: {}", activationRequestData);
        String version = "v4";
        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setId(1L);
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setImei("123");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setFaulty(false);
        deviceFactoryData.setStolen(false);
        deviceFactoryData.setState("PROVISIONED");
        deviceFactoryData.setBssid("84284");
        deviceFactoryData.setIccid("26482468");
        deviceFactoryData.setImsi("24662482");
        deviceFactoryData.setModel("MX6424");
        deviceFactoryData.setPlatformVersion("v1");
        String secretKey = "r$27T30@*";
        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Mockito.doReturn(true).when(envConfig).getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        Mockito.doReturn("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0").when(deviceActivationStateDao)
            .getAssociatedVin(Mockito.any());
        Mockito.doReturn("1221").when(deviceActivationStateDao).getTransactionId(Mockito.any());
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        Assert.assertNotNull(activationRequestData);

        try {
            deviceServiceV2.activateDevice(activationRequestData, version);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void activateDeviceRandomNumberGenerationFailTest2() {
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
        LOGGER.info("Activation Request Data: " + activationRequestData);
        String version = "v4";
        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setId(1L);
        deviceFactoryData.setSerialNumber("523749811223666");
        deviceFactoryData.setImei("123");
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setFaulty(false);
        deviceFactoryData.setStolen(false);
        deviceFactoryData.setState("PROVISIONED");
        deviceFactoryData.setBssid("84284");
        deviceFactoryData.setIccid("26482468");
        deviceFactoryData.setImsi("24662482");
        deviceFactoryData.setModel("MX6424");
        deviceFactoryData.setPlatformVersion("v1");
        String secretKey = "r$27T30@*";
        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Mockito.doReturn(true).when(envConfig).getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        Mockito.doReturn("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0").when(deviceActivationStateDao)
            .getAssociatedVin(Mockito.any());
        Mockito.doReturn("1221").when(deviceActivationStateDao).getTransactionId(Mockito.any());
        Mockito.doReturn(false).when(envConfig).getBooleanValue(AuthProperty.SECRET_VAULT_ENABLE_FLG);
        Mockito.doReturn(secretKey).when(envConfig).getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY);
        Assert.assertNotNull(activationRequestData);

        try {
            deviceServiceV2.activateDevice(activationRequestData, version);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void activateDeviceResourceNotFoundTest() {
        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        activationRequestData.setSerialNumber("523749811223666");
        activationRequestData.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequestData.setProductType("TestProductType");
        activationRequestData.setHwVersion("1.4.1");
        activationRequestData.setSwVersion("1.2.1");
        activationRequestData.setDeviceType("ObdDongle");
        String version = "v3";
        assertThrows(ApiResourceNotFoundException.class, () -> {
            deviceServiceV2.activateDevice(activationRequestData, version);
        });
    }

    @Test
    public void getDeviceInfoFactoryDataTest() {
        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setSerialNumber("12345");
        deviceFactoryData.setImei("123");
        deviceFactoryData.setId(1L);
        deviceFactoryData.setDeviceType("dongle");
        deviceFactoryData.setFaulty(false);
        deviceFactoryData.setStolen(false);
        deviceFactoryData.setIccid("26482468");
        deviceFactoryData.setSsid("1234112");
        deviceFactoryData.setBssid("84284");
        deviceFactoryData.setState("PROVISIONED");
        deviceFactoryData.setImsi("24663923");
        deviceFactoryData.setModel("MX6424");
        deviceFactoryData.setPlatformVersion("v1");
        deviceFactoryData.setManufacturingDate("2020-05-01");
        deviceFactoryData.setMsisdn("342223");
        deviceFactoryData.setImsi("2434121");
        deviceFactoryData.setRecordDate("2020-05-01");
        deviceFactoryData.setCreatedDate("2020-05-01");
        deviceFactoryData.setFactoryAdmin("xyz");
        deviceFactoryData.setPackageSerialNumber("HT23233N3");
        String serialNumber = "12345";
        LinkedHashMap<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(SERIAL_NUMBER, serialNumber);
        LOGGER.info("orderedMap: " + orderedMap);
        Mockito.when(factoryDataDao.find(orderedMap)).thenReturn(deviceFactoryData);
        assertEquals(deviceFactoryData, deviceServiceV2.getDeviceInfoFactoryData(orderedMap));
    }

    @Test
    public void mapHarmanIdsForVinsTest() {
        Mockito.doReturn(1).when(hcpInfoDao).mapHarmanIdsForVins(ID);
        Mockito.doReturn(1L).when(hcpInfoDao).getTempGroupSize(ID);
        boolean actualResponse = deviceServiceV2.mapHarmanIdsForVins(ID);
        assertTrue(actualResponse);
    }

    @Test
    public void validateDeviceTypePassTest() {
        String deviceType = "dongle";
        String[] allowedTypes = {"dongle", "tcu", "headunit"};
        boolean validateDeviceType = deviceServiceV2.validateDeviceType(deviceType, allowedTypes);
        assertTrue(validateDeviceType);
    }

    @Test
    public void validateDeviceTypeFailTest() {
        String deviceType = "dashcam";
        String[] allowedTypes = {"dongle", "tcu", "headunit"};
        boolean validateDeviceType = deviceServiceV2.validateDeviceType(deviceType, allowedTypes);
        assertNotEquals(true, validateDeviceType);
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
        DeviceFactoryData difdValue = difd;
        boolean isVersionV4 = true;
        Mockito.doReturn(1).when(deviceDao).insert(newDevice, false);
        Mockito.doReturn(1).when(deviceInfoDao).updateDeviceInfo(Mockito.any(), Mockito.any(), Mockito.any());
        deviceServiceV2.updateAllActivationTables(newDevice, difdValue, activationRequestData, isVersionV4);
        Mockito.verify(deviceDao, times(1)).insert(newDevice, false);
    }

    @Test
    public void updateAllActivationTablesNullDeviceTypeTest() {
        DeviceFactoryData difd = new DeviceFactoryData();
        difd.setId(1L);
        difd.setImei("123");
        difd.setSerialNumber("12345");
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
        activationRequestData.setDeviceType("tcu");
        boolean isVersionV4 = true;
        Device newDevice = new Device();
        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .updateDeviceType(difd.getId(), activationRequestData.getDeviceType(), "Device Activated");
        Mockito.doReturn(1).when(deviceInfoDao).updateDeviceInfo(Mockito.any(), Mockito.any(), Mockito.any());
        deviceServiceV2.updateAllActivationTables(newDevice, difd, activationRequestData, isVersionV4);
        Mockito.verify(deviceInfoFactoryDataDao, times(1))
            .updateDeviceType(difd.getId(), activationRequestData.getDeviceType(), "Device Activated");
    }

    @Test
    public void updateAllActivationTablesNotV4Test() {
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
        boolean isVersionV4 = false;
        Device newDevice = new Device();
        Mockito.doReturn(1).when(deviceDao).insert(newDevice, false);
        Mockito.doReturn(1).when(deviceInfoDao).updateDeviceInfo(Mockito.any(), Mockito.any(), Mockito.any());
        deviceServiceV2.updateAllActivationTables(newDevice, difd, activationRequestData, isVersionV4);
        Mockito.verify(deviceDao, times(1)).insert(newDevice, false);
    }

    @Test
    public void sendActivationFailureEventTest() {
        ActivationRequestData dto = new ActivationRequestData();
        Mockito.when(associationService.sendEventToTopic(Mockito.any(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString())).thenReturn(true);
        ReflectionTestUtils.invokeMethod(deviceServiceV2, "sendActivationFailureEvent", dto);
        assertTrue(associationService.sendEventToTopic(Mockito.any(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString()));
    }

    @Test
    public void activateAccountFromSpringAuthPassTestForActivation() {
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
        LOGGER.info("Activation Request Data: " + activationRequestData);
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

        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Mockito.doReturn(false).when(envConfig).getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        String secretKey = "HarmanAct";
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        Mockito.doReturn("dummytoken").when(springAuthTokenGenerator).fetchSpringAuthToken();
        Mockito.doNothing().when(springAuthRestClient).createRegisteredClient("dummytoken", "HC34703",
            "498jd328e", "Dongle");
        Mockito.doNothing().when(deviceStateChangeObservable).newDeviceActivated(deviceStateActivation);
        Assert.assertNotNull(activationRequestData);
        deviceServiceV2.activateDevice(activationRequestData, "v4");
    }

    @Test
    public void activateAccountFromSpringAuthPassTestForReActivation() throws InvalidAttributeValueException {
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
        LOGGER.info("Re Activation Request Data: {}", activationRequestData);
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
        Assert.assertNotNull(activationRequestData);
        deviceServiceV2.activateDevice(activationRequestData, "v4");
    }

    @Test
    public void activateAccountFromSpringAuthPassTestForV3Activation() {
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
        LOGGER.info("Activation Request Data: {}", activationRequestData);
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

        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Mockito.doReturn(false).when(envConfig).getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        String secretKey = "HarmanAct";
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        Mockito.doReturn("dummytoken").when(springAuthTokenGenerator).fetchSpringAuthToken();
        Mockito.doNothing().when(springAuthRestClient).createRegisteredClient("dummytoken", "HC34703",
            "498jd328e", "Dongle");
        Mockito.doNothing().when(deviceStateChangeObservable).newDeviceActivated(deviceStateActivation);
        Assert.assertNotNull(activationRequestData);
        deviceServiceV2.activateDevice(activationRequestData, "v3");
    }

    @Test
    public void activateAccountFromSpringAuthPassTestForV3ReActivation() throws InvalidAttributeValueException {
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
        LOGGER.info("Re Activation Request Data: {}", activationRequestData);
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
        deviceServiceV2.activateDevice(activationRequestData, "v3");
        Assert.assertNotNull(activationRequestData);
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
        LOGGER.info("Re Activation Request Data: {}", activationRequestData);
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
        Mockito.doReturn(deviceFactoryData).when(factoryDataDao).find(Mockito.any());
        Mockito.doReturn(null).when(deviceDao).findActiveDevice(1L);
        Mockito.doReturn(false).when(envConfig).getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        assertThrows(ApiTechnicalException.class, () -> {
            deviceServiceV2.activateDevice(activationRequestData, "v3");
        });
    }

    @Test
    public void activateFirstTimeDeviceV2() throws Exception {
        ActivationRequestDataV2 activationRequestDataV2 = new ActivationRequestDataV2(
            "49d5dab90474bcba5c346d14ceff15f4f0063700", "t4Iz9JR2hGzFbCYRkP+4GzRHHgf4aOUVhMiB9TxD/LQ=");
        String secretKey = "HarmanAct";
        String vaultPreSharedKey = "t4Iz9JR2hGzFbCYRkP+4GzRHHgf4aOUVhMiB9TxD/LQ=";
        String encryptedPassKey = "GPHzwNC1qJIrOE0T";
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY))
            .thenReturn(secretKey);
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_ACTIVATION_PRESHARED_KEY))
            .thenReturn(vaultPreSharedKey);
        Mockito.doReturn(encryptedPassKey).when(cryptographyUtil)
            .decrypt(secretKey, DatatypeConverter.parseBase64Binary(vaultPreSharedKey));
        Mockito.doReturn(0L).when(deviceActivationDao).activeDeviceCount(activationRequestDataV2.getJitActId());

        Mockito.doReturn("".getBytes()).when(cryptographyUtil).encrypt(Mockito.anyString(), Mockito.any());
        DeviceActivation activation = new DeviceActivation();
        //activation.setPasscode("498jd328e");
        Mockito.doReturn(1).when(deviceActivationDao).insertDeviceActivation(activation);
        //activation.setHarmanId("HU34703");
        Mockito.doNothing().when(deviceActivationDao).updateHarmanId(activation);
        Mockito.doReturn("dummytoken").when(springAuthTokenGenerator).fetchSpringAuthToken();
        Mockito.doNothing().when(springAuthRestClient).createRegisteredClient("dummytoken", "HU34703",
                "498jd328e", "HU");
        ActivationResponse activationResponse = deviceServiceV2.activateDevice(activationRequestDataV2, "v5");
        assertNotNull(activationResponse.getDeviceId());
        assertNotNull(activationResponse.getPasscode());
    }

    @Test
    public void reactivationV2() throws Exception {
        ActivationRequestDataV2 activationRequestDataV2 = new ActivationRequestDataV2(
            "49d5dab90474bcba5c346d14ceff15f4f0063700", "t4Iz9JR2hGzFbCYRkP+4GzRHHgf4aOUVhMiB9TxD/LQ=");
        String secretKey = "HarmanAct";
        String vaultPreSharedKey = "t4Iz9JR2hGzFbCYRkP+4GzRHHgf4aOUVhMiB9TxD/LQ=";
        String dectyptedPassKey = "GPHzwNC1qJIrOE0T";
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_ACTIVATION_PRESHARED_KEY))
            .thenReturn(vaultPreSharedKey);
        Mockito.doReturn(dectyptedPassKey).when(cryptographyUtil)
            .decrypt(secretKey, DatatypeConverter.parseBase64Binary(vaultPreSharedKey));
        Mockito.doReturn(1L).when(deviceActivationDao).activeDeviceCount(activationRequestDataV2.getJitActId());
        DeviceActivation activation = new DeviceActivation();
        activation.setHarmanId("HULKE5LUZAMR31");
        List<DeviceActivation> activations = new ArrayList<>();
        activations.add(activation);
        Mockito.doReturn(activations).when(deviceActivationDao)
            .findActiveDevice(activationRequestDataV2.getJitActId());

        Mockito.doReturn("dummytoken").when(springAuthTokenGenerator).fetchSpringAuthToken();
        Map testMap = new LinkedHashMap<>();
        Mockito.doReturn(testMap).when(springAuthRestClient).deleteRegisteredClient("dummytoken", "HULKE5LUZAMR31");

        Mockito.doReturn("".getBytes()).when(cryptographyUtil).encrypt(Mockito.anyString(), Mockito.any());
        Mockito.doNothing().when(springAuthRestClient)
            .updateRegisteredClient("dummytoken", "HULKE5LUZAMR31",
                "8725372", "HU", "approved");
        Mockito.doNothing().when(deviceActivationDao).updatePasscode(activation);
        ActivationResponse activationResponse = deviceServiceV2.activateDevice(activationRequestDataV2, "v5");
        LOGGER.info("activationResponse : {}", activationResponse);
        assertNotNull(activationResponse.getDeviceId());
        assertNotNull(activationResponse.getPasscode());
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void inValidPresharedKeyFromVaultTest() {
        ActivationRequestDataV2 activationRequestDataV2 = new ActivationRequestDataV2(
            "49d5dab90474bcba5c346d14ceff15f4f0063700", "t4Iz9JR2hGzFbCYRkP+4GzRHHgf4aOUVhMiB9TxD/LQ=");
        String secretKey = "HarmanAct";
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        deviceServiceV2.activateDevice(activationRequestDataV2, "v5");
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void shouldThrowApiPreConditionFailedException() {
        ActivationRequestDataV2 activationRequestDataV2 = new ActivationRequestDataV2(
            "49d5dab90474bcba5c346d14ceff15f4f0063700", "t4Iz9JR2hGzFbCYRkP+4GzRHHgf4aOUVhMiB9TxD/LQ=");
        String secretKey = "HarmanAct";
        String vaultPreSharedKey = "t4Iz9JR2hGzFbCYRkP+4GzRHHgf4aOUVhMiB9TxD/LQ=";
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_ACTIVATION_PRESHARED_KEY))
            .thenReturn(vaultPreSharedKey);
        deviceServiceV2.activateDevice(activationRequestDataV2, "v5");
    }

    @Test
    public void getPresahredKetTest() throws Exception {
        String secretKey = "HarmanAct";
        String passKey = "GPHzwNC1qJIrOE0T";
        String encryptedKey = "t4Iz9JR2hGzFbCYRkP+4GzRHHgf4aOUVhMiB9TxD/LQ=";
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        Mockito.doReturn(passKey).when(cryptographyUtil).getRandomAlphanumericString(ALPHA_NUM_LENGTH);
        Mockito.doReturn(encryptedKey.getBytes(StandardCharsets.UTF_8)).when(cryptographyUtil)
            .encrypt(secretKey, passKey.getBytes(StandardCharsets.UTF_8));
        PreSharedKeyResponse preSharedKeyResponse = deviceServiceV2.getPresharedKey();
        assertNotNull(preSharedKeyResponse);
    }

    @Test(expected = ApiTechnicalException.class)
    public void shouldThrowExceptionGetPreSharedKey() {
        String secretKey = "HarmanAct";
        String passKey = "GPHzwNC1qJIrOE0T";
        Mockito.when(envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY)).thenReturn(secretKey);
        Mockito.doReturn(passKey).when(cryptographyUtil).getRandomAlphanumericString(ALPHA_NUM_LENGTH);
        deviceServiceV2.getPresharedKey();
    }
}