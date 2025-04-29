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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an activation request for a device.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivationRequest {
    private String vin;
    private String serialNumber;
    private String qualifier;
    private String hwSerialNumber;
    private String hwModelId;
    private String hwVersion;
    private String swVersion;
    private String productType;

    /**
     * Default constructor.
     */
    public ActivationRequest() {
    }

    /**
     * Constructor with parameters.
     *
     * @param vin           the VIN (Vehicle Identification Number)
     * @param serialNumber  the serial number of the device
     * @param qualifier     the qualifier
     */
    public ActivationRequest(String vin, String serialNumber, String qualifier) {
        super();
        this.vin = vin;
        this.serialNumber = serialNumber;
        this.qualifier = qualifier;
    }

    /**
     * Gets the hardware serial number.
     *
     * @return the hardware serial number
     */
    public String getHwSerialNumber() {
        return hwSerialNumber;
    }

    /**
     * Sets the hardware serial number.
     *
     * @param hwSerialNumber the hardware serial number to set
     */
    public void setHwSerialNumber(String hwSerialNumber) {
        this.hwSerialNumber = hwSerialNumber;
    }

    /**
     * Gets the hardware model ID.
     *
     * @return the hardware model ID
     */
    public String getHwModelId() {
        return hwModelId;
    }

    /**
     * Sets the hardware model ID.
     *
     * @param hwModelId the hardware model ID to set
     */
    public void setHwModelId(String hwModelId) {
        this.hwModelId = hwModelId;
    }

    /**
     * Gets the VIN (Vehicle Identification Number).
     *
     * @return the VIN
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the VIN (Vehicle Identification Number).
     *
     * @param vin the VIN to set
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Gets the serial number of the device.
     *
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber the serial number to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the qualifier.
     *
     * @return the qualifier
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * Sets the qualifier.
     *
     * @param qualifier the qualifier to set
     */
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    /**
     * Gets the hardware version.
     *
     * @return the hardware version
     */
    @JsonProperty(value = "HW-Version")
    public String getHwVersion() {
        return hwVersion;
    }

    /**
     * Sets the hardware version.
     *
     * @param hwVersion the hardware version to set
     */
    @JsonProperty(value = "HW-Version")
    public void setHwVersion(String hwVersion) {
        this.hwVersion = hwVersion;
    }

    /**
     * Gets the software version.
     *
     * @return the software version
     */
    @JsonProperty(value = "SW-Version")
    public String getSwVersion() {
        return swVersion;
    }

    /**
     * Sets the software version.
     *
     * @param swVersion the software version to set
     */
    @JsonProperty(value = "SW-Version")
    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    /**
     * Gets the product type.
     *
     * @return the product type
     */
    public String getProductType() {
        return productType;
    }

    /**
     * Sets the product type.
     *
     * @param deviceType the deviceType to set
     */
    public void setProductType(String deviceType) {
        this.productType = deviceType;
    }

    /**
     * Returns a string representation of the ActivationRequest object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "ActivationRequest [vin=" + vin + ", serialNumber=" + serialNumber + ", qualifier=" + qualifier
            + ", hwSerialNumber=" + hwSerialNumber + ", hwModelId=" + hwModelId + "]";
    }
}
