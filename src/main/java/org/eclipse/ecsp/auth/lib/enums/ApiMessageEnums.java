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

import org.eclipse.ecsp.common.CommonConstants;

import java.text.MessageFormat;

/**
 * This enum represents the API message codes and their corresponding messages.
 */
public enum ApiMessageEnums {
    GENERAL_ERROR("dauth-777", "Internal server error", "Not successful. Something went wrong. Please contact admin."),
    NOT_SUPPORTED_VERSION("dauth-001", "Not supported", "This version of api is not supported."),
    AUTH_SUCCESS("dauth-002", "Success", "Device authentication completed successfully."),
    AUTH_PROVISION_ALIVE("dauth-003", CommonConstants.PRE_CONDITION_FAILED,
        "Device will be in PROVISIONED_ALIVE state. Reason: Association was not performed."),
    AUTH_FAILED("dauth-004", CommonConstants.PRE_CONDITION_FAILED, "Activation Failed: Duplicate activation request."),
    AUTH_INPUT_VALIDATION_FAILED("dauth-005", CommonConstants.VALIDATION_FAILED,
        "Not sufficient data is provided to activate"),
    DEVICE_DETAILS_NOT_FOUND("dauth-006", "Resource not found", "Device details are not in inventory"),
    INVALID_DEVICE("dauth-007", CommonConstants.VALIDATION_FAILED, "Invalid device."),
    ACTIVATION_CHECK("dauth-008", CommonConstants.PRE_CONDITION_FAILED,
        "Activation Failed: Sim transaction is either empty or not active."),
    VIN_ASSO_MANDATORY("dauth-008", CommonConstants.PRE_CONDITION_FAILED,
        "Vin Association is mandatory before activation."),
    QUALIFIER_VALIDATION_FAILED("dauth-009", CommonConstants.VALIDATION_FAILED,
        "During random number generation, validation failed for qualifier. Possible cause: qualifier static"
            + " secretKey is missing in vault.  Please contact admin."),
    UNKNOWN_ERROR("dauth-010", CommonConstants.VALIDATION_FAILED, "Something is not right. Please contact admin"),
    DEVICE_INVALID_STATE("dauth-011", CommonConstants.PRE_CONDITION_FAILED,
        "Device is in invalid state to activate. i.e. It should not be faulty or stolen."),
    // Added the invalid device type error as part of US 292046
    INVALID_DEVICE_TYPE("dauth-012", CommonConstants.VALIDATION_FAILED, "Invalid Device Type."),
    DEVICE_NOT_FOUND("dauth-013", CommonConstants.NO_DEVICE_FOUND,
            "no device found or more than one device found in device activation table"),
    PRESHAREDKEY_VALIDATION_FAILED("dauth-014", CommonConstants.INVALID_PRESHAREDKEY,
            "PreSharedKey from request is not right"),
    ;

    private String code;
    private String message;
    private String generalMessage;

    /**
     * Constructs an ApiMessageEnums enum with the specified code, message, and general message.
     *
     * @param code           the code of the API message
     * @param message        the message of the API message
     * @param generalMessage the general message of the API message
     */
    ApiMessageEnums(String code, String message, String generalMessage) {
        this.code = code;
        this.message = message;
        this.generalMessage = generalMessage;
    }

    /**
     * Returns the code of the API message.
     *
     * @return the code of the API message
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the message of the API message.
     *
     * @return the message of the API message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the formatted message of the API message with the specified values.
     *
     * @param message the message to format
     * @param value   the values to be used in the message format
     * @return the formatted message of the API message
     */
    public String getMessage(String message, Object[] value) {
        return new MessageFormat(message).format(value);
    }

    /**
     * Returns the general message of the API message.
     *
     * @return the general message of the API message
     */
    public String getGeneralMessage() {
        return generalMessage;
    }
}
