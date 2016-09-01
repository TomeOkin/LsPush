/*
 * Copyright (C) 2015 The Libphonenumber Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomeokin.phonenumberutil;

import android.annotation.SuppressLint;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of {@link MetadataSource} that reads from multiple resource files.
 */
final class MultiFileMetadataSourceImpl implements MetadataSource {

    private static final Logger logger = Logger.getLogger(MultiFileMetadataSourceImpl.class.getName());

    private static final String META_DATA_FILE_PREFIX = "libphone_data/PhoneNumberMetadataProto";

    // A mapping from a region code to the PhoneMetadata for that region.
    private final Map<String, PhoneMetadata> geographicalRegions =
        Collections.synchronizedMap(new HashMap<String, PhoneMetadata>());

    // A mapping from a country calling code for a non-geographical entity to the PhoneMetadata for
    // that country calling code. Examples of the country calling codes include 800 (International
    // Toll Free Service) and 808 (International Shared Cost Service).
    @SuppressLint("UseSparseArrays") private final Map<Integer, PhoneMetadata> nonGeographicalRegions =
        Collections.synchronizedMap(new HashMap<Integer, PhoneMetadata>());

    // The prefix of the metadata files from which region data is loaded.
    private final String filePrefix;

    // The metadata loader used to inject alternative metadata sources.
    private final MetadataLoader metadataLoader;

    // It is assumed that metadataLoader is not null. If needed, checks should happen before passing
    // here.
    // @VisibleForTesting
    MultiFileMetadataSourceImpl(String filePrefix, MetadataLoader metadataLoader) {
        this.filePrefix = filePrefix;
        this.metadataLoader = metadataLoader;
    }

    // It is assumed that metadataLoader is not null. If needed, checks should happen before passing
    // here.
    public MultiFileMetadataSourceImpl(MetadataLoader metadataLoader) {
        this(META_DATA_FILE_PREFIX, metadataLoader);
    }

    @Override public PhoneMetadata getMetadataForRegion(String regionCode) {
        PhoneMetadata metadata;
        synchronized (geographicalRegions) {
            metadata = geographicalRegions.get(regionCode);
            if (metadata == null) {
                metadata = loadMetadataFromFile(regionCode, geographicalRegions, filePrefix, metadataLoader);
            }
        }
        return metadata;
    }

    @Override public PhoneMetadata getMetadataForNonGeographicalRegion(int countryCallingCode) {
        PhoneMetadata metadata = nonGeographicalRegions.get(countryCallingCode);
        if (metadata != null) {
            return metadata;
        }
        if (isNonGeographical(countryCallingCode)) {
            return loadMetadataFromFile(countryCallingCode, nonGeographicalRegions, filePrefix, metadataLoader);
        }
        // The given country calling code was for a geographical region.
        return null;
    }

    // A country calling code is non-geographical if it only maps to the non-geographical region code, i.e. "001".
    private boolean isNonGeographical(int countryCallingCode) {
        CountryRegionCodeMap.initCountryCodeToRegionCodeMap();
        List<String> regionCodes = CountryRegionCodeMap.countryCodeToRegionCodeMap.get(countryCallingCode);
        return (regionCodes.size() == 1 && PhoneNumberUtilCore.REGION_CODE_FOR_NON_GEO_ENTITY.equals(regionCodes.get(0)));
    }

    /**
     * @param key The geographical region code or the non-geographical region's country
     * calling code.
     * @param map The map to contain the mapping from {@code key} to the corresponding
     * metadata.
     * @param filePrefix The prefix of the metadata files from which region data is loaded.
     * @param metadataLoader The metadata loader used to inject alternative metadata sources.
     */
    // @VisibleForTesting
    static <T> PhoneMetadata loadMetadataFromFile(T key, Map<T, PhoneMetadata> map, String filePrefix,
        MetadataLoader metadataLoader) {
        // We assume key.toString() is well-defined.
        String fileName = filePrefix + "_" + key;
        InputStream source = metadataLoader.loadMetadata(fileName);
        if (source == null) {
            logger.log(Level.SEVERE, "missing metadata: " + fileName);
            throw new IllegalStateException("missing metadata: " + fileName);
        }

        try {
            List<PhoneMetadata> phoneMetadataList = loadMetadataAndCloseInput(new ObjectInputStream(source)).metadata;
            if (phoneMetadataList == null || phoneMetadataList.isEmpty()) {
                logger.log(Level.SEVERE, "empty metadata: " + fileName);
                throw new IllegalStateException("empty metadata: " + fileName);
            }
            if (phoneMetadataList.size() > 1) {
                logger.log(Level.WARNING, "invalid metadata (too many entries): " + fileName);
            }
            PhoneMetadata metadata = phoneMetadataList.get(0);
            map.put(key, metadata);
            return metadata;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "cannot load/parse metadata: " + fileName, e);
            throw new RuntimeException("cannot load/parse metadata: " + fileName, e);
        }
    }

    private static PhoneMetadataCollection loadMetadataAndCloseInput(ObjectInputStream objectInputStream) {
        PhoneMetadataCollection phoneMetadataCollection = new PhoneMetadataCollection();
        try {
            phoneMetadataCollection.readExternal(objectInputStream);
        } catch (Exception e) {
            logger.log(Level.WARNING, "error reading input (ignored)", e);
            try {
                objectInputStream.close();
            } catch (Exception e1) {
                logger.log(Level.WARNING, "error closing input stream (ignored)", e1);
            }
        }
        return phoneMetadataCollection;
    }
}
