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

package org.eclipse.ecsp.deviceassociation.lib.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents information about a device.
 * This class contains properties for the Harman ID, serial number, software version,
 * hardware version, device type, VIN (Vehicle Identification Number), and extended device information.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfo {

    private String harmanId;
    private String serialNumber;
    private String softwareVersion;
    private String hardwareVersion;
    private String deviceType;
    private String vin;

    private ExtentedDeviceInfo extentedDeviceInfo;

    /**
     * Constructs a new DeviceInfo object.
     */
    public DeviceInfo() {
        super();
    }

    /**
     * Constructs a new DeviceInfo object with the specified parameters.
     *
     * @param harmanId           the Harman ID of the device
     * @param serialNumber       the serial number of the device
     * @param softwareVersion    the software version of the device
     * @param hardwareVersion    the hardware version of the device
     * @param deviceType         the type of the device
     * @param vin                the vehicle identification number associated with the device
     * @param extentedDeviceInfo the extended device information
     */
    public DeviceInfo(String harmanId, String serialNumber, String softwareVersion, String hardwareVersion,
                      String deviceType, String vin, ExtentedDeviceInfo extentedDeviceInfo) {
        super();
        this.harmanId = harmanId;
        this.serialNumber = serialNumber;
        this.softwareVersion = softwareVersion;
        this.hardwareVersion = hardwareVersion;
        this.deviceType = deviceType;
        this.vin = vin;
        this.extentedDeviceInfo = extentedDeviceInfo;
    }

    /**
     * Gets the Harman ID of the device.
     *
     * @return The Harman ID.
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID of the device.
     *
     * @param harmanId The Harman ID to set.
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Gets the serial number of the device.
     *
     * @return The serial number.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber The serial number to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the software version of the device.
     *
     * @return The software version.
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Sets the software version of the device.
     *
     * @param softwareVersion The software version to set.
     */
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    /**
     * Gets the device type.
     *
     * @return The device type.
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the device type.
     *
     * @param deviceType The device type to set.
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Gets the VIN (Vehicle Identification Number) of the device.
     *
     * @return The VIN.
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the VIN (Vehicle Identification Number) of the device.
     *
     * @param vin The VIN to set.
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Gets the hardware version of the device.
     *
     * @return The hardware version.
     */
    public String getHardwareVersion() {
        return hardwareVersion;
    }

    /**
     * Sets the hardware version of the device.
     *
     * @param hardwareVersion The hardware version to set.
     */
    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    /**
     * Gets the extended device information.
     *
     * @return The extended device information.
     */
    public ExtentedDeviceInfo getExtentedDeviceInfo() {
        return extentedDeviceInfo;
    }

    /**
     * Sets the extended device information.
     *
     * @param extentedDeviceInfo The extended device information to set.
     */
    public void setExtentedDeviceInfo(ExtentedDeviceInfo extentedDeviceInfo) {
        this.extentedDeviceInfo = extentedDeviceInfo;
    }

    /**
     * Returns a string representation of the DeviceInfo object.
     *
     * @return A string representation of the DeviceInfo object.
     */
    @Override
    public String toString() {
        return "DeviceInfo [harmanID=" + harmanId + ", serialNumber=" + serialNumber + ", softwareVersion="
            + softwareVersion + ", hardwareVersion=" + hardwareVersion + ", deviceType=" + deviceType + ", vin="
            + vin + "]";
    }

}
