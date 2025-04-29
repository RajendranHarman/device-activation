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

import org.eclipse.ecsp.services.deviceactivation.dao.DeviceActivationDao;
import org.eclipse.ecsp.services.deviceactivation.model.DeviceActivation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceActivationDao.
 */
public class DeviceActivationDaoTest {

    private static final String INSERT =
        "insert into device_activation(jitact_id,harman_id,passcode,activation_date,device_type,is_active) "
            + "values(?,?,?,?,?,?)";
    private static final String FIND_ACTIVE_RECORD =
        "select id from device_activation where jitact_id=? and is_active=true";
    private static final String ACTIVE_DEVICE_COUNT =
        "select count(*) from device_activation where jitact_id=? and is_active=true";
    private static final String UPDATE_HARMAN_ID = "update device_activation set harman_id=? where jitact_id=?";
    private static final String UPDATE_PASSCODE =
        "update device_activation set passcode=? , activation_date=? where id=?";
    private static final String GET_ACTIVE_DEVICE =
        "select device_activation.id,device_activation.jitact_id,device_activation.harman_id,"
        + "device_activation.passcode,device_activation.activation_date,device_activation.device_type,"
        + "device_activation.is_active from device_activation where jitact_id=? and is_active=true";
    private static final String JITACT_ID = "49d5dab90474bcba5c346d14ceff15f4f0063700";

    @InjectMocks
    DeviceActivationDao deviceActivationDao;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Before
    public void beforeEach() {

        initMocks(this);
    }

    @Test
    public void insertDeviceActivationTest() {
        DeviceActivation deviceActivation = new DeviceActivation();
        deviceActivation.setActivationDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        deviceActivation.setActive(true);
        deviceActivation.setDeviceType("hu");
        deviceActivation.setPasscode("hLFPy6YNsgyBRDZTlfexANrBqdUwSjqpXm5nVl70djo=");
        deviceActivation.setJitactId(JITACT_ID);
        deviceActivation.setHarmanId(null);
        Mockito.doReturn(1).when(jdbcTemplate).update((PreparedStatementCreator) Mockito.any(), Mockito.any());
        int rows = deviceActivationDao.insertDeviceActivation(deviceActivation);
        assertEquals(1, rows);
    }

    @Test
    public void updateHarmanIdTest() {
        DeviceActivation deviceActivation = new DeviceActivation();
        deviceActivation.setJitactId(JITACT_ID);
        deviceActivation.setHarmanId("HUAJF8H0LN7Y82");
        Mockito.when(jdbcTemplate.update(UPDATE_HARMAN_ID, deviceActivation.getHarmanId(),
            deviceActivation.getJitactId())).thenReturn(1);

        deviceActivationDao.updateHarmanId(deviceActivation);
        assertEquals(1, jdbcTemplate.update(UPDATE_HARMAN_ID, deviceActivation.getHarmanId(),
            deviceActivation.getJitactId()));
    }

    @Test
    public void updatePasscodeTest() {
        DeviceActivation deviceActivation = new DeviceActivation();
        deviceActivation.setActivationDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        deviceActivation.setPasscode("hLFPy6YNsgyBRDZTlfexANrBqdUwSjqpXm5nVl70djo=");
        deviceActivation.setId(1L);

        Mockito.when(jdbcTemplate.update(UPDATE_PASSCODE, deviceActivation.getPasscode(),
            deviceActivation.getActivationDate(), deviceActivation.getId())).thenReturn(1);
        deviceActivationDao.updatePasscode(deviceActivation);
        assertEquals(1, jdbcTemplate.update(UPDATE_PASSCODE, deviceActivation.getPasscode(),
            deviceActivation.getActivationDate(), deviceActivation.getId()));
    }

    @Test
    public void activeDeviceCountTest() {
        Mockito.doReturn(1L).when(jdbcTemplate)
            .queryForObject(ACTIVE_DEVICE_COUNT, new Object[]{JITACT_ID}, Long.class);
        long recordCount = deviceActivationDao.activeDeviceCount(JITACT_ID);
        assertEquals(1, recordCount);
    }

    @Test
    public void findActiveDeviceTest() {
        Map<String, Object> activations = new HashMap<>();
        activations.put("activation_date", new Timestamp(Calendar.getInstance().getTimeInMillis()));
        activations.put("device_type", "hu");
        activations.put("harman_id", "HUAJF8H0LN7Y82");
        activations.put("jitact_id", JITACT_ID);
        activations.put("passcode", "hLFPy6YNsgyBRDZTlfexANrBqdUwSjqpXm5nVl70djo=");
        activations.put("is_active", true);
        activations.put("id", 1L);
        List<Map<String, Object>> deviceActivations = new ArrayList<>();
        deviceActivations.add(activations);
        Mockito.doReturn(deviceActivations).when(jdbcTemplate).queryForList(GET_ACTIVE_DEVICE, JITACT_ID);
        List<DeviceActivation> deviceActivationsMap = deviceActivationDao.findActiveDevice(JITACT_ID);
        assertEquals(1, deviceActivationsMap.size());
    }

}
