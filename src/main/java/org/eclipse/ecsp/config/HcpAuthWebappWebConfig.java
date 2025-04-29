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

package org.eclipse.ecsp.config;

import org.eclipse.ecsp.auth.springmvc.rest.support.ControllerExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configuration class for the HcpAuthWebapp web application.
 * This class enables web MVC and configures handler exception resolvers.
 */
@Configuration
@EnableWebMvc
public class HcpAuthWebappWebConfig implements WebMvcConfigurer {

    /**
     * Configures the handler exception resolvers for the web application.
     * This method adds a custom ControllerExceptionHandler to handle unhandled exceptions in controllers.
     *
     * @param exceptionResolvers the list of exception resolvers to configure
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new ControllerExceptionHandler());
    }
}