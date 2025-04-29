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
import org.eclipse.ecsp.services.deviceactivation.model.DeviceActivationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The DAO (Data Access Object) class for managing device activation state in the database.
 * This class provides methods to check if a device can be activated, insert device activation state,
 * disable device activation record, find active devices, and retrieve associated VIN.
 */
@Configurable
@Repository
@Slf4j
public class DeviceActivationStateDao {

    public static final long RECORD_ID = -1L;
    private static final String CAN_DEVICE_BE_ACTIVATED =
        "select count(*)>0 from device_activation_state where serial_number=? and activation_ready = true";
    private static final String CAN_DEVICE_BE_ACTIVATED_BY_FD_ID =
        "select count(*)>0 from device_activation_state where factory_data=? and activation_ready = true";
    private static final String INSERT =
        "insert into device_activation_state(serial_number,activation_initiated_on,activation_initiated_by,"
            + "activation_ready) values(?,?,?,?)";
    private static final String INSERT_REPLACE_DEVICE_ACTIVATION_STATE =
        "insert into device_activation_state(serial_number,activation_initiated_on,activation_initiated_by,"
            + "activation_ready,factory_data) values(?,?,?,?,?)";
    private static final String FIND_ACTIVE_RECORD =
        "select id from device_activation_state where serial_number=? and activation_ready=true";
    private static final String DISABLE_RECORD =
        "update device_activation_state set activation_ready=false and deactivation_initiated_by=? and"
            + " deactivation_initiated_on=now() where id=?";
    private static final String FIND_ACTIVE_RECORD_SQL =
        "select id from device_activation_state where factory_data=? and activation_ready=true";
    private static final String DISABLE_ACTIVATION_READY =
        "update device_activation_state set activation_ready=false and deactivation_initiated_by=? and"
            + " deactivation_initiated_on=now() where factory_data=? and activation_ready=true";
    private static final String DISABLE_ACTIVATION_READY_BY_FACTORYID =
        "update device_activation_state set activation_ready=false where factory_data=? and activation_ready=true";
    private static final String GET_ASSOCIATED_VIN =
        "select vin from vin_details v  left join device_association d on v.reference_id=d.id where d.serial_number=?"
            + " and d.association_status in ('ASSOCIATED','ASSOCIATION_INITIATED')";
    private static final String GET_TRANSACTION_ID =
        "select tran_id from sim_details s left join device_association d on s.reference_id=d.id where"
            + " d.serial_number=? and d.association_status in ('ASSOCIATED','ASSOCIATION_INITIATED')";
    private static final String UPDATE_TRANSACTION_STATUS = "update sim_details set tran_status=? where tran_id=?";
    private static final String GET_ASSOCIATED_USER =
        "select user_id from device_association d where d.serial_number=? and d.association_status in ('ASSOCIATED')";
    private static final String GET_SIM_TRANSACTION_STATUS =
        "select tran_status from sim_details s left join device_association d on s.reference_id=d.id where"
            + " d.serial_number=? and d.association_status in ('ASSOCIATION_INITIATED','ASSOCIATED')";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Check if a device can be activated based on its serial number.
     *
     * @param serailNumber The serial number of the device.
     * @return true if the device can be activated, false otherwise.
     */
    public boolean canBeActivated(String serailNumber) {
        Boolean canBeActivated =
            jdbcTemplate.queryForObject(CAN_DEVICE_BE_ACTIVATED, new Object[]{serailNumber}, Boolean.class);
        return canBeActivated != null ? canBeActivated : false;
    }

    /**
     * Check if a device can be activated based on its factory data ID.
     *
     * @param factoryDataId The factory data ID of the device.
     * @return true if the device can be activated, false otherwise.
     */
    public boolean canBeActivated(long factoryDataId) {
        Boolean canBeActivated =
            jdbcTemplate.queryForObject(CAN_DEVICE_BE_ACTIVATED_BY_FD_ID, new Object[]{factoryDataId}, Boolean.class);
        return canBeActivated != null ? canBeActivated : false;
    }

    /**
     * Insert a device activation state into the database.
     *
     * @param activationState The device activation state to insert.
     */
    public void insert(DeviceActivationState activationState) {
        jdbcTemplate.update(INSERT, activationState.getSerialNumber(), activationState.getActivationInitiatedOn(),
            activationState.getActivationInitiatedBy(), activationState.isActivationReady());
    }

    /**
     * Insert a device activation state for replacing a device into the database.
     *
     * @param activationState The device activation state to insert.
     */
    public void insertReplaceDeviceActivationState(DeviceActivationState activationState) {
        jdbcTemplate.update(INSERT_REPLACE_DEVICE_ACTIVATION_STATE, activationState.getSerialNumber(),
            activationState.getActivationInitiatedOn(),
            activationState.getActivationInitiatedBy(), activationState.isActivationReady(),
            activationState.getFactoryDataId());
    }

    /**
     * Disable a device activation record.
     *
     * @param id     The ID of the device activation record.
     * @param userId The ID of the user who initiated the deactivation.
     */
    public void disableRecord(long id, String userId) {
        jdbcTemplate.update(DISABLE_RECORD, userId, id);
    }

    /**
     * Disable the activation ready status for a device based on its factory ID.
     *
     * @param factoryId The factory ID of the device.
     * @param userId    The ID of the user who initiated the deactivation.
     */
    public void disableActivationReady(long factoryId, String userId) {
        jdbcTemplate.update(DISABLE_ACTIVATION_READY, userId, factoryId);
    }

