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

package org.eclipse.ecsp.observer;

import org.eclipse.ecsp.auth.lib.config.AuthProperty;
import org.eclipse.ecsp.auth.lib.obsever.DefaultDeviceStateChangeObservable;
import org.eclipse.ecsp.auth.lib.obsever.DeviceStateActivation;
import org.eclipse.ecsp.auth.lib.obsever.DeviceStateObserver;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationResponse;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DefaultDeviceStateChangeObservable.
 */
public class DefaultDeviceStateChangeObservableTest {

    @InjectMocks
    private DefaultDeviceStateChangeObservable defaultDeviceStateChangeObservable;

    @Mock
    private DeviceStateObserver deviceStateObserver;

    @Mock
    private EnvConfig<AuthProperty> envConfig;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void newDeviceActivatedTest() {
        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setPasscode("24828rh2yr2");
        activationResponse.setDeviceId("HUEUE6283681");
        DeviceStateActivation deviceStateActivation = new DeviceStateActivation();
        deviceStateActivation.setActivationResponse(activationResponse);
        deviceStateActivation.setSwVersion("1.2.1");
        deviceStateActivation.setHwVersion("1.4.1");
        deviceStateActivation.setSerialNumber("12345");
        deviceStateActivation.setDeviceType("dongle");
        deviceStateActivation.setReactivationFlag(false);
        DefaultDeviceStateChangeObservable newDefaultDeviceStateChangeObservable =
            spy(defaultDeviceStateChangeObservable);
        int type = DeviceStateObserver.TYPE_DEVICE_ACTIVATION;
        Mockito.doNothing().when(newDefaultDeviceStateChangeObservable).notifyObservers(type, deviceStateActivation);
        newDefaultDeviceStateChangeObservable.newDeviceActivated(deviceStateActivation);
        verify(newDefaultDeviceStateChangeObservable, times(1)).notifyObservers(type, deviceStateActivation);
    }

    @Test
    public void notifyObserversTest() {
        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setPasscode("24828rh2yr2");
        activationResponse.setDeviceId("HUEUE6283681");
        DeviceStateActivation deviceStateActivation = new DeviceStateActivation();
        deviceStateActivation.setActivationResponse(activationResponse);
        deviceStateActivation.setSwVersion("1.2.1");
        deviceStateActivation.setHwVersion("1.4.1");
        deviceStateActivation.setSerialNumber("12345");
        deviceStateActivation.setDeviceType("dongle");
        deviceStateActivation.setReactivationFlag(false);
        int type = DeviceStateObserver.TYPE_DEVICE_ACTIVATION;
        DefaultDeviceStateChangeObservable newDefaultDeviceStateChangeObservable =
            spy(defaultDeviceStateChangeObservable);
        newDefaultDeviceStateChangeObservable.notifyObservers(type, deviceStateActivation);
        Mockito.doNothing().when(newDefaultDeviceStateChangeObservable).notifyObservers(type, deviceStateActivation);
        verify(newDefaultDeviceStateChangeObservable, times(1)).notifyObservers(type, deviceStateActivation);
    }

    @Test
    public void addObserverTest() {
        defaultDeviceStateChangeObservable.addObserver(deviceStateObserver);
        Assertions.assertNotNull(deviceStateObserver);

    }

    @Test
    public void afterPropertiesSetTest() throws Exception {
        Mockito.when(envConfig.getBooleanValue(AuthProperty.NOTIFY_DEVICE_ACTIVATION_TO_DEVICE_ASSOCIATION))
            .thenReturn(true);
        Mockito.when(envConfig.getBooleanValue(AuthProperty.NOTIFY_DEVICE_ACTIVATION)).thenReturn(true);
        defaultDeviceStateChangeObservable.afterPropertiesSet();
        Assertions.assertEquals(true,
            envConfig.getBooleanValue(AuthProperty.NOTIFY_DEVICE_ACTIVATION_TO_DEVICE_ASSOCIATION));
        Assertions.assertEquals(true, envConfig.getBooleanValue(AuthProperty.NOTIFY_DEVICE_ACTIVATION));
    }
}