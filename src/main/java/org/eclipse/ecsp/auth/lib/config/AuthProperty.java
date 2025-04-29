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

package org.eclipse.ecsp.auth.lib.config;

import org.eclipse.ecsp.common.config.EnvConfigProperty;
import org.eclipse.ecsp.common.config.EnvConfigPropertyType;
import org.eclipse.ecsp.common.CommonConstants;

/**
 * This enum represents the authentication properties used in the application.
 */
public enum AuthProperty implements EnvConfigProperty {

    /**
     * notify_device_activation_to_device_association=true.
     * device_association_base_url=https://dev-auth.ahanet.net/dev-hcp-deviceassociation-webapp/
     * device_association_state_chnage_url=/devices/stateChanged/
     * device_association_login_url=http://sdpesbs4.ahanet.net:8281/sdp/1.1/session/login
     * device_association_login_user=bob_argo
     * device_association_login_password=password
     */
    SERVICE_REST_URL_BASE("service_rest_url_base"),
    AUTH_TOKEN_TTL("auth_ttl"),
    /*
     * NOTIFICATION_TYPE("toyotaNotoficationType"),
     * ADMIN_USER("toyotaAdminUser"), ADMIN_PWD("toyotaAdminPwd"),
     * DEVICE_ROLE("toyotaDeviceRole"),
     * NOTIF_SERVICE_REST_URL_BASE("notif_service.rest_url_base"),
     * USER_AUTH_WEBAPP_URL("userauth_webapp_url"),
     */
    // DUMMY_PUBLIC_PROPERTY("dummy.public_property","default value"),
    // DUMMY_SECURED_PROPERTY("dummy.secured_property","default value",
    // EnvConfigPropertyType.SECURED),
    ENABLE_DEVICE_VALIDATION("device_validation_by_oem", "DISABLE"),
    OEM_ENVIRONMENT("oem_environment"),
    ENABLE_DEVICE_ASSOCIATION_CODE_DURING_ACTIVATION("enable_device_association_code_during_activation",
        CommonConstants.FALSE_PROPERTY),
    NOTIFY_DEVICE_ACTIVATION("notify_device_activation", CommonConstants.FALSE_PROPERTY),
    SDP_NOTIFY_DEVICE_ACTIVATION_URL("sdp_notify_device_activation_url"),
    SDP_OAUTH_USERNAME("sdp_oauth_username"),
    SDP_OAUTH_PASSWORD("sdp_oauth_password"),
    SDP_OAUTH_CONSUMER_KEY("sdp_oauth_consumer_key"),
    SDP_OAUTH_CONSUMER_SECRET("sdp_oauth_consumer_secret"),
    SDP_OAUTH_SERVICE_URL("sdp_oauth_service_url"),
    NOTIFY_DEVICE_ACTIVATION_TO_DEVICE_ASSOCIATION("notify_device_activation_to_device_association",
        CommonConstants.FALSE_PROPERTY),
    DEVICE_ASSOCIATION_BASE_URL("device_association_base_url"),
    DEVICE_ASSOCIATION_STATE_CHNAGE_URL("device_association_state_chnage_url"),
    // DEVICE_ASSOCIATION_SDP_LOGIN_URL("device_association_sdp_login_url"),
    // DEVICE_ASSOCIATION_SDP_LOGIN_USER("device_association_sdp_login_user"),
    // DEVICE_ASSOCIATION_SDP_LOGIN_PASSWORD("device_association_sdp_login_password"),

    //SpringAuth related properties
    SPRING_AUTH_CLIENT_ID("client_id"),
    SPRING_AUTH_CLIENT_SECRET("client_secret"),
    SPRING_AUTH_SERVICE_URL("spring_auth_service_url"),
    SPRING_AUTH_BASE_URL("spring_auth_base_url"),

    VAULT_SERVER_IP_ADDRESS("vault_server_ip_address"),
    VAULT_SERVER_PORT("vault_server_port"),
    VAULT_ENV("environment"),
    SECRET_VAULT_ENABLE_FLG("secerets_vault_enable_flg"),

