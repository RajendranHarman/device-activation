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

package org.eclipse.ecsp.services.deviceactivation.model;

import java.sql.Timestamp;

/**
 * Represents device activation fields.
 */
public class DeviceActivation {

    /**
     * id bigserial primary key, jitactId character varying not null.
     * harman_id character varying, passcode character varying,
     * activation_date timestamp with time zone, device_type character varying,
     * is_active boolean
     */

    private long id;
    private String jitactId;
    private String harmanId;
    private String passcode;
    private Timestamp activationDate;
    private String deviceType;
    private boolean isActive;


    /**
     * Retrieves the sequence generated id of the device activation.
     *
     * @return the sequence generated id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id of the device activation.
     *
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retrieves the jitactId of the device.
     *
     * @return the jitactId
     */
    public String getJitactId() {
        return jitactId;
    }

    /**
     * Sets the jitactId of the device activation.
     *
     * @param jitactId to set
     */
    public void setJitactId(String jitactId) {
        this.jitactId = jitactId;
    }

    /**
     * Retrieves the harmanId of the device.
     *
     * @return the harmanId
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the harmanId of the device activation.
     *
     * @param harmanId to set
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Retrieves the passcode of the device.
     *
     * @return the passcode
     */
    public String getPasscode() {
        return passcode;
    }

    /**
     * Sets the passcode of the device activation.
     *
     * @param passcode to set
     */
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    /**
     * Retrieves the activationDate of the device.
     *
     * @return the activationDate
     */
    public Timestamp getActivationDate() {
        return activationDate;
    }

    /**
     * Sets the activationDate of the device activation.
     *
     * @param activationDate to set
     */
    public void setActivationDate(Timestamp activationDate) {
        this.activationDate = activationDate;
    }

    /**
     * Retrieves the deviceType of the device.
     *
     * @return the deviceType
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the deviceType of the device activation.
     *
     * @param deviceType to set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Retrieves the isActive of the device.
     *
     * @return the isActive
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets the active of the device activation.
     *
     * @param active to set
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Returns a string representation of the DeviceActivation object.
     *
     * @return a string representation of the DeviceActivation object
     */
    @Override
    public String toString() {
        return "DeviceActivation["
                + "id=" + id
                + ", jitactId='" + jitactId + '\''
                + ", harmanId='" + harmanId + '\''
                + ", passcode='" + passcode + '\''
                + ", activationDate=" + activationDate
                + ", deviceType='" + deviceType + '\''
                + ", isActive=" + isActive
                + ']';
    }
}
