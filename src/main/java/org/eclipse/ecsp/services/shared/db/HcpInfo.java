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

package org.eclipse.ecsp.services.shared.db;

/**
 * The HcpInfo class represents information about a device activation in the system.
 */
public class HcpInfo {

    private Long id;
    private String harmanId;
    private String vin;
    private String serialNumber;
    private String factoryId;

    /**
     * Gets the ID of the device activation.
     *
     * @return The ID of the device activation.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the device activation.
     *
     * @param id The ID of the device activation.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the Harman ID of the device activation.
     *
     * @return The Harman ID of the device activation.
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID of the device activation.
     *
     * @param harmanId The Harman ID of the device activation.
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Gets the VIN (Vehicle Identification Number) of the device activation.
     *
     * @return The VIN of the device activation.
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the VIN (Vehicle Identification Number) of the device activation.
     *
     * @param vin The VIN of the device activation.
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Gets the serial number of the device activation.
     *
     * @return The serial number of the device activation.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device activation.
     *
     * @param serialNumber The serial number of the device activation.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the factory ID of the device activation.
     *
     * @return The factory ID of the device activation.
     */
    public String getFactoryId() {
        return factoryId;
    }

    /**
     * Sets the factory ID of the device activation.
     *
     * @param factoryId The factory ID of the device activation.
     */
    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    /**
     * Returns a string representation of the HcpInfo object.
     *
     * @return A string representation of the HcpInfo object.
     */
    @Override
    public String toString() {
        return "HCPInfo [id=" + id + ", harmanId=" + harmanId + ", vin=" + vin + ", serialNumber=" + serialNumber
            + ", factoryId="
            + factoryId + "]";
    }

}
