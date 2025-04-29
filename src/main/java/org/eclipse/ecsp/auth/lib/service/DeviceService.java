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

package org.eclipse.ecsp.auth.lib.service;

import org.eclipse.ecsp.common.config.EnvConfig;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.auth.lib.config.AuthProperty;
import org.eclipse.ecsp.auth.lib.dao.DeviceFactoryData;
import org.eclipse.ecsp.auth.lib.dao.DeviceFactoryDataDao;
import org.eclipse.ecsp.auth.lib.dao.DeviceInfoSharedDao;
import org.eclipse.ecsp.auth.lib.obsever.DefaultDeviceStateChangeObservable;
import org.eclipse.ecsp.auth.lib.obsever.DeviceStateActivation;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationResponse;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationStateRequest;
import org.eclipse.ecsp.auth.lib.rest.model.DeactivationRequestData;
import org.eclipse.ecsp.auth.lib.rest.support.ActivationFailException;
import org.eclipse.ecsp.auth.lib.rest.support.DeactivationFailException;
import org.eclipse.ecsp.auth.lib.rest.support.SpringAuthTokenGenerator;
import org.eclipse.ecsp.auth.lib.util.DeviceActivationUtil;
import org.eclipse.ecsp.auth.lib.validate.DeviceValidator;
import org.eclipse.ecsp.auth.lib.validate.DeviceValidatorFactory;
import org.eclipse.ecsp.auth.lib.validate.DeviceValidatorFactory.OemEnvironment;
import org.eclipse.ecsp.notification.lib.model.nc.UserProfile;
import org.eclipse.ecsp.notification.lib.rest.NotificationCenterClient;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.eclipse.ecsp.services.device.dao.DeviceDao;
import org.eclipse.ecsp.services.device.model.Device;
import org.eclipse.ecsp.services.deviceactivation.dao.DeviceActivationStateDao;
import org.eclipse.ecsp.services.deviceactivation.model.DeviceActivationState;
import org.eclipse.ecsp.services.factorydata.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryData;
import org.eclipse.ecsp.services.factorydata.domain.DeviceState;
import org.eclipse.ecsp.services.shared.dao.HcpInfoDao;
import org.eclipse.ecsp.services.shared.db.HcpInfo;
import org.eclipse.ecsp.springauth.client.rest.SpringAuthRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.naming.directory.InvalidAttributeValueException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class represents the Device Service that handles device-related operations.
 */
@Component
@Transactional
public class DeviceService {

    private static final int RETURN_VALUE = -1;
    public static final String DEVICE_ATTRIBUTE_NAME_HW_VERSION = "HW-Version";
    public static final String DEVICE_ATTRIBUTE_NAME_SW_VERSION = "SW-Version";
    public static final String HCP_AUTH_QUALIFIER_SECRET_KEY = "hcp_auth_qualifier_secret_key";
    public static final String SIM_TRANSACTION_STATE_COMPLETED = "Completed";
    public static final String COUNTRY = "Country";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String MANUFACTURER = "Manufacturer";
    public static final String MODEL = "Model";
    public static final String BODY_TYPE = "Bodytype";
    public static final String SERIES = "Series";
    public static final String VEHICLE_TYPE = "Vehicletype";
    public static final String DEVICE = "device";
    public static final String ACTIVATION_RESPONSE = "activationResponse";
    public static final String UPDATED_PASSCODE =
        "## Updated new pass code into DB successfully, SerialNumber: {}, IMEI: {}";
    private static final String CONFIG_PROPERTY_DEVICE_VALIDATION_ENABLE = "ENABLE";
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    @Autowired
    @Lazy
    protected SpringAuthTokenGenerator springAuthTokenGenerator;
    @Autowired
    SpringAuthRestClient springAuthRestClient;
    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private HcpInfoDao hcpInfoDao;
    @Autowired
    private DeviceInfoSharedDao deviceInfoDao;
    @Autowired
    private DeviceValidatorFactory deviceValidatorFactory;
    @Autowired
    private EnvConfig<AuthProperty> envConfig;
    @Autowired
    private HcpRestClientLibrary hcpRestClientLibrary;
    @Autowired
    private DefaultDeviceStateChangeObservable deviceStateChangeObservable;
    @Autowired
    private DeviceActivationStateDao deviceActivationStateDao;
    @Autowired
    private DeviceFactoryDataDao factoryDataDao;
    @Autowired
    private DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;
    @Autowired
    private NotificationCenterClient ncClient;
    @Value("${notification_center_base_url}")
    private String ncBaseUrl;
    @Value("${activate_notification_id:Activate}")
    private String notificationId;

