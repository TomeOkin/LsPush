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

public final class PhoneNumberDesc implements Externalizable {
    boolean hasNationalNumberPattern;
    String nationalNumberPattern;
    boolean hasPossibleNumberPattern;
    String possibleNumberPattern;
    boolean hasExampleNumber;
    String exampleNumber;

    public PhoneNumberDesc() {
        nationalNumberPattern = "";
        possibleNumberPattern = "";
        exampleNumber = "";
    }

    @Override public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        if (input.readBoolean()) {
            hasNationalNumberPattern = true;
            nationalNumberPattern = input.readUTF();
        }
        if (input.readBoolean()) {
            hasPossibleNumberPattern = true;
            possibleNumberPattern = input.readUTF();
        }
        if (input.readBoolean()) {
            hasExampleNumber = true;
            exampleNumber = input.readUTF();
        }
    }

    @Override public void writeExternal(ObjectOutput output) throws IOException {
        output.writeBoolean(hasNationalNumberPattern);
        if (hasNationalNumberPattern) {
            output.writeUTF(nationalNumberPattern);
        }
        output.writeBoolean(hasPossibleNumberPattern);
        if (hasPossibleNumberPattern) {
            output.writeUTF(possibleNumberPattern);
        }
        output.writeBoolean(hasExampleNumber);
        if (hasExampleNumber) {
            output.writeUTF(exampleNumber);
        }
    }
}
