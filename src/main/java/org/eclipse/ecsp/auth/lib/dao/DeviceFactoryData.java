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

package org.eclipse.ecsp.auth.lib.dao;

/**
 * The DeviceFactoryData class represents the data of a device in a factory.
 */
public class DeviceFactoryData {
    private long id;
    private String manufacturingDate;
    private String model;
    private String imei;
    private String serialNumber;
    private String platformVersion;
    private String iccid;
    private String ssid;
    private String bssid;
    private String msisdn;
    private String imsi;
    private String recordDate;
    private String createdDate;
    private String factoryAdmin;
    private String state;
    private String packageSerialNumber;
    private Boolean stolen;
    private Boolean faulty;
    private String deviceType;

    /**
     * Constructs a new DeviceFactoryData object.
     */
    public DeviceFactoryData() {

    }

    /**
     * Gets the ID of the device.
     *
     * @return the ID of the device
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the device.
     *
     * @param id the ID of the device
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the manufacturing date of the device.
     *
     * @return the manufacturing date of the device
     */
    public String getManufacturingDate() {
        return manufacturingDate;
    }

    /**
     * Sets the manufacturing date of the device.
     *
     * @param manufacturingDate the manufacturing date of the device
     */
    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    /**
     * Gets the model of the device.
     *
     * @return the model of the device
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the device.
     *
     * @param model the model of the device
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @return the IMEI of the device
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI (International Mobile Equipment Identity) number of the device.
     *
     * @param imei the IMEI number to set
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Returns the serial number of the device.
     *
     * @return the serial number of the device
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
     * Returns the platform version of the device.
     *
     * @return the platform version of the device
     */
    public String getPlatformVersion() {
        return platformVersion;
    }

    /**
     * Sets the platform version of the device.
     *
     * @param platformVersion the platform version to set
     */
    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    /**
     * Returns the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @return the ICCID of the device
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID (Integrated Circuit Card Identifier) for the device.
     *
     * @param iccid the ICCID to set
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Returns the SSID (Service Set Identifier) of the device.
     *
     * @return the SSID of the device
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID (Service Set Identifier) for the device.
     *
     * @param ssid the SSID to set
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Returns the BSSID (Basic Service Set Identifier) of the device.
     *
     * @return the BSSID of the device
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID (Basic Service Set Identifier) of the device.
     *
     * @param bssid the BSSID to set
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Returns the MSISDN (Mobile Station International Subscriber Directory Number) associated with this device.
     *
     * @return the MSISDN of the device
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN (Mobile Station International Subscriber Directory Number) for the device.
     *
     * @param msisdn the MSISDN to set
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Returns the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @return the IMSI of the device
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @param imsi the IMSI to set
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Returns the record date of the device.
     *
     * @return the record date of the device
     */
    public String getRecordDate() {
        return recordDate;
    }

    /**
     * Sets the record date for the device factory data.
     *
     * @param recordDate the record date to set
     */
    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    /**
     * Returns the created date of the device.
     *
     * @return the created date of the device
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the created date of the device factory data.
     *
     * @param createdDate the created date to set
     */
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Returns the factory admin.
     *
     * @return the factory admin
     */
    public String getFactoryAdmin() {
        return factoryAdmin;
    }

    /**
     * Sets the factory admin for the device.
     *
     * @param factoryAdmin the factory admin to set
     */
    public void setFactoryAdmin(String factoryAdmin) {
        this.factoryAdmin = factoryAdmin;
    }

    /**
     * Returns the state of the device.
     *
     * @return the state of the device
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state of the device.
     *
     * @param state the state to set for the device
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Returns the package serial number.
     *
     * @return the package serial number
     */
    public String getPackageSerialNumber() {
        return packageSerialNumber;
    }

    /**
     * Sets the package serial number for the device.
     *
     * @param packageSerialNumber the package serial number to set
     */
    public void setPackageSerialNumber(String packageSerialNumber) {
        this.packageSerialNumber = packageSerialNumber;
    }

    /**
    * Returns a string representation of the DeviceFactoryData object.
    *
    * @return A string representation of the DeviceFactoryData object.
    */
    @Override
    public String toString() {
        return "DeviceFactoryData [id=" + id + ", manufacturingDate=" + manufacturingDate + ", model=" + model
            + ", imei=" + imei
            + ", serialNumber=" + serialNumber + ", platformVersion=" + platformVersion + ", iccid=" + iccid
            + ", ssid=" + ssid
            + ", bssid=" + bssid + ", msisdn=" + msisdn + ", imsi=" + imsi + ", recordDate=" + recordDate
            + ", createdDate="
            + createdDate + ", factoryAdmin=" + factoryAdmin + ", state=" + state + ", packageSerialNumber="
            + packageSerialNumber
            + ", stolen=" + stolen + ", faulty=" + faulty + ", deviceType=" + deviceType + "]";
    }

    /**
     * Gets the stolen status of the device.
     *
     * @return the stolen status of the device
     */
    public Boolean getStolen() {
        return stolen;
    }

    /**
     * Sets the stolen status of the device.
     *
     * @param stolen the stolen status of the device
     */
    public void setStolen(Boolean stolen) {
        this.stolen = stolen;
    }

    /**
     * Gets the faulty status of the device.
     *
     * @return the faulty status of the device
     */
    public Boolean getFaulty() {
        return faulty;
    }

    /**
     * Sets the faulty status of the device.
     *
     * @param faulty the faulty status of the device
     */
    public void setFaulty(Boolean faulty) {
        this.faulty = faulty;
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
     * @param deviceType the device type
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

}
