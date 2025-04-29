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

package org.eclipse.ecsp.auth.lib.rest.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.ecsp.common.config.EnvConfig;
import jakarta.annotation.PostConstruct;
import org.apache.hc.core5.http.ContentType;
import org.eclipse.ecsp.auth.lib.config.AuthProperty;
import org.eclipse.ecsp.auth.lib.rest.model.AccessTokenDetails;
import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.io.IOException;

/**
 * The SpringAuthTokenGenerator class is responsible for generating a Spring authentication token.
 * It uses the HcpRestClientLibrary to make a POST request to the specified Spring authentication URL
 * and fetches the token using the provided client ID and client secret.
 */
@Service
@Lazy
public class SpringAuthTokenGenerator {

    public static final String CONTENT_TYPE = "Content-Type";
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAuthTokenGenerator.class);
    @Autowired
    private HcpRestClientLibrary restClientLibrary;
    @Autowired
    private EnvConfig<AuthProperty> envConfig;
    private String clientId;
    private String clientSecret;
    private String springAuthUrl;

    /**
     * Fetches the Spring authentication token.
     *
     * @return The Spring authentication token.
     */
    public String fetchSpringAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString());
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add(CommonConstants.GRANT_TYPE_KEY, CommonConstants.SPRING_AUTH_CLIENT_CREDENTIALS);
        map.add(CommonConstants.SPRING_AUTH_SCOPE_KEY, CommonConstants.SPRING_AUTH_SCOPE_VALUE);
        map.add(CommonConstants.SPRING_AUTH_CLIENT_ID, clientId);
        map.add(CommonConstants.SPRING_AUTH_CLIENT_SECRET, clientSecret);

        LOGGER.debug("Fetching spring auth token for config: {}", map);
        LOGGER.info("Fetching spring auth token for config: {}", map);
        LOGGER.debug(" Spring Auth URL: {}", springAuthUrl);
        ResponseEntity<String> response = restClientLibrary.doPost(springAuthUrl, headers, map,
            String.class);
        String token = null;
        if (null != response && response.getStatusCode().equals(HttpStatus.OK)) {
            String responseBody = response.getBody();
            ObjectMapper mapper = new ObjectMapper();
            try {
                AccessTokenDetails accessTokenDetails = mapper.readValue(responseBody, AccessTokenDetails.class);
                token = accessTokenDetails.getAccessToken();
                LOGGER.info("Fetched spring auth token successfully");
                LOGGER.debug("Fetched spring auth token successfully");
            } catch (IOException e) {
                LOGGER.error("Exception occurred while parsing spring auth token", e);
            }
        }

        return token;
    }

    /**
     * Loads the client id, client secret, and spring auth URL from the environment configuration.
     */
    @PostConstruct
    public void loadSecrets() {
        clientId = envConfig.getStringValue(AuthProperty.SPRING_AUTH_CLIENT_ID).trim();
        clientSecret = envConfig.getStringValue(AuthProperty.SPRING_AUTH_CLIENT_SECRET).trim();
        LOGGER.info("Fetched the client id and client secret details successfully");
        springAuthUrl = envConfig.getStringValue(AuthProperty.SPRING_AUTH_SERVICE_URL);
        LOGGER.info("Fetched the spring auth URL successfully");
    }
}
