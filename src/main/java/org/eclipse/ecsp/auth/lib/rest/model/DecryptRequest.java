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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents a request to decrypt data.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecryptRequest {
    private String hcpId;
    private String authToken;

    /**
     * Gets the HCP ID.
     *
     * @return The HCP ID.
     */
    public String getHcpId() {
        return hcpId;
    }

    /**
     * Sets the HCP ID.
     *
     * @param hcpId The HCP ID to set.
     */
    public void setHcpId(String hcpId) {
        this.hcpId = hcpId;
    }

    /**
     * Gets the authentication token.
     *
     * @return The authentication token.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Sets the authentication token.
     *
     * @param authToken The authentication token to set.
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Returns a string representation of the DecryptRequest object.
     *
     * @return A string representation of the DecryptRequest object.
     */
    @Override
    public String toString() {
        return "DecryptRequest [hcpId=" + hcpId + ", authToken=" + authToken + "]";
    }
}
