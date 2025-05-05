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
 * Annotation to validate that a field is either null or not empty.
 * This constraint ensures that the annotated field is valid only if it is either null
 * or contains a non-empty value.
 *
 * <p>Usage example:</p>
 * <pre>
 * &#64;NullOrNotEmpty
 * private String exampleField;
 * </pre>
 *
 * <p>Attributes:</p>
 * <ul>
 *   <li><b>message:</b> Customizable error message when the constraint is violated.</li>
 *   <li><b>groups:</b> Specifies the validation groups this constraint belongs to.</li>
 *   <li><b>payload:</b> Provides additional metadata information for the constraint.</li>
 * </ul>
 *
 * <p>This annotation is validated by the {@code NullOrNotEmptyValidator} class.</p>
 *
 * <p>Target: {@code FIELD}</p>
 *
 * <p>Retention: {@code RUNTIME}</p>
 *
 * <p>Documented: Yes</p>
 *
 * @see jakarta.validation.Constraint
 * @see org.eclipse.ecsp.auth.lib.rest.model.NullOrNotEmptyValidator
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
    String message() default "{jakarta.validation.constraints.NullOrNotEmpty.message}";

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
