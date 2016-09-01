/*
 * Copyright 2016 TomeOkin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tomeokin.phonenumberutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhoneNumberUtilCore {
    private static final Logger logger = Logger.getLogger(PhoneNumberUtilCore.class.getName());

    private static final int NANPA_COUNTRY_CODE = 1;

    private final Map<Integer, List<String>> countryCodeToRegionCodeMap;
    private final Map<String, Integer> regionCodeToCountryCodeMap;

    // The set of regions that share country calling code 1.
    // There are roughly 26 regions.
    // We set the initial capacity of the HashSet to 35 to offer a load factor of roughly 0.75.
    private final Set<String> nanpaRegions = new HashSet<>(35);

    // The set of regions the library supports.
    // There are roughly 240 of them and we set the initial capacity of the HashSet to 320 to offer a
    // load factor of roughly 0.75.
    private final Set<String> supportedRegions = new HashSet<>(320);

    // The set of country calling codes that map to the non-geo entity region ("001"). This set
    // currently contains < 12 elements so the default capacity of 16 (load factor=0.75) is fine.
    private final Set<Integer> countryCodesForNonGeographicalRegion = new HashSet<>();

    private final MetadataSource metadataSource;

    private static PhoneNumberUtilCore instance = null;

    public static final String REGION_CODE_FOR_NON_GEO_ENTITY = "001";

    PhoneNumberUtilCore(MetadataSource metadataSource, Map<Integer, List<String>> countryCodeToRegionCodeMap,
        Map<String, Integer> regionCodeToCountryCodeMap) {
        this.metadataSource = metadataSource;
        this.countryCodeToRegionCodeMap = countryCodeToRegionCodeMap;
        this.regionCodeToCountryCodeMap = regionCodeToCountryCodeMap;

        for (Map.Entry<Integer, List<String>> entry : countryCodeToRegionCodeMap.entrySet()) {
            List<String> regionCodes = entry.getValue();
            // We can assume that if the country calling code maps to the non-geo entity region code then
            // that's the only region code it maps to.
            if (regionCodes.size() == 1 && REGION_CODE_FOR_NON_GEO_ENTITY.equals(regionCodes.get(0))) {
                // This is the subset of all country codes that map to the non-geo entity region code.
                countryCodesForNonGeographicalRegion.add(entry.getKey());
            } else {
                // The supported regions set does not include the "001" non-geo entity region code.
                supportedRegions.addAll(regionCodes);
            }
        }

        // If the non-geo entity still got added to the set of supported regions it must be because
        // there are entries that list the non-geo entity alongside normal regions (which is wrong).
        // If we discover this, remove the non-geo entity from the set of supported regions and log.
        if (supportedRegions.remove(REGION_CODE_FOR_NON_GEO_ENTITY)) {
            logger.log(Level.WARNING, "invalid metadata "
                + "(country calling code was mapped to the non-geo entity as well as specific region(s))");
        }
        nanpaRegions.addAll(countryCodeToRegionCodeMap.get(NANPA_COUNTRY_CODE));
    }

    public static synchronized PhoneNumberUtilCore getInstance(MetadataSource metadataSource) {
        synchronized (PhoneNumberUtilCore.class) {
            if (instance == null) {
                CountryRegionCodeMap.initCountryCodeToRegionCodeMap();
                setInstance(new PhoneNumberUtilCore(metadataSource, CountryRegionCodeMap.countryCodeToRegionCodeMap,
                    CountryRegionCodeMap.regionCodeToCountryCodeMap));
            }
        }
        return instance;
    }

    /**
     * Sets or resets the PhoneNumberUtilCore singleton instance. If set to null, the next call to
     * {@code getInstance()} will load (and return) the default instance.
     */
    private static synchronized void setInstance(PhoneNumberUtilCore util) {
        synchronized (PhoneNumberUtilCore.class) {
            instance = util;
        }
    }

    /**
     * Convenience method to get a list of what regions the library has metadata for.
     */
    public Set<String> getSupportedRegions() {
        return Collections.unmodifiableSet(supportedRegions);
    }

    /**
     * Convenience method to get a list of what global network calling codes the library has metadata
     * for.
     */
    public Set<Integer> getSupportedGlobalNetworkCallingCodes() {
        return Collections.unmodifiableSet(countryCodesForNonGeographicalRegion);
    }

    /**
     * Checks if this is a region under the North American Numbering Plan Administration (NANPA).
     *
     * @return true if regionCode is one of the regions under NANPA
     */
    public boolean isNANPACountry(String regionCode) {
        return nanpaRegions.contains(regionCode);
    }

    /**
     * Helper function to check region code is not unknown or null.
     */
    private boolean isValidRegionCode(String regionCode) {
        return regionCode != null && supportedRegions.contains(regionCode);
    }

    private PhoneMetadata getMetadataForRegion(String regionCode) {
        if (!isValidRegionCode(regionCode)) {
            return null;
        }

        return metadataSource.getMetadataForRegion(regionCode);
    }

    /**
     * Returns the country calling code for a specific region. For example, this would be 1 for the
     * United States, and 64 for New Zealand.
     *
     * @param regionCode the region that we want to get the country calling code for
     * @return the country calling code for the region denoted by regionCode
     */
    public int getCountryCodeForRegion(String regionCode) {
        if (!isValidRegionCode(regionCode)) {
            logger.log(Level.WARNING,
                "Invalid or missing region code (" + ((regionCode == null) ? "null" : regionCode) + ") provided.");
            return 0;
        }

        return getCountryCodeForValidRegion(regionCode);
    }

    public int getCountryCodeForValidRegion(String regionCode) {
        if (regionCodeToCountryCodeMap.containsKey(regionCode)) {
            return regionCodeToCountryCodeMap.get(regionCode);
        } else {
            PhoneMetadata metadata = getMetadataForRegion(regionCode);
            if (metadata == null) {
                throw new IllegalArgumentException("Invalid region code: " + regionCode);
            }
            return metadata.countryCode;
        }
    }

    PhoneMetadata getMetadataForNonGeographicalRegion(int countryCallingCode) {
        if (!countryCodeToRegionCodeMap.containsKey(countryCallingCode)) {
            return null;
        }
        return metadataSource.getMetadataForNonGeographicalRegion(countryCallingCode);
    }

    PhoneMetadata getMetadataForRegionOrCallingCode(int countryCallingCode, String regionCode) {
        return REGION_CODE_FOR_NON_GEO_ENTITY.equals(regionCode) ? getMetadataForNonGeographicalRegion(
            countryCallingCode) : getMetadataForRegion(regionCode);
    }

    static NumberFormat copyNumberFormat(NumberFormat other) {
        NumberFormat copy = new NumberFormat();
        copy.pattern = other.pattern;
        copy.format = other.format;
        int leadingDigitsPatternSize = other.leadingDigitsPattern.size();
        copy.leadingDigitsPattern = new ArrayList<>(leadingDigitsPatternSize);
        for (int i = 0; i < leadingDigitsPatternSize; i++) {
            copy.leadingDigitsPattern.add(other.leadingDigitsPattern.get(i));
        }
        copy.nationalPrefixFormattingRule = other.nationalPrefixFormattingRule;
        copy.domesticCarrierCodeFormattingRule = other.domesticCarrierCodeFormattingRule;
        copy.nationalPrefixOptionalWhenFormatting = other.nationalPrefixOptionalWhenFormatting;
        return copy;
    }
}
