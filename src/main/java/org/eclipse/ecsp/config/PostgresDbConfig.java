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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.ecsp.auth.lib.config.AuthProperty;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * Configuration class for Postgres database.
 */
@Configuration
@EnableScheduling
public class PostgresDbConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDbConfig.class);
    private static final StopWatch STOPWATCH = new StopWatch();
    private static final String VAULT_POSTGRES_USERNAME_KEY = "username";
    private static final String VAULT_POSTGRES_PASS_KEY = "password";
    private static final String VAULT_POSTGRES_LEASE_DURATION = "lease_duration";
    private static final int MILLIS = 50;
    private static final int VALUE_1000 = 1000;

    /**
     * we are refering service component name under vault.json section from
     * device-auth-api.yaml file.
     */
    @Autowired
    private EnvConfig<AuthProperty> envConfig;
    private volatile boolean isRefreshInProgress = false;
    private String userName;
    private String password;
    private long leaseDuration;
    private boolean postgresVaultEnabled;
    private DataSource dataSource = null;

    /**
     * Number of Connections a pool will try to acquire upon startup. Should be between minPoolSize and maxPoolSize.
     * Default: 3
     */
    private int initialPoolSize;

    /**
     * Minimum number of Connections a pool will maintain at any given time.
     * Default: 3
     */
    private int minPoolSize;

    /**
     * Maximum number of Connections a pool will maintain at any given time.
     */
    private int maxPoolSize;

    /**
     * Seconds a Connection can remain pooled but unused before being discarded. Zero means idle connections
     * never expire.
     * In second, after that time it will release the unused connections
     */
    private int maxIdleTime;

    /**
     * Determines how many connections at a time c3p0 will try to acquire when the pool is exhausted.
     */
    private int acquireIncrement;

    /**
     * If this is a number greater than 0, c3p0 will test all idle, pooled but unchecked-out connections,
     * every this number of seconds.
     */
    private int idleConnectionTestPeriod;

    /**
     * Retrieves the data source for the application.
     *
     * @return The data source object.
     * @throws ApiTechnicalException If the data source was not set properly.
     */
    @Bean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public DataSource dataSource() {
        LOGGER.debug("In Datasource bean method......");
        while (isRefreshInProgress) {
            LOGGER.info("## Datasource vault refresh in progress");
            // Sleep the thread for milli secs and again check the progress
            // status.
            try {
                Thread.sleep(MILLIS);
            } catch (InterruptedException e) {
                LOGGER.error("## DataSource refresh thread interrupted......");
                Thread.currentThread().interrupt();
            }
        }
        if (null == dataSource) {
            throw new ApiTechnicalException("## Datasource was not set properly.");
        }
        return dataSource;
    }

    /**
     * Loads the Postgres properties and initializes the connection pool.
     * Retrieves the required values from the environment configuration and sets them to the corresponding variables.
     * Also refreshes the data source after setting the properties.
     */
    @PostConstruct
    private void loadPostgresProperties() {
        //Connection pool properties
        initialPoolSize = envConfig.getIntegerValue(AuthProperty.INITIAL_POOL_SIZE);
        minPoolSize = envConfig.getIntegerValue(AuthProperty.MIN_POOL_SIZE);
        maxPoolSize = envConfig.getIntegerValue(AuthProperty.MAX_POOL_SIZE);
        maxIdleTime = envConfig.getIntegerValue(AuthProperty.MAX_IDLE_TIME);
        acquireIncrement = envConfig.getIntegerValue(AuthProperty.ACQUIRE_INCREMENT);
        idleConnectionTestPeriod = envConfig.getIntegerValue(AuthProperty.IDLE_CONNECTION_TEST_PERIOD);
        userName = envConfig.getStringValue(AuthProperty.POSTGRES_USERNAME);
        password = envConfig.getStringValue(AuthProperty.POSTGRES_PASSWORD);
        dataSource = refreshDataSource();
    }

    /**
     * Refreshes the data source by creating a new instance of ComboPooledDataSource and configuring its properties.
     * Cleans up the existing data source before creating a new one.
     *
     * @return The refreshed data source.
     * @throws ApiTechnicalException If an exception occurs while creating the connection pool.
     */
    private DataSource refreshDataSource() {
        cleanupDataSource(dataSource);
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(envConfig.getStringValue(AuthProperty.POSTGRES_DRIVER_CLASS_NAME));
            cpds.setJdbcUrl(envConfig.getStringValue(AuthProperty.POSTGRES_URL));
            cpds.setUser(userName);
            cpds.setPassword(password);
            cpds.setInitialPoolSize(initialPoolSize);
            cpds.setMinPoolSize(minPoolSize);
            cpds.setMaxPoolSize(maxPoolSize);
            cpds.setMaxIdleTime(maxIdleTime);
            cpds.setAcquireIncrement(acquireIncrement);
            cpds.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
            LOGGER.info("## Device Auth- ConnectionPool properties: initialPoolSize: {}, minPoolSize:{}, "
                    + "maxPoolSize: {}, maxIdleTime: {}, acquireIncrement: {}, idleConnectionTestPeriod: {}",
                cpds.getInitialPoolSize(),
                cpds.getMinPoolSize(), cpds.getMaxPoolSize(), cpds.getMaxIdleTime(), cpds.getAcquireIncrement(),
                cpds.getIdleConnectionTestPeriod());
        } catch (PropertyVetoException e) {
            throw new ApiTechnicalException(
                "## Exception while creating connection pool for Device Auth component, Error: " + e.getMessage());
        }
        return cpds;
    }

    /**
     * Cleans up the given DataSource object.
     *
     * @param ds The DataSource object to be cleaned up.
     */
    private void cleanupDataSource(DataSource ds) {
        if (ds instanceof ComboPooledDataSource cpds) {
            cpds.close();
        }
    }
}
