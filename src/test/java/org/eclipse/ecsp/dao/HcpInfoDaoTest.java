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
import org.eclipse.ecsp.services.shared.dao.HcpInfoDao;
import org.eclipse.ecsp.services.shared.db.HcpInfo;
import org.eclipse.ecsp.services.shared.db.HcpInfoMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for HcpInfoDao.
 */
@Slf4j
public class HcpInfoDaoTest {
    private static final int EXPECTED_RESPONSE = -1;
    private static final long ID = 12L;
    private static final int RETURN_VALUE = 2;

    @InjectMocks
    private HcpInfoDao hcpInfoDao;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void insertTest() {
        final String vin = "vin";
        final String serialNumber = "123";
        Mockito.doReturn(0).when(jdbcTemplate).update((PreparedStatementCreator) Mockito.any(), Mockito.any());
        Long response = hcpInfoDao.insert(vin, serialNumber);
        assertEquals(EXPECTED_RESPONSE, response);
    }

    @Test
    public void insertTest_factDataId() {
        
        final String vin = "vin";
        final String serialNumber = "123";
        Mockito.doReturn(0).when(jdbcTemplate).update((PreparedStatementCreator) Mockito.any(), Mockito.any());
        Long response = hcpInfoDao.insert(ID, serialNumber, vin);
        assertEquals(EXPECTED_RESPONSE, response);
    }

    @Test
    public void findByHarmandIdsTest() {
        List<String> harmandIds = new ArrayList<>();
        harmandIds.add("HU1");
        harmandIds.add("HU2");
        Mockito.doReturn(null).when(namedParameterJdbcTemplate)
            .query(Mockito.any(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        List<HcpInfo> actualResponse = hcpInfoDao.findByHarmandIds(harmandIds);
        assertNotNull(actualResponse);
    }

    @Test
    public void updateHarmanIdTest() {
        String harmanId = "HU1";
        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.any(), (Object) Mockito.any());
        int response = hcpInfoDao.updateHarmanId(harmanId, ID);
        assertEquals(1, response);
    }

    @Test
    public void getTempGroupSizeTest() {
        long tempGroupId = 1L;
        Mockito.doReturn(ID).when(jdbcTemplate)
            .queryForObject(Mockito.any(), (Class<Object>) Mockito.any(), Mockito.any());
        long response = hcpInfoDao.getTempGroupSize(tempGroupId);
        Assertions.assertEquals(ID, response);
    }

    @Test
    public void deleteByHarmanIdTest() {
        String harmanId = "HU1";
        Mockito.doReturn(RETURN_VALUE).when(jdbcTemplate).update(Mockito.anyString(), (Object) Mockito.any());
        int response = hcpInfoDao.deleteByHarmanId(harmanId);
        assertEquals(RETURN_VALUE, response);
    }

    @Test
    public void deleteByFactoryIdTest() {
        
        Mockito.doReturn(RETURN_VALUE).when(jdbcTemplate).update(Mockito.anyString(), (Object) Mockito.any());
        int response = hcpInfoDao.deleteByFactoryId(ID);
        assertEquals(RETURN_VALUE, response);
    }

    @Test
    public void updateForReplaceDeviceTest() {
        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setId(ID);
        hcpInfo.setFactoryId("12");
        hcpInfo.setHarmanId("HU1");
        hcpInfo.setVin("vin1");
        hcpInfo.setSerialNumber("12345");
        //Testing getters
        log.info("id: " + hcpInfo.getId());
        log.info("vin: " + hcpInfo.getVin());
        Mockito.doReturn(RETURN_VALUE).when(jdbcTemplate).update(Mockito.anyString(), (Object) Mockito.any());
        int response = hcpInfoDao.updateForReplaceDevice(hcpInfo);
        assertEquals(RETURN_VALUE, response);
    }

    @Test
    public void mapHarmanIdsForVinsTest() {
        long tempGroupId = 1L;
        Mockito.doReturn(RETURN_VALUE).when(jdbcTemplate).update(Mockito.any(), Mockito.any(), Mockito.any());
        int response = hcpInfoDao.mapHarmanIdsForVins(tempGroupId);
        Assertions.assertEquals(RETURN_VALUE, response);
    }

    @Test
    public void getVinsToPreactivateTest() {
        long tempGroupId = 1L;
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForList(Mockito.any(), (Class<Object>) Mockito.any(), Mockito.any());
        List<String> response = hcpInfoDao.getVinsToPreactivate(tempGroupId);
        assertNotNull(response);
    }

    @Test
    public void updateTempDeviceGroupTest() {
        String vin = "vin";
        String harmanId = "123";
        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.any(), Mockito.any(), Mockito.any());
        int response = hcpInfoDao.updateTempDeviceGroup(harmanId, vin, ID);
        assertEquals(1, response);
    }

