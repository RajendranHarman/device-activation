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

/**
 * Represents a login response version 2.
 */
@JsonAutoDetect
public class LoginResponseV2 implements LoginResponse {

    private long ttl;
    private long issuedOn;
    private String accessToken;

    /**
     * Gets the time to live (TTL) value.
     *
     * @return the TTL value
     */
    public long getTtl() {
        return ttl;
    }

    /**
     * Sets the time to live (TTL) value.
     *
     * @param ttl the TTL value to set
     */
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    /**
     * Gets the issued on timestamp.
     *
     * @return the issued on timestamp
     */
    public long getIssuedOn() {
        return issuedOn;
    }

    /**
     * Sets the issued on timestamp.
     *
     * @param issuedOn the issued on timestamp to set
     */
    public void setIssuedOn(long issuedOn) {
        this.issuedOn = issuedOn;
    }

    /**
     * Gets the access token.
     *
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Sets the access token.
     *
     * @param accessToken the access token to set
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
