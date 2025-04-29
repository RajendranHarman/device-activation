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

package org.eclipse.ecsp.auth.lib.obsever;

/**
 * The DeviceStateObserver interface defines the contract for objects that observe the state of a device.
 * Implementations of this interface can be registered with a device to receive notifications about its activation
 * or deactivation.
 */
public interface DeviceStateObserver {

    public static final int TYPE_DEVICE_ACTIVATION = 1;
    public static final int TYPE_DEVICE_DEACTIVATION = 2;

    /**
     * Notifies the observer about a device state change.
     *
     * @param type The type of the state change. Should be one of the TYPE_DEVICE_ACTIVATION or
*             TYPE_DEVICE_DEACTIVATION constants.
     * @param data Additional data associated with the state change.
     */
    public void notify(int type, Object data);
}
