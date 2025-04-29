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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.ecsp.services.factorydata.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryData;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryDataWithSubscription;
import org.eclipse.ecsp.services.factorydata.domain.DeviceState;
import org.eclipse.ecsp.services.factorydata.domain.DeviceStateHistory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceInfoFactoryDataDao.
 */
@Slf4j
public class DeviceInfoFactoryDataDaoTest {

    private static final long FACTORY_ID = 12345L;
    private static final int SIZE = 20;
    private static final int PAGE = 5;
    private static final String SQL_FOR_CHANGE_DEVICE_STATE_TO_STOLEN =
        "update public.\"DeviceInfoFactoryData\" set isstolen=true where \"ID\"=?";
    private static final String SQL_FOR_CHANGE_DEVICE_STATE_TO_FAULTY =
        "update public.\"DeviceInfoFactoryData\" set isfaulty=true where \"ID\"=?";
    private static final String SQL_FOR_CHANGE_DEVICE_STATE_TO_ACTIVE =
        "update public.\"DeviceInfoFactoryData\" set state=?, isstolen=false, isfaulty=false where \"ID\"=?";
    private static final String SQL_FOR_UPDATE_DEVICE_TYPE =
        "update public.\"DeviceInfoFactoryData\" set device_type=? where \"ID\"=?";
    private static final String SQL_FOR_CHANGE_DEVICE_STATE_FOR_STOLEN_OR_FAULTY =
        "update public.\"DeviceInfoFactoryData\" set state=?  where \"ID\"=?";
    private static final String SQL_FOR_UPDATE_FACTORY_DATA_STATE =
        "update public.\"DeviceInfoFactoryData\" set state=? where \"ID\"=?";
    private static final String GET_MODEL_BY_IMEI =
        "select \"model\" from public.\"DeviceInfoFactoryData\" where \"imei\" = ?";
    private static final String STATE = "state";

