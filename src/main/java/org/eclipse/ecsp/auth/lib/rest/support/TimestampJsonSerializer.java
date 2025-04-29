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

package org.eclipse.ecsp.auth.lib.rest.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Serializes a Timestamp object to a JSON string representation.
 */
public class TimestampJsonSerializer extends JsonSerializer<Timestamp> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimestampJsonSerializer.class);

    /**
     * Serializes the given Timestamp object to a JSON string.
     *
     * @param value    The Timestamp object to be serialized.
     * @param gen      The JsonGenerator to write the JSON string to.
     * @param provider The SerializerProvider to use for serialization.
     * @throws IOException If an I/O error occurs during serialization.
     */
    @Override
    public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDate = formatter.format(value);
        LOGGER.info("TimestampJsonSerializer at work>>>");
        gen.writeString(formattedDate);
    }
}
