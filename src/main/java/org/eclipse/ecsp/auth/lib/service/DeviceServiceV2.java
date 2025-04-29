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
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestDataToDeviceInfoAdapter;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationRequestDataV2;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationResponse;
import org.eclipse.ecsp.auth.lib.rest.model.PreSharedKeyResponse;
import org.eclipse.ecsp.auth.lib.rest.support.ActivationFailException;
import org.eclipse.ecsp.auth.lib.rest.support.SpringAuthTokenGenerator;
import org.eclipse.ecsp.auth.lib.util.CryptographyUtil;
import org.eclipse.ecsp.auth.lib.util.DeviceActivationUtil;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiResourceNotFoundException;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.eclipse.ecsp.notification.lib.model.nc.UserProfile;
import org.eclipse.ecsp.notification.lib.rest.NotificationCenterClient;
import org.eclipse.ecsp.services.device.dao.DeviceDao;
import org.eclipse.ecsp.services.device.model.Device;
import org.eclipse.ecsp.services.deviceactivation.dao.DeviceActivationDao;
import org.eclipse.ecsp.services.deviceactivation.dao.DeviceActivationStateDao;
import org.eclipse.ecsp.services.deviceactivation.model.DeviceActivation;
import org.eclipse.ecsp.services.factorydata.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.services.factorydata.domain.DeviceState;
import org.eclipse.ecsp.services.shared.dao.HcpInfoDao;
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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.ACTIVATION_CHECK;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.AUTH_INPUT_VALIDATION_FAILED;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.DEVICE_DETAILS_NOT_FOUND;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.DEVICE_INVALID_STATE;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.DEVICE_NOT_FOUND;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.GENERAL_ERROR;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.INVALID_DEVICE_TYPE;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.PRESHAREDKEY_VALIDATION_FAILED;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.QUALIFIER_VALIDATION_FAILED;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.UNKNOWN_ERROR;
import static org.eclipse.ecsp.auth.lib.enums.ApiMessageEnums.VIN_ASSO_MANDATORY;


/**
 * The DeviceServiceV2 class is responsible for handling device activation and related operations.
 * It provides methods to activate a device, validate device types, and perform reactivation.
 * This class is a Spring component and is transactional.
 */