    /**
     * Checks if the device is valid.
     *
     * @return true if the device is valid, false otherwise.
     */
    private boolean isDeviceValid() {
        String configuredValidationProperty = envConfig
            .getStringValue(AuthProperty.ENABLE_DEVICE_VALIDATION).trim();
        String oemEnvironment = envConfig.getStringValue(AuthProperty.OEM_ENVIRONMENT).trim();
        if (StringUtils.isBlank(configuredValidationProperty) || StringUtils.isBlank(oemEnvironment)
            || !configuredValidationProperty.equalsIgnoreCase(CONFIG_PROPERTY_DEVICE_VALIDATION_ENABLE)) {
            LOGGER.debug("OEM specific validation seems to be disabled.. property value: {}",
                configuredValidationProperty);
            return true;
        }
        OemEnvironment environment = OemEnvironment.valueOf(oemEnvironment.toUpperCase());

        DeviceValidator deviceValidator = deviceValidatorFactory
            .getInstance(environment);
        return deviceValidator != null && deviceValidator.isDeviceValid();
    }

    /**
     * Pre-activates VINs for a given temporary group ID.
     *
     * @param tempGroupId The temporary group ID.
     */
    public void preActivateVins(long tempGroupId) {
        List<String> vins = hcpInfoDao.getVinsToPreactivate(tempGroupId);
        LOGGER.info("preActivateVins: tempGroupId = {} : vins = {}", tempGroupId, vins);
        for (String vin : vins) {

            String passcode = DeviceActivationUtil.getPasscode();
            Device newDevice = new Device(null, null, passcode, RETURN_VALUE);
            deviceDao.insert(newDevice, true); // Insert record and get
            // auto-generated primary key
            String harmanId = DeviceActivationUtil.generateDeviceId("HU", newDevice.getId());
            // Using
            // auto-generated
            // primary
            // key
            // to
            // generate
            // HarmanId

            long hcpInfoId = hcpInfoDao.insert(vin, null); // Insert data into
            // HCPInfo

            deviceDao.updateDevice(harmanId, newDevice.getId()); // Update the
            // record in
            // Device table
            // with the
            // generated
            // HarmanId
            hcpInfoDao.updateHarmanId(harmanId, hcpInfoId); // Update the record
            // in HCPInfo table
            // with the
            // generated
            // HarmanId
            hcpInfoDao.updateTempDeviceGroup(harmanId, vin, tempGroupId);
            LOGGER.info("Pre-activated device with VIN = {}. Assigned HarmanID = {}", vin, harmanId);
        }

    }

    /**
     * Maps HarmanIds for VINs in a given temporary group ID.
     *
     * @param tempGroupId The temporary group ID.
     * @return true if the mapping is successful, false otherwise.
     */
    public boolean mapHarmanIdsForVins(long tempGroupId) {
        boolean isSuccess = true;
        try {
            hcpInfoDao.mapHarmanIdsForVins(tempGroupId);
            hcpInfoDao.getTempGroupSize(tempGroupId);
        } catch (Exception e) {
            LOGGER.error("Error in mapHarmanIdsForVins", e);
            isSuccess = false;
        }
        return isSuccess;

    }

    /**
     * Sets the device ready for activation.
     *
     * @param readyToActivateRequest The activation state request.
     * @param userId The user ID.
     */
    public void setReadyToActivate(ActivationStateRequest readyToActivateRequest, String userId) {

        deactivate(readyToActivateRequest.getSerialNumber(), userId);
        DeviceActivationState deviceActivationState = new DeviceActivationState();
        deviceActivationState.setActivationInitiatedBy(userId);
        deviceActivationState.setActivationInitiatedOn(new Timestamp(System.currentTimeMillis()));
        deviceActivationState.setActivationReady(true);
        deviceActivationState.setSerialNumber(readyToActivateRequest.getSerialNumber());
        deviceActivationStateDao.insert(deviceActivationState);
    }

