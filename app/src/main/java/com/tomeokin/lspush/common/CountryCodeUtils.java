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
package com.tomeokin.lspush.common;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.tomeokin.lspush.data.local.CountryCodeData;
import com.tomeokin.phonenumberutil.PhoneNumberUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class CountryCodeUtils {
    public static CountryCodeData getDefault(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simCountryIso = telephonyManager.getSimCountryIso();
        if (simCountryIso == null) {
            simCountryIso = telephonyManager.getNetworkCountryIso();
            if (simCountryIso == null) {
                simCountryIso = Locale.getDefault().getCountry();
            }
        }
        simCountryIso = simCountryIso.toUpperCase(Locale.US);
        if (TextUtils.isEmpty(simCountryIso)) {
            simCountryIso = "CN";
        }
        int countryCode = PhoneNumberUtil.getInstance(context).getCountryCodeForRegion(simCountryIso);
        return new CountryCodeData(String.valueOf(countryCode), new Locale("", simCountryIso).getDisplayCountry(),
            simCountryIso);
    }

    public static List<CountryCodeData> getCountryCodeDatas(Context context) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance(context);
        List<CountryCodeData> countryCodeDatas = new ArrayList<>();
        String[] countries = Locale.getISOCountries();
        String language = Locale.getDefault().getLanguage();
        for (String country : countries) {
            Locale locale = new Locale(language, country);
            int phoneCode = phoneNumberUtil.getCountryCodeForRegion(locale.getCountry());
            countryCodeDatas.add(
                new CountryCodeData(String.valueOf(phoneCode), locale.getDisplayCountry(), locale.getCountry()));
        }
        Collections.sort(countryCodeDatas);
        return countryCodeDatas;
    }
}
