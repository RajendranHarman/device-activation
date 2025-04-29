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

package org.eclipse.ecsp.auth.lib.dao;

import org.eclipse.ecsp.auth.lib.rest.model.DeviceSeqNoDetailsResponse;
import org.eclipse.ecsp.auth.lib.rest.model.SeqNoHidMapInfo;
import org.eclipse.ecsp.services.shared.util.HealthCheckConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a DAO (Data Access Object) for Device related operations.
 * It provides methods to retrieve device sequence number and Harman ID details,
 * as well as perform a health check on the database.
 */
@Configurable
@Component
public class DeviceCommonDao {

    private static final String SELECT_DEVICE_ID_HARMANID = "select  \"ID\", \"HarmanID\" from public.\"Device\"";
    private static final String DEFAULT_QUERY = "SELECT " + HealthCheckConstants.DB_CONN_SUCCESS_CODE;
    private static final int COLUMN_INDEX_2 = 2;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Get device sequence number and Harman ID details.
     *
     * @return A {@link DeviceSeqNoDetailsResponse} object containing the device sequence number and Harman ID details.
     */
    public DeviceSeqNoDetailsResponse getDeviceSeqNoAndHarmanIdDetails() {
        return namedParameterJdbcTemplate.query(SELECT_DEVICE_ID_HARMANID,
            new ResultSetExtractor<DeviceSeqNoDetailsResponse>() {

                @Override
                public DeviceSeqNoDetailsResponse extractData(ResultSet rs) throws SQLException {
                    List<SeqNoHidMapInfo> seqNoHidMapInfoList = new ArrayList<>();
                    while (rs.next()) {
                        SeqNoHidMapInfo seqNoHidMapInfo = new SeqNoHidMapInfo();
                        seqNoHidMapInfo.setId(rs.getLong(1));
                        seqNoHidMapInfo.setHarmanId(rs.getString(COLUMN_INDEX_2));
                        seqNoHidMapInfoList.add(seqNoHidMapInfo);
                    }
                    DeviceSeqNoDetailsResponse deviceSeqNoDetailsRes = new DeviceSeqNoDetailsResponse();

                    deviceSeqNoDetailsRes.setSeqNoHidMapInfo(seqNoHidMapInfoList);
                    return deviceSeqNoDetailsRes;
                }
            });
    }

    /**
     * Check the health of the database.
     *
     * @return The database connection success code.
     */
    public int healthCheck() {
        Integer healthCheck =
            namedParameterJdbcTemplate.queryForObject(DEFAULT_QUERY, new MapSqlParameterSource(), Integer.class);
        return healthCheck != null ? healthCheck : 0;
    }

}
