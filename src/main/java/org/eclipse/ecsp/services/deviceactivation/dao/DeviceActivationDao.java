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

package org.eclipse.ecsp.services.deviceactivation.dao;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.ecsp.services.deviceactivation.model.DeviceActivation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The DAO (Data Access Object) class for managing device activation in the database.
 * This class provides methods to insert device activation, update the device id,
 * find active device count, find active device id, find active device objects
 */
@Configurable
@Repository
@Slf4j
public class DeviceActivationDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceActivationDao.class);
    private static final int INDEX_1 = 1;
    private static final int INDEX_2 = 2;
    private static final int INDEX_3 = 3;
    private static final int INDEX_4 = 4;
    private static final int INDEX_5 = 5;
    private static final int INDEX_6 = 6;
    private static final int RETURN_VALUE = -1;
    private static final long RECORD_ID = -1L;
    private static final String ACTIVE_DEVICE_COUNT =
            "select count(*) from device_activation where jitact_id=? and is_active=true";
    private static final String UPDATE_HARMAN_ID =
        "update device_activation set harman_id=? where jitact_id=? and is_active=true";
    private static final String UPDATE_PASSCODE =
        "update device_activation set passcode=? , activation_date=? where id=? and is_active=true";
    private static final String GET_ACTIVE_DEVICE =
        "select device_activation.id,device_activation.jitact_id,device_activation.harman_id,"
        + "device_activation.passcode,device_activation.activation_date,device_activation.device_type,"
        + "device_activation.is_active from device_activation where jitact_id=? and is_active=true";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * To insert the device activation object to the table.
     *
     * @param deviceActivation DeviceActivation object to be inserted.
     * @return updated count
     */
    public int insertDeviceActivation(DeviceActivation deviceActivation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn
                    .prepareStatement(
                        "insert into public.\"device_activation\"(\"jitact_id\",\"harman_id\",\"passcode\","
                            + "\"activation_date\",\"device_type\",\"is_active\") values(?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(INDEX_1, deviceActivation.getJitactId());
                ps.setString(INDEX_2, deviceActivation.getHarmanId());
                ps.setString(INDEX_3, deviceActivation.getPasscode());
                ps.setTimestamp(INDEX_4, deviceActivation.getActivationDate());
                ps.setString(INDEX_5, deviceActivation.getDeviceType());
                ps.setBoolean(INDEX_6, true);
                return ps;
            }
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        Long id = keys != null ? (Long) keys.get("id") : null;
        if (id != null) {
            deviceActivation.setId(id);
        }
        LOGGER.info("Id of inserted tuple in device_activation table:{}", id);
        // 0 row affected, return -1 to indicate failure
        if (rows == 0) {
            return RETURN_VALUE;
        }
        return rows;
    }

    /**
     * To update the harman_id in device activation table.
     *
     * @param deviceActivation DeviceActivation object.
     */
    public void updateHarmanId(DeviceActivation deviceActivation) {
        jdbcTemplate.update(UPDATE_HARMAN_ID, deviceActivation.getHarmanId(), deviceActivation.getJitactId());
    }

    /**
     * To update the passcode in device activation table.
     *
     * @param deviceActivation DeviceActivation object.
     */
    public void updatePasscode(DeviceActivation deviceActivation) {
        jdbcTemplate.update(UPDATE_PASSCODE, deviceActivation.getPasscode(),
                deviceActivation.getActivationDate(), deviceActivation.getId());
    }

    /**
     * Find the active device count with the given jitact_id from device activation table.
     *
     * @param jitactId of the device
     * @return active device count with given jitact_id
     */
    public long activeDeviceCount(String jitactId) {
        Long activeRecordCount = RECORD_ID;
        try {
            activeRecordCount =
                    jdbcTemplate.queryForObject(ACTIVE_DEVICE_COUNT, new Object[]{jitactId}, Long.class);
            LOGGER.info("activeRecordCount :: {}", activeRecordCount);
        } catch (DataAccessException e) {
            LOGGER.error("Exception occurred while trying to retrieve active device count,"
                + " could be that no activate record found for the jitact_id {}", jitactId, e);
        }
        return activeRecordCount != null ? activeRecordCount : 0L;
    }

    /**
     * Find the active device with the given jitact_id from device activation table.
     *
     * @param jitactId of the device
     * @return the list of device activation object from the table with given jitact_id
     */
    public List<DeviceActivation> findActiveDevice(String jitactId) {
        List<Map<String, Object>> deviceActivations = jdbcTemplate.queryForList(GET_ACTIVE_DEVICE, jitactId);
        String printJitActId = jitactId.replaceAll("[\n\r]", "_");
        LOGGER.info("Found device for jitact_id:{} deviceActivation.size: {}", printJitActId, deviceActivations.size());
        List<DeviceActivation> deviceActivationList = null;
        if (!deviceActivations.isEmpty()) {
            deviceActivationList = new ArrayList<>();
            for (Map<String, Object> row : deviceActivations) {
                DeviceActivation activation = new DeviceActivation();
                activation.setActivationDate((Timestamp) row.get("activation_date"));
                activation.setDeviceType((String) row.get("device_type"));
                activation.setHarmanId((String) row.get("harman_id"));
                activation.setJitactId((String) row.get("jitact_id"));
                activation.setPasscode((String) row.get("passcode"));
                activation.setActive((boolean) row.get("is_active"));
                activation.setId((long) row.get("id"));
                deviceActivationList.add(activation);
            }
        }
        return deviceActivationList;
    }

}
