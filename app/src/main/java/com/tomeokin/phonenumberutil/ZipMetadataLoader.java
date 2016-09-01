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

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ZipMetadataLoader implements MetadataLoader {
    private final Context context;

    public ZipMetadataLoader(Context context) {
        this.context = context;
    }

    @Override public InputStream loadMetadata(String metadataFileName) {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(context.getAssets().open("libphone_data.zip"));
            ZipEntry nextEntry;
            do {
                nextEntry = zipInputStream.getNextEntry();
                if (nextEntry == null) {
                    return null;
                }
            } while (!nextEntry.getName().equals(metadataFileName));
            return zipInputStream;
        } catch (IOException e) {
            return null;
        }
    }
}
