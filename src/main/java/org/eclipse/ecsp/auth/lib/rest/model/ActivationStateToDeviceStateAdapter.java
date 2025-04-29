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

import org.eclipse.ecsp.auth.lib.obsever.DeviceStateActivation;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceState;

/**
 * This class represents an adapter that converts an ActivationState object to a DeviceState object.
 */
public class ActivationStateToDeviceStateAdapter extends DeviceState {

    private DeviceStateActivation deviceStateActivation;

    /**
     * Constructs a new ActivationStateToDeviceStateAdapter with the specified DeviceStateActivation.
     *
     * @param deviceStateActivation the DeviceStateActivation object to be adapted
     */
    public ActivationStateToDeviceStateAdapter(DeviceStateActivation deviceStateActivation) {
        this.deviceStateActivation = deviceStateActivation;
    }

    /**
     * Returns the Harman ID associated with this adapter.
     *
     * @return the Harman ID
     */
    @Override
    public String getHarmanId() {
        return deviceStateActivation.getActivationResponse().getDeviceId();
    }

    /**
     * Returns the serial number associated with this adapter.
     *
     * @return the serial number
     */
    @Override
    public String getSerialNumber() {
        return deviceStateActivation.getSerialNumber();
    }

    /**
     * Returns the state associated with this adapter.
     *
     * @return the state
     */
    @Override
    public State getState() {
        return State.ACTIVATED;
    }

    /**
     * Returns the software version associated with this adapter.
     *
     * @return the software version
     */
    @Override
    public String getSoftwareVersion() {
        return deviceStateActivation.getSwVersion();
    }

    /**
     * Returns the device type associated with this adapter.
     *
     * @return the device type
     */
    @Override
    public String getDeviceType() {
        return deviceStateActivation.getDeviceType();
    }

    /**
     * Returns whether the reactivation flag is set for this adapter.
     *
     * @return true if the reactivation flag is set, false otherwise
     */
    @Override
    public boolean isReactivationFlag() {
        return deviceStateActivation.isReactivationFlag();
    }

}
