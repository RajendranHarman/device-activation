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

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * This class implements the ConstraintValidator interface for the NullOrNotEmpty annotation.
 * It checks if the given string value is either null or not empty.
 *
 * @param <NullOrNotEmpty> the annotation type
 * @param <String> the type of value being validated
 */
public class NullOrNotEmptyValidator implements ConstraintValidator<NullOrNotEmpty, String> {

    public void initialize(NullOrNotEmpty parameters) {
    }

    /**
     * Validates whether a string value is null or not empty.
     *
     * @param value The string value to be validated.
     * @param constraintValidatorContext The context in which the constraint is evaluated.
     * @return {@code true} if the value is null or not empty, {@code false} otherwise.
     */
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        } else if (value.isEmpty()) {
            return false;
        }
        return true;
    }
}