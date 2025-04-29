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

package org.eclipse.ecsp.auth.lib.service;

import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * This class represents the Association Service, which is responsible for sending events to a topic.
 */
@Service
public class AssociationService {

    /**
     * URL Separator Constant.
     */
    public static final String URL_SEPARATOR = "/";
    /**
     * APPLICATION_JSON Constant.
     */
    public static final String APPLICATION_JSON = "application/json";
    /**
     * CONTENT TYPE constant.
     */
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "accept";
    public static final String QUESTION_MARK = "?";
    public static final String EQUALS_SYMBOL = "=";
    public static final String EVENT_ID = "eventId";
    public static final String TOPIC_NAME = "topicName";
    public static final String KEY = "key";
    public static final String AND = "&";
    private static final Logger LOGGER = LoggerFactory.getLogger(AssociationService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${device_association_base_url}")
    private String baseUrl;

    @Value("${association_event_trigger_url:triggerKafkaEvent}")
    private String eventTriggerUrl;

    /**
     * Sends an event to a topic.
     *
     * @param deviceInfo The device information.
     * @param eventId    The event ID.
     * @param topicName  The topic name.
     * @param key        The key.
     * @return true if the event was sent successfully, false otherwise.
     */
    public boolean sendEventToTopic(DeviceInfo deviceInfo, String eventId, String topicName, String key) {
        LOGGER.debug("## Sending the event from Association Service with eventId:{} and topicName:{} and Key:{}",
            eventId, topicName, key);

        String sendEventUrl = buildSendEventUri(eventId, topicName, key);
        LOGGER.debug("## sendEventUrl is called: {}", sendEventUrl);
        try {
            HttpEntity<?> httpEntity = new HttpEntity<>(deviceInfo, createHeaders());
            ResponseEntity<Object> responseEntity = restTemplate.exchange(sendEventUrl, HttpMethod.POST, httpEntity,
                Object.class);
            if (responseEntity.getStatusCode().value() == HttpStatus.OK.value()) {
                return true;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occured during sending Event :{} ,{}", deviceInfo, e);
        } catch (Exception e) {
            LOGGER.error("Exception occured during sending Event :{} ,{}", deviceInfo, e);
        }
        return false;
    }

    /**
     * Builds the URL for sending the event.
     *
     * @param eventId   The event ID.
     * @param topicName The topic name.
     * @param key       The key.
     * @return The URL for sending the event.
     */
    private String buildSendEventUri(String eventId, String topicName, String key) {
        return baseUrl + eventTriggerUrl + QUESTION_MARK + EVENT_ID + EQUALS_SYMBOL + eventId + AND + TOPIC_NAME
            + EQUALS_SYMBOL + topicName + AND + KEY + EQUALS_SYMBOL + key;
    }

    /**
     * Creates the HTTP headers for the request.
     *
     * @return The HTTP headers.
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCEPT, APPLICATION_JSON);
        headers.add(CONTENT_TYPE, APPLICATION_JSON);
        return headers;
    }
}