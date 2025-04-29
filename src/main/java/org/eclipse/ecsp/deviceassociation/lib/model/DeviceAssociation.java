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

package org.eclipse.ecsp.deviceassociation.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.ecsp.deviceassociation.lib.util.DateTimeUtils;
import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceAttributes;

import java.sql.Timestamp;
import java.util.Map;

import static org.eclipse.ecsp.common.CommonConstants.ID;

/**
 * Represents a device association.
 * This class contains various properties related to the device association, such as ID, serial number, user ID,
 * Harman ID, association status, etc.
 * It also provides getter and setter methods for accessing and modifying these properties.
 */
@JsonInclude(Include.NON_NULL)
public class DeviceAssociation {

    /**
     * id | bigint | not null default.
     * nextval('device_association_id_seq'::regclass) | plain | | serial_number
     * | character varying | not null | extended | | user_id | character varying
     * | not null | extended | | harmanid | character varying | | extended | |
     * association_status | character varying | not null | extended | |
     * associated_on | timestamp with time zone | not null | plain | |
     * associated_by | character varying | not null | extended | |
     * disassociated_on | timestamp with time zone | | plain | |
     * disassociated_by | character varying | | extended | | modified_on |
     * timestamp with time zone | | plain | | modified_by | character varying |
     */

    private long id;
    private String serialNumber;
    @JsonIgnore
    private String userId;
    private String harmanId;
    private String vehicleId;
    private AssociationStatus associationStatus;
    private Timestamp associatedOn;
    @JsonIgnore
    private String associatedBy;
    private Timestamp disassociatedOn;
    @JsonIgnore
    private String disassociatedBy;
    @JsonIgnore
    private Timestamp modifiedOn;
    @JsonIgnore
    private String modifiedBy;
    private DeviceAttributes deviceAttributes;
    @JsonIgnore
    private long factoryId;
    // temporary to avoid making calls to AUTH.
    @JsonIgnore
    private boolean isAuthsRequest;

    private String imei;
    private String imsi;
    private String ssid;
    private String iccid;
    private String msisdn;
    private String bssid;
    @JsonIgnore
    private String model;
    @JsonIgnore
    private Timestamp manufacturingDate;
    @JsonIgnore
    private String platformVersion;
    @JsonIgnore
    private Timestamp recordDate;
    @JsonIgnore
    private boolean isDeviceAuthV2Deactivate;

    private String softwareVersion;
    private String vin;

    private String simTranStatus;
    private String deviceType;
    private String terminateFor;
    private Map<String, Object> metadata;

    /**
     * Gets the termination reason for the device association.
     *
     * @return the termination reason
     */
    public String getTerminateFor() {
        return terminateFor;
    }

    /**
     * Sets the termination reason for the device association.
     *
     * @param terminateFor the termination reason to set
     */
    public void setTerminateFor(String terminateFor) {
        this.terminateFor = terminateFor;
    }

    /**
     * Gets the SIM transaction status for the device association.
     *
     * @return the SIM transaction status
     */
    public String getSimTranStatus() {
        return simTranStatus;
    }

    /**
     * Sets the SIM transaction status for the device association.
     *
     * @param simTranStatus the SIM transaction status to set
     */
    public void setSimTranStatus(String simTranStatus) {
        this.simTranStatus = simTranStatus;
    }

    /**
     * Gets the ID of the device association.
     *
     * @return the ID of the device association
     */
    @JsonProperty("associationId")
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the device association.
     *
     * @param id the ID of the device association to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the serial number of the device.
     *
     * @return the serial number of the device
     */
    @JsonProperty("serialNumber")
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber the serial number of the device to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the user ID associated with the device.
     *
     * @return the user ID associated with the device
     */
    @JsonIgnore
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with the device.
     *
     * @param userId the user ID associated with the device to set
     */
    @JsonIgnore
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the Harman ID of the device.
     *
     * @return the Harman ID of the device
     */
    @JsonProperty("deviceId")
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID of the device.
     *
     * @param harmanId the Harman ID of the device to set
     */
    @JsonProperty("deviceId")
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Gets the vehicle ID associated with the device.
     *
     * @return the vehicle ID associated with the device
     */
    public String getVehicleId() {
        return vehicleId;
    }

    /**
     * Sets the vehicle ID associated with the device.
     *
     * @param vehicleId the vehicle ID associated with the device to set
     */
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    /**
     * Gets the association status of the device.
     *
     * @return the association status of the device
     */
    @JsonProperty("associationStatus")
    public AssociationStatus getAssociationStatus() {
        return associationStatus;
    }

    /**
     * Sets the association status of the device.
     *
     * @param associationStatus the association status of the device to set
     */
    public void setAssociationStatus(AssociationStatus associationStatus) {
        this.associationStatus = associationStatus;
    }

