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

package org.eclipse.ecsp.services;

import org.eclipse.ecsp.common.CommonConstants;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for HcpRestClientLibrary.
 */
public class HcpRestClientLibraryTest {

    private static final String URI = "/v4/device/activate";
    private static final int STATUS_CODE_200 = 200;
    private static final String RESPONSE1 = "SUCCESS";
    public static final ResponseEntity<String> RESPONSE_ENTITY1 = new ResponseEntity<>(RESPONSE1, HttpStatus.OK);
    private static final String URI2 = "/v1/devices";
    private static final String PASSWORD_TYPE = "password";
    private static final String RESPONSE2 = "{\"id\":\"1\"}";
    public static final ResponseEntity<String> RESPONSE_ENTITY2 = new ResponseEntity<>(RESPONSE2, HttpStatus.OK);

    @InjectMocks
    private HcpRestClientLibrary hcpRestClientLibrary;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void doGet() {
        Mockito.doReturn(RESPONSE_ENTITY2).when(restTemplate)
            .exchange(URI2, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
        ResponseEntity<String> stringResponseEntity = hcpRestClientLibrary.doGet(URI2, new HttpHeaders(), String.class);

        assertEquals(STATUS_CODE_200, stringResponseEntity.getStatusCodeValue());
        assertNotNull(stringResponseEntity.getBody());
        assertEquals(RESPONSE2, stringResponseEntity.getBody());
    }

    @Test
    public void doGetNullResponse() {
        Mockito.doReturn(RESPONSE_ENTITY2).when(restTemplate)
            .exchange(URI2, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
        ResponseEntity<String> stringResponseEntity = hcpRestClientLibrary.doGet(null, new HttpHeaders(), String.class);
        assertNull(stringResponseEntity);
    }

    @Test
    public void doPost() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        Mockito.doReturn(RESPONSE_ENTITY1).when(restTemplate)
            .exchange(URI, HttpMethod.POST, new HttpEntity<>(body, null), String.class);
        ResponseEntity<String> stringResponseEntity =
            hcpRestClientLibrary.doPost(URI, new HttpHeaders(), body, String.class);

        assertEquals(STATUS_CODE_200, stringResponseEntity.getStatusCodeValue());
        assertNotNull(stringResponseEntity.getBody());
        assertEquals(RESPONSE1, stringResponseEntity.getBody());
    }

    @Test
    public void doPut() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(CommonConstants.AUTH_TOKEN, PASSWORD_TYPE);

        Mockito.doReturn(RESPONSE_ENTITY2).when(restTemplate)
            .exchange(URI2, HttpMethod.PUT, new HttpEntity<>(body, new HttpHeaders()), String.class);
        ResponseEntity<String> stringResponseEntity =
            hcpRestClientLibrary.doPut(URI2, new HttpHeaders(), body, String.class);

        assertEquals(STATUS_CODE_200, stringResponseEntity.getStatusCodeValue());
        assertNotNull(stringResponseEntity.getBody());
        assertEquals(RESPONSE2, stringResponseEntity.getBody());
    }

    @Test
    public void getAuthHeaders() {
        assertNotNull(hcpRestClientLibrary.getAuthHeaders("my-token"));
    }
}
