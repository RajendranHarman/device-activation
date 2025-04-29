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

import org.eclipse.ecsp.auth.lib.obsever.DeviceStateDeactivation;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceState;

/**
 * Adapter class that converts a DeactivationState object to a DeviceState object.
 */
public class DeactivationStateToDeviceStateAdapter extends DeviceState {

    private DeviceStateDeactivation deviceStateDeactivation;

    /**
     * Constructs a new DeactivationStateToDeviceStateAdapter object.
     *
     * @param deviceStateDeactivation the DeactivationState object to be adapted
     */
    public DeactivationStateToDeviceStateAdapter(DeviceStateDeactivation deviceStateDeactivation) {
        this.deviceStateDeactivation = deviceStateDeactivation;
    }

    /**
     * Retrieves the Harman ID associated with the device state.
     *
     * @return the Harman ID
     */
    @Override
    public String getHarmanId() {
        return deviceStateDeactivation.getHarmanId();
    }

    /**
     * Retrieves the serial number associated with the device state.
     *
     * @return the serial number
     */
    @Override
    public String getSerialNumber() {
        return deviceStateDeactivation.getSerialNumber();
    }

    /**
     * Retrieves the state of the device.
     *
     * @return the device state
     */
    @Override
    public State getState() {
        return State.DEACTIVATED;
    }
}