    /**
     * Retrieves the DeviceFactoryData based on the ordered map of attributes and values.
     *
     * @param orderedMap The ordered map of attributes and values.
     * @return The DeviceFactoryData object.
     */
    public DeviceFactoryData getDeviceInfoFactoryData(LinkedHashMap<String, Object> orderedMap) {
        DeviceFactoryData deviceInfoFactoryData = factoryDataDao.find(orderedMap);
        LOGGER.debug("getDeviceInfoFactoryData:deviceInfoFactoryData:{}", deviceInfoFactoryData);
        return deviceInfoFactoryData;
    }

    /**
     * Retrieves the DeviceFactoryData based on the activation request data.
     *
     * @param activationRequestData The activation request data.
     * @return The DeviceFactoryData object.
     */
    private DeviceFactoryData getDeviceInfoFactoryData(ActivationRequestData activationRequestData) {
        LinkedHashMap<String, Object> attributeValueMap = new LinkedHashMap<>();
        if (activationRequestData.getImei() != null) {
            attributeValueMap.put("imei", activationRequestData.getImei());
        }
        if (activationRequestData.getSerialNumber() != null) {
            attributeValueMap.put("serial_number", activationRequestData.getSerialNumber());
        }
        if (activationRequestData.getBssid() != null) {
            attributeValueMap.put("bssid", activationRequestData.getBssid());
        }

        DeviceFactoryData factoryData = null;
        if (attributeValueMap.size() > 0) {
            factoryData = getDeviceInfoFactoryData(attributeValueMap);
        } else {
            throw new ActivationFailException("One of imei, ssid or serial_number must be passed.");
        }
        LOGGER.debug("Fetching difd with map: {}", attributeValueMap);
        LOGGER.debug("getDeviceInfoFactoryData():difd: {}", factoryData);
        return factoryData;
    }

    /**
     * Deactivates a device with the given serial number and user ID.
     *
     * @param serialNumber The serial number of the device.
     * @param userId The user ID.
     */
    public void deactivate(String serialNumber, String userId) {
        deviceDao.deactivate(serialNumber);
        disableActivationStateForDevice(serialNumber, userId);
    }

    /**
     * Disables the activation state for a device with the given serial number and user ID.
     *
     * @param serialNumber The serial number of the device.
     * @param userId The user ID.
     */
    private void disableActivationStateForDevice(String serialNumber, String userId) {
        List<Long> activeRecordIdList = deviceActivationStateDao.findActiveDevices(serialNumber);
        LOGGER.error("disableActivationStateForDevice serialNUmber:{}", serialNumber);
        int count = 0;
        if (activeRecordIdList != null && !activeRecordIdList.isEmpty()) {
            LOGGER.error("activeRecordlists is not empty for serial_number: {}", serialNumber);
            for (long activeRecordId : activeRecordIdList) {
                if (activeRecordId > 0) {
                    LOGGER.error("Going to deactivate activerecordId: {} ,serialNumber: {}", activeRecordId,
                        serialNumber);
                    deviceActivationStateDao.disableRecord(activeRecordId, userId);
                    LOGGER.debug("Deactivated successfully: {}", activeRecordId);
                    count++;
                }
            }
        }
        LOGGER.error("deactivated all successfully: {} for serialNumber: {}", count, serialNumber);

    }

    /**
     * Disables the activation state for a device with the given factory ID and user ID.
     *
     * @param factoryId The factory ID of the device.
     * @param userId The user ID.
     */
    private void disableActivationStateForDevice(long factoryId, String userId) {
        deviceActivationStateDao.disableActivationReady(factoryId, userId);
    }

    /**
     * Performs a health check on the device.
     *
     * @return The health check result.
     */
    public int healthCheck() {
        return deviceDao.healthCheck();
    }

