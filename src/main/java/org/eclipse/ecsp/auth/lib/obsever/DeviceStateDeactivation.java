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

package org.eclipse.ecsp.auth.lib.obsever;

/**
 * Represents the state of device deactivation.
 */
public class DeviceStateDeactivation {
    private String harmanId;
    private String serialNumber;

    /**
     * Constructs a new instance of the {@code DeviceStateDeactivation} class with the specified Harman ID and
     * serial number.
     *
     * @param harmanId     the Harman ID of the device
     * @param serialNumber the serial number of the device
     */
    public DeviceStateDeactivation(String harmanId, String serialNumber) {
        super();
        this.harmanId = harmanId;
        this.serialNumber = serialNumber;
    }

    /**
     * Default constructor.
     */
    public DeviceStateDeactivation() {
        super();
    }

    /**
     * Gets the Harman ID.
     *
     * @return the Harman ID
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID.
     *
     * @param harmanId the Harman ID to set
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Gets the serial number.
     *
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number.
     *
     * @param serialNumber the serial number to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Returns a string representation of the DeviceStateDeactivation object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "DeviceStateDeactivation [harmanId=" + harmanId + ", serialNumber=" + serialNumber + "]";
    }
}
