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

package org.eclipse.ecsp.service;

import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestDataToDeviceInfoAdapter;
import org.eclipse.ecsp.auth.lib.service.AssociationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for AssociationService.
 */
public class AssociationServiceTest {

    @Value("${activate_failure_event_id:ActivationFailure}")
    private String activationFailureEventId;

    @Value("${activate_failure_event_topic:activation}")
    private String activationFailureEventTopic;

    @InjectMocks
    private AssociationService associationService;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void shouldSuccessfullySendEventToTopic() {

        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("123456");

        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(HttpStatus.OK);
        Mockito.doReturn(responseEntity).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Object>) Mockito.any());
        boolean eventSentStatus = associationService.sendEventToTopic(
            new ActivationRequestDataToDeviceInfoAdapter(activationRequestData), activationFailureEventId,
            activationFailureEventTopic, activationRequestData.getImei());

        assertTrue(eventSentStatus);
    }

    @Test
    public void shouldThrowHttpClientErrorExceptionWhileSendingEventToTopic() {

        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("123456");

        HttpClientErrorException e = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        Mockito.doThrow(e).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Object>) Mockito.any());
        boolean eventSentStatus = associationService.sendEventToTopic(
            new ActivationRequestDataToDeviceInfoAdapter(activationRequestData), activationFailureEventId,
            activationFailureEventTopic, activationRequestData.getImei());
        assertFalse(eventSentStatus);
    }

    @Test
    public void shouldThrowExceptionWhileSendingEventToTopic() {

        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("123456");

        RestClientException e = new RestClientException("Error Occurred");
        Mockito.doThrow(e).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Object>) Mockito.any());
        boolean eventSentStatus = associationService.sendEventToTopic(
            new ActivationRequestDataToDeviceInfoAdapter(activationRequestData), activationFailureEventId,
            activationFailureEventTopic, activationRequestData.getImei());
        assertFalse(eventSentStatus);
    }

    @Test
    public void shouldFailToSendEventToTopic() {

        ActivationRequestData activationRequestData = new ActivationRequestData();
        activationRequestData.setImei("123456");

        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        Mockito.doReturn(responseEntity).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Object>) Mockito.any());
        boolean eventSentStatus = associationService.sendEventToTopic(
            new ActivationRequestDataToDeviceInfoAdapter(activationRequestData), activationFailureEventId,
            activationFailureEventTopic, activationRequestData.getImei());

        assertFalse(eventSentStatus);
    }
}