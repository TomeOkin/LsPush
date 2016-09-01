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

public final class NumberFormat implements Externalizable {
    String pattern = "";
    String format = "";
    List<String> leadingDigitsPattern; // always
    boolean hasNationalPrefixFormattingRule;
    String nationalPrefixFormattingRule;
    boolean nationalPrefixOptionalWhenFormatting; // always
    boolean hasDomesticCarrierCodeFormattingRule;
    String domesticCarrierCodeFormattingRule;

    public NumberFormat() {
        pattern = "";
        format = "";
        leadingDigitsPattern = new ArrayList<>();
        nationalPrefixFormattingRule = "";
        nationalPrefixOptionalWhenFormatting = false;
        domesticCarrierCodeFormattingRule = "";
    }

    @Override public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        pattern = input.readUTF();
        format = input.readUTF();
        int size = input.readInt();
        for (int i = 0; i < size; i++) {
            leadingDigitsPattern.add(input.readUTF());
        }
        if (input.readBoolean()) {
            hasNationalPrefixFormattingRule = true;
            nationalPrefixFormattingRule = input.readUTF();
        }
        if (input.readBoolean()) {
            hasDomesticCarrierCodeFormattingRule = true;
            domesticCarrierCodeFormattingRule = input.readUTF();
        }
        nationalPrefixOptionalWhenFormatting = input.readBoolean();
    }

    @Override public void writeExternal(ObjectOutput output) throws IOException {
        output.writeUTF(pattern);
        output.writeUTF(format);
        int size = leadingDigitsPattern.size();
        output.writeInt(size);
        for (int i = 0; i < size; i++) {
            output.writeUTF(leadingDigitsPattern.get(i));
        }
        output.writeBoolean(hasNationalPrefixFormattingRule);
        if (hasNationalPrefixFormattingRule) {
            output.writeUTF(nationalPrefixFormattingRule);
        }
        output.writeBoolean(hasDomesticCarrierCodeFormattingRule);
        if (hasDomesticCarrierCodeFormattingRule) {
            output.writeUTF(domesticCarrierCodeFormattingRule);
        }
        output.writeBoolean(nationalPrefixOptionalWhenFormatting);
    }
}