    POSTGRES_USERNAME("postgres_username"),
    POSTGRES_PASSWORD("postgres_password"),
    POSTGRES_VAULT_ENABLE_FLG("postgres_vault_enable_flg"),
    POSTGRES_URL("postgres_url"),
    POSTGRES_DRIVER_CLASS_NAME("postgres_driver_class_name"),
    POSTGRES_VAULT_LEASE_INTERVAL_GAP("postgres_vault_leaseIntervalGap"),
    POSTGRES_VAULT_REFRESH_CHECK_INTERVAL("postgres_vault_refreshCheckInterval"),
    HCP_AUTH_WEBAPP_ACTIVATE_URL("hcp_auth_webapp_activate_url"),
    HCP_AUTH_WEBAPP_LOGIN_URL("hcp_auth_webapp_login_url"),
    HCP_AUTH_WEBAPP_KEY_URL("hcp_auth_webapp_key_url"),
    HCP_AUTH_WEBAPP_TTL_URL("hcp_auth_webapp_ttl_url"),
    HCP_ACTIVATION_PRESHARED_KEY("hcp_activation_preSharedKey"),

    VIN_ENABLED_FLAG("vin_enabled_flag"),

    HCP_AUTH_QUALIFIER_SECRET_KEY("hcp_auth_qualifier_secret_key"),

    INITIAL_POOL_SIZE("initial_pool_size"),
    MIN_POOL_SIZE("min_pool_size"),
    MAX_POOL_SIZE("max_pool_size"),
    MAX_IDLE_TIME("max_idle_time"),
    ACQUIRE_INCREMENT("acquire_increment"),
    IDLE_CONNECTION_TEST_PERIOD("idle_connection_test_period"),
    API_REGISTRY_ENABLED("api_registry_enabled"),
    SPRING_APPLICATION_VERSION("spring_application_version"),
    OPENAPI_PATH_INCLUDE("openapi_path_include"),
    SERVER_PORT("server_port"),
    SPRING_APPLICATION_NAME("spring_application_name"),
    SPRING_APPLICATION_SERVICENAME("spring_application_servicename"),
    API_SECURITY_ENABLED("api_security_enabled"),
    OPENAPI_PATH_EXCLUDE("openapi_path_exclude"),
    API_CONTEXT_PATH("api_context-path"),
    API_REGISTRY_SERVICE_NAME("api_registry_service-name");

    private String nameInFile;
    private String defaultValue;
    private EnvConfigPropertyType type;

    /**
     * Constructs an `AuthProperty` enum with the specified name in the file.
     *
     * @param nameInFile the name of the property in the file
     */
    private AuthProperty(String nameInFile) {
        this(nameInFile, null);
    }

    /**
     * Constructs an `AuthProperty` enum with the specified name in the file and default value.
     *
     * @param nameInFile the name of the property in the file
     * @param defaultValue the default value of the property
     */
    private AuthProperty(String nameInFile, String defaultValue) {
        this(nameInFile, defaultValue, EnvConfigPropertyType.PUBLIC);
    }

    /**
     * Constructs an `AuthProperty` enum with the specified name in the file, default value, and type.
     *
     * @param nameInFile the name of the property in the file
     * @param defaultValue the default value of the property
     * @param type the type of the property
     */
    private AuthProperty(String nameInFile, String defaultValue, EnvConfigPropertyType type) {
        this.nameInFile = nameInFile;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    /**
     * Returns the name of the property in the file.
     *
     * @return the name of the property in the file
     */
    @Override
    public String getNameInFile() {
        return nameInFile;
    }

    /**
     * Returns the default value of the property.
     *
     * @return the default value of the property
     */
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the type of the property.
     *
     * @return the type of the property
     */
    @Override
    public EnvConfigPropertyType getType() {
        return type;
    }
}
