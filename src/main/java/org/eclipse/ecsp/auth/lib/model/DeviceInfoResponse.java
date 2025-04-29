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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the response containing device information.
 */
public class DeviceInfoResponse {
    public static final String COUNTRY_KEY_NAME = "Country";
    public static final String MANUFACTURER_KEY_NAME = "Manufacturer";
    public static final String MAKE_KEY_NAME = "Make";
    public static final String MODEL_KEY_NAME = "Model";
    public static final String YEAR_KEY_NAME = "Year";
    public static final String BODYTYPE_KEY_NAME = "Bodytype";
    public static final String SERIES_KEY_NAME = "Series";
    public static final String VEHICLETYPE_KEY_NAME = "Vehicletype";

    @JsonProperty(COUNTRY_KEY_NAME)
    private String country;
    @JsonProperty(MANUFACTURER_KEY_NAME)
    private String manufacturer;
    @JsonProperty(MAKE_KEY_NAME)
    private String make;
    @JsonProperty(MODEL_KEY_NAME)
    private String model;
    @JsonProperty(YEAR_KEY_NAME)
    private String year;
    @JsonProperty(BODYTYPE_KEY_NAME)
    private String bodytype;
    @JsonProperty(SERIES_KEY_NAME)
    private String series;
    @JsonProperty(VEHICLETYPE_KEY_NAME)
    private String vehicleType;

    /**
     * Returns the country.
     *
     * @return the country
     */
    @JsonProperty(COUNTRY_KEY_NAME)
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the country to set
     */
    @JsonProperty(COUNTRY_KEY_NAME)
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Returns the manufacturer.
     *
     * @return the manufacturer
     */
    @JsonProperty(MANUFACTURER_KEY_NAME)
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the manufacturer.
     *
     * @param manufacturer the manufacturer to set
     */
    @JsonProperty(MANUFACTURER_KEY_NAME)
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Returns the make.
     *
     * @return the make
     */
    @JsonProperty(MAKE_KEY_NAME)
    public String getMake() {
        return make;
    }

    /**
     * Sets the make.
     *
     * @param make the make to set
     */
    @JsonProperty(MAKE_KEY_NAME)
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Returns the model.
     *
     * @return the model
     */
    @JsonProperty(MODEL_KEY_NAME)
    public String getModel() {
        return model;
    }

    /**
     * Sets the model.
     *
     * @param model the model to set
     */
    @JsonProperty(MODEL_KEY_NAME)
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the year.
     *
     * @return the year
     */
    @JsonProperty(YEAR_KEY_NAME)
    public String getYear() {
        return year;
    }

    /**
     * Sets the year.
     *
     * @param year the year to set
     */
    @JsonProperty(YEAR_KEY_NAME)
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Returns the body type.
     *
     * @return the body type
     */
    @JsonProperty(BODYTYPE_KEY_NAME)
    public String getBodytype() {
        return bodytype;
    }

    /**
     * Sets the body type.
     *
     * @param bodytype the body type to set
     */
    @JsonProperty(BODYTYPE_KEY_NAME)
    public void setBodytype(String bodytype) {
        this.bodytype = bodytype;
    }

    /**
     * Returns the series.
     *
     * @return the series
     */
    @JsonProperty(SERIES_KEY_NAME)
    public String getSeries() {
        return series;
    }

    /**
     * Sets the series.
     *
     * @param series the series to set
     */
    @JsonProperty(SERIES_KEY_NAME)
    public void setSeries(String series) {
        this.series = series;
    }

    /**
     * Returns the vehicle type.
     *
     * @return the vehicle type
     */
    @JsonProperty(VEHICLETYPE_KEY_NAME)
    public String getVehicleType() {
        return vehicleType;
    }

    /**
     * Sets the vehicle type.
     *
     * @param vehicleType the vehicle type to set
     */
    @JsonProperty(VEHICLETYPE_KEY_NAME)
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    /**
     * Returns a string representation of the DeviceInfoResponse object.
     *
     * @return a string representation of the DeviceInfoResponse object
     */
    @Override
    public String toString() {
        return "DeviceInfoResponse [country=" + country + ", manufacturer="
            + manufacturer + ", make=" + make + ", model=" + model
            + ", year=" + year + ", bodytype=" + bodytype + ", series="
            + series + ", vehicleType=" + vehicleType + "]";
    }
}
