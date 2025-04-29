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

package org.eclipse.ecsp.services.shared.dao;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.ecsp.services.shared.db.HcpInfo;
import org.eclipse.ecsp.services.shared.db.HcpInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.common.CommonConstants.ID;

/**
 * This class provides data access operations for the HCPInfo entity.
 */
@Component
@Slf4j
public class HcpInfoDao {
    public static final int INDEX_2 = 2;
    public static final int INDEX_3 = 3;
    public static final int INDEX_4 = 4;
    public static final int INDEX_5 = 5;
    private static final int RETURN_VALUE = -1;

    private static final String FACTORYID = "FACTORYID";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Inserts a new record into the HCPInfo table.
     *
     * @param vin           The VIN (Vehicle Identification Number).
     * @param serialNumber  The serial number.
     * @return The updated count.
     */
    public long insert(final String vin, final String serialNumber) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn
                    .prepareStatement(
                        "insert into public.\"HCPInfo\"(\"HarmanID\",\"VIN\",\"SerialNumber\",\"CreatedAt\","
                            + "\"UpdatedAt\") values(?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                Timestamp timestamp = new Timestamp(new Date().getTime());
                ps.setString(1, vin + serialNumber);
                ps.setString(INDEX_2, vin);
                ps.setString(INDEX_3, serialNumber);
                ps.setTimestamp(INDEX_4, timestamp);
                ps.setTimestamp(INDEX_5, timestamp);
                return ps;
            }
        }, keyHolder);

        // 0 row affected, return -1 to indicate failure
        if (rows == 0) {
            return RETURN_VALUE;
        }
        Map<String, Object> key = keyHolder.getKeys();
        return key != null ? (Long) key.get(ID) : 0;
    }

    /**
     * Inserts a new record into the HCPInfo table.
     *
     * @param factoryDataId The factory data ID.
     * @param serialNumber  The serial number.
     * @param vin           The VIN (Vehicle Identification Number).
     * @return The updated count.
     */
    public long insert(final long factoryDataId, final String serialNumber, final String vin) {
        log.debug("Inside to insert data in HCPInfo for factoryData:{}, serialNumber:{} , vin:{}",
            new Object[]{factoryDataId, serialNumber, vin});
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn
                    .prepareStatement(
                        "insert into public.\"HCPInfo\"(\"HarmanID\",\"factory_data\",\"SerialNumber\","
                            + "\"CreatedAt\",\"UpdatedAt\") values(?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                Timestamp timestamp = new Timestamp(new Date().getTime());
                ps.setString(1, vin + serialNumber);
                ps.setLong(INDEX_2, factoryDataId);
                ps.setString(INDEX_3, serialNumber);
                ps.setTimestamp(INDEX_4, timestamp);
                ps.setTimestamp(INDEX_5, timestamp);

                return ps;
            }
        }, keyHolder);
        // 0 row affected, return -1 to indicate failure
        if (rows == 0) {
            return RETURN_VALUE;
        }
        Map<String, Object> keys = keyHolder.getKeys();
        return keys != null ? (Long) keys.get(ID) : 0;
    }

    /**
     * Retrieves HCPInfo details for the given Harman IDs.
     *
     * @param harmandIds The list of Harman IDs.
     * @return The list of HCPInfo details.
     */
    public List<HcpInfo> findByHarmandIds(List<String> harmandIds) {
        String sql = "select * from \"HCPInfo\" where \"HarmanID\" in (:fields)";
        Map<String, List<String>> params = Collections.singletonMap("fields",
            harmandIds);
        return namedParameterJdbcTemplate.query(sql, params,
            new HcpInfoMapper());

    }

    /**
     * Updates the Harman ID for a given record.
     *
     * @param harmanId The new Harman ID.
     * @param id       The ID of the record.
     * @return The number of rows affected.
     */
    public int updateHarmanId(String harmanId, long id) {
        String sql = "update public.\"HCPInfo\" set \"HarmanID\"=? where \"ID\"=?";
        return jdbcTemplate.update(sql, harmanId, id);
    }

    /**
     * Retrieves the group size for a given temp group ID.
     *
     * @param tempGroupId The temp group ID.
     * @return The group size.
     */
    public long getTempGroupSize(long tempGroupId) {
        String sql = "select count(*) from \"TempDeviceGroup\" where \"GroupID\"=? ";
        Long groupSize = jdbcTemplate.queryForObject(sql, Long.class, tempGroupId);
        return groupSize != null ? groupSize : 0;
    }

    /**
     * Deletes records with the given Harman ID.
     *
     * @param harmanId The Harman ID.
     * @return The number of rows affected.
     */
    public int deleteByHarmanId(String harmanId) {
        String sql = "delete from \"HCPInfo\" where \"HarmanID\"=?";
        return jdbcTemplate.update(sql, harmanId);
    }

    /**
     * Deletes records with the given factory ID.
     *
     * @param factoryId The factory ID.
     * @return The number of rows affected.
     */
    public int deleteByFactoryId(Long factoryId) {

        String sql = "delete from \"HCPInfo\" where factory_data=?";
        return jdbcTemplate.update(sql, factoryId);
    }

    /**
     * Updates the HCPInfo record for a replace device operation.
     *
     * @param hcpInfo The HCPInfo object.
     * @return The number of rows affected.
     */
    public int updateForReplaceDevice(HcpInfo hcpInfo) {

        String sql =
            "update \"HCPInfo\" set \"SerialNumber\"=?, \"UpdatedAt\"=now(), \"factory_data\"=? where \"HarmanID\"=?";
        return jdbcTemplate.update(sql, hcpInfo.getSerialNumber(), Long.parseLong(hcpInfo.getFactoryId()),
            hcpInfo.getHarmanId());
    }

    /**
     * Maps Harman IDs for Vins.
     *
     * @param tempGroupId The temp group ID.
     * @return The number of rows affected.
     */
    public int mapHarmanIdsForVins(long tempGroupId) {

        String sql =
            "update \"TempDeviceGroup\" set \"HarmanID\" = \"HCPInfo\".\"HarmanID\",\"IsActive\"=true from"
                + " \"HCPInfo\",\"Device\" d WHERE d.\"HarmanID\"=\"HCPInfo\".\"HarmanID\" and "
                + "\"TempDeviceGroup\".\"VIN\"=\"HCPInfo\".\"VIN\" and \"GroupID\"=? and d.\"IsActive\"=true ";
        return jdbcTemplate.update(sql, new Object[]{tempGroupId}, new int[]{Types.BIGINT});
    }

    /**
     * Retrieves Vins to preactivate for a given temp group ID.
     *
     * @param tempGroupId The temp group ID.
     * @return The list of Vins.
     */
    public List<String> getVinsToPreactivate(long tempGroupId) {
        List<String> vins = null;
        String sql = "select \"VIN\" from \"TempDeviceGroup\" where \"HarmanID\" is null and \"GroupID\"=?";
        vins = jdbcTemplate.queryForList(sql, new Object[]{tempGroupId}, String.class);
        return vins;
    }

    /**
     * Updates the TempDeviceGroup record with the given Harman ID, VIN, and temp group ID.
     *
     * @param harmanId    The Harman ID.
     * @param vin         The VIN (Vehicle Identification Number).
     * @param tempGroupId The temp group ID.
     * @return The number of rows affected.
     */
    public int updateTempDeviceGroup(String harmanId, String vin, long tempGroupId) {
        String sql = "update \"TempDeviceGroup\" set  \"HarmanID\"=? where \"GroupID\"=? and \"VIN\"=?";
        return jdbcTemplate.update(sql, new Object[]{harmanId, tempGroupId, vin}, new int[]{Types.VARCHAR, Types.BIGINT,
            Types.VARCHAR});
    }

    /**
     * Retrieves the HCPInfo record for the given VIN.
     *
     * @param vin The VIN (Vehicle Identification Number).
     * @return The HCPInfo object.
     */
    public HcpInfo findByVin(String vin) {
        String sqlQuery =
            "select * from \"HCPInfo\" h,\"Device\" d where h.\"HarmanID\"=d.\"HarmanID\" and"
                + " d.\"IsActive\"=true and \"VIN\"=?";
        String[] vins = {vin};
        List<HcpInfo> hcpInfos = jdbcTemplate.query(sqlQuery, vins, new HcpInfoMapper());
        if (CollectionUtils.isEmpty(hcpInfos)) {
            return null;
        } else {
            return hcpInfos.get(0);
        }
    }

    /**
     * Retrieves the HCPInfo record for the given factory ID.
     *
     * @param factoryId The factory ID.
     * @return The HCPInfo object.
     */
    public HcpInfo findByFactoryId(Long factoryId) {
        log.debug("getting hcpinfo for factoryid {}", factoryId);
        String sql = "select * from \"HCPInfo\" where \"factory_data\" =:FACTORYID";
        MapSqlParameterSource params = new MapSqlParameterSource(FACTORYID, factoryId);
        List<HcpInfo> hcpInfos = namedParameterJdbcTemplate.query(sql, params, new HcpInfoMapper());
        if (CollectionUtils.isEmpty(hcpInfos)) {
            return null;
        }
        return hcpInfos.get(0);
    }

    /**
     * Retrieves the active HCPInfo record for the given factory ID.
     *
     * @param factoryId The factory ID.
     * @return The HCPInfo object.
     */
    public HcpInfo findActiveHcpInfo(Long factoryId) {
        String sql =
            " select * from \"HCPInfo\" h,\"Device\" d where d.\"HarmanID\"=h.\"HarmanID\" and d.\"IsActive\"=true"
                + " and h.\"factory_data\"=?";
        Long[] vins = {factoryId};
        List<HcpInfo> hcpInfos = jdbcTemplate.query(sql, vins, new HcpInfoMapper());
        if (hcpInfos.isEmpty()) {
            return null;
        }
        return hcpInfos.get(0);
    }
}