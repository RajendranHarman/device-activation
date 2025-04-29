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

package org.eclipse.ecsp.deviceassociation.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.sql.Timestamp;

/**
 * Represents the history of device associations.
 */
@JsonInclude(Include.NON_NULL)
public class DeviceAssociationHistory {
    @JsonIgnore
    private long id;
    private String serialNumber;
    private String userId;
    private String harmanId;
    private AssociationStatus associationStatus;
    private Timestamp associatedOn;
    private String associatedBy;
    private Timestamp disassociatedOn;
    private String disassociatedBy;
    private Timestamp modifiedOn;
    private String modifiedBy;
    @JsonIgnore
    private long factoryId;

    /**
     * Gets the ID of the device association history.
     *
     * @return The ID of the device association history.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the device association history.
     *
     * @param id The ID of the device association history.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the serial number of the device.
     *
     * @return The serial number of the device.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber The serial number of the device.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the user ID associated with the device.
     *
     * @return The user ID associated with the device.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with the device.
     *
     * @param userId The user ID associated with the device.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the Harman ID associated with the device.
     *
     * @return The Harman ID associated with the device.
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID associated with the device.
     *
     * @param harmanId The Harman ID associated with the device.
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Gets the association status of the device.
     *
     * @return The association status of the device.
     */
    public AssociationStatus getAssociationStatus() {
        return associationStatus;
    }

    /**
     * Sets the association status of the device.
     *
     * @param associationStatus The association status of the device.
     */
    public void setAssociationStatus(AssociationStatus associationStatus) {
        this.associationStatus = associationStatus;
    }

    /**
     * Gets the timestamp when the device was associated.
     *
     * @return The timestamp when the device was associated.
     */
    public Timestamp getAssociatedOn() {
        return associatedOn != null ? (Timestamp) associatedOn.clone() : null;
    }

    /**
     * Sets the timestamp when the device was associated.
     *
     * @param associatedOn The timestamp when the device was associated.
     */
    public void setAssociatedOn(Timestamp associatedOn) {
        this.associatedOn = associatedOn != null ? (Timestamp) associatedOn.clone() : null;
    }

    /**
     * Gets the user who associated the device.
     *
     * @return The user who associated the device.
     */
    public String getAssociatedBy() {
        return associatedBy;
    }

    /**
     * Sets the user who associated the device.
     *
     * @param associatedBy The user who associated the device.
     */
    public void setAssociatedBy(String associatedBy) {
        this.associatedBy = associatedBy;
    }

    /**
     * Gets the timestamp when the device was disassociated.
     *
     * @return The timestamp when the device was disassociated.
     */
    public Timestamp getDisassociatedOn() {
        return disassociatedOn != null ? (Timestamp) disassociatedOn.clone() : null;
    }

    /**
     * Sets the timestamp when the device was disassociated.
     *
     * @param disassociatedOn The timestamp when the device was disassociated.
     */
    public void setDisassociatedOn(Timestamp disassociatedOn) {
        this.disassociatedOn = disassociatedOn != null ? (Timestamp) disassociatedOn.clone() : null;
    }

    /**
     * Gets the user who disassociated the device.
     *
     * @return The user who disassociated the device.
     */
    public String getDisassociatedBy() {
        return disassociatedBy;
    }

    /**
     * Sets the user who disassociated the device.
     *
     * @param disassociatedBy The user who disassociated the device.
     */
    public void setDisassociatedBy(String disassociatedBy) {
        this.disassociatedBy = disassociatedBy;
    }

    /**
     * Gets the timestamp when the device was last modified.
     *
     * @return The timestamp when the device was last modified.
     */
    public Timestamp getModifiedOn() {
        return modifiedOn != null ? (Timestamp) modifiedOn.clone() : null;
    }

    /**
     * Sets the timestamp when the device was last modified.
     *
     * @param modifiedOn The timestamp when the device was last modified.
     */
    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = modifiedOn != null ? (Timestamp) modifiedOn.clone() : null;
    }

    /**
     * Gets the user who last modified the device.
     *
     * @return The user who last modified the device.
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the user who last modified the device.
     *
     * @param modifiedBy The user who last modified the device.
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * Gets the factory ID associated with the device.
     *
     * @return The factory ID associated with the device.
     */
    public long getFactoryId() {
        return factoryId;
    }

    /**
     * Sets the factory ID associated with the device.
     *
     * @param factoryId The factory ID associated with the device.
     */
    public void setFactoryId(long factoryId) {
        this.factoryId = factoryId;
    }
}