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

package org.eclipse.ecsp.auth.lib.rest.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.ecsp.auth.lib.rest.support.TimestampJsonSerializer;

import java.sql.Timestamp;

/**
 * Represents a login response version 1.
 */
@JsonAutoDetect
public class LoginResponseV1 implements LoginResponse {
    private String accessToken;
    private Timestamp expirationTime;
    private Timestamp issueTime;

    /**
     * Default constructor.
     */
    public LoginResponseV1() {

    }

    /**
     * Constructor that initializes the LoginResponseV1 object using a LoginResponseV2 object.
     *
     * @param loginResponseV2 The LoginResponseV2 object to initialize from.
     */
    public LoginResponseV1(LoginResponseV2 loginResponseV2) {
        this.accessToken = loginResponseV2.getAccessToken();
        this.issueTime = new Timestamp(loginResponseV2.getIssuedOn());
        this.expirationTime = new Timestamp(this.issueTime.getTime() + loginResponseV2.getTtl());
    }

    /**
     * Gets the access token.
     *
     * @return The access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Sets the access token.
     *
     * @param accessToken The access token to set.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Gets the expiration time.
     *
     * @return The expiration time.
     */
    @JsonSerialize(using = TimestampJsonSerializer.class)
    public Timestamp getExpirationTime() {
        return expirationTime != null ? (Timestamp) expirationTime.clone() : null;
    }

    /**
     * Sets the expiration time.
     *
     * @param expirationTime The expiration time to set.
     */
    public void setExpirationTime(Timestamp expirationTime) {
        this.expirationTime = expirationTime != null ? (Timestamp) expirationTime.clone() : null;
    }

    /**
     * Gets the issue time.
     *
     * @return The issue time.
     */
    @JsonSerialize(using = TimestampJsonSerializer.class)
    public Timestamp getIssueTime() {
        return issueTime != null ? (Timestamp) issueTime.clone() : null;
    }

    /**
     * Sets the issue time.
     *
     * @param issueTime The issue time to set.
     */
    public void setIssueTime(Timestamp issueTime) {
        this.issueTime = issueTime != null ? (Timestamp) issueTime.clone() : null;
    }
}
