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

/**
 * Represents an extended device information.
 * Extends the base class DeviceInfo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtentedDeviceInfo extends DeviceInfo {

    private String imei;
    private String ssid;
    private String iccid;
    private String bssid;
    private String msisdn;
    private String imsi;
    private String productType;

    /**
     * Gets the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @return The IMEI of the device.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @param imei The IMEI to set.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the SSID (Service Set Identifier) of the device.
     *
     * @return The SSID of the device.
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID (Service Set Identifier) of the device.
     *
     * @param ssid The SSID to set.
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Gets the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @return The ICCID of the device.
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @param iccid The ICCID to set.
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the BSSID (Basic Service Set Identifier) of the device.
     *
     * @return The BSSID of the device.
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID (Basic Service Set Identifier) of the device.
     *
     * @param bssid The BSSID to set.
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Gets the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     *
     * @return The MSISDN of the device.
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     *
     * @param msisdn The MSISDN to set.
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Gets the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @return The IMSI of the device.
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @param imsi The IMSI to set.
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the product type of the device.
     *
     * @return The product type of the device.
     */
    public String getProductType() {
        return productType;
    }

    /**
     * Sets the product type of the device.
     *
     * @param productType The product type to set.
     */
    public void setProductType(String productType) {
        this.productType = productType;
    }

    /**
     * Returns a string representation of the ExtendedDeviceInfo object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "ExtendedDeviceInfo [imei=" + imei + ", ssid=" + ssid + ", iccid=" + iccid + ", bssid=" + bssid
            + ", msisdn="
            + msisdn + ", imsi=" + imsi + ", productType=" + productType + "]";
    }
}

