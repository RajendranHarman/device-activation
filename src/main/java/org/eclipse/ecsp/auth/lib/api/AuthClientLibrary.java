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

package org.eclipse.ecsp.auth.lib.api;

import org.eclipse.ecsp.auth.lib.config.AuthProperty;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * The AuthClientLibrary class provides methods for interacting with the authentication API.
 * It encapsulates the functionality to send requests for vehicle activation, login, key retrieval,
 * and time-to-live (TTL) retrieval.
 */
public class AuthClientLibrary {
    RestTemplate restTemplate;

    @Autowired
    private EnvConfig<AuthProperty> envConfig;

    /**
     * Constructs a new instance of the AuthClientLibrary class.
     * Initializes the RestTemplate and sets up the message converters.
     */
    @Autowired
    public AuthClientLibrary() {
        restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        List<HttpMessageConverter<?>> converters = new ArrayList<>(messageConverters);

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        converters.add(jsonConverter);
        restTemplate.setMessageConverters(converters);
    }

    /**
     * Gets the RestTemplate instance used by the AuthClientLibrary.
     *
     * @return The RestTemplate instance.
     */
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * Sets the RestTemplate instance used by the AuthClientLibrary.
     *
     * @param restTemplate The RestTemplate instance to set.
     */
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends a request to activate a vehicle.
     *
     * @param vehicleId The ID of the vehicle to activate.
     * @return The response from the activation API.
     */
    public String activate(String vehicleId) {
        return restTemplate.getForObject(
            envConfig.getStringValue(AuthProperty.HCP_AUTH_WEBAPP_ACTIVATE_URL).trim() + vehicleId,
            String.class);
    }

    /**
     * Sends a request to perform a login.
     *
     * @param passcode The passcode for the login.
     * @return The response from the login API.
     */
    public String login(String passcode) {
        return restTemplate.getForObject(
            envConfig.getStringValue(AuthProperty.HCP_AUTH_WEBAPP_LOGIN_URL).trim() + passcode,
            String.class);
    }

    /**
     * Sends a request to get a key.
     *
     * @param passcode The passcode to get the key for.
     * @return The response from the key API.
     */
    public String getKey(String passcode) {
        return restTemplate.getForObject(
            envConfig.getStringValue(AuthProperty.HCP_AUTH_WEBAPP_KEY_URL).trim() + passcode,
            String.class);
    }

    /**
     * Sends a request to get the time-to-live (TTL) value.
     *
     * @param passcode The passcode to get the TTL for.
     * @return The TTL value.
     */
    public int getTtl(String passcode) {
        Integer ttl =
            restTemplate.getForObject(envConfig.getStringValue(AuthProperty.HCP_AUTH_WEBAPP_TTL_URL).trim() + passcode,
                Integer.class);
        // 2.33 Release - Sonar NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE code smell fix
        return ttl != null ? ttl : 0;
    }
}
