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

package org.eclipse.ecsp.notification.lib.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.notification.lib.constants.NotificationConstants;
import org.eclipse.ecsp.notification.lib.model.nc.NotificationCenterNonRegUserConfig;
import org.eclipse.ecsp.notification.lib.model.nc.Recipient;
import org.eclipse.ecsp.notification.lib.model.nc.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class represents a client for interacting with the Notification Center.
 * It provides methods to retrieve user profiles and send notifications to non-registered users.
 */
@Component
public class NotificationCenterClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationCenterClient.class);

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Autowired
    RestTemplate restTemplate;

    /**
     * Retrieves the user profile for the specified user ID from the notification center.
     *
     * @param userId    The ID of the user.
     * @param ncBaseUrl The base URL of the notification center.
     * @return The user profile associated with the specified user ID, or null if the user profile is not found.
     */
    public UserProfile getUserProfile(String userId, String ncBaseUrl) {
        String url =
            ncBaseUrl + NotificationConstants.GET_USER_PROFILE_API + NotificationConstants.URL_SEPARATOR + userId;
        LOGGER.info("## get UserProfile endpoint: {} ", url);
        UserProfile userProfile = null;
        try {
            userProfile = restTemplate.getForObject(url, UserProfile.class);
            if (userProfile != null) {
                LOGGER.info("## found user {}", userProfile.getUserId());
            }
        } catch (Exception e) {
            LOGGER.error("Can't deliver notification for User {}, UserProfile not found: {}", userId, e.getMessage());
        }
        LOGGER.debug("## decodeVin - END");
        return userProfile;
    }

    /**
     * Calls the Notification Center API to send a notification to a non-registered user.
     *
     * @param userProfile The user profile of the recipient.
     * @param ncBaseUrl The base URL of the Notification Center.
     * @param notificationId The ID of the notification.
     */
    public void callNotifCenterNonRegisteredUserApi(UserProfile userProfile, String ncBaseUrl, String notificationId) {
        String url = ncBaseUrl + NotificationConstants.POST_NOTIFICATION_NON_REG_USER_API;
        LOGGER.info("## Notification Center URI: {}", url);
        // set recipient Obj
        List<Recipient> recipients = new ArrayList<>();
        Recipient recipient = getRecipient(userProfile);
        recipients.add(recipient);

        //setting main RequestBody
        NotificationCenterNonRegUserConfig ncConfig = new NotificationCenterNonRegUserConfig();
        ncConfig.setNotificationId(notificationId);
        ncConfig.setVersion(NotificationConstants.API_VERSION_V1);
        ncConfig.setRecipients(recipients);

        Map<String, String> headerMap = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        headerMap.put("RequestId", uuid.toString());
        LOGGER.info("## RequestId: {}", uuid);
        String notifConfigJsonBody;
        try {
            notifConfigJsonBody = MAPPER.writeValueAsString(ncConfig);
            LOGGER.debug("## notifCenterConfigJsonBody: {}", notifConfigJsonBody);

            HttpHeaders headers = createHeaders(headerMap);
            ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(notifConfigJsonBody, headers),
                    String.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                LOGGER.info("## SMS notification sent successfully");
            } else {
                LOGGER.error("## Error from Non-RegisteredUser send Notification api, status code: {} error {}",
                    response.getStatusCode(), response.getBody());
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error while converting to Json: {} ", e.getMessage());
        } catch (Exception e) {
            LOGGER.error(
                "## Error has occurred while calling Non-RegisteredUser API to send SMS Notification, "
                    + "Error Msg: {}, Error: {}", e.getMessage(), e);
        }
    }

    /**
     * Returns a recipient object based on the provided user profile.
     *
     * @param userProfile The user profile used to create the recipient object.
     * @return The recipient object with the user's default phone number, brand, locale, and user ID.
     */
    private Recipient getRecipient(UserProfile userProfile) {
        Recipient recipient = new Recipient();
        recipient.setSms(userProfile.getDefaultPhoneNumber());
        recipient.setBrand(NotificationConstants.DEFAULT_BRAND);
        recipient.setLocale(
            (!StringUtils.isEmpty(userProfile.getLocale().toLanguageTag())) ? userProfile.getLocale().toLanguageTag() :
                NotificationConstants.DEFAULT_LOCALE);
        Map<String, String> data = new HashMap<>();
        data.put(NotificationConstants.USERID, userProfile.getUserId());
        recipient.setData(data);
        return recipient;
    }

    /**
     * Creates HttpHeaders object based on the provided headers map.
     *
     * @param headersMap a map containing the headers to be added to the HttpHeaders object
     * @return the created HttpHeaders object
     */
    private HttpHeaders createHeaders(Map<String, String> headersMap) {
        HttpHeaders headers = new HttpHeaders();
        if (headersMap != null && headersMap.size() != 0) {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                headers.add(key, value);
            }
        }
        headers.add(NotificationConstants.CONTENT_TYPE, NotificationConstants.APPLICATION_JSON);
        return headers;
    }
}