    @Test
    public void findByVinTest1() {
        String vin = "vin123";
        List<HcpInfo> hcpInfos = new LinkedList<>();
        Mockito.doReturn(hcpInfos).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.any(), Mockito.any(HcpInfoMapper.class));
        HcpInfo response = hcpInfoDao.findByVin(vin);
        assertNull(response);
    }

    @Test
    public void findByVinTest2() {
        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setId(1L);
        hcpInfo.setFactoryId("12345");
        hcpInfo.setHarmanId("HUASO2ZTW10917");
        hcpInfo.setSerialNumber("12345");
        hcpInfo.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        List<HcpInfo> hcpInfos = new ArrayList<>();
        hcpInfos.add(hcpInfo);
        String vin = "TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0";
        Mockito.doReturn(hcpInfos).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.any(), Mockito.any(HcpInfoMapper.class));
        HcpInfo response = hcpInfoDao.findByVin(vin);
        assertNotNull(response);
    }

    @Test
    public void findByFactoryIdTest() {
        
        Mockito.doReturn(null).when(namedParameterJdbcTemplate)
            .query(Mockito.anyString(), (MapSqlParameterSource) Mockito.any(),
                (ResultSetExtractor<Object>) Mockito.any());
        HcpInfo response = hcpInfoDao.findByFactoryId(ID);
        assertNull(response);
    }

    @Test
    public void findActiveHcpInfoTest_null_hcpInfo() {
        
        List<HcpInfo> hcpInfoList = new LinkedList<>();
        Mockito.doReturn(hcpInfoList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.any(), Mockito.any(HcpInfoMapper.class));
        HcpInfo response = hcpInfoDao.findActiveHcpInfo(ID);
        assertNull(response);
    }

    @Test
    public void findActiveHcpInfoTest_valid_hcpInfo() {
        
        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setId(1L);
        hcpInfo.setFactoryId("12L");
        hcpInfo.setHarmanId("HUASO2ZTW10917");
        hcpInfo.setSerialNumber("12345");
        hcpInfo.setVin("TESTVIN_Make:vehicle1_Model:500_Year:2012_Type:Car_0");
        List<HcpInfo> hcpInfos = new ArrayList<>();
        hcpInfos.add(hcpInfo);
        Mockito.doReturn(hcpInfos).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.any(), Mockito.any(HcpInfoMapper.class));
        HcpInfo response = hcpInfoDao.findActiveHcpInfo(ID);
        assertNotNull(response);
    }

    @Test
    public void insertTest_3params() {
        
        final String vin = "vin";
        final String serialNumber = "123";
        Mockito.doReturn(1).when(jdbcTemplate).update((PreparedStatementCreator) Mockito.any(), Mockito.any());
        Long response = hcpInfoDao.insert(ID, serialNumber, vin);
        assertEquals(0, response);
    }

    @Test
    public void insertTest_2params() {
        final String vin = "vin";
        final String serialNumber = "123";
        Mockito.doReturn(RETURN_VALUE).when(jdbcTemplate).update((PreparedStatementCreator) Mockito.any(),
            Mockito.any());
        Long response = hcpInfoDao.insert(vin, serialNumber);
        assertEquals(0, response);
    }

    @Test
    public void getTempGroupSizeTest_null() {
        long tempGroupId = 1L;
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.any(), (Class<Object>) Mockito.any(), Mockito.any());
        long response = hcpInfoDao.getTempGroupSize(tempGroupId);
        Assertions.assertEquals(0L, response);
    }
}