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

public final class PhoneMetadata implements Externalizable {
    boolean hasGeneralDesc;
    PhoneNumberDesc generalDesc;
    boolean hasFixedLine;
    PhoneNumberDesc fixedLine;
    boolean hasMobile;
    PhoneNumberDesc mobile;
    boolean hasTollFree;
    PhoneNumberDesc tollFree;
    boolean hasPremiumRate;
    PhoneNumberDesc premiumRate;
    boolean hasSharedCost;
    PhoneNumberDesc sharedCost;
    boolean hasPersonalNumber;
    PhoneNumberDesc personalNumber;
    boolean hasVoip;
    PhoneNumberDesc voip;
    boolean hasPager;
    PhoneNumberDesc pager;
    boolean hasUan;
    PhoneNumberDesc uan;
    boolean hasEmergency;
    PhoneNumberDesc emergency;
    boolean hasVoicemail;
    PhoneNumberDesc voicemail;
    boolean hasShortCode;
    PhoneNumberDesc shortCode;
    boolean hasStandardRate;
    PhoneNumberDesc standardRate;
    boolean hasCarrierSpecific;
    PhoneNumberDesc carrierSpecific;
    boolean hasNoInternationalDialling;
    PhoneNumberDesc noInternationalDialling;

    boolean hasId;
    String id;
    boolean hasCountryCode;
    int countryCode;
    boolean hasInternationalPrefix;
    String internationalPrefix;
    boolean hasPreferredInternationalPrefix;
    String preferredInternationalPrefix;
    boolean hasNationalPrefix;
    String nationalPrefix;
    boolean hasPreferredExtnPrefix;
    String preferredExtnPrefix;
    boolean hasNationalPrefixForParsing;
    String nationalPrefixForParsing;
    boolean hasNationalPrefixTransformRule;
    String nationalPrefixTransformRule;
    boolean sameMobileAndFixedLinePattern; // always
    
    List<NumberFormat> numberFormats;
    List<NumberFormat> intlNumberFormat;
    
    boolean mainCountryForCode; // always
    boolean hasLeadingDigits;
    String leadingDigits;
    boolean leadingZeroPossible; // always
    boolean mobileNumberPortableRegion; // always

    public PhoneMetadata() {
        id = "";
        countryCode = 0;
        internationalPrefix = "";
        preferredInternationalPrefix = "";
        nationalPrefix = "";
        preferredExtnPrefix = "";
        nationalPrefixForParsing = "";
        nationalPrefixTransformRule = "";
        sameMobileAndFixedLinePattern = false;

        numberFormats = new ArrayList<>();
        intlNumberFormat = new ArrayList<>();
        
        mainCountryForCode = false;
        leadingDigits = "";
        leadingZeroPossible = false;
        mobileNumberPortableRegion = false;
    }

