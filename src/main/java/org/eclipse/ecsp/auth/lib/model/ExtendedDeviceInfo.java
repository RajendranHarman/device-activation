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
 * Represents an extended device information.
 */
public class ExtendedDeviceInfo {
    private static final int ID = 32;
    private long modelId;
    private int year;
    private long bodytypeId;
    private long seriesId;

    /**
     * Constructs a new ExtendedDeviceInfo object with the specified parameters.
     *
     * @param modelId    the model ID of the device
     * @param year       the year of the device
     * @param bodytypeId the body type ID of the device
     * @param seriesId   the series ID of the device
     */
    public ExtendedDeviceInfo(long modelId, int year, long bodytypeId, long seriesId) {
        this.modelId = modelId;
        this.year = year;
        this.bodytypeId = bodytypeId;
        this.seriesId = seriesId;
    }

    /**
     * Returns the modelId.
     *
     * @return the modelId
     */
    public long getModelId() {
        return modelId;
    }

    /**
     * Sets the modelId.
     *
     * @param modelId the modelId to set
     */
    public void setModelId(long modelId) {
        this.modelId = modelId;
    }

    /**
     * Returns the year.
     *
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * Sets the year.
     *
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Returns the bodytypeId.
     *
     * @return the bodytypeId
     */
    public long getBodytypeId() {
        return bodytypeId;
    }

    /**
     * Sets the bodytypeId.
     *
     * @param bodytypeId the bodytypeId to set
     */
    public void setBodytypeId(long bodytypeId) {
        this.bodytypeId = bodytypeId;
    }

    /**
     * Returns the seriesId.
     *
     * @return the seriesId
     */
    public long getSeriesId() {
        return seriesId;
    }

    /**
     * Sets the seriesId.
     *
     * @param seriesId the seriesId to set
     */
    public void setSeriesId(long seriesId) {
        this.seriesId = seriesId;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "modelId=" + modelId
            + ", year=" + year
            + ", bodytypeId=" + bodytypeId
            + ", seriesId=" + seriesId;
    }

    /**
     * Returns the hash code value for the object.
     *
     * @return the hash code value for the object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (bodytypeId ^ (bodytypeId >>> ID));
        result = prime * result + (int) (modelId ^ (modelId >>> ID));
        result = prime * result + (int) (seriesId ^ (seriesId >>> ID));
        result = prime * result + year;
        return result;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
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
        ExtendedDeviceInfo other = (ExtendedDeviceInfo) obj;
        if (year != other.year) {
            return false;
        }
        if (bodytypeId != other.bodytypeId) {
            return false;
        }
        if (modelId != other.modelId) {
            return false;
        }
        if (seriesId != other.seriesId) {
            return false;
        }
        return true;
    }
}
