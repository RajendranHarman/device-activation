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

package org.eclipse.ecsp.auth.lib.validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating instances of DeviceValidator based on the OEM environment.
 */
@Component
public class DeviceValidatorFactory {

    private Logger logger = LoggerFactory.getLogger(DeviceValidatorFactory.class);

    @Autowired
    private DeviceValidatorDefaultImpl deviceValidatorDefaultImpl;

    /**
     * Returns an instance of DeviceValidator based on the provided OEM environment.
     *
     * @param environment the OEM environment
     * @return an instance of DeviceValidator
     */
    public DeviceValidator getInstance(OemEnvironment environment) {
        logger.debug("requested instance for environment: {}", environment);

        if (environment == null) {
            return null;
        }

        return deviceValidatorDefaultImpl;
    }

    /**
     * Represents the OEM environment.
     */
    public enum OemEnvironment {
        OEM1, OEM2
    }
}
