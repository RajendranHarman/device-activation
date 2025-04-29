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

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The DeviceInfoFactoryDataMapper class is responsible for mapping the result set of a database query
 * to a DeviceFactoryData object.
 */
public class DeviceInfoFactoryDataMapper implements RowMapper<DeviceFactoryData> {

    /**
     * Maps a single row of the result set to a DeviceFactoryData object.
     *
     * @param resultSet the result set containing the data to be mapped
     * @param rowNum    the current row number
     * @return the mapped DeviceFactoryData object
     * @throws SQLException if an error occurs while accessing the result set
     */
    @Override
    public DeviceFactoryData mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        DeviceFactoryData factoryData = new DeviceFactoryData();
        factoryData.setId(resultSet.getLong("ID"));
        factoryData.setManufacturingDate(resultSet.getString("manufacturing_date"));
        factoryData.setModel("model");
        factoryData.setImei(resultSet.getString("imei"));
        factoryData.setSerialNumber(resultSet.getString("serial_number"));
        factoryData.setPlatformVersion(resultSet.getString("platform_version"));
        factoryData.setIccid(resultSet.getString("iccid"));
        factoryData.setSsid(resultSet.getString("ssid"));
        factoryData.setMsisdn(resultSet.getString("msisdn"));
        factoryData.setImsi(resultSet.getString("imsi"));
        factoryData.setRecordDate(resultSet.getString("record_date"));
        factoryData.setCreatedDate(resultSet.getString("created_date"));
        factoryData.setFactoryAdmin(resultSet.getString("factory_admin"));
        factoryData.setState(resultSet.getString("state"));
        factoryData.setFaulty(resultSet.getBoolean("isfaulty"));
        factoryData.setStolen(resultSet.getBoolean("isstolen"));
        factoryData.setPackageSerialNumber(resultSet.getString("package_serial_number"));
        factoryData.setDeviceType(resultSet.getString("device_type"));
        return factoryData;
    }
}