    @InjectMocks
    private DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void changeDeviceStateToStolenTest1() {
        
        String state = "STOLEN";
        final String action = "action1";
        boolean exceptionCaught = false;
        Mockito.doReturn(0).when(jdbcTemplate).update(SQL_FOR_CHANGE_DEVICE_STATE_TO_STOLEN, new Object[]{FACTORY_ID});
        try {
            deviceInfoFactoryDataDao.changeDeviceState(FACTORY_ID, state, action);
        } catch (InvalidParameterException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void changeDeviceStateToStolenTest2() {
        
        String state = "STOLEN";
        final String action = "action1";
        boolean exceptionCaught = false;
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_FOR_CHANGE_DEVICE_STATE_TO_STOLEN, new Object[]{FACTORY_ID});
        try {
            deviceInfoFactoryDataDao.changeDeviceState(FACTORY_ID, state, action);
        } catch (InvalidParameterException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void changeDeviceStateToFaultyTest() {
        
        String state = "FAULTY";
        final String action = "action2";
        boolean exceptionCaught = false;
        Mockito.doReturn(0).when(jdbcTemplate).update(SQL_FOR_CHANGE_DEVICE_STATE_TO_FAULTY, new Object[]{FACTORY_ID});
        try {
            deviceInfoFactoryDataDao.changeDeviceState(FACTORY_ID, state, action);
        } catch (InvalidParameterException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void changeDeviceStateToActiveTest() {
        
        String state = "ACTIVE";
        final String action = "action3";
        boolean exceptionCaught = false;
        Mockito.doReturn(0).when(jdbcTemplate).update(SQL_FOR_CHANGE_DEVICE_STATE_TO_ACTIVE,
            new Object[]{state, FACTORY_ID});
        try {
            deviceInfoFactoryDataDao.changeDeviceState(FACTORY_ID, state, action);
        } catch (InvalidParameterException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void changeDeviceStateDefaultTest() {
        
        String state = "PROVISIONED";
        final String action = "action4";
        boolean exceptionCaught = false;
        Mockito.doReturn(0).when(jdbcTemplate).update(SQL_FOR_CHANGE_DEVICE_STATE_TO_ACTIVE,
            new Object[]{state, FACTORY_ID});
        try {
            deviceInfoFactoryDataDao.changeDeviceState(FACTORY_ID, state, action);
        } catch (InvalidParameterException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void updateDeviceTypeFailedTest() {
        
        String deviceType = "dongle";
        final String action = "Activated";
        boolean exceptionCaught = false;
        Mockito.doReturn(0).when(jdbcTemplate).update(SQL_FOR_UPDATE_DEVICE_TYPE,
            new Object[]{deviceType, FACTORY_ID});
        try {
            deviceInfoFactoryDataDao.updateDeviceType(FACTORY_ID, deviceType, action);
        } catch (InvalidParameterException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void updateDeviceTypeTest() {
        
        String deviceType = "dongle";
        final String action = "Device Reactivated";
        boolean exceptionCaught = false;
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_FOR_UPDATE_DEVICE_TYPE, new Object[]{deviceType, FACTORY_ID});
        try {
            deviceInfoFactoryDataDao.updateDeviceType(FACTORY_ID, deviceType, action);
        } catch (InvalidParameterException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void updateDeviceTypeNullActionTest() {
        
        String deviceType = "dongle";
        boolean exceptionCaught = false;
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_FOR_UPDATE_DEVICE_TYPE, new Object[]{deviceType, FACTORY_ID});
        try {
            deviceInfoFactoryDataDao.updateDeviceType(FACTORY_ID, deviceType, null);
        } catch (InvalidParameterException e) {
            exceptionCaught = true;
        }
        assertFalse(exceptionCaught);
    }

    @Test
    public void changeDeviceStateForStolenOrFaultyTest1() {
        
        String state = "stolen";
        final String action = "Activated";
        boolean exceptionCaught = false;
        Mockito.doReturn(0).when(jdbcTemplate)
            .update(SQL_FOR_CHANGE_DEVICE_STATE_FOR_STOLEN_OR_FAULTY, new Object[]{state, FACTORY_ID});
        try {
            deviceInfoFactoryDataDao.changeDeviceStateForStolenOrFaulty(FACTORY_ID, state, action);
        } catch (InvalidParameterException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void changeDeviceStateForStolenOrFaultyTest2() {
        
        String state = "stolen";
        final String action = "Activated";
        boolean exceptionCaught = false;
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(SQL_FOR_CHANGE_DEVICE_STATE_FOR_STOLEN_OR_FAULTY, new Object[]{state, FACTORY_ID});
        try {
            deviceInfoFactoryDataDao.changeDeviceStateForStolenOrFaulty(FACTORY_ID, state, action);
        } catch (InvalidParameterException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void updateFactoryDataStateTest() {
        
        String state = "Active";
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_FOR_UPDATE_FACTORY_DATA_STATE,
            new Object[]{state, FACTORY_ID});
        int actualResponse = deviceInfoFactoryDataDao.updateFactoryDataState(FACTORY_ID, state);
        assertEquals(1, actualResponse);
    }

    @Test
    public void getModelByImeiTest() {
        String imei = "12345";
        Mockito.doReturn("model").when(jdbcTemplate)
            .queryForObject(GET_MODEL_BY_IMEI, new Object[]{imei}, String.class);
        String actualResponse = deviceInfoFactoryDataDao.getModelByImei(imei);
        assertEquals("model", actualResponse);
    }

    @Test
    public void updateTest() {
        Map<String, Object> conditionalOrderedMap = new HashMap<>();
        Map<String, Object> orderedMap = new HashMap<>();
        conditionalOrderedMap.put(STATE, DeviceState.PROVISIONED.getValue());
        orderedMap.put(STATE, DeviceState.STOLEN.getValue());
        log.info("conditionalOrderedMap: " + conditionalOrderedMap + " orderedMap: " + orderedMap);
        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString(), (Object) Mockito.any());
        assertEquals(1, deviceInfoFactoryDataDao.update(conditionalOrderedMap, orderedMap));
        log.info("conditionalOrderedMap: " + conditionalOrderedMap + " orderedMap: " + orderedMap);
        assertEquals(orderedMap, conditionalOrderedMap);
    }

    @Test
    public void deleteFactoryDataTest1() {
        String imei = "12345";
        String serialnumber = "123";
        DeviceInfoFactoryData currentData = new DeviceInfoFactoryData();
        currentData.setId(1L);
        currentData.setSerialNumber("123");
        currentData.setImei("12345");
        boolean exceptionOccurred = false;
        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString(), (Object) Mockito.any());
        try {
            deviceInfoFactoryDataDao.deletefactoryData(imei, serialnumber, currentData);
        } catch (InvalidParameterException e) {
            exceptionOccurred = true;
        }
        assertTrue(exceptionOccurred);
    }

    @Test
    public void deleteFactoryDataTest2() {
        DeviceInfoFactoryData currentData = new DeviceInfoFactoryData();
        currentData.setId(1L);
        currentData.setSerialNumber("123");
        currentData.setImei("12345");
        boolean exceptionOccurred = false;
        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString(), (Object) Mockito.any());
        try {
            deviceInfoFactoryDataDao.deletefactoryData(null, null, currentData);
        } catch (InvalidParameterException e) {
            exceptionOccurred = true;
        }
        assertFalse(exceptionOccurred);
    }

    @Test
    public void createHistoryTableEntryTest() {
        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(1L);
        deviceInfoFactoryData.setSerialNumber("12345");
        deviceInfoFactoryData.setImei("234");
        deviceInfoFactoryData.setDeviceType("dongle");
        String action = "action";
        Mockito.doReturn(1).when(jdbcTemplate).update((PreparedStatementCreator) Mockito.any());
        deviceInfoFactoryDataDao.createHistoryTableEntry(deviceInfoFactoryData, action);
        assertEquals(1, jdbcTemplate.update((PreparedStatementCreator) Mockito.any()));
    }

    @Test
    public void insertIntoDeviceInfoFactoryDataTest() {
        final DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        String manufacturingDate = "20/08/2020";
        String recordDate = "20/08/2020";
        Mockito.doReturn(1).when(jdbcTemplate).update((PreparedStatementCreator) Mockito.any());
        deviceInfoFactoryDataDao.insertIntoDeviceInfoFactoryData(factoryData, manufacturingDate, recordDate);
        assertEquals(1, jdbcTemplate.update((PreparedStatementCreator) Mockito.any()));
    }

    @Test
    public void findByFactoryIdTest() {
        
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        DeviceInfoFactoryData deviceInfoFactoryData = deviceInfoFactoryDataDao.findByFactoryId(FACTORY_ID);
        assertNull(deviceInfoFactoryData);
    }

    @Test
    public void findByFactoryImeiTest() {
        String imei = "12345";
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        DeviceInfoFactoryData deviceInfoFactoryData = deviceInfoFactoryDataDao.findByFactoryImei(imei);
        assertNull(deviceInfoFactoryData);
    }

    @Test
    public void findIdByFactoryImeiTest() {
        String imei = "12";
        Mockito.doReturn(1L).when(jdbcTemplate)
            .queryForObject(Mockito.any(), (Class<Object>) Mockito.any(), Mockito.any());
        Long response = deviceInfoFactoryDataDao.findIdByFactoryImei(imei);
        assertNull(response);
    }

    @Test
    public void findByFactoryIdAndImeiTest() {
        long factoryId = 1L;
        String imei = "123";
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        DeviceInfoFactoryData actualDeviceInfoFactoryData =
            deviceInfoFactoryDataDao.findByFactoryIdAndImei(FACTORY_ID, imei);
        assertNull(actualDeviceInfoFactoryData);
    }

    @Test
    public void getTimestampValidDateFormatTest() {
        Timestamp response = deviceInfoFactoryDataDao.getTimestamp("2020-09-01 06:36:47.240");
        Timestamp expectedTimestamp = Timestamp.valueOf("2020-09-01 06:36:47.240");
        assertEquals(expectedTimestamp, response);
    }

    @Test
    public void getTimestampInvalidDateFormatTest() {
        Timestamp response = deviceInfoFactoryDataDao.getTimestamp("2020/09/01");
        assertNull(response);
    }

    @Test
    public void findFactoryDataBySerialNumberTest() {
        String serialNumber = "123";
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        DeviceInfoFactoryData deviceInfoFactoryData =
            deviceInfoFactoryDataDao.findFactoryDataBySerialNumber(serialNumber);
        assertNull(deviceInfoFactoryData);
    }

    @Test
    public void constructFetchFactoryDataAscTest() {
        String asc = "asc";
        String serialNumber = "111";
        String imei = "111";
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        List<DeviceInfoFactoryData> response =
            deviceInfoFactoryDataDao.constructFetchFactoryData(SIZE, PAGE, asc, null, serialNumber, imei);
        assertNotNull(response);
    }

    @Test
    public void constructFetchFactoryDataDescTest() {
        String desc = "desc";
        String serialNumber = "111";
        String imei = "111";
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        List<DeviceInfoFactoryData> response =
            deviceInfoFactoryDataDao.constructFetchFactoryData(SIZE, PAGE, null, desc, serialNumber, imei);
        assertNotNull(response);
    }

    @Test
    public void constructFetchFactoryDataIdTest() {
        String asc = null;
        String desc = null;
        String serialNumber = "111";
        String imei = "111";
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        List<DeviceInfoFactoryData> response =
            deviceInfoFactoryDataDao.constructFetchFactoryData(SIZE, PAGE, asc, desc, serialNumber, imei);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAgrigateDeviceState1Test() {
        String serialNumber = "111";
        String imei = "111";
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.any(), (Map<String, ?>) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(serialNumber, imei);
        assertNotNull(response);
    }

    @Test
    public void fetchDeviceInfoFactoryDataTest() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("serial_number", "1234");
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        DeviceInfoFactoryData response = deviceInfoFactoryDataDao.fetchDeviceInfoFactoryData(orderedMap);
        assertNull(response);
    }

    @Test
    public void constructFetchTotalFactoryDataTest1() {
        String serialNumber = "111";
        String imei = "111";
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response = deviceInfoFactoryDataDao.constructFetchTotalFactoryData(serialNumber, imei);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchTotalFactoryDataTest2() {
        String serialNumber = "111";
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response = deviceInfoFactoryDataDao.constructFetchTotalFactoryData(serialNumber, null);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchTotalFactoryDataTest3() {
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response = deviceInfoFactoryDataDao.constructFetchTotalFactoryData(null, null);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchTotalFactoryData_Test1() {
        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        List<String> rangeFieldList = new ArrayList<>();
        containsLikeFieldList.add("likefield1");
        containsLikeValueList.add("likevalue1");
        rangeFieldList.add("rangefield1");
        List<String> rangeValueList = new ArrayList<>();
        rangeValueList.add("r_1");
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response =
            deviceInfoFactoryDataDao.constructFetchTotalFactoryData(containsLikeFieldList, containsLikeValueList,
                rangeFieldList, rangeValueList);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchTotalFactoryData_Test2() {
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();
        rangeFieldList.add("rangefield1");
        rangeValueList.add("r_1");
        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response =
            deviceInfoFactoryDataDao.constructFetchTotalFactoryData(containsLikeFieldList, containsLikeValueList,
                rangeFieldList, rangeValueList);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchTotalFactoryData_Test3() {
        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        containsLikeFieldList.add("likefield1");
        containsLikeValueList.add("likevalue1");
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response =
            deviceInfoFactoryDataDao.constructFetchTotalFactoryData(containsLikeFieldList, containsLikeValueList,
                rangeFieldList, rangeValueList);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchTotalFactoryData_Test4() {
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response = deviceInfoFactoryDataDao.constructFetchTotalFactoryData(null, null, null, null);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchAgrigateDeviceState_Test1() {
        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        containsLikeFieldList.add("likefield1");
        containsLikeValueList.add("likevalue1");
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();
        rangeFieldList.add("rangefield1");
        rangeValueList.add("r_1");
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.any(), (Map<String, ?>) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(containsLikeFieldList, containsLikeValueList,
                rangeFieldList, rangeValueList);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAgrigateDeviceState_Test2() {
        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        containsLikeFieldList.add("likefield1");
        containsLikeValueList.add("likevalue1");
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.any(), (Map<String, ?>) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(containsLikeFieldList, containsLikeValueList,
                rangeFieldList, rangeValueList);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAgrigateDeviceState_Test3() {
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();
        rangeFieldList.add("rangefield1");
        rangeValueList.add("r_1");
        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.any(), (Map<String, ?>) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(containsLikeFieldList, containsLikeValueList,
                rangeFieldList, rangeValueList);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAgrigateDeviceState_Test4() {
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.any(), (Map<String, ?>) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(null, null, null, null);
        assertNotNull(response);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest1() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.IMEI;
        String inputTypeValue = "111";
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response =
            deviceInfoFactoryDataDao.constructFetchTotalFactoryDataForDeviceDetails(inputType, inputTypeValue);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest2() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.SERIAL_NUMBER;
        String inputTypeValue = "111";
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response =
            deviceInfoFactoryDataDao.constructFetchTotalFactoryDataForDeviceDetails(inputType, inputTypeValue);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest3() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID;
        String inputTypeValue = "111";
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response =
            deviceInfoFactoryDataDao.constructFetchTotalFactoryDataForDeviceDetails(inputType, inputTypeValue);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest4() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.VIN;
        String inputTypeValue = "111";
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response =
            deviceInfoFactoryDataDao.constructFetchTotalFactoryDataForDeviceDetails(inputType, inputTypeValue);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest5() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.STATE;
        String inputTypeValue = "111";
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.any(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response =
            deviceInfoFactoryDataDao.constructFetchTotalFactoryDataForDeviceDetails(inputType, inputTypeValue);
        assertEquals(1L, response);
    }

    @Test
    public void constructFetchFactoryDataTest1() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.IMEI;
        String searchKey = "111";
        int sizeValue = 1;
        int pageValue = 1;
        String sortby = "serial_number";
        String orderBy = "imei";
        boolean deviceVinEnabled = true;
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.any(),
                (ResultSetExtractor<Object>) Mockito.any());
        List<DeviceInfoFactoryDataWithSubscription> response =
            deviceInfoFactoryDataDao.constructFetchFactoryData(inputType, searchKey, sizeValue, pageValue, sortby,
                orderBy, deviceVinEnabled);
        assertNotNull(response);
    }

    @Test
    public void constructFetchFactoryDataTest2() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.SERIAL_NUMBER;
        String searchKey = "111";
        int sizeValue = 1;
        int pageValue = 1;
        String sortby = "serial_number";
        String orderBy = "imei";
        boolean deviceVinEnabled = true;
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.any(),
                (ResultSetExtractor<Object>) Mockito.any());
        List<DeviceInfoFactoryDataWithSubscription> response =
            deviceInfoFactoryDataDao.constructFetchFactoryData(inputType, searchKey, sizeValue, pageValue, sortby,
                orderBy, deviceVinEnabled);
        assertNotNull(response);
    }

    @Test
    public void constructFetchFactoryDataTest3() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID;
        String searchKey = "111";
        int sizeValue = 1;
        int pageValue = 1;
        String sortby = "serial_number";
        String orderBy = "imei";
        boolean deviceVinEnabled = true;
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.any(),
                (ResultSetExtractor<Object>) Mockito.any());
        List<DeviceInfoFactoryDataWithSubscription> response =
            deviceInfoFactoryDataDao.constructFetchFactoryData(inputType, searchKey, sizeValue, pageValue, sortby,
                orderBy, deviceVinEnabled);
        assertNotNull(response);
    }

    @Test
    public void constructFetchFactoryDataTest4() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.VIN;
        String searchKey = "111";
        int sizeValue = 1;
        int pageValue = 1;
        String sortby = "serial_number";
        String orderBy = "imei";
        boolean deviceVinEnabled = true;
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.any(),
                (ResultSetExtractor<Object>) Mockito.any());
        List<DeviceInfoFactoryDataWithSubscription> response =
            deviceInfoFactoryDataDao.constructFetchFactoryData(inputType, searchKey, sizeValue, pageValue, sortby,
                orderBy, deviceVinEnabled);
        assertNotNull(response);
    }

    @Test
    public void constructFetchFactoryDataTest5() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.STATE;
        String searchKey = "111";
        int sizeValue = 1;
        int pageValue = 1;
        String sortby = "serial_number";
        String orderBy = "imei";
        boolean deviceVinEnabled = false;
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.any(),
                (ResultSetExtractor<Object>) Mockito.any());
        List<DeviceInfoFactoryDataWithSubscription> response =
            deviceInfoFactoryDataDao.constructFetchFactoryData(inputType, searchKey, sizeValue, pageValue, sortby,
                orderBy, deviceVinEnabled);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAggregrateFactoryDataImeiTest1() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.IMEI;
        String searchKey = "111";
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(inputType, searchKey);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAggregrateFactoryDataImeiTest2() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.IMEI;
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(inputType, null);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAggregrateFactoryDataSerialNumberTest1() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.SERIAL_NUMBER;
        String searchKey = "111";
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(inputType, searchKey);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAggregrateFactoryDataSerialNumberTest2() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.SERIAL_NUMBER;
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(inputType, null);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAggregrateFactoryDataDeviceIdTest1() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID;
        String searchKey = "111";
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(inputType, searchKey);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAggregrateFactoryDataDeviceIdTest2() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID;
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(inputType, null);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAggregrateFactoryDataVinTest1() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.VIN;
        String searchKey = "111";
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(inputType, searchKey);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAggregrateFactoryDataVinTest2() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.VIN;
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(inputType, null);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAggregrateFactoryDataStateTest1() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.STATE;
        String searchKey = "111";
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(inputType, searchKey);
        assertNotNull(response);
    }

    @Test
    public void constructFetchAggregrateFactoryDataStateTest2() {
        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum inputType =
            DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.STATE;
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        DeviceInfoAggregateFactoryData.StateCount response =
            deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(inputType, null);
        assertNotNull(response);
    }

    @Test
    public void findTotalDeviceStateTest() {
        String imei = "111";
        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.anyString(), (MapSqlParameterSource) Mockito.any(), (Class<Object>) Mockito.any());
        Long response = deviceInfoFactoryDataDao.findTotalDeviceState(imei);
        assertEquals(1L, response);
    }

    @Test
    public void constructAndFetchDeviceStatesTest() {
        int size = 1;
        int page = 1;
        String sortingOrder = "ASC";
        String sortBy = "serial_number";
        String imei = "111";
        Mockito.doReturn(null).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.any(),
                (ResultSetExtractor<Object>) Mockito.any());
        List<DeviceStateHistory> response =
            deviceInfoFactoryDataDao.constructAndFetchDeviceStates(size, page, sortingOrder, sortBy, imei);
        assertNotNull(response);
    }

    @Test
    public void findVinEitherByImeiOrSerialNumberTest() {
        String serialNumber = "111";
        String imei = "111";
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        String response = deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(imei, serialNumber);
        assertNull(response);
    }

    @Test
    public void findVinEitherByImeiOrSerialNumberTest2() {
        String serialNumber = null;
        String imei = "111";
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        String response = deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(imei, serialNumber);
        assertNull(response);
    }

    @Test
    public void findVinEitherByImeiOrSerialNumberTest3() {
        String serialNumber = null;
        String imei = null;
        boolean exceptionOccurred = false;
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        try {
            String response = deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(imei, serialNumber);
        } catch (IllegalArgumentException e) {
            exceptionOccurred = true;
        }
        assertTrue(exceptionOccurred);
    }

    @Test
    public void findVinEitherByImeiOrSerialNumberTest4() {
        String serialNumber = "111";
        String imei = "111";
        List<String> strLst = new ArrayList<>();
        strLst.add("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(RowMapper.class));
        String response = deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber(imei, serialNumber);
        assertEquals("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0", response);
    }

    @Test
    public void insertDataTest() {
        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(1L);
        deviceInfoFactoryData.setSerialNumber("12345");
        deviceInfoFactoryData.setImei("234");
        deviceInfoFactoryData.setDeviceType("dongle");
        String userId = "ID123";
        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.any(), (KeyHolder) Mockito.any());
        deviceInfoFactoryDataDao.insertData(deviceInfoFactoryData, userId);
        assertEquals(0, jdbcTemplate.update(Mockito.any(), (KeyHolder) Mockito.any()));
    }
}