    @Override public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        if (input.readBoolean()) {
            hasGeneralDesc = true;
            generalDesc = new PhoneNumberDesc();
            generalDesc.readExternal(input);
        }
        if (input.readBoolean()) {
            hasFixedLine = true;
            fixedLine = new PhoneNumberDesc();
            fixedLine.readExternal(input);
        }
        if (input.readBoolean()) {
            hasMobile = true;
            mobile = new PhoneNumberDesc();
            mobile.readExternal(input);
        }
        if (input.readBoolean()) {
            hasTollFree = true;
            tollFree = new PhoneNumberDesc();
            tollFree.readExternal(input);
        }
        if (input.readBoolean()) {
            hasPremiumRate = true;
            premiumRate = new PhoneNumberDesc();
            premiumRate.readExternal(input);
        }
        if (input.readBoolean()) {
            hasSharedCost = true;
            sharedCost = new PhoneNumberDesc();
            sharedCost.readExternal(input);
        }
        if (input.readBoolean()) {
            hasPersonalNumber = true;
            personalNumber = new PhoneNumberDesc();
            personalNumber.readExternal(input);
        }
        if (input.readBoolean()) {
            hasVoip = true;
            voip = new PhoneNumberDesc();
            voip.readExternal(input);
        }
        if (input.readBoolean()) {
            hasPager = true;
            pager = new PhoneNumberDesc();
            pager.readExternal(input);
        }
        if (input.readBoolean()) {
            hasUan = true;
            uan = new PhoneNumberDesc();
            uan.readExternal(input);
        }
        if (input.readBoolean()) {
            hasEmergency = true;
            emergency = new PhoneNumberDesc();
            emergency.readExternal(input);
        }
        if (input.readBoolean()) {
            hasVoicemail = true;
            voicemail = new PhoneNumberDesc();
            voicemail.readExternal(input);
        }
        if (input.readBoolean()) {
            hasShortCode = true;
            shortCode = new PhoneNumberDesc();
            shortCode.readExternal(input);
        }
        if (input.readBoolean()) {
            hasStandardRate = true;
            standardRate = new PhoneNumberDesc();
            standardRate.readExternal(input);
        }
        if (input.readBoolean()) {
            hasCarrierSpecific = true;
            carrierSpecific = new PhoneNumberDesc();
            carrierSpecific.readExternal(input);
        }
        if (input.readBoolean()) {
            hasNoInternationalDialling = true;
            noInternationalDialling = new PhoneNumberDesc();
            noInternationalDialling.readExternal(input);
        }
        hasId = true;
        id = input.readUTF();
        hasCountryCode = true;
        countryCode = input.readInt();
        hasInternationalPrefix = true;
        internationalPrefix = input.readUTF();
        if (input.readBoolean()) {
            hasPreferredInternationalPrefix = true;
            preferredInternationalPrefix = input.readUTF();
        }
        if (input.readBoolean()) {
            hasNationalPrefix = true;
            nationalPrefix = input.readUTF();
        }
        if (input.readBoolean()) {
            hasPreferredExtnPrefix = true;
            preferredExtnPrefix = input.readUTF();
        }
        if (input.readBoolean()) {
            hasNationalPrefixForParsing = true;
            nationalPrefixForParsing = input.readUTF();
        }
        if (input.readBoolean()) {
            hasNationalPrefixTransformRule = true;
            nationalPrefixTransformRule = input.readUTF();
        }
        sameMobileAndFixedLinePattern = input.readBoolean();
        int i, size = input.readInt();
        for (i = 0; i < size; i++) {
            NumberFormat element = new NumberFormat();
            element.readExternal(input);
            numberFormats.add(element);
        }
        size = input.readInt();
        for (i = 0; i < size; i++) {
            NumberFormat element = new NumberFormat();
            element.readExternal(input);
            intlNumberFormat.add(element);
        }
        mainCountryForCode = input.readBoolean();
        if (input.readBoolean()) {
            hasLeadingDigits = true;
            leadingDigits = input.readUTF();
        }
        leadingZeroPossible = input.readBoolean();
        mobileNumberPortableRegion = input.readBoolean();
    }

    @Override public void writeExternal(ObjectOutput output) throws IOException {
        output.writeBoolean(hasGeneralDesc);
        if (hasGeneralDesc) {
            generalDesc.writeExternal(output);
        }
        output.writeBoolean(hasFixedLine);
        if (hasFixedLine) {
            fixedLine.writeExternal(output);
        }
        output.writeBoolean(hasMobile);
        if (hasMobile) {
            mobile.writeExternal(output);
        }
        output.writeBoolean(hasTollFree);
        if (hasTollFree) {
            tollFree.writeExternal(output);
        }
        output.writeBoolean(hasPremiumRate);
        if (hasPremiumRate) {
            premiumRate.writeExternal(output);
        }
        output.writeBoolean(hasSharedCost);
        if (hasSharedCost) {
            sharedCost.writeExternal(output);
        }
        output.writeBoolean(hasPersonalNumber);
        if (hasPersonalNumber) {
            personalNumber.writeExternal(output);
        }
        output.writeBoolean(hasVoip);
        if (hasVoip) {
            voip.writeExternal(output);
        }
        output.writeBoolean(hasPager);
        if (hasPager) {
            pager.writeExternal(output);
        }
        output.writeBoolean(hasUan);
        if (hasUan) {
            uan.writeExternal(output);
        }
        output.writeBoolean(hasEmergency);
        if (hasEmergency) {
            emergency.writeExternal(output);
        }
        output.writeBoolean(hasVoicemail);
        if (hasVoicemail) {
            voicemail.writeExternal(output);
        }
        output.writeBoolean(hasShortCode);
        if (hasShortCode) {
            shortCode.writeExternal(output);
        }
        output.writeBoolean(hasStandardRate);
        if (hasStandardRate) {
            standardRate.writeExternal(output);
        }
        output.writeBoolean(hasCarrierSpecific);
        if (hasCarrierSpecific) {
            carrierSpecific.writeExternal(output);
        }
        output.writeBoolean(hasNoInternationalDialling);
        if (hasNoInternationalDialling) {
            noInternationalDialling.writeExternal(output);
        }
        output.writeUTF(id);
        output.writeInt(countryCode);
        output.writeUTF(internationalPrefix);
        output.writeBoolean(hasPreferredInternationalPrefix);
        if (hasPreferredInternationalPrefix) {
            output.writeUTF(preferredInternationalPrefix);
        }
        output.writeBoolean(hasNationalPrefix);
        if (hasNationalPrefix) {
            output.writeUTF(nationalPrefix);
        }
        output.writeBoolean(hasPreferredExtnPrefix);
        if (hasPreferredExtnPrefix) {
            output.writeUTF(preferredExtnPrefix);
        }
        output.writeBoolean(hasNationalPrefixForParsing);
        if (hasNationalPrefixForParsing) {
            output.writeUTF(nationalPrefixForParsing);
        }
        output.writeBoolean(hasNationalPrefixTransformRule);
        if (hasNationalPrefixTransformRule) {
            output.writeUTF(nationalPrefixTransformRule);
        }
        output.writeBoolean(sameMobileAndFixedLinePattern);
        int i, size = numberFormats.size();
        output.writeInt(size);
        for (i = 0; i < size; i++) {
            numberFormats.get(i).writeExternal(output);
        }
        size = intlNumberFormat.size();
        output.writeInt(i);
        for (i = 0; i < size; i++)  {
            intlNumberFormat.get(i).writeExternal(output);
        }
        output.writeBoolean(mainCountryForCode);
        output.writeBoolean(hasLeadingDigits);
        if (hasLeadingDigits) {
            output.writeUTF(leadingDigits);
        }
        output.writeBoolean(leadingZeroPossible);
        output.writeBoolean(mobileNumberPortableRegion);
    }
}
