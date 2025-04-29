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

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used to validate that a field is either null or not empty.
 * This annotation is used in conjunction with the NullOrNotEmptyValidator class.
 *
 * <p>@Target({ElementType.FIELD}) - Indicates that this annotation can only be applied to fields.</p>
 *
 * <p>@Retention(RUNTIME) - Indicates that this annotation should be retained at runtime.</p>
 *
 * <p>@Constraint(validatedBy = NullOrNotEmptyValidator.class) - Specifies the validator class to be used for
 * validation.</p>
 *
 * @Documented - Indicates that this annotation should be included in the generated JavaDoc.
 */
@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotEmptyValidator.class)
public @interface NullOrNotEmpty {

    /**
     * Returns the message for the validation constraint.
     *
     * @return the message for the validation constraint
     */
    String message() default "{javax.validation.constraints.NullOrNotEmpty.message}";

    /**
     * Specifies the validation groups that this constraint belongs to.
     *
     * @return the validation groups
     */
    Class<?>[] groups() default { };

    /**
     * Returns the payload classes associated with the constraint.
     *
     * @return the payload classes associated with the constraint
     */
    Class<? extends Payload>[] payload() default {};
}
