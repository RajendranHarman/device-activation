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

/**
 * The LoginResponse interface represents the response received after a login request.
 * It provides methods to get and set the access token.
 */
public interface LoginResponse {

    /**
     * Gets the access token.
     *
     * @return The access token.
     */
    public String getAccessToken();

    /**
     * Sets the access token.
     *
     * @param accessToken The access token to set.
     */
    public void setAccessToken(String accessToken);

}
