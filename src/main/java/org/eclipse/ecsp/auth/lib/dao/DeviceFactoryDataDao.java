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

import org.eclipse.ecsp.services.shared.util.SqlUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * The DeviceFactoryDataDao class is responsible for interacting with the database
 * to perform CRUD operations on the DeviceFactoryData table.
 */
@Configurable
@Component
public class DeviceFactoryDataDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceFactoryDataDao.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Fetches device details based on the provided ordered map.
     *
     * @param orderedMap the ordered map containing the search criteria
     * @return the device details
     */
    public DeviceFactoryData find(Map<String, Object> orderedMap) {
        String prefix = "select * from public.\"DeviceInfoFactoryData\" where ";
        String operator = " and ";
        String sql = SqlUtility.getPreparedSql(prefix, operator, orderedMap);

        Object[] values = SqlUtility.getArrayValues(orderedMap);
        DeviceFactoryData deviceInfoFactoryData = null;
        List<DeviceFactoryData> deviceInfoFactoryDataList = jdbcTemplate.query(sql, values,
            new DeviceInfoFactoryDataMapper());

        if (!deviceInfoFactoryDataList.isEmpty()) {
            deviceInfoFactoryData = deviceInfoFactoryDataList.get(0);
        }
        LOGGER.debug("findByImeiSsidSerialNumber,:DeviceInfoFactoryDataId:{}", deviceInfoFactoryData);
        return deviceInfoFactoryData;
    }

    /**
     * Finds a device by its factory ID.
     *
     * @param factoryId the factory ID of the device
     * @return the DeviceFactoryData object
     */
    public DeviceFactoryData findById(long factoryId) {
        String sql = "select * from public.\"DeviceInfoFactoryData\" where \"ID\"=?";
        return (DeviceFactoryData) jdbcTemplate.queryForObject(sql, new Object[]{factoryId},
            new DeviceInfoFactoryDataMapper());
    }

    /**
     * Updates the state of the factory data for a device.
     *
     * @param factoryId the factory ID of the device
     * @param state the new state to be set
     * @return the number of rows affected by the update
     */
    public int updateFactoryDataState(long factoryId, String state) {
        String sql = "update public.\"DeviceInfoFactoryData\" set state=? where \"ID\"=?";
        return jdbcTemplate.update(sql, state, factoryId);
    }

    /**
     * Updates the state of the factory data to "PROVISIONED" for a device.
     *
     * @param factoryId the factory ID of the device
     * @return the number of rows affected by the update
     */
    public int updateStateToProvisioned(String factoryId) {
        String sql = "update public.\"DeviceInfoFactoryData\" set \"state\"=? where \"ID\"=?";
        return jdbcTemplate.update(sql, "PROVISIONED", factoryId);
    }

}
