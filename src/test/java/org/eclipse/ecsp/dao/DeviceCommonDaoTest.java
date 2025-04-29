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

import org.eclipse.ecsp.auth.lib.dao.DeviceCommonDao;
import org.eclipse.ecsp.auth.lib.rest.model.DeviceSeqNoDetailsResponse;
import org.eclipse.ecsp.auth.lib.rest.model.SeqNoHidMapInfo;
import org.eclipse.ecsp.services.shared.util.HealthCheckConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceCommonDao.
 */
public class DeviceCommonDaoTest {

    private static final String SELECT_DEVICE_ID_HARMANID = "select  \"ID\", \"HarmanID\" from public.\"Device\"";
    private static final String DEFAULT_QUERY = "SELECT " + HealthCheckConstants.DB_CONN_SUCCESS_CODE;
    private static final int VALUE_2 = 2;

    @InjectMocks
    private DeviceCommonDao deviceCommonDao;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void getDeviceSeqNoAndHarmanIdDetailsTest() {
        List<SeqNoHidMapInfo> seqNoHidMapInfoList = new ArrayList<>();
        SeqNoHidMapInfo seqNoHarmanIdMapInfo = new SeqNoHidMapInfo();
        seqNoHarmanIdMapInfo.setHarmanId("H1");
        seqNoHarmanIdMapInfo.setId(1);

        DeviceSeqNoDetailsResponse deviceSeqNoDetailsResponse = new DeviceSeqNoDetailsResponse();
        seqNoHidMapInfoList.add(seqNoHarmanIdMapInfo);
        deviceSeqNoDetailsResponse.setSeqNoHidMapInfo(seqNoHidMapInfoList);
        Mockito.doReturn(deviceSeqNoDetailsResponse).when(namedParameterJdbcTemplate).query(SELECT_DEVICE_ID_HARMANID,
            new ResultSetExtractor<DeviceSeqNoDetailsResponse>() {

                @Override
                public DeviceSeqNoDetailsResponse extractData(ResultSet rs) throws SQLException {
                    List<SeqNoHidMapInfo> seqNoHidMapInfoList = new ArrayList<SeqNoHidMapInfo>();
                    while (rs.next()) {
                        SeqNoHidMapInfo seqNoHidMapInfo = new SeqNoHidMapInfo();
                        seqNoHidMapInfo.setId(rs.getLong(1));
                        seqNoHidMapInfo.setHarmanId(rs.getString(VALUE_2));
                        seqNoHidMapInfoList.add(seqNoHidMapInfo);
                    }
                    DeviceSeqNoDetailsResponse deviceSeqNoDetailsRes = new DeviceSeqNoDetailsResponse();

                    deviceSeqNoDetailsRes.setSeqNoHidMapInfo(seqNoHidMapInfoList);
                    return deviceSeqNoDetailsRes;
                }
            });
        DeviceSeqNoDetailsResponse actualDeviceSeqNoDetailsResponse =
            deviceCommonDao.getDeviceSeqNoAndHarmanIdDetails();
        assertNotNull(deviceSeqNoDetailsResponse);
    }

    @Test
    public void healthCheckTest() {
        Mockito.doReturn(0).when(namedParameterJdbcTemplate)
            .queryForObject(DEFAULT_QUERY, new MapSqlParameterSource(), Integer.class);
        int actualResponse;
        try {
            actualResponse = deviceCommonDao.healthCheck();
        } catch (Exception e) {
            actualResponse = 0;
        }
        assertEquals(0, actualResponse);
    }
}