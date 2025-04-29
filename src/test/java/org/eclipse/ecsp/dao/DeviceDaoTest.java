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

import org.eclipse.ecsp.services.device.dao.DeviceDao;
import org.eclipse.ecsp.services.device.model.Device;
import org.eclipse.ecsp.services.device.model.DeviceMapper;
import org.eclipse.ecsp.services.shared.util.HealthCheckConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceDao.
 */
public class DeviceDaoTest {
    private static final long ID = 12345L;
    private static final long RANDOM_NUMBER = 2L;
    private static final int ROWS = 2;
    private static final String SQL_FOR_UPDATE_DEVICE = "update public.\"Device\" set \"HarmanID\"=? where \"ID\"=?";
    private static final String SQL_FOR_UPDATE_REGISTERED_SCOPE_ID_BY_ID =
        "update public.\"Device\" set \"registered_scope_id\"=? where \"ID\"=?";
    private static final String SQL_FOR_UPDATE_REGISTERED_SCOPE_ID_BY_HARMAN_ID =
        "update public.\"Device\" set \"registered_scope_id\"=? where \"HarmanID\"=?";
    private static final String SQL_FOR_DEACTIVATE_HARMAN_ID =
        "update public.\"Device\" set \"IsActive\"=false where \"HarmanID\"=?";
    private static final String SQL_FOR_DEACTIVATE =
        "update \"Device\" set \"IsActive\"=false where \"HarmanID\" in (select \"HarmanID\" from "
            + "\"HCPInfo\" where \"SerialNumber\"=?)";
    private static final String SQL_FOR_HEALTH_CHECK = "SELECT " + HealthCheckConstants.DB_CONN_SUCCESS_CODE;

