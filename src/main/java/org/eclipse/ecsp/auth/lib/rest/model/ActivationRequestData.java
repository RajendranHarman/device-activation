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
import jakarta.validation.constraints.NotBlank;

/**
 * Represents the activation request data for a device.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivationRequestData {
    @NotBlank(message = "vin is required and not allowed be empty")
    private String vin;
    @NotBlank(message = "SW-Version is required and not allowed be empty")
    private String swVersion;
    @NotBlank(message = "HW-Version is required and not allowed be empty")
    private String hwVersion;
    @NotBlank(message = "serialNumber is required and not allowed be empty")
    private String serialNumber;
    private String imei;
    private String iccid;
    private String msisdn;
    private String imsi;
    @NotBlank(message = "qualifier is required and not allowed be empty")
    private String qualifier;
    @NotBlank(message = "productType is required and not allowed be empty")
    private String productType;
    @NotBlank(message = "deviceType is required and not allowed be empty")
    private String deviceType;
    private String bssid;
    private String ssid;
    private String aad;

    /**
     * Default constructor.
     */
    public ActivationRequestData() {
    }

    /**
     * Constructs a new ActivationRequestData object with the specified VIN, serial number, and qualifier.
     *
     * @param vin The Vehicle Identification Number (VIN) of the device.
     * @param serialNumber The serial number of the device.
     * @param qualifier The qualifier for the device activation.
     */
    public ActivationRequestData(String vin, String serialNumber, String qualifier) {
        super();
        this.vin = vin;
        this.serialNumber = serialNumber;
        this.qualifier = qualifier;
    }

    /**
     * Gets the VIN.
     *
     * @return the VIN
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the VIN.
     *
     * @param vin the VIN to set
     */
    public void setVin(String vin) {
        this.vin = vin;
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
     * Gets the IMEI.
     *
     * @return the IMEI
     */
    @JsonProperty(value = "imei")
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI.
     *
     * @param imei the IMEI to set
     */
    @JsonProperty(value = "imei")
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the ICCID.
     *
     * @return the ICCID
     */
    @JsonProperty(value = "iccid")
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID.
     *
     * @param iccid the ICCID to set
     */
    @JsonProperty(value = "iccid")
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the MSISDN.
     *
     * @return the MSISDN
     */
    @JsonProperty(value = "msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN.
     *
     * @param msisdn the MSISDN to set
     */
    @JsonProperty(value = "msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Gets the IMSI.
     *
     * @return the IMSI
     */
    @JsonProperty(value = "imsi")
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI.
     *
     * @param imsi the IMSI to set
     */
    @JsonProperty(value = "imsi")
    public void setImsi(String imsi) {
        this.imsi = imsi;
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
     * @param deviceType the product type to set
     */
    public void setProductType(String deviceType) {
        this.productType = deviceType;
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
     * Gets the SSID.
     *
     * @return the SSID
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID.
     *
     * @param ssid the SSID to set
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Gets the AAD.
     *
     * @return the AAD
     */
    @JsonProperty(value = "aad")
    public String getAad() {
        return aad;
    }

    /**
     * Sets the AAD.
     *
     * @param aad the AAD to set
     */
    @JsonProperty(value = "aad")
    public void setAad(String aad) {
        this.aad = aad;
    }

    /**
     * Gets the BSSID.
     *
     * @return the BSSID
     */
    @JsonProperty(value = "bssid")
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID.
     *
     * @param bssid the BSSID to set
     */
    @JsonProperty(value = "bssid")
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Returns a string representation of the ActivationRequestData object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "ActivationRequestData [vin=" + vin + ", swVersion=" + swVersion + ", hwVersion=" + hwVersion
            + ", serialNumber="
            + serialNumber + ", imei=" + imei + ", iccid=" + iccid + ", msisdn=" + msisdn + ", imsi=" + imsi
            + ", qualifier="
            + qualifier + ", productType=" + productType + ", deviceType=" + deviceType + ", bssid=" + bssid
            + ", ssid=" + ssid + ", aad=" + aad + "]";
    }
}
