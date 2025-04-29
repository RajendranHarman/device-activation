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

package org.eclipse.ecsp.services.device.dao;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.ecsp.services.device.model.Device;
import org.eclipse.ecsp.services.device.model.DeviceMapper;
import org.eclipse.ecsp.services.shared.util.HealthCheckConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.common.CommonConstants.HARMAN_ID;
import static org.eclipse.ecsp.common.CommonConstants.ID;
import static org.eclipse.ecsp.common.CommonConstants.PASS_CODE;
import static org.eclipse.ecsp.common.CommonConstants.RANDOM_NUMBER;

/**
 * The DeviceDao class is responsible for interacting with the device table in the database.
 * It provides methods for querying, inserting, updating, and deactivating devices.
 */
@Configurable
@Component
@Slf4j
public class DeviceDao {
    public static final int INDEX_2 = 2;
    public static final int INDEX_3 = 3;
    public static final int INDEX_4 = 4;
    public static final int INDEX_5 = 5;
    public static final int RETURN_VALUE = -1;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Finds a device by its device ID.
     *
     * @param deviceId the device ID to search for
     * @return the device with the specified device ID, or null if not found
     */
    public Device findByDeviceId(String deviceId) {
        Device device = null;
        String sql = "select * from public.\"Device\" where \"HarmanID\" = ?";

        List<Device> devices = jdbcTemplate.query(sql, new Object[]{deviceId}, new DeviceMapper());

        if (devices != null && !devices.isEmpty()) {
            log.debug("findByDeviceId:devices:{}", devices);
            device = devices.get(0);
        }
        return device;
    }

    /**
     * Insert into Device table.
     *
     * @param device       device
     * @param preactivation preactivation
     * @return updated count
     */
    public int insert(final Device device, boolean preactivation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final boolean isActive = !(preactivation);
        int rows = jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn
                    .prepareStatement(
                        "insert into public.\"Device\"(\"ActivationDate\",\"PassCode\",\"UpdatedAt\","
                            + "\"RandomNumber\",\"IsActive\") values(?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                Timestamp timestamp = device.getActivationDate();
                ps.setTimestamp(1, timestamp);
                ps.setString(INDEX_2, device.getPasscode());
                ps.setTimestamp(INDEX_3, timestamp);
                ps.setLong(INDEX_4, device.getRandomNumber());
                ps.setBoolean(INDEX_5, isActive);
                return ps;
            }
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        Long id = keys != null ? (Long) keys.get(ID) : null;
        if (id != null) {
            device.setId(id);
        }
        log.debug("Id of inserted tuple in Device table:{}", id);
        // 0 row affected, return -1 to indicate failure
        if (rows == 0) {
            return RETURN_VALUE;
        }
        return rows;
    }

    /**
     * Check if device is activated already.
     *
     * @param vin           vin
     * @param serialNumber  serialNumber
     * @return Device
     */
    public Device checkIfActivatedAlready(String vin, String serialNumber) {
        Device device = null;
        String sql = "select \"Device\".\"RandomNumber\",\"Device\".\"HarmanID\" from \"Device\",\"HCPInfo\" where "
            + " \"Device\".\"IsActive\"=true and \"HCPInfo\".\"VIN\"=? and \"HCPInfo\".\"SerialNumber\"=? and"
            + " \"HCPInfo\".\"HarmanID\"=\"Device\".\"HarmanID\"";

        List<Map<String, Object>> devices = jdbcTemplate.queryForList(sql, new Object[]{vin, serialNumber},
            new int[]{Types.VARCHAR, Types.VARCHAR});

        if (devices != null && !devices.isEmpty()) {
            log.debug("checkIfActivatedAlready:devices:{}", devices);
            Map<String, Object> row = devices.get(0);
            device = new Device();
            device.setHarmanId((String) row.get(HARMAN_ID));
            device.setRandomNumber((Long) row.get(RANDOM_NUMBER));
        }
        return device;
    }

    /**
     * Check if device is activated already.
     *
     * @param factoryDataId factoryDataId
     * @return Device
     */
    public Device checkIfActivatedAlready(long factoryDataId) {
        Device device = null;
        String sql = "select \"Device\".\"RandomNumber\",\"Device\".\"HarmanID\" from \"Device\",\"HCPInfo\" where "
            + " \"Device\".\"IsActive\"=true and \"HCPInfo\".\"factory_data\"=? and "
            + "\"HCPInfo\".\"HarmanID\"=\"Device\".\"HarmanID\"";

        List<Map<String, Object>> devices = jdbcTemplate.queryForList(sql, factoryDataId);

        if (devices != null && !devices.isEmpty()) {
            Map<String, Object> row = devices.get(0);
            device = new Device();
            device.setHarmanId((String) row.get(HARMAN_ID));
            device.setRandomNumber((Long) row.get(RANDOM_NUMBER));
            log.info("isDevicesFoundin HCPInfo and Device table for factoryId:{} : {}", factoryDataId, devices.size());
        }
        return device;
    }

    /**
     * Update device table.
     *
     * @param deviceId deviceId
     * @param id       id
     * @return updated count
     */
    public int updateDevice(String deviceId, long id) {
        String sql = "update public.\"Device\" set \"HarmanID\"=? where \"ID\"=?";
        int updated = jdbcTemplate.update(sql, deviceId, id);
        log.debug("Inserted HArmanId {} for Device.ID={}", deviceId, id);
        return updated;
    }

