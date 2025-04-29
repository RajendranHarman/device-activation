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
import jakarta.validation.constraints.NotBlank;


/**
 * Represents the activation request data for a device with v2 version.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivationRequestDataV2 {
    @NotBlank(message = "jitActId is required and not allowed be empty")
    private String jitActId;
    @NotBlank(message = "passKey is required and not allowed be empty")
    private String passKey;

    /**
     * Default constructor.
     */
    public ActivationRequestDataV2() {
    }

    /**
     * Constructs a new ActivationRequestDataV2 object with the specified jitActId, passKey.
     *
     * @param jitActId The unique id of the device.
     * @param passKey The pre shared key to activate device.
     */
    public ActivationRequestDataV2(String jitActId, String passKey) {
        super();
        this.jitActId = jitActId;
        this.passKey = passKey;
    }

    /**
     * Gets the JITActId.
     *
     * @return the JITActId
     */
    @JsonProperty(value = "JITActId")
    public String getJitActId() {
        return jitActId;
    }

    /**
     * Set the JITActId.
     *
     * @param jitActId the JITActId to set
     */
    @JsonProperty(value = "JITActId")
    public void setJitActId(String jitActId) {
        this.jitActId = jitActId;
    }

    /**
     * Gets the passKey.
     *
     * @return the passKey
     */
    @JsonProperty(value = "passKey")
    public String getPassKey() {
        return passKey;
    }

    /**
     * Set the passKey.
     *
     * @param passKey the passKey to set
     */
    @JsonProperty(value = "passKey")
    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }

    /**
     * Returns a string representation of the ActivationRequestDataV2 object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "ActivationRequestDataV2 ["
                + "jitActId='" + jitActId + '\''
                + ", passKey='" + passKey + '\''
                + ']';
    }
}
