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
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the response received after generating pre shared key.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreSharedKeyResponse {
    private String preSharedKey;
    private String encryptedPreSharedKey;

    /**
     * Retrieves the preSharedKey.
     *
     * @return the preSharedKey
     */
    @JsonProperty("preSharedKey")
    public String getPreSharedKey() {
        return preSharedKey;
    }

    /**
     * Set the preSharedKey.
     *
     * @param preSharedKey the preSharedKey to Set
     */
    public void setPreSharedKey(String preSharedKey) {
        this.preSharedKey = preSharedKey;
    }

    /**
     * Retrieves the encryptedPreSharedKey.
     *
     * @return the encryptedPreSharedKey
     */
    @JsonProperty("encryptedPreSharedKey")
    public String  getEncryptedPreSharedKey() {
        return encryptedPreSharedKey;
    }

    /**
     * Set the encryptedPreSharedKey.
     *
     * @param encryptedPreSharedKey the encryptedPreSharedKey to set
     */
    public void setEncryptedPreSharedKey(String  encryptedPreSharedKey) {
        this.encryptedPreSharedKey = encryptedPreSharedKey;
    }

    /**
     * Returns a string representation of the PreSharedKeyResponse object.
     *
     * @return A string representation of the object.
     */
    public String toString() {
        return "preSharedKey : " + this.preSharedKey + " encryptedPreSharedKey : " + this.encryptedPreSharedKey;
    }

}
