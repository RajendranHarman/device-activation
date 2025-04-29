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

import org.eclipse.ecsp.auth.lib.config.AuthProperty;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationStateToDeviceStateAdapter;
import org.eclipse.ecsp.auth.lib.rest.model.DeactivationStateToDeviceStateAdapter;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * This class is an implementation of the DeviceStateObserver interface. It is responsible for observing the device
 * state changes and performing the necessary actions based on the type of change.
 * The class provides methods to notify the state changes and call the association -> stateChange API accordingly.
 */
@Component
public class DeviceAssociationDeviceStateObserver implements DeviceStateObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAssociationDeviceStateObserver.class);
    private static final String USER_ID = "user-id";
    private static final String ADMIN_USER = "admin@carbon.super";
    @Autowired
    private HcpRestClientLibrary hcpRestClientLibrary;
    @Autowired
    private EnvConfig<AuthProperty> envConfig;

    /**
     * Notifies the observer about a device state change.
     *
     * @param type The type of device state change.
     * @param data The data associated with the device state change.
     */
    @Override
    public void notify(int type, Object data) {
        LOGGER.debug("## notify - START");
        String baseUrl = envConfig.getStringValue(AuthProperty.DEVICE_ASSOCIATION_BASE_URL);
        String stateUrl = envConfig.getStringValue(AuthProperty.DEVICE_ASSOCIATION_STATE_CHNAGE_URL);

        String stateChangeUrl = baseUrl + stateUrl;
        if (type == TYPE_DEVICE_ACTIVATION) {
            DeviceStateActivation deviceStateActivation = (DeviceStateActivation) data;
            LOGGER.debug("## Device ACTIVATION flow - calling association -> stateChange api, URI: {}, data: {}",
                stateChangeUrl, data);
            LOGGER.info(
                "## Device ACTIVATION flow - calling association -> stateChange api, URI: {}, SerialNumber: {},"
                    + " HarmanID: {}", stateChangeUrl, deviceStateActivation.getSerialNumber(),
                deviceStateActivation.getActivationResponse().getDeviceId());
            hcpRestClientLibrary.doPost(stateChangeUrl, generateHeaders(),
                new ActivationStateToDeviceStateAdapter(deviceStateActivation), Object.class);
            LOGGER.info("## Device ACTIVATION flow - calling association -> stateChange api - SUCCESS");
        } else if (type == TYPE_DEVICE_DEACTIVATION) {
            DeviceStateDeactivation deviceStateDeactivation = (DeviceStateDeactivation) data;
            LOGGER.debug("## Device ACTIVATION flow - calling association -> stateChange api, URI: {}, data: {}",
                stateChangeUrl, data);
            LOGGER.info(
                "## Device DE-ACTIVATION flow - calling association -> stateChange api, URI: {}, SerialNumber: {},"
                    + " HarmanID: {}", stateChangeUrl, deviceStateDeactivation.getSerialNumber(),
                deviceStateDeactivation.getHarmanId());
            hcpRestClientLibrary.doPost(stateChangeUrl, generateHeaders(),
                new DeactivationStateToDeviceStateAdapter(deviceStateDeactivation), Object.class);
            LOGGER.info("## Device DE-ACTIVATION flow - calling association -> stateChange api - SUCCESS");
        }
    }

    /**
     * Generates the HTTP headers for the request.
     *
     * @return The generated HttpHeaders object.
     */
    private HttpHeaders generateHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add(USER_ID, ADMIN_USER);
        return httpHeaders;
    }

}
