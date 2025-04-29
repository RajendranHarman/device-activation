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

import org.eclipse.ecsp.auth.lib.rest.model.ActivationResponse;

/**
 * Represents the state of a device activation.
 */
public class DeviceStateActivation {

    private ActivationResponse activationResponse;
    private String swVersion;
    private String hwVersion;
    private String serialNumber;
    private String deviceType;
    private boolean reactivationFlag;

    /**
     * Default constructor.
     */
    public DeviceStateActivation() {
        super();
    }

    /**
     * Constructs a new DeviceStateActivation object with the specified parameters.
     *
     * @param activationResponse The activation response.
     * @param swVersion The software version of the device.
     * @param hwVersion The hardware version of the device.
     * @param serialNumber The serial number of the device.
     * @param deviceType The type of the device.
     * @param reactivationFlag The reactivation flag indicating whether the device is being reactivated.
     */
    public DeviceStateActivation(ActivationResponse activationResponse,
                                    String swVersion, String hwVersion, String serialNumber, String deviceType,
                                    boolean reactivationFlag) {
        super();
        this.activationResponse = activationResponse;
        this.swVersion = swVersion;
        this.hwVersion = hwVersion;
        this.serialNumber = serialNumber;
        this.deviceType = deviceType;
        this.reactivationFlag = reactivationFlag;
    }

    /**
     * Gets the activation response.
     *
     * @return the activation response
     */
    public ActivationResponse getActivationResponse() {
        return activationResponse;
    }

    /**
     * Sets the activation response.
     *
     * @param activationResponse the activation response to set
     */
    public void setActivationResponse(ActivationResponse activationResponse) {
        this.activationResponse = activationResponse;
    }

    /**
     * Gets the software version.
     *
     * @return the software version
     */
    public String getSwVersion() {
        return swVersion;
    }

    /**
     * Sets the software version.
     *
     * @param swVersion the software version to set
     */
    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    /**
     * Gets the hardware version.
     *
     * @return the hardware version
     */
    public String getHwVersion() {
        return hwVersion;
    }

    /**
     * Sets the hardware version.
     *
     * @param hwVersion the hardware version to set
     */
    public void setHwVersion(String hwVersion) {
        this.hwVersion = hwVersion;
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
     * Gets the device type.
     *
     * @return the device type
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the device type.
     *
     * @param deviceType the device type to set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Checks if the device is being reactivated.
     *
     * @return true if the device is being reactivated, false otherwise
     */
    public boolean isReactivationFlag() {
        return reactivationFlag;
    }

    /**
     * Sets the reactivation flag.
     *
     * @param reactivationFlag the reactivation flag to set
     */
    public void setReactivationFlag(boolean reactivationFlag) {
        this.reactivationFlag = reactivationFlag;
    }

    /**
     * Returns a string representation of the DeviceStateActivation object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "DeviceStateActivation [activationResponse=" + activationResponse + ", swVersion=" + swVersion
            + ", hwVersion=" + hwVersion
            + ", serialNumber=" + serialNumber + ", deviceType=" + deviceType + ", reactivationFlag="
            + reactivationFlag + "]";
    }
}
