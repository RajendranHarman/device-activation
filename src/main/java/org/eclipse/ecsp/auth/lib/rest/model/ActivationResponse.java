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

package org.eclipse.ecsp.auth.lib.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.ecsp.auth.lib.model.DeviceInfoResponse;

/**
 * Represents the response received after device activation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ActivationResponse {
    private String deviceId;
    private String passcode;
    private String deviceAssociationCode;
    private DeviceInfoResponse deviceInfoResponse;
    @JsonIgnore
    private boolean isProvisionedAlive;

    /**
     * Retrieves the device ID.
     *
     * @return The device ID.
     */
    @JsonProperty("deviceId")
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device ID.
     *
     * @param deviceId The device ID to set.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Retrieves the device association code.
     *
     * @return The device association code.
     */
    public String getDeviceAssociationCode() {
        return deviceAssociationCode;
    }

    /**
     * Sets the device association code.
     *
     * @param deviceAssociationCode The device association code to set.
     */
    public void setDeviceAssociationCode(String deviceAssociationCode) {
        this.deviceAssociationCode = deviceAssociationCode;
    }

    /**
     * Retrieves the passcode.
     *
     * @return The passcode.
     */
    public String getPasscode() {
        return passcode;
    }

    /**
     * Sets the passcode.
     *
     * @param passcode The passcode to set.
     */
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    /**
     * Retrieves the device info response.
     *
     * @return The device info response.
     */
    public DeviceInfoResponse getDeviceInfoResponse() {
        return deviceInfoResponse;
    }

    /**
     * Sets the device info response.
     *
     * @param deviceInfoResponse The device info response to set.
     */
    public void setDeviceInfoResponse(DeviceInfoResponse deviceInfoResponse) {
        this.deviceInfoResponse = deviceInfoResponse;
    }

    /**
     * Returns a string representation of the ActivationResponse object.
     *
     * @return A string representation of the object.
     */
    public String toString() {
        return this.deviceId + ":" + this.passcode;
    }

    /**
     * Checks if the device is in the provisioned alive state.
     *
     * @return true if the device is provisioned alive, false otherwise.
     */
    @JsonIgnore
    public boolean isProvisionedAlive() {
        return isProvisionedAlive;
    }

    /**
     * Sets the provisioned alive state of the device.
     *
     * @param isProvisionedAlive The provisioned alive state to set.
     */
    @JsonIgnore
    public void setProvisionedAlive(boolean isProvisionedAlive) {
        this.isProvisionedAlive = isProvisionedAlive;
    }
}
