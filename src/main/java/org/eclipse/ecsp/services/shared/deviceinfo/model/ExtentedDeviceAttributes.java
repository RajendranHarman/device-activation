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

/**
 * Represents the extended attributes of a device.
 * This class contains information about the model, year, body type, series, vehicle type, and last login time of the
 * device.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtentedDeviceAttributes {

    private String model;
    private int year;
    private String bodytype;
    private String series;
    private String vehicletype;
    private String lastlogintime;

    /**
     * Constructor for creating an instance of the ExtentedDeviceAttributes class.
     *
     * @param model          the model of the device
     * @param year           the year of the device
     * @param bodytype       the body type of the device
     * @param series         the series of the device
     * @param vehicletype    the vehicle type of the device
     * @param lastlogintime  the last login time of the device
     */
    public ExtentedDeviceAttributes(String model, int year, String bodytype,
                                    String series, String vehicletype, String lastlogintime) {
        super();
        this.model = model;
        this.year = year;
        this.bodytype = bodytype;
        this.series = series;
        this.vehicletype = vehicletype;
        this.lastlogintime = lastlogintime;
    }

    /**
     * Get the model of the device.
     *
     * @return the model of the device
     */
    public String getModel() {
        return model;
    }

    /**
     * Set the model of the device.
     *
     * @param model the model of the device
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Get the year of the device.
     *
     * @return the year of the device
     */
    public int getYear() {
        return year;
    }

    /**
     * Set the year of the device.
     *
     * @param year the year of the device
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Get the body type of the device.
     *
     * @return the body type of the device
     */
    public String getBodytype() {
        return bodytype;
    }

    /**
     * Set the body type of the device.
     *
     * @param bodytype the body type of the device
     */
    public void setBodytype(String bodytype) {
        this.bodytype = bodytype;
    }

    /**
     * Get the series of the device.
     *
     * @return the series of the device
     */
    public String getSeries() {
        return series;
    }

    /**
     * Set the series of the device.
     *
     * @param series the series of the device
     */
    public void setSeries(String series) {
        this.series = series;
    }

    /**
     * Get the vehicle type of the device.
     *
     * @return the vehicle type of the device
     */
    public String getVehicletype() {
        return vehicletype;
    }

    /**
     * Set the vehicle type of the device.
     *
     * @param vehicletype the vehicle type of the device
     */
    public void setVehicletype(String vehicletype) {
        this.vehicletype = vehicletype;
    }

    /**
     * Get the last login time of the device.
     *
     * @return the last login time of the device
     */
    public String getLastlogintime() {
        return lastlogintime;
    }

    /**
     * Set the last login time of the device.
     *
     * @param lastlogintime the last login time of the device
     */
    public void setLastlogintime(String lastlogintime) {
        this.lastlogintime = lastlogintime;
    }

    /**
     * Calculates the hash code for the ExtentedDeviceAttributes object.
     *
     * @return the hash code value for the object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((bodytype == null) ? 0 : bodytype.hashCode());
        result = prime * result
            + ((lastlogintime == null) ? 0 : lastlogintime.hashCode());
        result = prime * result + ((model == null) ? 0 : model.hashCode());
        result = prime * result
            + ((series == null) ? 0 : series.hashCode());
        result = prime * result
            + ((vehicletype == null) ? 0 : vehicletype.hashCode());
        result = prime * result + year;
        return result;
    }

    /**
     * Checks if the given object is equal to this ExtentedDeviceAttributes object.
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
        ExtentedDeviceAttributes other = (ExtentedDeviceAttributes) obj;
        if (verifyBodyType(other)) {
            return false;
        }
        if (year != other.year) {
            return false;
        }
        if (verifyLastLogin(other)) {
            return false;
        }
        if (verifyModel(other)) {
            return false;
        }
        if (verifySeries(other)) {
            return false;
        }
        return !verifyVehicletype(other);
    }

    /**
     * Verifies if the body type of this `ExtentedDeviceAttributes` object is equal to the body type of another
     * `ExtentedDeviceAttributes` object.
     *
     * @param other The other `ExtentedDeviceAttributes` object to compare with.
     * @return `true` if the body types are different or if one of the body types is null, `false` otherwise.
     */
    private boolean verifyBodyType(ExtentedDeviceAttributes other) {
        if (bodytype == null) {
            if (other.bodytype != null) {
                return true;
            }
        } else if (!bodytype.equals(other.bodytype)) {
            return true;
        }
        return false;
    }

    /**
     * Compares the last login time of this `ExtentedDeviceAttributes` object with another `ExtentedDeviceAttributes`
     * object.
     *
     * @param other The other `ExtentedDeviceAttributes` object to compare with.
     * @return `true` if the last login time is different between the two objects, `false` otherwise.
     */
    private boolean verifyLastLogin(ExtentedDeviceAttributes other) {
        if (lastlogintime == null) {
            if (other.lastlogintime != null) {
                return true;
            }
        } else if (!lastlogintime.equals(other.lastlogintime)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the model of this `ExtentedDeviceAttributes` object is equal to the model of another
     * `ExtentedDeviceAttributes` object.
     *
     * @param other The other `ExtentedDeviceAttributes` object to compare with.
     * @return `true` if the models are different or if the model of this object is null and the model of the other
     *      object is not null, `false` otherwise.
     */
    private boolean verifyModel(ExtentedDeviceAttributes other) {
        if (model == null) {
            if (other.model != null) {
                return true;
            }
        } else if (!model.equals(other.model)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the series of this `ExtentedDeviceAttributes` object is equal to the series of another
     * `ExtentedDeviceAttributes` object.
     *
     * @param other The other `ExtentedDeviceAttributes` object to compare with.
     * @return `true` if the series are equal, `false` otherwise.
     */
    private boolean verifySeries(ExtentedDeviceAttributes other) {
        if (series == null) {
            if (other.series != null) {
                return true;
            }
        } else if (!series.equals(other.series)) {
            return true;
        }
        return false;
    }

    /**
     * Verifies if the vehicle type of this `ExtentedDeviceAttributes` object is equal to the vehicle type of another
     * `ExtentedDeviceAttributes` object.
     *
     * @param other The other `ExtentedDeviceAttributes` object to compare with.
     * @return `true` if the vehicle types are not equal, `false` otherwise.
     */
    private boolean verifyVehicletype(ExtentedDeviceAttributes other) {
        if (vehicletype == null) {
            if (other.vehicletype != null) {
                return true;
            }
        } else if (!vehicletype.equals(other.vehicletype)) {
            return true;
        }
        return false;
    }

    /**
     * Returns a string representation of the ExtendedDeviceInfo object.
     * The string contains the values of the model, year, bodytype, series, vehicletype, and lastlogintime attributes.
     *
     * @return A string representation of the ExtendedDeviceInfo object.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExtendedDeviceInfo [");
        builder.append(", Model=");
        builder.append(model);
        builder.append(", Year=");
        builder.append(year);
        builder.append(", Bodytype=");
        builder.append(bodytype);
        builder.append(", Series=");
        builder.append(series);
        builder.append(", Vehicletype=");
        builder.append(vehicletype);
        builder.append(", Lastlogintime=");
        builder.append(lastlogintime);
        builder.append("]");
        return builder.toString();
    }
}
