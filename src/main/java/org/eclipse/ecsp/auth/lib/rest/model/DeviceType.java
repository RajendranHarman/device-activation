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

/**
 * Represents the type of a device.
 */
public enum DeviceType {

    OBD_DONGLE("ObdDongle");

    private String clientName;

    /**
     * Constructs a new DeviceType with the specified client name.
     *
     * @param clientName the client name of the device type
     */
    DeviceType(String clientName) {
        this.clientName = clientName;
    }

    /**
     * Returns the client name of the device type.
     *
     * @return the client name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Sets the client name of the device type.
     *
     * @param clientName the client name to set
     */
    void setClientName(String clientName) {
        this.clientName = clientName;
    }

}
