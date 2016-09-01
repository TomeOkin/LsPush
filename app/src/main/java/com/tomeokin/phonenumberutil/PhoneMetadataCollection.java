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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public final class PhoneMetadataCollection implements Externalizable {
    List<PhoneMetadata> metadata;

    public PhoneMetadataCollection() {
        metadata = new ArrayList<>();
    }

    @Override public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        int size = input.readInt();
        for (int i = 0; i < size; i++) {
            PhoneMetadata element = new PhoneMetadata();
            element.readExternal(input);
            metadata.add(element);
        }
    }

    @Override public void writeExternal(ObjectOutput output) throws IOException {
        int size = metadata.size();
        output.writeInt(size);
        for (int i = 0; i < size; i++) {
            metadata.get(i).writeExternal(output);
        }
    }
}