    /**
     * Update RegisteredScopId By Id.
     *
     * @param id              id
     * @param registerScopeId registerScopeId
     * @return updated count
     */
    public int updateRegisteredScopIdById(long id, String registerScopeId) {
        String sql = "update public.\"Device\" set \"registered_scope_id\"=? where \"ID\"=?";
        int updated = jdbcTemplate.update(sql, registerScopeId, id);
        log.debug("Updated registerScopeId {} for Device.ID={}", registerScopeId, id);
        return updated;
    }

    /**
     * Update RegisteredScopId By HarmanId.
     *
     * @param harmanId        harmanId
     * @param registerScopeId registerScopeId
     * @return updated count
     */
    public int updateRegisteredScopIdByHarmanId(String harmanId, String registerScopeId) {
        String sql = "update public.\"Device\" set \"registered_scope_id\"=? where \"HarmanID\"=?";
        int updated = jdbcTemplate.update(sql, registerScopeId, harmanId);
        log.info("Updated registerScopeId {} for Device.HarmanID={}", registerScopeId, harmanId);
        return updated;
    }

    /**
     * Deactivate HarmanId.
     *
     * @param harmanId harmanId
     * @return updated count
     */
    public int deactivateHarmanId(String harmanId) {
        String sql = "update public.\"Device\" set \"IsActive\"=false where \"HarmanID\"=?";
        return jdbcTemplate.update(sql, new Object[]{harmanId}, new int[]{Types.VARCHAR});
    }

    /**
     * Update Device table for replace device.
     *
     * @param device device
     * @return updated count
     */
    public int updateForReplaceDevice(Device device) {
        String sql =
            "update \"Device\" set \"PassCode\"=?, \"ActivationDate\"=now(), \"UpdatedAt\"=now() where \"HarmanID\"=?";
        return jdbcTemplate.update(sql, device.getPasscode(), device.getHarmanId());
    }

    /**
     * Deactivate.
     *
     * @param serialNumber serialNumber
     * @return updated count
     */
    public int deactivate(String serialNumber) {
        String sql =
            "update \"Device\" set \"IsActive\"=false where \"HarmanID\" in (select \"HarmanID\" from \"HCPInfo\""
                + " where \"SerialNumber\"=?)";
        return jdbcTemplate.update(sql, serialNumber);
    }

    /**
     * Check whether the passcode is valid and returns the device object associated with it.
     *
     * @param passcode passcode
     * @return Device
     * @throws SQLException           SQLException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public Device checkLogin(String deviceId, String passcode) {
        Device device = null;
        String sql = "select * from public.\"Device\" where \"HarmanID\" = ? and \"PassCode\"=? and \"IsActive\"=true";
        log.debug("checkLogin:Looking for passcode:{} sql {}", passcode, sql);
        List<Map<String, Object>> devices = jdbcTemplate.queryForList(sql, new Object[]{deviceId, passcode},
            new int[]{Types.VARCHAR, Types.VARCHAR});
        if (devices != null && !devices.isEmpty()) {
            log.debug("checkLogin:devices:{}", devices);
            Map<String, Object> row = devices.get(0);
            device = new Device();
            device.setHarmanId((String) row.get(HARMAN_ID));
            device.setPasscode((String) row.get(PASS_CODE));
        }
        return device;
    }

    /**
     * Performs a health check on the database connection.
     *
     * @return the health check status code. Returns 0 if the health check fails or the result is null.
     */
    public int healthCheck() {
        String sql = "SELECT " + HealthCheckConstants.DB_CONN_SUCCESS_CODE;
        Integer healthCheck = jdbcTemplate.queryForObject(sql, Integer.class);
        return healthCheck != null ? healthCheck : 0;
    }

    /**
     * Retrieves a list of active devices based on the provided factory data ID.
     *
     * @param factoryDataId The factory data ID used to filter the devices.
     * @return A list of Device objects representing the active devices.
     */
    public List<Device> findActiveDevice(long factoryDataId) {
        String sql =
            "select \"Device\".\"RandomNumber\",\"Device\".\"HarmanID\",\"Device\".\"ID\" from \"Device\","
                + "\"HCPInfo\" where \"Device\".\"IsActive\"=true and \"HCPInfo\".\"factory_data\"=?"
                + " and \"HCPInfo\".\"HarmanID\"=\"Device\".\"HarmanID\"";

        List<Map<String, Object>> devices = jdbcTemplate.queryForList(sql, factoryDataId);
        log.debug("Found device for factoryId:{} device.size:{}", factoryDataId, devices.size());
        List<Device> deviceList = null;
        if (devices != null && !devices.isEmpty()) {
            deviceList = new ArrayList<>();
            for (Map<String, Object> row : devices) {
                Device devic = new Device();
                devic.setHarmanId((String) row.get(HARMAN_ID));
                devic.setRandomNumber((Long) row.get(RANDOM_NUMBER));
                devic.setId((Long) row.get(ID));
                deviceList.add(devic);
            }
        }
        log.debug("is Devices Found in HCPInfo and Device table for factoryId:{} : {}", factoryDataId, devices.size());
        return deviceList;
    }

    /**
     * Updates the passcode for the specified device.
     *
     * @param device the device for which the passcode needs to be updated
     */
    public void updatePasscode(Device device) {
        updateForReplaceDevice(device);
    }
}