    /**
     * Deactivates the account based on the deactivation request data and user ID.
     *
     * @param deactivationRequestData The deactivation request data.
     * @param userId The user ID.
     * @throws InvalidAttributeValueException If the attribute value is invalid.
     */
    public void deactivateAccount(DeactivationRequestData deactivationRequestData, String userId)
        throws InvalidAttributeValueException {
        LOGGER.info("## deactivateAccount - Service START");
        Long factoryId = null;
        if (StringUtils.isNumeric(deactivationRequestData.getFactoryId())) {
            factoryId = Long.parseLong(deactivationRequestData.getFactoryId());
        } else {
            throw new DeactivationFailException("No factory id or Invalid factory id passed");
        }

        DeviceInfoFactoryData factoryData = deviceInfoFactoryDataDao.findByFactoryId(factoryId);
        if (factoryData == null) {
            throw new DeactivationFailException(
                "Device details is not present in the Factory data for the passed factoryID");
        }

        disableActivationStateForDevice(factoryId, userId);

        LOGGER.info("Deleting the device details in HCPInfo by factoryId {} ", factoryId);
        hcpInfoDao.deleteByFactoryId(factoryId);

        // Update the factory data state to provisioned
        deviceInfoFactoryDataDao.updateFactoryDataState(factoryId, DeviceState.PROVISIONED.toString());
        DeviceInfoFactoryData deviceFactoryData = deviceInfoFactoryDataDao.findByFactoryId(factoryId);
        deviceFactoryData.setState(DeviceState.DEACTIVATED.toString());
        deviceInfoFactoryDataDao.createHistoryTableEntry(deviceFactoryData, "Device deactivated");

        // Fetch the data from HCPInfo table for the passed factoryId
        HcpInfo hcpInfo = hcpInfoDao.findByFactoryId(factoryId);
        if (hcpInfo != null) {
            // Update Device and device_activation_state with isActive = false
            // for the fetched HarmanID
            final String harmanId = hcpInfo.getHarmanId();
            deviceDao.deactivateHarmanId(harmanId);
            LOGGER.info("Deleting the device details in DeviceInfo table for harmanID - {}", harmanId);
            deviceInfoDao.deleteByHarmanId(harmanId);
            String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
            springAuthRestClient.deleteRegisteredClient(authToken, harmanId);
        } else {
            LOGGER.info("No active device found for the passed factoryId");
        }
        LOGGER.info("## Completed deactivation from auth");
        LOGGER.info("## deactivateAccount - Service END");
    }

    /**
     * Activates a device based on the provided activation request data.
     *
     * @param activationRequestData The activation request data containing the necessary information for device
     *                              activation.
     * @return The activation response indicating the result of the device activation.
     * @throws ActivationFailException If an error occurs during the device activation process.
     */
    public ActivationResponse activateDevice(ActivationRequestData activationRequestData) {
        String vin;
        String serialNumber;
        String secretKey;
        ActivationResponse activationResponse = new ActivationResponse();
        long randomNumber;
        Device newDevice = null;
        boolean isProvisionAlive = false;
        boolean vinEnabledFlag = envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        validateRequest(activationRequestData);
        try {
            DeviceFactoryData deviceInfoFactoryData = this.getDeviceInfoFactoryData(activationRequestData);
            verifyFactoryData(deviceInfoFactoryData);
            validateDevice();
            serialNumber = deviceInfoFactoryData.getSerialNumber();
            LOGGER.info("## vinEnabledFlag: {} for serialNumber: {}", vinEnabledFlag, serialNumber);
            verifyVinEnabledFlag(vinEnabledFlag, serialNumber);
            vin = activationRequestData.getVin();
            secretKey = getSecretKey();
            // Story 546176, 550679, 554403 - Enabled AAD Authentication method (MAC)
            randomNumber = DeviceActivationUtil.checkQualifier(vin, serialNumber, activationRequestData.getQualifier(),
                secretKey, activationRequestData.getAad());
            verifyRandomNumber(randomNumber);
            String imei = deviceInfoFactoryData.getImei();
            //The purpose of this variable to control to send sms to user only during first time activation.
            boolean isFirstTimeActivation = false;
            if (isDeviceReadyToActivate(deviceInfoFactoryData)) {
                //At this level: we have done user association before and now we going to activate device.
                isFirstTimeActivation = true;
                newDevice = performFirstActivation(activationRequestData, randomNumber, deviceInfoFactoryData,
                    serialNumber, imei);
            } else if (isDeviceActive(deviceInfoFactoryData)) {
                //At this level:  Device is already Active and client(Ignite client) is activating device again
                //In this case we will just refresh passcode and harmanId would remain same
                LOGGER.debug("Inside isDeviceActive");
                newDevice = performReActivation(deviceInfoFactoryData, serialNumber, imei);
            } else if (!isDeviceStolenOrFaulty(deviceInfoFactoryData)
                && !deviceInfoFactoryData.getState().equalsIgnoreCase(DeviceState.PROVISIONED_ALIVE.toString())) {
                //At this level: Device is in PROVISION STATE and client (Ignite client ) is calling activate device and
                // Device status will go into PROVISION_ALIVE
                /* When device is connected without activating, this scenario is applicable. Device should be in
                provisioned state and not stolen/faulty. */
                LOGGER.debug("Performing state update when device is not active ");
                updateFactoryData(deviceInfoFactoryData);
                isProvisionAlive = true;
                activationResponse.setProvisionedAlive(Boolean.TRUE);
                LOGGER.info("## Device state updated without User Association. Hence device state become"
                    + " PROVISION_ALIVE, SerialNumber: {}, IMEI: {}", serialNumber, imei);
            } else {
                throw new ActivationFailException("Device is in invalid state to activate");
            }
            activationResponse = verifyProvisionAlive(isProvisionAlive, activationResponse, newDevice,
                activationRequestData, serialNumber, isFirstTimeActivation);
        } catch (DuplicateKeyException exception) {
            throw new ActivationFailException("Duplicate activation request!");
        } catch (ActivationFailException activationFailException) {
            throw new ActivationFailException(activationFailException.getMessage());
        } catch (Exception exception) {
            throw new ActivationFailException(
                "Error occurred while performing device activation, Please contact admin ", exception);
        }
        LOGGER.info("## Device activation completed successfully.");
        return activationResponse;
    }