    @InjectMocks
    private DeviceDao deviceDao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void updateDeviceTest() {
        String deviceId = "HU1";
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_FOR_UPDATE_DEVICE, new Object[]{deviceId, ID});
        int actualResult = deviceDao.updateDevice(deviceId, ID);
        assertEquals(1, actualResult);
    }

    @Test
    public void updateRegisteredScopeIdByIdTest() {
        String registerScopeId = "test";
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(SQL_FOR_UPDATE_REGISTERED_SCOPE_ID_BY_ID, new Object[]{registerScopeId, ID});
        int actualResult = deviceDao.updateRegisteredScopIdById(ID, registerScopeId);
        assertEquals(1, actualResult);
    }

    @Test
    public void updateRegisteredScopIdByHarmanIdTest() {
        String harmanId = "HU1";
        String registerScopeId = "test";
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(SQL_FOR_UPDATE_REGISTERED_SCOPE_ID_BY_HARMAN_ID, new Object[]{registerScopeId, harmanId});
        int actualResult = deviceDao.updateRegisteredScopIdByHarmanId(harmanId, registerScopeId);
        assertEquals(1, actualResult);
    }

    @Test
    public void deactivateHarmanIdTest() {
        String harmanId = "HU1";
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(SQL_FOR_DEACTIVATE_HARMAN_ID, new Object[]{harmanId}, new int[]{Types.VARCHAR});
        int actualResult = deviceDao.deactivateHarmanId(harmanId);
        assertEquals(1, actualResult);
    }

    @Test
    public void deactivateTest() {
        String serialNumber = "12345";
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_FOR_DEACTIVATE, serialNumber);
        int actualResult = deviceDao.deactivate(serialNumber);
        assertEquals(1, actualResult);
    }

    @Test
    public void healthCheckTest() {
        Mockito.doReturn(1).when(jdbcTemplate).queryForObject(SQL_FOR_HEALTH_CHECK, Integer.class);
        int actualResult = deviceDao.healthCheck();
        assertEquals(1, actualResult);
    }

    @Test
    public void findByDeviceIdTest1() {
        String deviceId = "HU123";
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceMapper.class));
        Device actualResponse = deviceDao.findByDeviceId(deviceId);
        assertNull(actualResponse);
    }

    @Test
    public void findByDeviceIdTest2() {
        Device device = new Device();
        Timestamp date = new Timestamp(1L);
        device.setId(1L);
        device.setActivationDate(date);
        device.setPasscode("24828rh2yr2");
        device.setHarmanId("HUKF9EETO2OQ00");
        device.setRandomNumber(1L);
        device.setRegisteredScopeId("scope1");
        String deviceId = "HUKF9EETO2OQ00";
        List<Device> devices = new ArrayList<>();
        devices.add(device);
        Mockito.doReturn(devices).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceMapper.class));
        Device actualResponse = deviceDao.findByDeviceId(deviceId);
        assertNotNull(actualResponse);
    }

    @Test
    public void checkIfActivatedAlreadyTest1() {
        String vin = "vin123";
        String serialNumber = "123";
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.anyObject(), (int[]) Mockito.any());
        Device actualResponse = deviceDao.checkIfActivatedAlready(vin, serialNumber);
        assertNull(actualResponse);
    }

    @Test
    public void checkIfActivatedAlreadyTest2() {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("HarmanID", "HUKF9EETO2OQ00");
        deviceMap.put("RandomNumber", RANDOM_NUMBER);
        List<Map<String, Object>> devices = new ArrayList<>();
        devices.add(deviceMap);
        String vin = "vin123";
        String serialNumber = "123";
        Mockito.doReturn(devices).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.anyObject(), (int[]) Mockito.any());
        Device actualResponse = deviceDao.checkIfActivatedAlready(vin, serialNumber);
        assertNotNull(actualResponse);
    }

    @Test
    public void updateForReplaceDeviceTest() {
        Device device = new Device();
        device.setId(1L);
        device.setHarmanId("HU1");
        device.setRandomNumber(RANDOM_NUMBER);
        device.setPasscode("1ehue");
        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString(), (Object) Mockito.any());
        int response = deviceDao.updateForReplaceDevice(device);
        assertEquals(0, response);
    }

    @Test
    public void checkLoginTest1() {
        String deviceId = "HU1";
        String passcode = "24gdrd";
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.anyObject(), (int[]) Mockito.any());
        Device response = deviceDao.checkLogin(deviceId, passcode);
        assertNull(response);
    }

    @Test
    public void checkLoginTest2() {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("HarmanID", "HUKF9EETO2OQ00");
        deviceMap.put("PassCode", "24gdrd");
        List<Map<String, Object>> devices = new ArrayList<>();
        devices.add(deviceMap);
        String deviceId = "HU1";
        String passcode = "24gdrd";
        Mockito.doReturn(devices).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.anyObject(), (int[]) Mockito.any());
        Device response = deviceDao.checkLogin(deviceId, passcode);
        assertNotNull(response);
    }

    @Test
    public void checkIfActivatedAlreadyTest3() {
        long factoryDataId = 1L;
        Mockito.doReturn(null).when(jdbcTemplate).queryForList(Mockito.anyString(), (Object[]) Mockito.anyObject());
        Device response = deviceDao.checkIfActivatedAlready(factoryDataId);
        assertNull(response);
    }

    @Test
    public void checkIfActivatedAlreadyTest4() {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("HarmanID", "HUKF9EETO2OQ00");
        deviceMap.put("RandomNumber", RANDOM_NUMBER);
        List<Map<String, Object>> devices = new ArrayList<>();
        devices.add(deviceMap);
        long factoryDataId = 1L;
        Mockito.doReturn(devices).when(jdbcTemplate).queryForList(Mockito.anyString(), (Object[]) Mockito.anyObject());
        Device response = deviceDao.checkIfActivatedAlready(factoryDataId);
        assertNotNull(response);
    }

    @Test
    public void findActiveDeviceTest() {
        long factoryDataId = 1L;
        List<Map<String, Object>> devices = new ArrayList<>();
        Mockito.doReturn(devices).when(jdbcTemplate).queryForList(Mockito.anyString(), (Object[]) Mockito.anyObject());
        List<Device> response = deviceDao.findActiveDevice(factoryDataId);
        assertNull(response);
    }

    @Test
    public void findActiveDeviceTest2() {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("HarmanID", "HUKF9EETO2OQ00");
        deviceMap.put("RandomNumber", RANDOM_NUMBER);
        deviceMap.put("ID", RANDOM_NUMBER);
        List<Map<String, Object>> devices = new ArrayList<>();
        devices.add(deviceMap);
        long factoryDataId = 1L;
        Mockito.doReturn(devices).when(jdbcTemplate).queryForList(Mockito.anyString(), (Object[]) Mockito.anyObject());
        List<Device> response = deviceDao.findActiveDevice(factoryDataId);
        assertNotNull(response);
    }

    @Test
    public void updatePasscodeTest() {
        Device device = new Device();
        device.setId(1L);
        device.setHarmanId("HU1");
        device.setRandomNumber(RANDOM_NUMBER);
        device.setPasscode("1ehue");
        DeviceDao deviceDaoMock = spy(deviceDao);
        Mockito.doReturn(0).when(deviceDaoMock).updateForReplaceDevice(device);
        deviceDao.updatePasscode(device);
        assertEquals(0, deviceDaoMock.updateForReplaceDevice(device));
    }

    @Test
    public void insertTest() {
        Device device = new Device();
        boolean preactivation = true;
        Mockito.doReturn(ROWS).when(jdbcTemplate).update((PreparedStatementCreator) Mockito.any(), Mockito.any());
        int rows = deviceDao.insert(device, preactivation);
        assertEquals(ROWS, rows);
    }

    @Test
    public void healthCheckTest1() {
        Mockito.doReturn(null).when(jdbcTemplate).queryForObject(SQL_FOR_HEALTH_CHECK, Integer.class);
        int actualResult = deviceDao.healthCheck();
        assertEquals(0, actualResult);
    }
}