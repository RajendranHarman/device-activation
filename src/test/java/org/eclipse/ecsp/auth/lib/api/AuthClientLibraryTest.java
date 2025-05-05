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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.ecsp.auth.lib.config.AuthProperty;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for AuthClientLibrary.
 */
@Slf4j
public class AuthClientLibraryTest {

    public static final int TTL = 123;

    @InjectMocks
    private AuthClientLibrary authClientLibrary;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EnvConfig<AuthProperty> envConfig;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void activateTest() {
        String passcode = "24828rh2yr2";
        String vehicleId = "HUEUE6283681";
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("url");
        Mockito.when(restTemplate.getForObject("url" + vehicleId,
            String.class)).thenReturn(passcode);
        log.info("RestTemplate: " + authClientLibrary.getRestTemplate());
        String response = authClientLibrary.activate(vehicleId);
        assertEquals(passcode, response);
    }

    @Test
    public void activateNullTest() {
        String vehicleId = "HUEUE6283681";
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("url");
        Mockito.when(restTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(null);
        String response = authClientLibrary.activate(vehicleId);
        assertNull(response);
    }

    @Test
    public void loginTest() {
        String accessToken = "token";
        String passcode = "24828rh2yr2";
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("url");
        Mockito.when(restTemplate.getForObject("url" + passcode,
            String.class)).thenReturn(accessToken);
        String response = authClientLibrary.login(passcode);
        assertEquals(accessToken, response);
    }

    @Test
    public void loginNullTest() {
        String passcode = "24828rh2yr2";
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("url");
        Mockito.when(restTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(null);
        String response = authClientLibrary.login(passcode);
        assertNull(response);
    }

    @Test
    public void getKeyTest() {
        String passcode = "24828rh2yr2";
        String key = "key1";
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("url");
        Mockito.when(restTemplate.getForObject("url" + passcode,
            String.class)).thenReturn(key);
        String response = authClientLibrary.getKey(passcode);
        assertEquals(key, response);
    }

    @Test
    public void getKeyNullTest() {
        String passcode = "24828rh2yr2";
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("url");
        Mockito.when(restTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(null);
        String response = authClientLibrary.getKey(passcode);
        assertNull(response);
    }

    @Test
    public void getTtlTest() {
        String passcode = "24828rh2yr2";
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("url");
        Mockito.when(restTemplate.getForObject("url" + passcode,
            Integer.class)).thenReturn(TTL);
        int response = authClientLibrary.getTtl(passcode);
        assertEquals(TTL, response);
    }

    @Test
    public void getTtlNullTest() {
        String passcode = "24828rh2yr2";
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("url");
        Mockito.when(restTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(null);
        int response = authClientLibrary.getTtl(passcode);
        assertEquals(0, response);
    }
}