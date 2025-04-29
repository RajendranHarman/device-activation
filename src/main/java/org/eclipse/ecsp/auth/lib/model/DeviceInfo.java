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

package org.eclipse.ecsp.auth.lib.model;

/**
 * Represents device information.
 */
public class DeviceInfo {

    private static final int ID = 32;

    private String harmanId;
    private long countryId;
    private long manufacturerId;
    private long makeId;
    private ExtendedDeviceInfo extendedDeviceInfo;

    /**
     * Default constructor.
     */
    public DeviceInfo() {
    }

    /**
     * Constructs a new DeviceInfo object with the specified parameters.
     *
     * @param harmanId           the Harman ID of the device
     * @param countryId          the country ID of the device
     * @param manufacturerId     the manufacturer ID of the device
     * @param makeId             the make ID of the device
     * @param extendedDeviceInfo the extended device information
     */
    public DeviceInfo(String harmanId, long countryId, long manufacturerId, long makeId,
                        ExtendedDeviceInfo extendedDeviceInfo) {
        super();
        this.harmanId = harmanId;
        this.countryId = countryId;
        this.manufacturerId = manufacturerId;
        this.makeId = makeId;
        this.extendedDeviceInfo = extendedDeviceInfo;
    }

    /**
     * Gets the harmanId.
     *
     * @return the harmanId
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the harmanId.
     *
     * @param harmanId the harmanId to set
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Gets the countryId.
     *
     * @return the countryId
     */
    public long getCountryId() {
        return countryId;
    }

    /**
     * Sets the countryId.
     *
     * @param countryId the countryId to set
     */
    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }

    /**
     * Gets the manufacturerId.
     *
     * @return the manufacturerId
     */
    public long getManufacturerId() {
        return manufacturerId;
    }

    /**
     * Sets the manufacturerId.
     *
     * @param manufacturerId the manufacturerId to set
     */
    public void setManufacturerId(long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    /**
     * Gets the makeId.
     *
     * @return the makeId
     */
    public long getMakeId() {
        return makeId;
    }

    /**
     * Sets the makeId.
     *
     * @param makeId the makeId to set
     */
    public void setMakeId(long makeId) {
        this.makeId = makeId;
    }

    /**
     * Gets the extendedDeviceInfo.
     *
     * @return the extendedDeviceInfo
     */
    public ExtendedDeviceInfo getExtendedDeviceInfo() {
        return extendedDeviceInfo;
    }

    /**
     * Sets the extendedDeviceInfo.
     *
     * @param extendedDeviceInfo the extendedDeviceInfo to set
     */
    public void setExtendedDeviceInfo(ExtendedDeviceInfo extendedDeviceInfo) {
        this.extendedDeviceInfo = extendedDeviceInfo;
    }

    /**
     * Returns a string representation of the DeviceInfo object.
     *
     * @return a string representation of the DeviceInfo object
     */
    @Override
    public String toString() {
        return "DeviceInfo [harmanId=" + harmanId + ", countryId=" + countryId + ", manufacturerId=" + manufacturerId
            + ", makeId="
            + makeId + "," + extendedDeviceInfo + "]";
    }

    /**
     * Generates a hash code for the DeviceInfo object.
     *
     * @return the hash code value for the DeviceInfo object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (countryId ^ (countryId >>> ID));
        result = prime * result + (int) (makeId ^ (makeId >>> ID));
        result = prime * result + (int) (manufacturerId ^ (manufacturerId >>> ID));
        result = prime * result + ((harmanId == null) ? 0 : harmanId.hashCode());
        return result;
    }

    /**
     * Checks if the DeviceInfo object is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DeviceInfo other = (DeviceInfo) obj;
        if (countryId != other.countryId) {
            return false;
        }
        if (makeId != other.makeId) {
            return false;
        }
        if (manufacturerId != other.manufacturerId) {
            return false;
        }
        if (harmanId == null) {
            if (other.harmanId != null) {
                return false;
            }
        } else if (!harmanId.equals(other.harmanId)) {
            return false;
        }
        return true;
    }
}