    /**
     * Gets the timestamp when the device was associated.
     *
     * @return the timestamp when the device was associated
     */
    @JsonIgnore
    public Timestamp getAssociatedOn() {
        if (associatedOn != null) {
            return new Timestamp(associatedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the timestamp when the device was associated.
     *
     * @param associatedOn the timestamp when the device was associated to set
     */
    public void setAssociatedOn(Timestamp associatedOn) {
        if (associatedOn != null) {
            this.associatedOn = new Timestamp(associatedOn.getTime());
        } else {
            this.associatedOn = null;
        }
    }

    /**
     * Gets the formatted associated on date in ISO format.
     *
     * @return the formatted associated on date
     */
    @JsonProperty("associatedOn")
    public String getFormattedAssociatedOn() {
        return DateTimeUtils.getIsoDate(associatedOn);
    }

    /**
     * Gets the user who associated the device.
     *
     * @return the user who associated the device
     */
    @JsonIgnore
    public String getAssociatedBy() {
        return associatedBy;
    }

    /**
     * Sets the user who associated the device.
     *
     * @param associatedBy the user who associated the device to set
     */
    @JsonIgnore
    public void setAssociatedBy(String associatedBy) {
        this.associatedBy = associatedBy;
    }

    /**
     * Gets the user who disassociated the device.
     *
     * @return the user who disassociated the device
     */
    @JsonIgnore
    public String getDisassociatedBy() {
        return disassociatedBy;
    }

    /**
     * Sets the user who disassociated the device.
     *
     * @param disassociatedBy the user who disassociated the device to set
     */
    @JsonIgnore
    public void setDisassociatedBy(String disassociatedBy) {
        this.disassociatedBy = disassociatedBy;
    }

    /**
     * Gets the user who modified the device.
     *
     * @return the user who modified the device
     */
    @JsonIgnore
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the user who modified the device.
     *
     * @param modifiedBy the user who modified the device to set
     */
    @JsonIgnore
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * Gets the timestamp when the device was disassociated.
     *
     * @return the timestamp when the device was disassociated
     */
    @JsonIgnore
    public Timestamp getDisassociatedOn() {
        if (disassociatedOn != null) {
            return new Timestamp(disassociatedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the timestamp when the device was disassociated.
     *
     * @param disassociatedOn the timestamp when the device was disassociated to set
     */
    public void setDisassociatedOn(Timestamp disassociatedOn) {
        if (disassociatedOn != null) {
            this.disassociatedOn = new Timestamp(disassociatedOn.getTime());
        } else {
            this.disassociatedOn = null;
        }
    }

    /**
     * Gets the formatted disassociated on date in ISO format.
     *
     * @return the formatted disassociated on date
     */
    @JsonProperty("disassociatedOn")
    public String getDisplayDisassociatedOn() {
        return DateTimeUtils.getIsoDate(disassociatedOn);
    }

    /**
     * Gets the timestamp when the device was last modified.
     *
     * @return the timestamp when the device was last modified
     */
    @JsonIgnore
    public Timestamp getModifiedOn() {
        if (modifiedOn != null) {
            return new Timestamp(modifiedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the timestamp when the device was last modified.
     *
     * @param modifiedOn the timestamp when the device was last modified to set
     */
    @JsonIgnore
    public void setModifiedOn(Timestamp modifiedOn) {
        if (modifiedOn != null) {
            this.modifiedOn = new Timestamp(modifiedOn.getTime());
        } else {
            this.modifiedOn = null;
        }
    }

    /**
     * Gets the device attributes.
     *
     * @return the device attributes
     */
    public DeviceAttributes getDeviceAttributes() {
        return deviceAttributes;
    }

    /**
     * Sets the device attributes.
     *
     * @param deviceAttributes the device attributes to set
     */
    public void setDeviceAttributes(DeviceAttributes deviceAttributes) {
        this.deviceAttributes = deviceAttributes;
    }

    /**
     * Checks if the device association is an authentication request.
     *
     * @return true if the device association is an authentication request, false otherwise
     */
    @JsonIgnore
    public boolean isAuthsRequest() {
        return isAuthsRequest;
    }

    /**
     * Sets whether the device association is an authentication request.
     *
     * @param isAuthsRequest true if the device association is an authentication request, false otherwise
     */
    @JsonIgnore
    public void setAuthsRequest(boolean isAuthsRequest) {
        this.isAuthsRequest = isAuthsRequest;
    }

    /**
     * Gets the factory ID.
     *
     * @return the factory ID
     */
    @JsonIgnore
    @JsonProperty(ID)
    public long getFactoryId() {
        return factoryId;
    }

    /**
     * Sets the factory ID.
     *
     * @param factoryId the factory ID to set
     */
    @JsonIgnore
    public void setFactoryId(long factoryId) {
        this.factoryId = factoryId;
    }

    /**
     * Gets the IMEI of the device.
     *
     * @return the IMEI of the device
     */
    @JsonProperty("imei")
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI of the device.
     *
     * @param imei the IMEI of the device to set
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the IMSI of the device.
     *
     * @return the IMSI of the device
     */
    @JsonProperty("imsi")
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI of the device.
     *
     * @param imsi the IMSI of the device to set
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the SSID of the device.
     *
     * @return the SSID of the device
     */
    @JsonProperty("ssid")
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
     * Retrieves the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @return The ICCID of the device.
     */
    @JsonProperty("iccid")
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
     * Gets the MSISDN (Mobile Station International Subscriber Directory Number) associated with the device.
     *
     * @return The MSISDN associated with the device.
     */
    @JsonProperty("msisdn")
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
     * Returns a string representation of the DeviceAssociation object.
     *
     * @return A string representation of the DeviceAssociation object.
     */
    @Override
    public String toString() {
        return "DeviceAssociation [id=" + id + ", serialNumber=" + serialNumber + ", userID=" + userId + ", harmanID="
            + harmanId
            + ", vehicleId=" + vehicleId + ", associationStatus=" + associationStatus + ", associatedOn="
            + associatedOn + ", associatedBy=" + associatedBy
            + ", disassociatedOn=" + disassociatedOn + ", disassociatedBy=" + disassociatedBy + ", modifiedOn="
            + modifiedOn
            + ", modifiedBy=" + modifiedBy + ", deviceAttributes=" + deviceAttributes + ", factoryId=" + factoryId
            + ", isAuthsRequest=" + isAuthsRequest + ", imei=" + imei + ", imsi=" + imsi + ", ssid=" + ssid
            + ", iccid=" + iccid
            + ", msisdn=" + msisdn + ", softwareVersion=" + softwareVersion + "]";
    }

    /**
     * Gets the BSSID (Basic Service Set Identifier) of the device.
     *
     * @return The BSSID of the device.
     */
    @JsonProperty("bssid")
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID (Basic Service Set Identifier) of the device.
     *
     * @param bssid the BSSID to set
     */
    @JsonProperty("bssid")
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Returns the model of the device.
     *
     * @return the model of the device
     */
    @JsonIgnore
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the device.
     *
     * @param model the model of the device
     */
    @JsonIgnore
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Retrieves the platform version of the device.
     *
     * @return The platform version of the device.
     */
    @JsonIgnore
    public String getPlatformVersion() {
        return platformVersion;
    }

    /**
     * Sets the platform version of the device.
     *
     * @param platformVersion the platform version to set
     */
    @JsonIgnore
    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    /**
     * Retrieves the manufacturing date of the device.
     *
     * @return The manufacturing date of the device as a Timestamp object, or null if the manufacturing date is not set.
     */
    @JsonIgnore
    public Timestamp getManufacturingDate() {
        if (manufacturingDate != null) {
            return new Timestamp(manufacturingDate.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the manufacturing date of the device.
     *
     * @param manufacturingDate the manufacturing date to be set
     */
    @JsonIgnore
    public void setManufacturingDate(Timestamp manufacturingDate) {
        if (manufacturingDate != null) {
            this.manufacturingDate = new Timestamp(manufacturingDate.getTime());
        } else {
            this.manufacturingDate = null;
        }
    }

    /**
     * Returns the record date of the device association.
     *
     * @return The record date as a Timestamp object, or null if the record date is null.
     */
    @JsonIgnore
    public Timestamp getRecordDate() {
        if (recordDate != null) {
            return new Timestamp(recordDate.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the record date for the device association.
     *
     * @param recordDate the record date to set
     */
    @JsonIgnore
    public void setRecordDate(Timestamp recordDate) {
        if (recordDate != null) {
            this.recordDate = new Timestamp(recordDate.getTime());
        } else {
            this.recordDate = null;
        }
    }

    /**
     * Checks if the device is deactivated using Device Auth V2.
     *
     * @return true if the device is deactivated using Device Auth V2, false otherwise.
     */
    @JsonIgnore
    public boolean isDeviceAuthV2Deactivate() {
        return isDeviceAuthV2Deactivate;
    }

    /**
     * Sets the flag indicating whether the device is deactivated using Device Auth V2.
     *
     * @param isDeviceAuthV2Deactivate true if the device is deactivated using Device Auth V2, false otherwise
     */
    @JsonIgnore
    public void setDeviceAuthV2Deactivate(boolean isDeviceAuthV2Deactivate) {
        this.isDeviceAuthV2Deactivate = isDeviceAuthV2Deactivate;
    }

    /**
     * Returns the software version of the device.
     *
     * @return the software version of the device
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Sets the software version of the device.
     *
     * @param softwareVersion the software version to set
     */
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    /**
     * Retrieves the VIN (Vehicle Identification Number) associated with this device association.
     *
     * @return The VIN associated with this device association.
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the Vehicle Identification Number (VIN) for the device association.
     *
     * @param vin the Vehicle Identification Number to set
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Returns the device type of this DeviceAssociation.
     *
     * @return the device type
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the device type for this DeviceAssociation.
     *
     * @param deviceType the device type to be set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Returns the metadata associated with the device.
     *
     * @return a map containing the metadata of the device
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata for the device association.
     *
     * @param metadata a map containing the metadata information
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}