    /**
     * Verifies the factory data of a device.
     *
     * @param deviceInfoFactoryData The factory data of the device to be verified.
     * @throws ActivationFailException If the device details are not in the inventory.
     */
    private void verifyFactoryData(DeviceFactoryData deviceInfoFactoryData) {
        if (null == deviceInfoFactoryData) {
            throw new ActivationFailException("Device details are not in inventory");
        }
    }

    /**
     * Validates the device.
     *
     * @throws ActivationFailException if the device is invalid.
     */
    private void validateDevice() {
        if (!isDeviceValid()) {
            throw new ActivationFailException("Invalid device.");
        }
    }

    /**
     * Verifies the VIN enabled flag and performs VIN association if the flag is enabled.
     *
     * @param vinEnabledFlag  the flag indicating whether VIN is enabled or not
     * @param serialNumber    the serial number of the device
     */
    private void verifyVinEnabledFlag(boolean vinEnabledFlag, String serialNumber) {
        if (vinEnabledFlag) {
            performVinAssociation(serialNumber);
        }
    }

    /**
     * Verifies the given random number.
     *
     * @param randomNumber the random number to be verified
     * @throws ActivationFailException if the random number is less than 0
     *         Possible cause: static secretKey (hcp_auth_qualifier_secret_key) is missing in vault.
     *         Please contact admin.
     */
    private void verifyRandomNumber(long randomNumber) {
        if (randomNumber < 0) {
            throw new ActivationFailException("During random number generation, validation failed for qualifier. "
                + "Possible cause: static secretKey (hcp_auth_qualifier_secret_key) is missing in vault. "
                + "Please contact admin.");
        }
    }

    /**
     * Verifies if the provision is alive and performs state change and user notification if it is not.
     *
     * @param isProvisionAlive        a boolean indicating if the provision is alive
     * @param activationResponse      the activation response object
     * @param newDevice               the new device object
     * @param activationRequestData   the activation request data object
     * @param serialNumber            the serial number of the device
     * @param isFirstTimeActivation   a boolean indicating if it is the first time activation
     * @return the activation response object
     */
    private ActivationResponse verifyProvisionAlive(boolean isProvisionAlive, ActivationResponse activationResponse,
                                                    Device newDevice, ActivationRequestData activationRequestData,
                                                    String serialNumber, boolean isFirstTimeActivation) {
        if (!isProvisionAlive) {
            activationResponse = performStateChangeAndNotifyUser(activationResponse, newDevice,
                activationRequestData, serialNumber, isFirstTimeActivation);
            return activationResponse;
        }
        return activationResponse;
    }

