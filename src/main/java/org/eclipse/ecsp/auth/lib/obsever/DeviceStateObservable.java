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
 * The DeviceStateObservable interface represents an observable object that can be observed by DeviceStateObserver
 * objects.
 * It provides methods to add an observer and notify all observers of a change in device state.
 */
public interface DeviceStateObservable {

    /**
     * Adds a DeviceStateObserver to the list of observers.
     *
     * @param deviceStateObserver the observer to be added
     */
    public void addObserver(DeviceStateObserver deviceStateObserver);

    /**
     * Notifies all observers of a change in device state.
     *
     * @param type the type of the change
     * @param data additional data related to the change
     */
    public void notifyObservers(int type, Object data);
}
