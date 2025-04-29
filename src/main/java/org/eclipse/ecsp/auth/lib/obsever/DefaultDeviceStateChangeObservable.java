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

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.ecsp.auth.lib.config.AuthProperty;
import org.eclipse.ecsp.auth.lib.rest.support.ActivationFailException;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the default implementation of the DeviceStateChangeObservable interface.
 * It is responsible for notifying the registered observers about device state changes.
 */
@Component
public class DefaultDeviceStateChangeObservable implements
    DeviceStateObservable, InitializingBean {

    private List<DeviceStateObserver> deviceStateObservers = null;

    @Autowired
    private DeviceAssociationDeviceStateObserver deviceAssociationObserver;

    @Autowired
    private EnvConfig<AuthProperty> envConfig;

    /**
     * Notifies the observers about a new device activation.
     *
     * @param deviceStateActivation The device state activation object.
     */
    public void newDeviceActivated(DeviceStateActivation deviceStateActivation) {
        notifyObservers(DeviceStateObserver.TYPE_DEVICE_ACTIVATION, deviceStateActivation);
    }

    /**
     * Notifies the registered observers about a device state change.
     *
     * @param type The type of the device state change.
     * @param data The data associated with the device state change.
     */
    @Override
    public void notifyObservers(int type, Object data) {
        if (CollectionUtils.isEmpty(deviceStateObservers)) {
            return;
        }
        for (DeviceStateObserver observer : deviceStateObservers) {
            try {
                observer.notify(type, data);
            } catch (Exception e) {
                throw new ActivationFailException(
                    "Exception occurred while notifying the observers about device activation.", e);
            }
        }
    }

    /**
     * Adds a device state observer to the list of registered observers.
     *
     * @param deviceStateObserver The device state observer to be added.
     */
    @Override
    public void addObserver(DeviceStateObserver deviceStateObserver) {
        if (deviceStateObservers == null) {
            deviceStateObservers = new ArrayList<>();
        }
        if (!deviceStateObservers.contains(deviceStateObserver)) {
            deviceStateObservers.add(deviceStateObserver);
        }
    }

    /**
     * Initializes the object after its properties have been set.
     *
     * @throws Exception If an error occurs during initialization.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        boolean isDeviceAssociationNotifcationEnabled = envConfig
            .getBooleanValue(AuthProperty.NOTIFY_DEVICE_ACTIVATION_TO_DEVICE_ASSOCIATION);

        if (isDeviceAssociationNotifcationEnabled) {
            this.addObserver(deviceAssociationObserver);
        }
    }

}