    /**
     * Retrieves the secret key used for device activation.
     *
     * @return The secret key as a string.
     */
    private String getSecretKey() {
        return envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY).trim();
    }

    /**
     * Performs reactivation of a device.
     *
     * @param deviceInfoFactoryData The device factory data.
     * @param serialNumber The serial number of the device.
     * @param imei The IMEI number of the device.
     * @return The reactivated device.
     * @throws InvalidAttributeValueException If there is an invalid attribute value.
     * @throws ActivationFailException If the reactivation fails.
     */
    private Device performReActivation(DeviceFactoryData deviceInfoFactoryData, String serialNumber,
                                                    String imei)
        throws InvalidAttributeValueException {
        Device newDevice = null;

        List<Device> deviceList = deviceDao.findActiveDevice(deviceInfoFactoryData.getId());
        if (deviceList == null || deviceList.size() > 1) {
            throw new ActivationFailException("Something is not right . Please contact admin");
        }
        newDevice = deviceList.get(0);
        LOGGER.debug("## newDevice: {}", newDevice);
        String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
        springAuthRestClient.deleteRegisteredClient(authToken, newDevice.getHarmanId());
        // Generate new passcode
        String passcode = DeviceActivationUtil.getPasscode();
        newDevice.setPasscode(passcode);
        springAuthRestClient.updateRegisteredClient(authToken, newDevice.getHarmanId(), newDevice.getPasscode(),
            "Dongle", "approved");
        LOGGER.info(
            "## updated the status of the application with Spring Auth successfully, SerialNumber: {}, IMEI: {}",
            serialNumber, imei);
        deviceDao.updatePasscode(newDevice);
        LOGGER.info(UPDATED_PASSCODE, serialNumber, imei);

        return newDevice;
    }

    /**
     * Performs the state change of a device and notifies the user about the activation.
     *
     * @param activationResponse    The activation response object.
     * @param newDevice             The new device object.
     * @param activationRequestData The activation request data.
     * @param serialNumber          The serial number of the device.
     * @param isFirstTimeActivation Flag indicating if it's the first time activation.
     * @return The activation response object.
     */
    private ActivationResponse performStateChangeAndNotifyUser(ActivationResponse activationResponse, Device newDevice,
                                                               ActivationRequestData activationRequestData,
                                                               String serialNumber, boolean isFirstTimeActivation) {
        if (activationResponse == null) {
            activationResponse = new ActivationResponse();
        }
        activationResponse.setDeviceId(newDevice.getHarmanId());
        activationResponse.setPasscode(newDevice.getPasscode());

        //Here in this method: callActivationObservable -
        //We are calling Association component's - stateChange api to change Device status to ACTIVE via Rest API call
        callActivationObservable(new DeviceStateActivation(activationResponse, activationRequestData.getSwVersion(),
            activationRequestData.getHwVersion(), serialNumber, null, false));

        LOGGER.debug("Activation response: {}", activationResponse);

        //After successful activation- Send SMS notification to user
        //Pre-requisite: User profile must be created -  Normally user profile gets created after User sign up
        // operation by User.
        String associatedUserId = deviceActivationStateDao.getAssociatedUserId(serialNumber);
        if (StringUtils.isNotEmpty(associatedUserId) && isFirstTimeActivation) {
            UserProfile userProfile = ncClient.getUserProfile(associatedUserId, ncBaseUrl);
            if (!ObjectUtils.isEmpty(userProfile)) {
                ncClient.callNotifCenterNonRegisteredUserApi(userProfile, ncBaseUrl, notificationId);
            } else {
                LOGGER.info(
                    "Unable to send sms notification to the user: {} while activating device, since userProfile"
                        + " is not present.", associatedUserId);
            }
        }
        return activationResponse;
    }

    /**
     * Updates the factory data for a device.
     *
     * @param deviceInfoFactoryData The device factory data to be updated.
     */
    private void updateFactoryData(DeviceFactoryData deviceInfoFactoryData) {
        LOGGER.debug("Enter updateFactoryData method");
        deviceInfoFactoryDataDao.changeDeviceState(deviceInfoFactoryData.getId(),
            DeviceState.PROVISIONED_ALIVE.toString(),
            DeviceState.PROVISIONED_ALIVE.toString());
        LOGGER.debug("Exit updateFactoryData method");
    }

    /**
     * Validates the activation request data.
     *
     * @param activationRequestData The activation request data to be validated.
     * @throws ActivationFailException If the activation request data is not sufficient to activate.
     */
    private void validateRequest(ActivationRequestData activationRequestData) {
        if (!validateActivationRequest(activationRequestData)) {
            throw new ActivationFailException("Not sufficient data is provided to activate");
        }
    }

    /**
     * Checks if the device is ready to activate.
     *
     * @param deviceInfoFactoryData The device factory data.
     * @return True if the device is ready to activate, false otherwise.
     */
    private boolean isDeviceReadyToActivate(DeviceFactoryData deviceInfoFactoryData) {
        return deviceInfoFactoryData.getState().equals(DeviceState.READY_TO_ACTIVATE.toString());
    }

    /**
     * Checks if a device is active.
     *
     * @param deviceInfoFactoryData the device factory data containing the device information
     * @return true if the device is active, false otherwise
     */
    private boolean isDeviceActive(DeviceFactoryData deviceInfoFactoryData) {
        return deviceInfoFactoryData.getState().equals(DeviceState.ACTIVE.toString());
    }

    /**
     * Checks if the device is stolen or faulty.
     *
     * @param deviceInfoFactoryData The device information factory data.
     * @return true if the device is stolen or faulty, false otherwise.
     */
    private boolean isDeviceStolenOrFaulty(DeviceFactoryData deviceInfoFactoryData) {
        return deviceInfoFactoryData.getFaulty() || deviceInfoFactoryData.getStolen();
    }

    /**
     * Updates the device information with the specified name and value for the given Harman ID.
     *
     * @param harmanId the Harman ID of the device
     * @param name the name of the device information to update
     * @param value the new value for the device information
     */
    private void updateDeviceInfo(String harmanId, String name, String value) {
        if (value != null && value.trim().length() > 0) {
            deviceInfoDao.updateDeviceInfo(harmanId, name, value);
        }
    }

    /**
     * Calls the activation observable to notify observers about device activation.
     *
     * @param deviceStateActivation The device state activation object.
     * @throws ActivationFailException If an exception occurs while notifying the observers about device activation.
     */
    private void callActivationObservable(DeviceStateActivation deviceStateActivation) {
        try {
            if (deviceStateActivation.getActivationResponse() != null) {
                LOGGER.debug("## deviceStateActivation: {}", deviceStateActivation);
                deviceStateChangeObservable.newDeviceActivated(deviceStateActivation);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while notifying about the observer about device activation.", e);
            throw new ActivationFailException(
                "Exception occurred while notifying the observers about device activation.");
        }
    }

    /**
     * Validates the activation request data.
     *
     * @param activationRequestData The activation request data to be validated.
     * @return {@code true} if the activation request data is valid, {@code false} otherwise.
     */
    private boolean validateActivationRequest(ActivationRequestData activationRequestData) {
        if (activationRequestData == null) {
            LOGGER.error("createAccount:activationRequestData can not be empty");
            return false;
        }
        if (StringUtils.isBlank(activationRequestData.getVin())
            || StringUtils.isBlank(activationRequestData.getQualifier())) {
            return false;
        }
        if (StringUtils.isNotEmpty(activationRequestData.getAad())
            && !(activationRequestData.getAad().equalsIgnoreCase("yes")
            || activationRequestData.getAad().equalsIgnoreCase("no"))) {
            LOGGER.error("aad can only be yes or no ");
            return false;
        }
        return true;
    }

    /**
     * Deactivates devices based on the provided factory ID and random number.
     *
     * @param factoryId   the ID of the factory
     * @param randomNumber   the random number associated with the devices
     */
    public void deactivateDevices(Long factoryId, Long randomNumber) {

    }

    /**
     * Creates a new Device object with the specified parameters.
     *
     * @param randomNumber The random number associated with the device.
     * @return A new Device object with the activation date, passcode, and random number.
     */
    private Device getNewDevice(long randomNumber) {
        Timestamp activationDate = new Timestamp(new Date().getTime());
        String passcode = DeviceActivationUtil.getPasscode();

        return new Device(null, activationDate, passcode, randomNumber);
    }

    /**
     * Updates all activation tables with the provided device, device factory data, and activation request data.
     *
     * @param newDevice              The new device to be inserted and updated.
     * @param difd                   The device factory data.
     * @param activationRequestData  The activation request data.
     */
    public void updateAllActivationTables(Device newDevice, DeviceFactoryData difd,
                                          ActivationRequestData activationRequestData) {
        // 2.33 Release - Sonar DLS_DEAD_LOCAL_STORE code smell fix
        deviceDao.insert(newDevice, false);
        newDevice.setHarmanId(DeviceActivationUtil.generateDeviceId("HU", newDevice.getId()));
        LOGGER.debug("newlygeneratedHarmanId : {}, imei : {} ", newDevice.getHarmanId(), difd.getImei());
        deviceDao.updateDevice(newDevice.getHarmanId(), newDevice.getId());
        long hcpInfoId = hcpInfoDao.insert(difd.getId(), difd.getSerialNumber(), activationRequestData.getVin());
        // 2.33 Release - Sonar DLS_DEAD_LOCAL_STORE code smell fix
        hcpInfoDao.updateHarmanId(newDevice.getHarmanId(), hcpInfoId);
        updateDeviceInfo(newDevice.getHarmanId(), DEVICE_ATTRIBUTE_NAME_HW_VERSION,
            activationRequestData.getHwVersion());
        updateDeviceInfo(newDevice.getHarmanId(), DEVICE_ATTRIBUTE_NAME_SW_VERSION,
            activationRequestData.getSwVersion());
        deviceInfoFactoryDataDao.changeDeviceState(difd.getId(), DeviceState.ACTIVE.toString(), "Device Activated");
    }

    /**
     * Performs the VIN association for the given serial number.
     *
     * @param serialNumber The serial number of the device.
     * @throws ActivationFailException If the VIN association or SIM activation is not completed.
     */
    private void performVinAssociation(String serialNumber) {
        String associatedVin = deviceActivationStateDao.getAssociatedVin(serialNumber);
        if (StringUtils.isNotEmpty(associatedVin)) {
            // get tran_status from sim_details table
            String tranStatus = deviceActivationStateDao.getSimTransactionStatus(serialNumber);
            LOGGER.info("## Transaction Status: {} for user associated Vin: {}", tranStatus, associatedVin);
            if (!SIM_TRANSACTION_STATE_COMPLETED.equals(tranStatus)) {
                throw new ActivationFailException(
                    "Sim Activation and Vin Association are mandatory before activation.");
            }
        } else {
            throw new ActivationFailException("Vin association is mandatory before activation");
        }
    }

    /**
     * Performs the first activation of a device.
     *
     * @param activationRequestData The activation request data.
     * @param randomNumber The random number.
     * @param deviceInfoFactoryData The device info factory data.
     * @param serialNumber The serial number of the device.
     * @param imei The IMEI number of the device.
     * @return The newly activated device.
     * @throws InvalidAttributeValueException If an invalid attribute value is encountered.
     */
    private Device performFirstActivation(ActivationRequestData activationRequestData, long randomNumber,
                                          DeviceFactoryData deviceInfoFactoryData, String serialNumber, String imei) {
        Device newDevice = null;
        LOGGER.debug("## Device is ready to activate with Device Factory Data: {}", deviceInfoFactoryData);
        LOGGER.info("## Device is ready to activate with Device Factory Data, SerialNumber: {}, IMEI: {}", serialNumber,
            imei);
        newDevice = getNewDevice(randomNumber);
        LOGGER.debug("## Got new device: {}", newDevice);
        updateAllActivationTables(newDevice, deviceInfoFactoryData, activationRequestData);
        LOGGER.info("## Device activation tables got updated successfully, SerialNumber: {}, IMEI: {}", serialNumber,
            imei);
        String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
        springAuthRestClient.createRegisteredClient(authToken, newDevice.getHarmanId(), newDevice.getPasscode(),
            "Dongle");
        LOGGER.info("## Device got registered with Spring Auth successfully, SerialNumber: {}, IMEI: {}",
            serialNumber, imei);
        return newDevice;
    }
}
