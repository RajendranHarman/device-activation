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

import org.eclipse.ecsp.common.config.EnvConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.ecsp.auth.lib.config.AuthProperty;
import org.eclipse.ecsp.auth.lib.obsever.DeviceAssociationDeviceStateObserver;
import org.eclipse.ecsp.auth.lib.obsever.DeviceStateActivation;
import org.eclipse.ecsp.auth.lib.obsever.DeviceStateDeactivation;
import org.eclipse.ecsp.auth.lib.obsever.DeviceStateObserver;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationResponse;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceAssociationDeviceStateObserver.
 */
@Slf4j
public class DeviceAssociationDeviceStateObserverTest {
    public static final int EXPECTED = 2;

    @InjectMocks
    private DeviceAssociationDeviceStateObserver deviceAssociationDeviceStateObserver;

    @Mock
    private HcpRestClientLibrary hcpRestClientLibrary;

    @Mock
    private EnvConfig<AuthProperty> envConfig;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void notifyActivationTest1() {
        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setPasscode("24828rh2yr2");
        activationResponse.setDeviceId("HUEUE6283681");
        DeviceStateActivation deviceStateActivation = new DeviceStateActivation();
        deviceStateActivation.setActivationResponse(activationResponse);
        deviceStateActivation.setSwVersion("1.2.1");
        deviceStateActivation.setHwVersion("1.4.1");
        deviceStateActivation.setSerialNumber("523749811223666");
        deviceStateActivation.setDeviceType("dongle");
        int type = DeviceStateObserver.TYPE_DEVICE_ACTIVATION;
        Mockito.doReturn(null).when(hcpRestClientLibrary)
            .doPost(Mockito.any(), Mockito.any(), Mockito.anyObject(), Mockito.any());
        deviceAssociationDeviceStateObserver.notify(type, deviceStateActivation);
        Assertions.assertEquals(1, type);
    }

    @Test
    public void notifyActivationTest2() {
        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setPasscode("24828rh2yr2");
        activationResponse.setDeviceId("HUEUE6283681");
        DeviceStateActivation deviceStateActivation =
            new DeviceStateActivation(activationResponse, "1.2.1", "1.4.1", "523749811223666", "dongle", false);
        deviceStateActivation.getActivationResponse();
        deviceStateActivation.getSwVersion();
        deviceStateActivation.getHwVersion();
        deviceStateActivation.getSerialNumber();
        deviceStateActivation.getDeviceType();
        deviceStateActivation.isReactivationFlag();
        log.info("Device Activation details: " + deviceStateActivation);
        int type = DeviceStateObserver.TYPE_DEVICE_ACTIVATION;
        Mockito.doReturn(null).when(hcpRestClientLibrary)
            .doPost(Mockito.any(), Mockito.any(), Mockito.anyObject(), Mockito.any());
        deviceAssociationDeviceStateObserver.notify(type, deviceStateActivation);
        Assertions.assertEquals(1, type);
    }

    @Test
    public void notifyDeactivationTest1() {
        DeviceStateDeactivation deviceStateDeactivation = new DeviceStateDeactivation();
        deviceStateDeactivation.setSerialNumber("523749811223666");
        deviceStateDeactivation.setHarmanId("HUEUE6283681");
        int type = DeviceStateObserver.TYPE_DEVICE_DEACTIVATION;
        Mockito.doReturn(null).when(hcpRestClientLibrary)
            .doPost(Mockito.any(), Mockito.any(), Mockito.anyObject(), Mockito.any());
        deviceAssociationDeviceStateObserver.notify(type, deviceStateDeactivation);
        Assertions.assertEquals(EXPECTED, type);
    }

    @Test
    public void notifyDeactivationTest2() {
        DeviceStateDeactivation deviceStateDeactivation =
            new DeviceStateDeactivation("HUEUE6283681", "523749811223666");
        deviceStateDeactivation.getHarmanId();
        deviceStateDeactivation.getSerialNumber();
        log.info("Device Deactivation details: " + deviceStateDeactivation);
        int type = DeviceStateObserver.TYPE_DEVICE_DEACTIVATION;
        Mockito.doReturn(null).when(hcpRestClientLibrary)
            .doPost(Mockito.any(), Mockito.any(), Mockito.anyObject(), Mockito.any());
        deviceAssociationDeviceStateObserver.notify(type, deviceStateDeactivation);
    }

    @Test
    public void notifyDeactivationTest3() {
        int type = 0;
        DeviceStateDeactivation deviceStateDeactivation =
            new DeviceStateDeactivation("HUEUE6283681", "523749811223666");
        log.info("Device Deactivation details: " + deviceStateDeactivation);
        Mockito.doReturn(null).when(hcpRestClientLibrary)
            .doPost(Mockito.any(), Mockito.any(), Mockito.anyObject(), Mockito.any());
        deviceAssociationDeviceStateObserver.notify(type, deviceStateDeactivation);
    }
}