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

package org.eclipse.ecsp.services.shared.tables;

/**
 * The {@code Postgres} class represents a set of constants for table names in a PostgreSQL database.
 * It provides the table names for device association, device, HCPInfo, DeviceInfoFactoryData,
 * device activation state, and DeviceInfo.
 */
public final class Postgres {
    public static final String DEVICE_ASSOCIATION = "device_association";
    public static final String DEVICE = "\"Device\"";
    public static final String HCPINFO = "\"HCPInfo\"";
    public static final String DEVICEINFOFACTORYDATA = "\"DeviceInfoFactoryData\"";
    public static final String DEVICE_ACTIVATION_STATE = "device_activation_state";
    public static final String DEVICEINFO = "\"DeviceInfo\"";

    /**
     * The `Postgres` class provides functionality for interacting with a Postgres database.
     * This class is responsible for managing the connection to the database and executing queries.
     * It follows the singleton design pattern to ensure that only one instance of the class is created.
     */
    private Postgres() {

    }
}
