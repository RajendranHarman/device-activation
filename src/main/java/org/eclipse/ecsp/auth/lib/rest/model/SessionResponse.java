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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * Represents a session response object.
 */
@JsonAutoDetect
public class SessionResponse {

    @JsonProperty("otamSessionId")
    private String otamsessionid;

    @JsonProperty("expiryTime")
    private int expiryTime;

    @JsonProperty("expirationTimestamp")
    private Timestamp expirationTimestamp;

    /**
     * Gets the expiry time of the session.
     *
     * @return The expiry time.
     */
    @JsonProperty("expiryTime")
    public int getExpiryTime() {
        return expiryTime;
    }

    /**
     * Sets the expiry time of the session.
     *
     * @param expiryTime The expiry time to set.
     */
    @JsonProperty("expiryTime")
    public void setExpiryTime(int expiryTime) {
        this.expiryTime = expiryTime;
    }

    /**
     * Gets the expiration timestamp of the session.
     *
     * @return The expiration timestamp.
     */
    @JsonProperty("expirationTimestamp")
    public Timestamp getExpirationTimestamp() {
        return expirationTimestamp != null ? (Timestamp) expirationTimestamp.clone() : null;
    }

    /**
     * Sets the expiration timestamp of the session.
     *
     * @param expirationTimestamp The expiration timestamp to set.
     */
    @JsonProperty("expirationTimestamp")
    public void setExpirationTimestamp(Timestamp expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp != null ? (Timestamp) expirationTimestamp.clone() : null;
    }

    /**
     * Gets the OTAM session ID.
     *
     * @return The OTAM session ID.
     */
    @JsonProperty("otamSessionId")
    public String getOtamsessionid() {
        return otamsessionid;
    }

    /**
     * Sets the OTAM session ID.
     *
     * @param otamsessionid The OTAM session ID to set.
     */
    @JsonProperty("otamSessionId")
    public void setOtamsessionid(String otamsessionid) {
        this.otamsessionid = otamsessionid;
    }
}