@Component
@Transactional
public class DeviceServiceV2 {
    private static final int MAX_VALUE = 2;
    public static final String DEVICE_ATTRIBUTE_NAME_HW_VERSION = "HW-Version";
    public static final String DEVICE_ATTRIBUTE_NAME_SW_VERSION = "SW-Version";
    public static final String TRANSACTION_STATUS = "Active";
    public static final String DEVICE = "device";
    public static final String REACTIVATION_FLAG = "reactivationFlag";
    public static final String ACTIVATION_RESPONSE = "activationResponse";
    public static final String UPDATED_PASSCODE =
        "## Updated new pass code into DB successfully, SerialNumber: {}, IMEI: {}";
    public static final String REGISTRATION_STATUS = "approved";
    public static final String SECRET_KEY_FROM_VAULT =
            "## secretKey from vault for attribute - hcp_auth_qualifier_secret_key : {}";
    public static final int ALPHA_NUM_LENGTH = 16;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceServiceV2.class);
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
    private EnvConfig<AuthProperty> envConfig;
    @Autowired
    private DefaultDeviceStateChangeObservable deviceStateChangeObservable;
    @Autowired
    private DeviceActivationStateDao deviceActivationStateDao;
    @Autowired
    private DeviceFactoryDataDao factoryDataDao;
    @Autowired
    private DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;
    @Autowired
    private DeviceActivationDao deviceActivationDao;
    @Autowired
    private NotificationCenterClient ncClient;
    @Autowired
    private AssociationService associationService;
    @Autowired
    private CryptographyUtil cryptographyUtil;
    @Value("${notification_center_base_url}")
    private String ncBaseUrl;

    //Added the allowed_device_types variable - US 292046 and 292100
    @Value("#{'${allowed_device_types}'.split(',')}")
    private String[] allowedTypes;

    @Value("${activate_notification_id:Activate}")
    private String notificationId;

    @Value("${activation_failure_event_enabled:false}")
    private boolean activationFailureEventEnabled;

    @Value("#{'${allowed_device_types_for_act_failure}'.split(',')}")
    private String[] allowedTypesForActFailure;

    @Value("${activate_failure_event_id:ActivationFailure}")
    private String activationFailureEventId;

    @Value("${activate_failure_event_topic:activation}")
    private String activationFailureEventTopic;

    /**
     * Validates if the given device type is allowed based on the allowed types array.
     *
     * @param deviceType   the device type to validate
     * @param allowedTypes the array of allowed device types
     * @return true if the device type is allowed, false otherwise
     */
    public static boolean validateDeviceType(String deviceType, String[] allowedTypes) {
        for (String type : allowedTypes) {
            if (type.equals(deviceType)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Validates the device type based on the provided activation request data.
     *
     * @param activationRequestData The activation request data to validate.
     * @param isVersionV4           A flag indicating whether the version is V4 or not.
     * @throws ApiValidationFailedException If the activation request data is invalid or the device type is invalid.
     */
    private void validateDeviceType(ActivationRequestData activationRequestData, boolean isVersionV4) {
        if (!validateActivationRequest(activationRequestData)) {
            throw new ApiValidationFailedException(AUTH_INPUT_VALIDATION_FAILED.getCode(),
                AUTH_INPUT_VALIDATION_FAILED.getMessage(),
                AUTH_INPUT_VALIDATION_FAILED.getGeneralMessage());
        }

        if (isVersionV4) {
            if (activationRequestData.getDeviceType() == null) {
                throw new ApiValidationFailedException(AUTH_INPUT_VALIDATION_FAILED.getCode(),
                    AUTH_INPUT_VALIDATION_FAILED.getMessage(),
                    AUTH_INPUT_VALIDATION_FAILED.getGeneralMessage());
            }
            if (!validateDeviceType(activationRequestData.getDeviceType(), allowedTypes)) {
                throw new ApiValidationFailedException(INVALID_DEVICE_TYPE.getCode(), INVALID_DEVICE_TYPE.getMessage(),
                    INVALID_DEVICE_TYPE.getGeneralMessage());
            }
        }
    }

    /**
     * Activates a device with the provided activation request data and version.
     *
     * @param activationRequestData The activation request data.
     * @param version              The version of the device.
     * @return The activation response.
     * @throws ApiValidationFailedException     If the device is in an invalid state.
     * @throws ApiResourceNotFoundException    If the device details are not found.
     * @throws ApiPreConditionFailedException   If a precondition for activation fails.
     * @throws ApiTechnicalException            If an unexpected technical error occurs.
     */
    public ActivationResponse activateDevice(ActivationRequestData activationRequestData, String version) {
        LOGGER.debug("## activateDevice - START - Version : {}", version);
        ActivationResponse activationResponse;
        boolean isVersionV4;
        boolean updateDeviceType = false;
        boolean reactivationFlag = false;
        isVersionV4 = validateVersion(version);
        validateDeviceType(activationRequestData, isVersionV4);
        //Adding validation for device type as part of US 292046 and 292100 - end
        try {
            DeviceFactoryData factoryData = findDeviceFactoryData(activationRequestData);
            String serialNumber = factoryData.getSerialNumber();
            Device newDevice = null;
            activationResponse = new ActivationResponse();
            boolean isProvisionAlive = false;
            //The purpose of this variable to control to send sms to user only during first time activation.
            boolean isFirstTimeActivation = false;
            String imei = factoryData.getImei();
            boolean vinEnabledFlag = envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
            String transactionId = getTransactionStatusForSimDetails(vinEnabledFlag, serialNumber);
            long randomNumber = generateRandomNumberForCreatingNewDevice(activationRequestData, serialNumber);

            updateDeviceType = setUpdateDeviceType(isVersionV4, factoryData, activationRequestData);

            if (isDeviceReadyToActivate(factoryData)) {
                // At this level: we have done user association before and now
                // we going to activate device.
                isFirstTimeActivation = true;
                newDevice = performFirstActivation(activationRequestData, randomNumber, factoryData, isVersionV4,
                    updateDeviceType, serialNumber, imei);
            } else if (isDeviceActive(factoryData)) {
                // At this level: Device is already Active and client(Ignite
                // client) is activating device again
                // In this case we will just refresh passcode and harmanId
                // would remain same
                LOGGER.debug("Inside isDeviceActive");
                Map<String, Object> reactivationMap =
                    performReActivation(factoryData, activationRequestData, isVersionV4, serialNumber, imei);
                activationResponse = (ActivationResponse) getDetailsFromMap(reactivationMap, ACTIVATION_RESPONSE);
                if (null != activationResponse) {
                    LOGGER.debug("activationResponse: {}", activationResponse);
                    return activationResponse;
                }
                newDevice = (Device) getDetailsFromMap(reactivationMap, DEVICE);
                reactivationFlag = (boolean) getDetailsFromMap(reactivationMap, REACTIVATION_FLAG);
            } else if (!isDeviceStolenOrFaulty(factoryData)
                && !factoryData.getState().equalsIgnoreCase(DeviceState.PROVISIONED_ALIVE.toString())) {
                // At this level: Device is in PROVISION STATE and client
                // (Ignite client ) is calling activate device and
                // Device status will go into PROVISION_ALIVE
                /*
                 * When device is connected with out activating it scenario
                 * is applicable. Device should be in provisioned state and
                 * not stolen/faulty.
                 */
                provisionAliveActivation(updateDeviceType, factoryData, activationRequestData, isVersionV4);
                isProvisionAlive = true;
                activationResponse.setProvisionedAlive(Boolean.TRUE);
            } else {
                deviceInvalidState(isVersionV4, activationRequestData);
            }
            activationResponse = verifyProvisionAlive(isProvisionAlive, newDevice, activationResponse,
                activationRequestData, reactivationFlag, transactionId, isFirstTimeActivation);
        } catch (DuplicateKeyException exception) {
            throw new ApiValidationFailedException(DEVICE_INVALID_STATE.getCode(), DEVICE_INVALID_STATE.getMessage(),
                DEVICE_INVALID_STATE.getGeneralMessage());
        } catch (ApiResourceNotFoundException exception) {
            throw new ApiResourceNotFoundException(DEVICE_DETAILS_NOT_FOUND.getCode(),
                DEVICE_DETAILS_NOT_FOUND.getMessage(), DEVICE_DETAILS_NOT_FOUND.getGeneralMessage());
        } catch (ApiPreConditionFailedException e) {
            throw new ApiPreConditionFailedException(e.getCode(), e.getMessage(), e.generalMessage());
        } catch (Exception e) {
            throw new ApiTechnicalException(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
                GENERAL_ERROR.getGeneralMessage(), e);
        }
        LOGGER.info("## Device activation completed successfully.");
        return activationResponse;
    }

    /**
     * Activates a device with the provided activation request data and version.
     *
     * @param request The activation request data
     * @param version The version of the device.
     * @return The activation response.
     * @throws ApiPreConditionFailedException If a precondition for activation fails.
     * @throws ApiResourceNotFoundException If a device not found or more than one device found.
     * @throws ApiTechnicalException If an unexpected error occurs.
     */
    public ActivationResponse activateDevice(ActivationRequestDataV2 request, String version) {
        LOGGER.debug("## activateDevice with JITActId passKey - START - {}", version);
        ActivationResponse activationResponse = null;

        try {
            String secretKey = envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY).trim();
            LOGGER.debug(SECRET_KEY_FROM_VAULT, secretKey);
            String jitActId = request.getJitActId();
            validatePreSharedKey(request, secretKey);
            LOGGER.info("preSharedKey validation is successful");
            long activeDeviceCount = deviceActivationDao.activeDeviceCount(jitActId);
            activationResponse = new ActivationResponse();
            String printJitActId = jitActId.replaceAll("[\n\r]", "_");
            if (activeDeviceCount == 0L) {
                // At this level: we have don't have any device activated with given jitact_id
                // we are going to activate device for the first time.
                LOGGER.debug("device is being activated for the first time");
                String passcode = DeviceActivationUtil.getPasscode();
                byte[] encryptedPasscode = cryptographyUtil.encrypt(secretKey,
                    passcode.getBytes(StandardCharsets.UTF_8));
                DeviceActivation activation = createNewDeviceActivation(request,
                    DatatypeConverter.printBase64Binary(encryptedPasscode));
                deviceActivationDao.insertDeviceActivation(activation);

                String harmanId = DeviceActivationUtil.generateDeviceId("HU", activation.getId());
                activation.setHarmanId(harmanId);
                deviceActivationDao.updateHarmanId(activation);
                LOGGER.info("## Device activation table got updated successfully, jitact_id : {} :",
                    printJitActId);

                String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
                springAuthRestClient.createRegisteredClient(authToken, activation.getHarmanId(), passcode,
                    "hu");
                LOGGER.info("## Device got registered with Spring Auth successfully, jitact_id : {} :",
                    printJitActId);
                activationResponse.setDeviceId(harmanId);
                activationResponse.setPasscode(passcode);
            } else if (activeDeviceCount == 1L) {
                // At this level: Device is already Active and client(Ignite
                // client) is activating device again
                // In this case we will just refresh passcode and harmanId
                // would remain same
                activationResponse = performReActivationV2(jitActId, secretKey);
            } else {
                // At this level if more than one device is found, So throwing the exception
                LOGGER.error(
                    "device for activation with jitact_id   :: {} is found multiple entry in device activation table",
                    printJitActId);
                throw new ApiResourceNotFoundException(DEVICE_NOT_FOUND.getCode(),
                    DEVICE_NOT_FOUND.getMessage(),
                    DEVICE_NOT_FOUND.getGeneralMessage());
            }

        } catch (ApiPreConditionFailedException e) {
            throw new ApiPreConditionFailedException(e.getCode(), e.getMessage(), e.generalMessage());
        } catch (ApiResourceNotFoundException e) {
            throw new ApiResourceNotFoundException(e.getCode(), e.getMessage(), e.getErrorMessage(), e);
        } catch (Exception e) {
            throw new ApiTechnicalException(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
                GENERAL_ERROR.getGeneralMessage(), e);
        }

        return activationResponse;
    }

    /**
     * Perform Reactivation of the device.
     *
     * @param jitActId jitact_id from the activation request.
     * @param secretKey SecretKey used for encrypt and decrypt of pass keys.
     * @return the activation response.
     * @throws InvalidAlgorithmParameterException if the specified algorithm parameters are invalid
     * @throws NoSuchPaddingException if the specified padding scheme is not available
     * @throws IllegalBlockSizeException if the block size of the cipher is invalid
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     * @throws BadPaddingException if the padding of the cipher is invalid
     * @throws InvalidKeyException if the specified key is invalid
     * @throws InvalidAttributeValueException if the attribute value if invalid
     */
    private ActivationResponse performReActivationV2(String jitActId, String secretKey)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAttributeValueException {
        String printJitActId = jitActId.replaceAll("[\n\r]", "_");
        LOGGER.debug("device for activation with jitact_id :: {} is ready to reactivate", printJitActId);

        List<DeviceActivation> deviceActivationList = deviceActivationDao.findActiveDevice(jitActId);
        DeviceActivation newDeviceActivation = null;
        if (deviceActivationList == null || deviceActivationList.size() > 1) {
            throw new ApiResourceNotFoundException(DEVICE_NOT_FOUND.getCode(),
                DEVICE_NOT_FOUND.getMessage(),
                DEVICE_NOT_FOUND.getGeneralMessage());
        }
        newDeviceActivation = deviceActivationList.get(0);
        String reActivationHarmanId = newDeviceActivation.getHarmanId();
        String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
        springAuthRestClient.deleteRegisteredClient(authToken, reActivationHarmanId);
        LOGGER.info(
            "## Scope, device and application deleted from Spring Auth successfully, jitact_id : {}",
            printJitActId);

        // Generate new passcode
        String passcode = DeviceActivationUtil.getPasscode();
        String encryptedPasscode = DatatypeConverter.printBase64Binary(
            cryptographyUtil.encrypt(secretKey, passcode.getBytes(StandardCharsets.UTF_8)));
        newDeviceActivation.setPasscode(encryptedPasscode);
        newDeviceActivation.setActivationDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        springAuthRestClient.updateRegisteredClient(authToken, reActivationHarmanId, passcode,
            "hu", REGISTRATION_STATUS);
        LOGGER.info(
            "## updated the status of the application with Spring Auth successfully, jitact_id : {}",
            printJitActId);

        deviceActivationDao.updatePasscode(newDeviceActivation);
        LOGGER.info("## Updated new pass code into DB successfully, jitact_id : {}", printJitActId);
        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setDeviceId(reActivationHarmanId);
        activationResponse.setPasscode(passcode);
        return activationResponse;
    }

    /**
     * Create new DeviceActivation object with the required fields set.
     *
     * @param requestData the activation request data
     * @return the DeviceActivation object created
     */
    private DeviceActivation createNewDeviceActivation(ActivationRequestDataV2 requestData, String passcode) {
        DeviceActivation activation = new DeviceActivation();
        activation.setJitactId(requestData.getJitActId());
        activation.setActivationDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        activation.setActive(true);
        activation.setDeviceType("hu");
        activation.setPasscode(passcode);
        activation.setHarmanId(null);
        return activation;
    }

    /**
     * Validating the passkey shared in the activation request with the passkey present in vault.
     *
     * @param requestData the activation request data
     */
    private void validatePreSharedKey(ActivationRequestDataV2 requestData, String secretKey) {
        try {
            String vaultPreSharedKey = envConfig.getStringValue(AuthProperty.HCP_ACTIVATION_PRESHARED_KEY);
            LOGGER.debug("## vaultPreSharedKey from vault for attribute - hcp_activation_preSharedKey : {}",
                vaultPreSharedKey);
            if (StringUtils.isBlank(secretKey) || StringUtils.isBlank(vaultPreSharedKey)) {
                LOGGER.error("Either hcp_auth_qualifier_secret_key or hcp_activation_preSharedKey "
                    + "is returned null from vault");
                throw new NullPointerException("Either hcp_auth_qualifier_secret_key or hcp_activation_preSharedKey "
                    + "is returned null from vault");
            } else {
                String vaultStringPreSharedKey = cryptographyUtil.decrypt(secretKey,
                    DatatypeConverter.parseBase64Binary(vaultPreSharedKey));
                String preSharedKeyfromRequest = cryptographyUtil.decrypt(secretKey,
                    DatatypeConverter.parseBase64Binary(requestData.getPassKey()));
                if (!(vaultStringPreSharedKey.equals(preSharedKeyfromRequest))) {
                    LOGGER.error("Valut preshared key and Request preshared keys are not equal");
                    throw new ApiPreConditionFailedException(PRESHAREDKEY_VALIDATION_FAILED.getCode(),
                        PRESHAREDKEY_VALIDATION_FAILED.getMessage(),
                        PRESHAREDKEY_VALIDATION_FAILED.getGeneralMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception while validating preSharedKey from vault and request");
            throw new ApiPreConditionFailedException(PRESHAREDKEY_VALIDATION_FAILED.getCode(),
                PRESHAREDKEY_VALIDATION_FAILED.getMessage(), PRESHAREDKEY_VALIDATION_FAILED.getGeneralMessage());
        }
    }

    

    /**
     * Validates the version string.
     *
     * @param version the version string to validate
     * @return true if the version is "v4", false otherwise
     */
    private boolean validateVersion(String version)  {
        boolean isVersionV4 = false;
        if (version.equals("v4")) {
            isVersionV4 = true;
            return isVersionV4;
        }
        return isVersionV4;
    }

    /**
     * Handles the case when the device is in an invalid state.
     *
     * @param isVersionV4            a boolean indicating whether the device version is V4 or not
     * @param activationRequestData  the activation request data
     * @throws ApiPreConditionFailedException if the device is in an invalid state
     */
    private void deviceInvalidState(boolean isVersionV4, ActivationRequestData activationRequestData) {
        if (isSendActivationFailureEvent(isVersionV4, activationRequestData)) {
            sendActivationFailureEvent(activationRequestData);
        }
        throw new ApiPreConditionFailedException(DEVICE_INVALID_STATE.getCode(),
            DEVICE_INVALID_STATE.getMessage(),
            DEVICE_INVALID_STATE.getGeneralMessage());
    }

    /**
     * Verifies if the provision is alive and performs necessary actions based on the result.
     *
     * @param isProvisionAlive         a boolean indicating if the provision is alive
     * @param newDevice                the Device object representing the new device
     * @param activationResponse       the ActivationResponse object to be updated
     * @param activationRequestData    the ActivationRequestData object containing the activation request data
     * @param reactivationFlag         a boolean indicating if it is a reactivation
     * @param transactionId            the transaction ID associated with the activation
     * @param isFirstTimeActivation    a boolean indicating if it is the first time activation
     * @return the updated ActivationResponse object
     */
    private ActivationResponse verifyProvisionAlive(boolean isProvisionAlive, Device newDevice,
                                                    ActivationResponse activationResponse,
                                                    ActivationRequestData activationRequestData,
                                                    boolean reactivationFlag, String transactionId,
                                                    boolean isFirstTimeActivation) {
        DeviceFactoryData factoryData = findDeviceFactoryData(activationRequestData);
        String serialNumber = factoryData.getSerialNumber();
        boolean vinEnabledFlag = envConfig.getBooleanValue(AuthProperty.VIN_ENABLED_FLAG);
        if (!isProvisionAlive) {
            activationResponse = performDeviceStateChange(newDevice, activationResponse, activationRequestData,
                serialNumber, factoryData, reactivationFlag);
            // After successful activation- Send SMS notification to user
            // Pre-requisite: User profile must be created - Normally user
            // profile gets created after User sign up operation by User.
            performNotifyUser(vinEnabledFlag, transactionId, isFirstTimeActivation, serialNumber);
            LOGGER.debug("activationResponse: {}", activationResponse);
            return activationResponse;
        }
        return activationResponse;
    }

    /**
     * Performs the alive activation process for a device.
     *
     * @param updateDeviceType       flag indicating whether to update the device type in the database
     * @param factoryData            the device factory data
     * @param activationRequestData  the activation request data
     * @param isVersionV4            flag indicating whether the version is V4
     */
    private void provisionAliveActivation(boolean updateDeviceType, DeviceFactoryData factoryData,
                                          ActivationRequestData activationRequestData,
                                          boolean isVersionV4) {
        String serialNumber = factoryData.getSerialNumber();
        String imei = factoryData.getImei();
        LOGGER.debug("Performing state update when device is not active ");
        // Update device type in postgres only if version is v4 as part of US 292046 and 292100
        performActivationForProvisionAliveDevice(updateDeviceType, factoryData, activationRequestData,
            isVersionV4, serialNumber, imei);
    }

    /**
     * Checks if the activation failure event should be sent.
     *
     * @param isVersionV4           a boolean indicating if the version is V4
     * @param activationRequestData the activation request data
     * @return true if the activation failure event should be sent, false otherwise
     */
    private boolean isSendActivationFailureEvent(boolean isVersionV4, ActivationRequestData activationRequestData) {

        return isVersionV4 && activationFailureEventEnabled
            && validateDeviceType(activationRequestData.getDeviceType(), allowedTypesForActFailure);
    }

    /**
     * Sends an activation failure event.
     *
     * @param activationRequestData The activation request data.
     */
    private void sendActivationFailureEvent(ActivationRequestData activationRequestData) {
        // 2.33 Release - Sonar CRLF_INJECTION_LOGS vulnerability fix
        String requestData = activationRequestData.toString().replaceAll("[\r\n]", "");
        LOGGER.error("## Sending ActivationFailureEvent :{}", requestData);
        boolean eventSentStatus = associationService.sendEventToTopic(
            new ActivationRequestDataToDeviceInfoAdapter(activationRequestData), activationFailureEventId,
            activationFailureEventTopic, activationRequestData.getImei());
        if (!eventSentStatus) {
            // 2.33 Release - Sonar CRLF_INJECTION_LOGS vulnerability fix
            LOGGER.error("## failed to send ActivationFailureEvent for data :{}", requestData);
        }

    }

    /**
     * Generates a random number for creating a new device.
     *
     * @param activationRequestData The activation request data.
     * @param serialNumber The serial number of the device.
     * @return The generated random number.
     * @throws ApiValidationFailedException If the qualifier validation fails.
     */
    private long generateRandomNumberForCreatingNewDevice(ActivationRequestData activationRequestData,
                                                          String serialNumber) {
        LOGGER.debug("## generateRandomNumberForCreatingNewDevice - START");
        String secretKey = envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY).trim();
        LOGGER.debug(SECRET_KEY_FROM_VAULT, secretKey);
        LOGGER.debug("## Value of secretKey must be - HarmanAct");
        String vin = activationRequestData.getVin();
        // Story 546176, 550679, 554403 - Enabled AAD Authentication method (MAC)
        long randomNumber =
            DeviceActivationUtil.checkQualifier(vin, serialNumber, activationRequestData.getQualifier(), secretKey,
                activationRequestData.getAad());
        if (randomNumber < 0) {
            throw new ApiValidationFailedException(QUALIFIER_VALIDATION_FAILED.getCode(),
                QUALIFIER_VALIDATION_FAILED.getMessage(),
                QUALIFIER_VALIDATION_FAILED.getGeneralMessage());
        }
        LOGGER.debug("## generateRandomNumberForCreatingNewDevice - END");
        return randomNumber;
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
     * Finds the device factory data based on the provided activation request data.
     *
     * @param activationRequestData The activation request data containing the IMEI, serial number, and BSSID.
     * @return The device factory data matching the activation request data.
     * @throws ApiResourceNotFoundException If the device details are not found.
     * @throws ActivationFailException If none of the IMEI, serial number, or BSSID is provided.
     */
    private DeviceFactoryData findDeviceFactoryData(ActivationRequestData activationRequestData) {
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
        DeviceFactoryData factoryData;
        if (attributeValueMap.size() > 0) {
            factoryData = getDeviceInfoFactoryData(attributeValueMap);
            if (factoryData == null) {
                throw new ApiResourceNotFoundException(DEVICE_DETAILS_NOT_FOUND.getCode(),
                    DEVICE_DETAILS_NOT_FOUND.getMessage(),
                    DEVICE_DETAILS_NOT_FOUND.getGeneralMessage());
            }
        } else {
            throw new ActivationFailException("One of imei, ssid or serial_number must be passed.");
        }
        LOGGER.debug("Fetching difd with map: {}", attributeValueMap);
        LOGGER.debug("getDeviceInfoFactoryData(): difd:{}", factoryData);
        return factoryData;
    }

    /**
     * Checks if the device is ready to activate.
     *
     * @param deviceInfoFactoryData The device factory data containing the device state.
     * @return true if the device is ready to activate, false otherwise.
     */
    private boolean isDeviceReadyToActivate(DeviceFactoryData deviceInfoFactoryData) {
        return deviceInfoFactoryData.getState().equals(DeviceState.READY_TO_ACTIVATE.toString());
    }

    /**
     * Checks if the device is active.
     *
     * @param deviceInfoFactoryData The device factory data containing the device information.
     * @return true if the device is active, false otherwise.
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
     * Calls the activation observable to notify the observers about device activation.
     *
     * @param deviceStateActivation The device state activation object.
     * @throws ActivationFailException If an exception occurs while notifying the observers.
     */
    private void callActivationObservable(DeviceStateActivation deviceStateActivation) {
        try {
            if (deviceStateActivation.getActivationResponse() != null) {
                LOGGER.debug("deviceStateActivation: {}", deviceStateActivation);
                deviceStateChangeObservable.newDeviceActivated(deviceStateActivation);
            }
        } catch (Exception e) {
            throw new ActivationFailException(
                "Exception occurred while notifying the observers about device activation.", e);
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
            LOGGER.error("aad can only be yes or no");
            return false;
        }
        return true;
    }

    /**
     * Creates a new device with the specified random number.
     *
     * @param randomNumber the random number for the device
     * @return the newly created device
     */
    private Device createNewDevice(long randomNumber) {
        Timestamp activationDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
        String passcode = DeviceActivationUtil.getPasscode();
        return new Device(null, activationDate, passcode, randomNumber);
    }

    /**
     * Updates all activation tables with the provided device information.
     *
     * @param newDevice              The new device to be inserted.
     * @param difd                   The device factory data.
     * @param activationRequestData  The activation request data.
     * @param isVersionV4            A boolean indicating if the version is V4.
     */
    public void updateAllActivationTables(Device newDevice, DeviceFactoryData difd,
                                          ActivationRequestData activationRequestData, boolean isVersionV4) {
        LOGGER.debug("## updateAllActivationTables - START ");
        deviceDao.insert(newDevice, false);
        boolean updateDeviceTypeReqd = false;
        //Generating prefix based on device type and update device type in postgres only if version is v4 as part of
        // US 292046 and 292100
        String prefix;
        if (isVersionV4) {
            prefix = generatePrefixByDeviceType(activationRequestData.getDeviceType());
            if (difd.getDeviceType() == null || !difd.getDeviceType().equals(activationRequestData.getDeviceType())) {
                updateDeviceTypeReqd = true;
            }
        } else {
            prefix = "HU";
        }
        LOGGER.debug("## Prefix Generated : {}", prefix);
        newDevice.setHarmanId(DeviceActivationUtil.generateDeviceId(prefix, newDevice.getId()));
        String harmanId = newDevice.getHarmanId();
        LOGGER.debug("## Newly Generated HarmanId: {}, imei: {} ", harmanId, difd.getImei());
        deviceDao.updateDevice(harmanId, newDevice.getId());
        long hcpInfoId = hcpInfoDao.insert(difd.getId(), difd.getSerialNumber(), activationRequestData.getVin());
        hcpInfoDao.updateHarmanId(harmanId, hcpInfoId);
        updateDeviceInfo(harmanId, DEVICE_ATTRIBUTE_NAME_HW_VERSION, activationRequestData.getHwVersion());
        updateDeviceInfo(harmanId, DEVICE_ATTRIBUTE_NAME_SW_VERSION, activationRequestData.getSwVersion());
        // Updating device type in postgres if version is v4 as part of US 292046 and 292100
        if (updateDeviceTypeReqd) {
            deviceInfoFactoryDataDao.updateDeviceType(difd.getId(), activationRequestData.getDeviceType(),
                "Device Activated");
            LOGGER.info("## Device type updated successfully in DB");
        }
        deviceInfoFactoryDataDao.changeDeviceState(difd.getId(), DeviceState.ACTIVE.toString(), "Device Activated");
        LOGGER.info("## Device activate - all tables are updated successfully.");
        LOGGER.debug("## updateAllActivationTables - END ");
    }

    /**
     * Generates a prefix based on the device type.
     *
     * @param deviceType the device type
     * @return the generated prefix
     */
    private String generatePrefixByDeviceType(String deviceType) {
        return deviceType.substring(0, MAX_VALUE).toUpperCase();
    }

    /**
     * Maps Harman IDs for VINs.
     *
     * @param tempGroupId The temporary group ID.
     * @return {@code true} if the mapping is successful, {@code false} otherwise.
     */
    public boolean mapHarmanIdsForVins(long tempGroupId) {
        boolean isSuccess = true;
        try {
            hcpInfoDao.mapHarmanIdsForVins(tempGroupId);
            hcpInfoDao.getTempGroupSize(tempGroupId);
        } catch (Exception e) {
            LOGGER.error("Error in mapHarmanIdsForVins v2", e);
            isSuccess = false;
        }
        return isSuccess;

    }

    /**
     * Retrieves the device information factory data based on the provided ordered map.
     *
     * @param orderedMap The ordered map containing the device information.
     * @return The device information factory data.
     */
    public DeviceFactoryData getDeviceInfoFactoryData(LinkedHashMap<String, Object> orderedMap) {
        DeviceFactoryData deviceInfoFactoryData = factoryDataDao.find(orderedMap);
        LOGGER.debug("getDeviceInfoFactoryData: deviceInfoFactoryData:{}", deviceInfoFactoryData);
        return deviceInfoFactoryData;
    }

    /**
     * Retrieves the pre-shared key to be shared with client.
     *
     * @return PreSharedKeyResponse contains both encrypted and non encrypted pre-shared key
     */
    public PreSharedKeyResponse getPresharedKey() {
        String preSharedKey = null;
        PreSharedKeyResponse preSharedKeyResponse = new PreSharedKeyResponse();
        try {
            String secretKey = envConfig.getStringValue(AuthProperty.HCP_AUTH_QUALIFIER_SECRET_KEY).trim();
            preSharedKey = cryptographyUtil.getRandomAlphanumericString(ALPHA_NUM_LENGTH);
            byte[] encryptedPreSharedKey = cryptographyUtil.encrypt(secretKey,
                preSharedKey.getBytes(StandardCharsets.UTF_8));
            preSharedKeyResponse.setPreSharedKey(preSharedKey);
            preSharedKeyResponse.setEncryptedPreSharedKey(DatatypeConverter.printBase64Binary(encryptedPreSharedKey));
        } catch (Exception e) {
            throw new ApiTechnicalException(" Exception while creating preSharedKey {}", e.getMessage());
        }
        return preSharedKeyResponse;
    }

    /**
     * Determines whether the device type needs to be updated based on the version and activation request data.
     *
     * @param isVersionV4           a boolean indicating whether the version is V4
     * @param factoryData           the DeviceFactoryData object containing the device type
     * @param activationRequestData the ActivationRequestData object containing the requested device type
     * @return true if the device type needs to be updated, false otherwise
     */
    private boolean setUpdateDeviceType(boolean isVersionV4, DeviceFactoryData factoryData,
                                        ActivationRequestData activationRequestData) {
        return (isVersionV4 && (factoryData.getDeviceType() == null
            || !factoryData.getDeviceType().equals(activationRequestData.getDeviceType())));
    }

    /**
     * Retrieves the transaction status for the SIM details based on the provided parameters.
     *
     * @param vinEnabledFlag   a boolean indicating whether VIN (Vehicle Identification Number) is enabled
     * @param serialNumber     the serial number of the device
     * @return the transaction ID associated with the SIM details, or null if not found
     * @throws ApiPreConditionFailedException if the VIN is not associated with the device or if the transaction ID
     *      is empty
     */
    private String getTransactionStatusForSimDetails(boolean vinEnabledFlag, String serialNumber) {
        LOGGER.debug("## vinEnabledFlag: {}", vinEnabledFlag);
        String transactionId = null;
        if (vinEnabledFlag) {
            String associatedVin = deviceActivationStateDao.getAssociatedVin(serialNumber);
            if (StringUtils.isNotEmpty(associatedVin)) {
                transactionId = deviceActivationStateDao.getTransactionId(serialNumber);
                // get tran_status from sim_details table
                LOGGER.info("## MNO adapter transactionId: {}, transactionStatus: {}", transactionId,
                    TRANSACTION_STATUS);
                if (StringUtils.isEmpty(transactionId)) {
                    throw new ApiPreConditionFailedException(ACTIVATION_CHECK.getCode(), ACTIVATION_CHECK.getMessage(),
                        ACTIVATION_CHECK.getGeneralMessage());
                }
            } else {
                throw new ApiPreConditionFailedException(VIN_ASSO_MANDATORY.getCode(), VIN_ASSO_MANDATORY.getMessage(),
                    VIN_ASSO_MANDATORY.getGeneralMessage());
            }
        }
        return transactionId;
    }

    /**
     * Performs the first activation of a device.
     *
     * @param activationRequestData The activation request data.
     * @param randomNumber The random number.
     * @param factoryData The device factory data.
     * @param isVersionV4 Flag indicating if the version is V4.
     * @param updateDeviceType Flag indicating if the device type should be updated.
     * @param serialNumber The serial number of the device.
     * @param imei The IMEI of the device.
     * @return The newly activated device.
     * @throws InvalidAttributeValueException If an invalid attribute value is encountered.
     */
    private Device performFirstActivation(ActivationRequestData activationRequestData, long randomNumber,
                                          DeviceFactoryData factoryData, boolean isVersionV4, boolean updateDeviceType,
                                          String serialNumber, String imei) {
        Device newDevice = null;

        LOGGER.debug("## Device is ready to activate with Device Factory Data: {}", factoryData);
        LOGGER.info("## Device is ready to activate with Device Factory Data, SerialNumber: {}, IMEI: {}", serialNumber,
            imei);
        newDevice = createNewDevice(randomNumber);
        LOGGER.debug("## Got new device: {}", newDevice);
        // Update device type in postgres only if version is v4 as part of US 292046 and 292100
        updateAllActivationTables(newDevice, factoryData, activationRequestData, isVersionV4);
        if (updateDeviceType) {
            factoryData.setDeviceType(activationRequestData.getDeviceType());
        }
        LOGGER.info("## Device activation tables got updated successfully, SerialNumber: {}, IMEI: {}", serialNumber,
            imei);

        String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
        if (isVersionV4) {
            springAuthRestClient.createRegisteredClient(authToken, newDevice.getHarmanId(), newDevice.getPasscode(),
                activationRequestData.getDeviceType());
            LOGGER.info("## Device got registered with Spring Auth successfully, SerialNumber: {}, IMEI: {}",
                serialNumber, imei);
        } else {
            springAuthRestClient.createRegisteredClient(authToken, newDevice.getHarmanId(), newDevice.getPasscode(),
                "Dongle");
            LOGGER.info("## Device got registered with Spring Auth successfully, SerialNumber: {}, IMEI: {}",
                serialNumber, imei);
        }
        return newDevice;
    }

    /**
     * Performs activation for a provision alive device.
     *
     * @param updateDeviceType        flag indicating whether to update the device type
     * @param factoryData             the device factory data
     * @param activationRequestData   the activation request data
     * @param isVersionV4             flag indicating whether the version is V4
     * @param serialNumber            the serial number of the device
     * @param imei                    the IMEI of the device
     */
    private void performActivationForProvisionAliveDevice(boolean updateDeviceType, DeviceFactoryData factoryData,
                                                          ActivationRequestData activationRequestData,
                                                          boolean isVersionV4, String serialNumber, String imei) {
        if (updateDeviceType) {
            deviceInfoFactoryDataDao.updateDeviceType(factoryData.getId(), activationRequestData.getDeviceType(), null);
            LOGGER.info("## Updated device type in DB successfully in provision alive flow, SerialNumber: {}, IMEI: {}",
                serialNumber, imei);
        }
        updateFactoryData(factoryData);
        if (isSendActivationFailureEvent(isVersionV4, activationRequestData)) {
            sendActivationFailureEvent(activationRequestData);
        }
        LOGGER.info(
            "## Device state updated without User Association. Hence device state become PROVISION_ALIVE, "
                + "SerialNumber: {}, IMEI: {}", serialNumber, imei);
    }

    /**
     * Performs reactivation of a device.
     *
     * @param factoryData The factory data of the device.
     * @param activationRequestData The activation request data.
     * @param isVersionV4 A boolean indicating whether the version is V4.
     * @param serialNumber The serial number of the device.
     * @param imei The IMEI number of the device.
     * @return A map containing the device and reactivation flag.
     * @throws InvalidAttributeValueException If there is an invalid attribute value.
     */
    private Map<String, Object> performReActivation(DeviceFactoryData factoryData,
                                                    ActivationRequestData activationRequestData, boolean isVersionV4,
                                                    String serialNumber, String imei)
        throws InvalidAttributeValueException {
        Device newDevice = null;
        List<Device> deviceList = deviceDao.findActiveDevice(factoryData.getId());
        if (deviceList == null || deviceList.size() > 1) {
            throw new ApiValidationFailedException(UNKNOWN_ERROR.getCode(), UNKNOWN_ERROR.getMessage(),
                UNKNOWN_ERROR.getGeneralMessage());
        }
        newDevice = deviceList.get(0);
        LOGGER.debug("## newDevice: {}", newDevice);

        boolean reactivationFlag;
        reactivationFlag = performSpringAuthReactivation(newDevice, isVersionV4, factoryData, activationRequestData,
            serialNumber, imei);

        Map<String, Object> deviceAndReactivationFlagMap = new HashMap<>();
        deviceAndReactivationFlagMap.put(DEVICE, newDevice);
        deviceAndReactivationFlagMap.put(REACTIVATION_FLAG, reactivationFlag);
        return deviceAndReactivationFlagMap;
    }

    /**
     * Performs the Spring Auth reactivation process for a device.
     *
     * @param newDevice             The new device object.
     * @param isVersionV4           A boolean indicating whether the device version is V4.
     * @param factoryData           The device factory data.
     * @param activationRequestData The activation request data.
     * @param serialNumber          The serial number of the device.
     * @param imei                  The IMEI number of the device.
     * @return A boolean indicating whether the reactivation was successful.
     * @throws InvalidAttributeValueException If an invalid attribute value is encountered.
     */
    private boolean performSpringAuthReactivation(Device newDevice, boolean isVersionV4, DeviceFactoryData factoryData,
                                                  ActivationRequestData activationRequestData, String serialNumber,
                                                  String imei) throws InvalidAttributeValueException {
        String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
        springAuthRestClient.deleteRegisteredClient(authToken, newDevice.getHarmanId());
        LOGGER.info(
            "## Scope, device and application deleted from Spring Auth successfully, SerialNumber: {}, IMEI: {}",
            serialNumber, imei);

        // Generate new passcode
        String passcode = DeviceActivationUtil.getPasscode();
        newDevice.setPasscode(passcode);
        boolean reactivationFlag = false;
        if (isVersionV4) {
            springAuthRestClient.updateRegisteredClient(authToken, newDevice.getHarmanId(), newDevice.getPasscode(),
                activationRequestData.getDeviceType(), REGISTRATION_STATUS);
            LOGGER.info(
                "## updated the status of the application with Spring Auth successfully, SerialNumber: {}, IMEI: {}",
                serialNumber, imei);
            deviceDao.updatePasscode(newDevice);
            LOGGER.info(UPDATED_PASSCODE, serialNumber, imei);
            if (factoryData.getDeviceType() == null
                || !factoryData.getDeviceType().equals(activationRequestData.getDeviceType())) {
                deviceInfoFactoryDataDao.updateDeviceType(factoryData.getId(), activationRequestData.getDeviceType(),
                    "Device Reactivated");
                factoryData.setDeviceType(activationRequestData.getDeviceType());
                //Task 300866 - Added below flag and set it to true; to send dummy vin event during reactivation when
                // the device type has changed
                reactivationFlag = true;
            }
            LOGGER.info(
                "## Updated scope and device type into Spring Auth and DB successfully, SerialNumber: {}, IMEI: {}",
                serialNumber, imei);
        } else {
            springAuthRestClient.updateRegisteredClient(authToken, newDevice.getHarmanId(), newDevice.getPasscode(),
                "Dongle", REGISTRATION_STATUS);
            LOGGER.info(
                "## updated the status of the application with Spring Auth successfully, SerialNumber: {}, IMEI: {}",
                serialNumber, imei);
            deviceDao.updatePasscode(newDevice);
            LOGGER.info(UPDATED_PASSCODE, serialNumber, imei);
        }
        return reactivationFlag;
    }

    /**
     * Retrieves the value associated with the specified key from the given device map.
     *
     * @param device the map containing device details
     * @param key the key to retrieve the value for
     * @return the value associated with the specified key, or null if the key is not found or the device map is null
     */
    private Object getDetailsFromMap(Map<String, Object> device, String key) {
        if (device != null && device.containsKey(key)) {
            return device.get(key);
        }
        return null;
    }

    /**
     * Performs the device state change and returns the activation response.
     *
     * @param newDevice             The new device object.
     * @param activationResponse    The activation response object.
     * @param activationRequestData The activation request data object.
     * @param serialNumber          The serial number of the device.
     * @param factoryData           The factory data object.
     * @param reactivationFlag      The flag indicating if it's a reactivation.
     * @return The activation response after the device state change.
     */
    private ActivationResponse performDeviceStateChange(Device newDevice, ActivationResponse activationResponse,
                                                        ActivationRequestData activationRequestData,
                                                        String serialNumber, DeviceFactoryData factoryData,
                                                        boolean reactivationFlag) {
        if (activationResponse == null) {
            activationResponse = new ActivationResponse();
        }
        if (newDevice != null) {
            activationResponse.setDeviceId(newDevice.getHarmanId());
            activationResponse.setPasscode(newDevice.getPasscode());
        }
        // Here in this method: callActivationObservable -
        // We are calling Association component's - stateChange api to
        // change Device status to ACTIVE via Rest API call
        LOGGER.info("## reactivationFlag value: {}", reactivationFlag);
        callActivationObservable(new DeviceStateActivation(activationResponse, activationRequestData.getSwVersion(),
            activationRequestData.getHwVersion(), serialNumber, factoryData.getDeviceType(), reactivationFlag));
        return activationResponse;
    }

    /**
     * Performs the notification process for the user based on the provided parameters.
     *
     * @param vinEnabledFlag        a boolean indicating whether the VIN (Vehicle Identification Number) is enabled
     * @param transactionId         the transaction ID associated with the device activation
     * @param isFirstTimeActivation a boolean indicating whether it is the first time activation for the device
     * @param serialNumber          the serial number of the device
     */
    private void performNotifyUser(boolean vinEnabledFlag, String transactionId, boolean isFirstTimeActivation,
                                   String serialNumber) {
        if (vinEnabledFlag && StringUtils.isNotEmpty(transactionId)) {
            deviceActivationStateDao.updateTransactionStatus(transactionId);
        }

        String userId = deviceActivationStateDao.getAssociatedUserId(serialNumber);

        if (StringUtils.isNotEmpty(userId) && isFirstTimeActivation) {
            UserProfile userProfile = ncClient.getUserProfile(userId, ncBaseUrl);
            if (!ObjectUtils.isEmpty(userProfile)) {
                ncClient.callNotifCenterNonRegisteredUserApi(userProfile, ncBaseUrl, notificationId);
            } else {
                LOGGER.info(
                    "Unable to send sms notification to the user :  {} while activating device, since userProfile is"
                        + " not present", userId);
            }
        }
    }
}
