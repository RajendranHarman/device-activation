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

package org.eclipse.ecsp.dao;

import org.eclipse.ecsp.auth.lib.dao.DeviceFactoryData;
import org.eclipse.ecsp.auth.lib.dao.DeviceFactoryDataDao;
import org.eclipse.ecsp.auth.lib.dao.DeviceInfoFactoryDataMapper;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceFactoryDataDao.
 */
public class DeviceFactoryDataDaoTest {

    private static final long FACTORY_ID = 12345L;
    private static final String SQL_FOR_UPDATE_FACTORY_DATA_STATE =
        "update public.\"DeviceInfoFactoryData\" set state=? where \"ID\"=?";
    private static final String SQL_FOR_UPDATE_STATE_TO_PROVISIONED =
        "update public.\"DeviceInfoFactoryData\" set \"state\"=? where \"ID\"=?";

    @InjectMocks
    private DeviceFactoryDataDao deviceFactoryDataDao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void updateFactoryDataStateTest() {
        String state = "Test";
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_FOR_UPDATE_FACTORY_DATA_STATE,
            new Object[]{state, FACTORY_ID});
        int actualResult = deviceFactoryDataDao.updateFactoryDataState(FACTORY_ID, state);
        assertEquals(1, actualResult);
    }

    @Test
    public void updateStateToProvisionedTest() {
        String factoryId = "12345";
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(SQL_FOR_UPDATE_STATE_TO_PROVISIONED, new Object[]{"PROVISIONED", factoryId});
        int actualResult = deviceFactoryDataDao.updateStateToProvisioned(factoryId);
        assertEquals(1, actualResult);
    }

    @Test
    public void findByIdTest() {
        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setId(1L);
        deviceFactoryData.setSerialNumber("12345");
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
        //Testing getters
        deviceFactoryData.getFactoryAdmin();
        deviceFactoryData.getState();
        deviceFactoryData.getFaulty();
        deviceFactoryData.getStolen();
        deviceFactoryData.getBssid();
        deviceFactoryData.getSsid();
        deviceFactoryData.getCreatedDate();
        deviceFactoryData.getIccid();
        deviceFactoryData.getImsi();
        deviceFactoryData.getMsisdn();
        deviceFactoryData.getManufacturingDate();
        deviceFactoryData.getModel();
        deviceFactoryData.getPackageSerialNumber();
        deviceFactoryData.getPlatformVersion();
        deviceFactoryData.getRecordDate();
        long factoryId = 1L;
        Mockito.doReturn(deviceFactoryData).when(jdbcTemplate)
            .queryForObject(Mockito.any(), (Class<Object>) Mockito.any(),
                Mockito.any());
        DeviceFactoryData actualDeviceFactoryData = deviceFactoryDataDao.findById(factoryId);
        assertNull(actualDeviceFactoryData);
    }

    @Test
    public void findTest1() {

        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setSerialNumber("12345");
        LinkedHashMap<String, Object> attributeValueMap = new LinkedHashMap<String, Object>();
        attributeValueMap.put("serial_number", activationRequestData.getSerialNumber());

        DeviceFactoryData deviceFactoryData = new DeviceFactoryData();
        deviceFactoryData.setId(1L);
        deviceFactoryData.setSerialNumber("12345");
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

        List<DeviceFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceFactoryData);

        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceInfoFactoryDataMapper.class));
        DeviceFactoryData actualDeviceFactoryData = deviceFactoryDataDao.find(attributeValueMap);
        assertEquals(actualDeviceFactoryData, deviceFactoryData);
    }

    @Test
    public void findTest2() {

        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setSerialNumber("12345");
        LinkedHashMap<String, Object> attributeValueMap = new LinkedHashMap<String, Object>();
        attributeValueMap.put("serial_number", activationRequestData.getSerialNumber());
        Mockito.doReturn(Collections.emptyList()).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceInfoFactoryDataMapper.class));
        DeviceFactoryData actualDeviceFactoryData = deviceFactoryDataDao.find(attributeValueMap);
        assertNull(actualDeviceFactoryData);
    }
}