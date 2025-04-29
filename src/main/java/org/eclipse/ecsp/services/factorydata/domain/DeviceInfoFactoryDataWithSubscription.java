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

package org.eclipse.ecsp.services.factorydata.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Represents device information factory data with subscription details.
 */
@RequiredArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceInfoFactoryDataWithSubscription extends DeviceInfoFactoryData {
    private String subscriptionStatus; // subscription status
    private String subscriberId;

    /**
     * Gets the subscription status of the device.
     *
     * @return The subscription status.
     */
    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    /**
     * Sets the subscription status of the device.
     *
     * @param subscriptionStatus The subscription status to set.
     */
    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    /**
     * Gets the subscriber ID of the device.
     *
     * @return The subscriber ID.
     */
    public String getSubscriberId() {
        return subscriberId;
    }

    /**
     * Sets the subscriber ID of the device.
     *
     * @param subscriberId The subscriber ID to set.
     */
    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }
}
