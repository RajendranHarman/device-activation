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
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceInfo;

/**
 * This class is an adapter that adapts the ActivationRequestData to the DeviceInfo class.
 * It provides methods to retrieve various device information such as serial number, software version, device type,
 * VIN, IMEI, SSID, ICCID, BSSID, MSISDN, IMSI, product type, hardware version, and AAD.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivationRequestDataToDeviceInfoAdapter extends DeviceInfo {

    private ActivationRequestData activationRequestData;

    /**
     * Constructs a new ActivationRequestDataToDeviceInfoAdapter with the given ActivationRequestData.
     *
     * @param activationRequestData The ActivationRequestData to be adapted.
     */
    public ActivationRequestDataToDeviceInfoAdapter(ActivationRequestData activationRequestData) {
        this.activationRequestData = activationRequestData;
    }

    /**
     * Returns the serial number of the device.
     *
     * @return The serial number.
     */
    @Override
    public String getSerialNumber() {
        return activationRequestData.getSerialNumber();
    }

    /**
     * Returns the software version of the device.
     *
     * @return The software version.
     */
    @Override
    public String getSoftwareVersion() {
        return activationRequestData.getSwVersion();
    }

    /**
     * Returns the device type.
     *
     * @return The device type.
     */
    @Override
    public String getDeviceType() {
        return activationRequestData.getDeviceType();
    }

    /**
     * Returns the VIN (Vehicle Identification Number) of the device.
     *
     * @return The VIN.
     */
    @Override
    public String getVin() {
        return activationRequestData.getVin();
    }

    /**
     * Returns the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @return The IMEI.
     */
    public String getImei() {
        return activationRequestData.getImei();
    }

    /**
     * Returns the SSID (Service Set Identifier) of the device.
     *
     * @return The SSID.
     */
    public String getSsid() {
        return activationRequestData.getSsid();
    }

    /**
     * Returns the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @return The ICCID.
     */
    public String getIccid() {
        return activationRequestData.getIccid();
    }

    /**
     * Returns the BSSID (Basic Service Set Identifier) of the device.
     *
     * @return The BSSID.
     */
    public String getBssid() {
        return activationRequestData.getBssid();
    }

    /**
     * Returns the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     *
     * @return The MSISDN.
     */
    public String getMsisdn() {
        return activationRequestData.getMsisdn();
    }

    /**
     * Returns the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @return The IMSI.
     */
    public String getImsi() {
        return activationRequestData.getImsi();
    }

    /**
     * Returns the product type of the device.
     *
     * @return The product type.
     */
    public String getProductType() {
        return activationRequestData.getProductType();
    }

    /**
     * Returns the hardware version of the device.
     *
     * @return The hardware version.
     */
    @Override
    public String getHardwareVersion() {
        return activationRequestData.getHwVersion();
    }

    /**
     * Returns the AAD (Additional Authenticated Data) of the device.
     *
     * @return The AAD.
     */
    public String getAad() {
        return activationRequestData.getAad();
    }
}