    /**
     * Disable the activation ready status for a device based on its factory ID.
     *
     * @param factoryId The factory ID of the device.
     */
    public void disableActivationReadyByFacotryId(long factoryId) {
        jdbcTemplate.update(DISABLE_ACTIVATION_READY_BY_FACTORYID, factoryId);
    }

    /**
     * Find the ID of the active device based on its serial number.
     *
     * @param serialNumber The serial number of the device.
     * @return The ID of the active device, or 0 if no active device is found.
     */
    public long findActiveDevice(String serialNumber) {
        Long activeRecordId = RECORD_ID;
        try {
            activeRecordId = jdbcTemplate.queryForObject(FIND_ACTIVE_RECORD, new Object[]{serialNumber}, Long.class);
        } catch (DataAccessException e) {
            log.error(
                "Exception occurred while trying to retrieve the ID, could be that no active record found for"
                    + " the serial {}", serialNumber, e);
        }
        return activeRecordId != null ? activeRecordId : 0L;
    }

    /**
     * Find the ID of the active device based on its factory data ID.
     *
     * @param factoryDataId The factory data ID of the device.
     * @return The ID of the active device, or 0 if no active device is found.
     */
    public long findActiveDevice(long factoryDataId) {
        Long activeRecordId = RECORD_ID;
        try {
            activeRecordId =
                jdbcTemplate.queryForObject(FIND_ACTIVE_RECORD_SQL, new Object[]{factoryDataId}, Long.class);
        } catch (DataAccessException e) {
            log.error(
                "Exception occurred while trying to retrieve the ID, could be that no active record found for"
                    + " factory_data_id:{}", factoryDataId, e);
        }
        return activeRecordId != null ? activeRecordId : 0L;
    }

    /**
     * Find the IDs of the active devices based on their serial number.
     *
     * @param serialNumber The serial number of the devices.
     * @return A list of active device IDs, or null if no active devices are found.
     */
    public List<Long> findActiveDevices(String serialNumber) {
        List<Long> activeRecordIdList = null;
        try {
            log.error("findingactivedevicefor serial:{}", serialNumber);
            activeRecordIdList = jdbcTemplate.queryForList(FIND_ACTIVE_RECORD, new Object[]{serialNumber}, Long.class);
            log.error("Found active devices list:{} for serial number:{}", activeRecordIdList, serialNumber);
            if (activeRecordIdList != null && !activeRecordIdList.isEmpty()) {
                log.error("Found active devices list:{}", activeRecordIdList.size());
                log.error("Returning active list size:{}", activeRecordIdList.size());
            } else {
                log.error("NOT Found active devices list:{}", activeRecordIdList);
            }

        } catch (DataAccessException e) {
            log.error(
                "Exception occurred while trying to retrieve the ID, could be that 0 or more than 1 activated record"
                    + " found for the serial {}", serialNumber, e);
        }
        log.error("Returning active list size:{}", activeRecordIdList);
        return activeRecordIdList;
    }

    /**
     * Get the associated VIN (Vehicle Identification Number) for a device.
     *
     * @param serialNumber The serial number of the device.
     * @return The associated VIN, or null if no record is found.
     */
    public String getAssociatedVin(String serialNumber) {
        String vin = null;
        try {
            vin = jdbcTemplate.queryForObject(GET_ASSOCIATED_VIN, new Object[]{serialNumber}, String.class);
        } catch (DataAccessException e) {
            log.error(
                "Exception occurred while trying to retrieve the vin, could be that no record found for the serial: {}",
                serialNumber, e);
        }
        return vin;
    }

    /**
     * Get the associated user ID for a device.
     *
     * @param serialNumber The serial number of the device.
     * @return The associated user ID, or null if no record is found.
     */
    public String getAssociatedUserId(String serialNumber) {
        String userId = null;
        try {
            userId = jdbcTemplate.queryForObject(GET_ASSOCIATED_USER, new Object[]{serialNumber}, String.class);
        } catch (DataAccessException e) {
            log.error(
                "Exception occurred while trying to retrieve the user ID, could be that no record found for the"
                    + " serial: {}", serialNumber, e);
        }
        return userId;
    }

    /**
     * Get the transaction status for a SIM card associated with a device.
     *
     * @param serialNumber The serial number of the device.
     * @return The transaction status, or null if no record is found.
     */
    public String getSimTransactionStatus(String serialNumber) {
        String transactionStatus = null;
        try {
            transactionStatus =
                jdbcTemplate.queryForObject(GET_SIM_TRANSACTION_STATUS, new Object[]{serialNumber}, String.class);
        } catch (DataAccessException e) {
            log.error(
                "Exception occurred while trying to retrieve the vin, could be that no record found for the serial: {}",
                serialNumber, e);
        }
        return transactionStatus;
    }

    /**
     * Retrieves the transaction ID associated with the given serial number.
     *
     * @param serialNumber the serial number of the device
     * @return the transaction ID associated with the serial number
     */
    public String getTransactionId(String serialNumber) {
        return jdbcTemplate.queryForObject(GET_TRANSACTION_ID, new Object[]{serialNumber}, String.class);
    }

    /**
     * Updates the transaction status for a given transaction ID.
     *
     * @param tranId the transaction ID to update the status for
     */
    public void updateTransactionStatus(String tranId) {
        jdbcTemplate.update(UPDATE_TRANSACTION_STATUS, "Completed", tranId);
    }

}
