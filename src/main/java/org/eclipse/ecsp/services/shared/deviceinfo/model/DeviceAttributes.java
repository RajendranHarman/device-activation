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

package org.eclipse.ecsp.services.shared.deviceinfo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.stereotype.Component;

/**
 * Represents the attributes of a device.
 * This class contains information such as the Harman ID, country, manufacturer, make, and extended device attributes.
 */
@Component("hcp-db-shared.DeviceAttributes")
@JsonInclude(Include.NON_NULL)
public class DeviceAttributes {

    private String harmanId;
    private String country;
    private String manufacturer;
    private String make;
    private ExtentedDeviceAttributes extentedDeviceAttributes;

    /**
     * Constructor for the DeviceAttributes class.
     *
     * @param harmanId                 the Harman ID of the device
     * @param country                  the country of the device
     * @param manufacturer             the manufacturer of the device
     * @param make                     the make of the device
     * @param extentedDeviceAttributes the extended device attributes
     */
    public DeviceAttributes(String harmanId, String country, String manufacturer,
                            String make, ExtentedDeviceAttributes extentedDeviceAttributes) {
        super();
        this.harmanId = harmanId;
        this.country = country;
        this.manufacturer = manufacturer;
        this.make = make;
        this.extentedDeviceAttributes = extentedDeviceAttributes;
    }

    /**
     * Default constructor for the DeviceAttributes class.
     */
    public DeviceAttributes() {
        super();
    }

    /**
     * Get the Harman ID of the device.
     *
     * @return the Harman ID
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Set the Harman ID of the device.
     *
     * @param harmanId the Harman ID to set
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Get the country of the device.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Set the country of the device.
     *
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Get the manufacturer of the device.
     *
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Set the manufacturer of the device.
     *
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Get the make of the device.
     *
     * @return the make
     */
    public String getMake() {
        return make;
    }

    /**
     * Set the make of the device.
     *
     * @param make the make to set
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Generate the hash code for the DeviceAttributes object.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((make == null) ? 0 : make.hashCode());
        result = prime * result
            + ((manufacturer == null) ? 0 : manufacturer.hashCode());
        result = prime * result
            + ((harmanId == null) ? 0 : harmanId.hashCode());
        return result;
    }

    /**
     * Check if the DeviceAttributes object is equal to another object.
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
        DeviceAttributes other = (DeviceAttributes) obj;
        if (verifyCountry(other)) {
            return false;
        }
        if (verifyMake(other)) {
            return false;
        }
        if (verifyManufacturer(other)) {
            return false;
        }
        return !verifyHarmanId(other);
    }

    /**
     * Compares the country attribute of this DeviceAttributes object with another DeviceAttributes object.
     * Returns true if the country attribute of this object is different from the country attribute of the other object,
     * or if the country attribute of this object is null and the country attribute of the other object is not null.
     * Returns false otherwise.
     *
     * @param other The DeviceAttributes object to compare with.
     * @return true if the country attribute is different or null, false otherwise.
     */
    private boolean verifyCountry(DeviceAttributes other) {
        if (country == null) {
            if (other.country != null) {
                return true;
            }
        } else if (!country.equals(other.country)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies the make of the device by comparing it with another DeviceAttributes object.
     *
     * @param other The DeviceAttributes object to compare with.
     * @return true if the make of the device is different from the make of the other DeviceAttributes object, false
     *      otherwise.
     */
    private boolean verifyMake(DeviceAttributes other) {
        if (make == null) {
            if (other.make != null) {
                return true;
            }
        } else if (!make.equals(other.make)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the manufacturer of this device is the same as the manufacturer of another device.
     *
     * @param other the other device attributes to compare with
     * @return true if the manufacturers are different, false otherwise
     */
    private boolean verifyManufacturer(DeviceAttributes other) {
        if (manufacturer == null) {
            if (other.manufacturer != null) {
                return true;
            }
        } else if (!manufacturer.equals(other.manufacturer)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the Harman ID of this device matches the Harman ID of another device.
     *
     * @param other The other device attributes to compare with.
     * @return {@code true} if the Harman IDs match, {@code false} otherwise.
     */
    private boolean verifyHarmanId(DeviceAttributes other) {
        if (harmanId == null) {
            if (other.harmanId != null) {
                return true;
            }
        } else if (!harmanId.equals(other.harmanId)) {
            return true;
        }
        return false;
    }

    /**
     * Get a string representation of the DeviceAttributes object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DeviceInfo [harmanID=");
        builder.append(harmanId);
        builder.append(", Country=");
        builder.append(country);
        builder.append(", Manufacturer=");
        builder.append(manufacturer);
        builder.append(", Make=");
        builder.append(make);
        builder.append(", ExtendedDeviceAttributes=");
        builder.append(extentedDeviceAttributes);
        builder.append("]");
        return builder.toString();
    }

}
