/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wifi;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;

import static org.junit.Assert.*;

/**
 * Helper for creating and populating WifiConfigurations in unit tests.
 */
public class WifiConfigurationTestUtil {
    /**
     * These values are used to describe AP's security setting. One AP can support multiple of them,
     * only if there is no conflict.
     */
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP =  1 << 0;
    public static final int SECURITY_PSK =  1 << 1;
    public static final int SECURITY_EAP =  1 << 2;

    /**
     * Construct a {@link android.net.wifi.WifiConfiguration}.
     * @param networkId the configuration's networkId
     * @param uid the configuration's creator uid
     * @param ssid the configuration's ssid
     * @param shared whether the configuration is shared with other users on the device
     * @param enabled whether the configuration is enabled
     * @param fqdn the configuration's FQDN (Hotspot 2.0 only)
     * @param providerFriendlyName the configuration's provider's friendly name (Hotspot 2.0 only)
     * @return the constructed {@link android.net.wifi.WifiConfiguration}
     */
    public static WifiConfiguration generateWifiConfig(int networkId, int uid, String ssid,
            boolean shared, boolean enabled, String fqdn, String providerFriendlyName) {
        final WifiConfiguration config = new WifiConfiguration();
        config.SSID = ssid;
        config.networkId = networkId;
        config.creatorUid = uid;
        config.shared = shared;
        config.status = enabled ? WifiConfiguration.Status.ENABLED
                : WifiConfiguration.Status.DISABLED;
        if (fqdn != null) {
            config.FQDN = fqdn;
            config.providerFriendlyName = providerFriendlyName;
            config.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.SIM);
        }
        return config;
    }

    /**
     * Construct a {@link android.net.wifi.WifiConfiguration}.
     * @param networkId the configuration's networkId
     * @param uid the configuration's creator uid
     * @param ssid the configuration's ssid
     * @param shared whether the configuration is shared with other users on the device
     * @param enabled whether the configuration is enabled
     * @param fqdn the configuration's FQDN (Hotspot 2.0 only)
     * @param providerFriendlyName the configuration's provider's friendly name (Hotspot 2.0 only)
     * @param security the configuration's security type
     * @return the constructed {@link android.net.wifi.WifiConfiguration}
     */
    public static WifiConfiguration generateWifiConfig(int networkId, int uid, String ssid,
            boolean shared, boolean enabled, String fqdn, String providerFriendlyName,
            int security) {
        WifiConfiguration config = generateWifiConfig(networkId, uid, ssid, shared, enabled, fqdn,
                providerFriendlyName);

        if (security == SECURITY_NONE) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else {
            if (((security & SECURITY_WEP) != 0) || ((security & SECURITY_PSK) != 0)) {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            }

            if ((security & SECURITY_EAP) != 0) {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
            }
        }
        return config;
    }

    /**
     * Asserts that the 2 WifiConfigurations are equal in the elements saved for both backup/restore
     * and config store.
     */
    private static void assertCommonConfigurationElementsEqual(
            WifiConfiguration expected, WifiConfiguration actual) {
        assertEquals(expected.SSID, actual.SSID);
        assertEquals(expected.BSSID, actual.BSSID);
        assertEquals(expected.preSharedKey, actual.preSharedKey);
        assertEquals(expected.wepKeys, actual.wepKeys);
        assertEquals(expected.wepTxKeyIndex, actual.wepTxKeyIndex);
        assertEquals(expected.hiddenSSID, actual.hiddenSSID);
        assertEquals(expected.allowedKeyManagement, actual.allowedKeyManagement);
        assertEquals(expected.allowedProtocols, actual.allowedProtocols);
        assertEquals(expected.allowedAuthAlgorithms, actual.allowedAuthAlgorithms);
        assertEquals(expected.shared, actual.shared);
        assertEquals(expected.getIpConfiguration(), actual.getIpConfiguration());
    }

    /**
     * Asserts that the 2 WifiConfigurations are equal. This only compares the elements saved
     * fpr backup/restore.
     */
    public static void assertConfigurationEqualForBackup(
            WifiConfiguration expected, WifiConfiguration actual) {
        assertCommonConfigurationElementsEqual(expected, actual);
    }

    /**
     * Asserts that the 2 WifiConfigurations are equal. This compares all the elements saved for
     * config store.
     */
    public static void assertConfigurationEqualForConfigStore(
            WifiConfiguration expected, WifiConfiguration actual) {
        assertCommonConfigurationElementsEqual(expected, actual);
        assertEquals(expected.creatorUid, actual.creatorUid);
        assertEquals(expected.creatorName, actual.creatorName);
    }
}
