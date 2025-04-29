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

package org.eclipse.ecsp.auth.lib.enums;

/**
 * This enum represents the key message enums used in the application.
 */
public enum KeyMessageEnums {
    ACTIVATION_CHECK("activation_check", "Sim Activation and Vin Association are mandatory before activation."),
    VIN_ASSO_MASNDSTORY("vin_asso_mandatory", "Vin Association is mandatory before activation."),
    ;

    private String key;
    private String defaultMessage;

    /**
     * Constructs a new KeyMessageEnums with the specified key and default message.
     *
     * @param key     the key associated with the message
     * @param message the default message
     */
    private KeyMessageEnums(String key, String message) {
        this.key = key;
        this.defaultMessage = message;
    }

    /**
     * Returns the key associated with the message.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the default message.
     *
     * @return the default message
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
