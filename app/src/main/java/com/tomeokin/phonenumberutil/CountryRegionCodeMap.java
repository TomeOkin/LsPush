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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryRegionCodeMap {
    static Map<Integer, List<String>> countryCodeToRegionCodeMap;
    static Map<String, Integer> regionCodeToCountryCodeMap;

    static void initCountryCodeToRegionCodeMap() {
        if (countryCodeToRegionCodeMap == null || regionCodeToCountryCodeMap == null) {
            HashMap<Integer, List<String>> countryToRegion = new HashMap<>(285);
            HashMap<String, Integer> regionToCountry = new HashMap<>(326);

            ArrayList<String> listWithRegionCode = new ArrayList<>(25);
            listWithRegionCode.add("US");
            regionToCountry.put("US", 1);
            listWithRegionCode.add("AG");
            regionToCountry.put("AG", 1);
            listWithRegionCode.add("AI");
            regionToCountry.put("AI", 1);
            listWithRegionCode.add("AS");
            regionToCountry.put("AS", 1);
            listWithRegionCode.add("BB");
            regionToCountry.put("BB", 1);
            listWithRegionCode.add("BM");
            regionToCountry.put("BM", 1);
            listWithRegionCode.add("BS");
            regionToCountry.put("BS", 1);
            listWithRegionCode.add("CA");
            regionToCountry.put("CA", 1);
            listWithRegionCode.add("DM");
            regionToCountry.put("DM", 1);
            listWithRegionCode.add("DO");
            regionToCountry.put("DO", 1);
            listWithRegionCode.add("GD");
            regionToCountry.put("GD", 1);
            listWithRegionCode.add("GU");
            regionToCountry.put("GU", 1);
            listWithRegionCode.add("JM");
            regionToCountry.put("JM", 1);
            listWithRegionCode.add("KN");
            regionToCountry.put("KN", 1);
            listWithRegionCode.add("KY");
            regionToCountry.put("KY", 1);
            listWithRegionCode.add("LC");
            regionToCountry.put("LC", 1);
            listWithRegionCode.add("MP");
            regionToCountry.put("MP", 1);
            listWithRegionCode.add("MS");
            regionToCountry.put("MS", 1);
            listWithRegionCode.add("PR");
            regionToCountry.put("PR", 1);
            listWithRegionCode.add("SX");
            regionToCountry.put("SX", 1);
            listWithRegionCode.add("TC");
            regionToCountry.put("TC", 1);
            listWithRegionCode.add("TT");
            regionToCountry.put("TT", 1);
            listWithRegionCode.add("VC");
            regionToCountry.put("VC", 1);
            listWithRegionCode.add("VG");
            regionToCountry.put("VG", 1);
            listWithRegionCode.add("VI");
            regionToCountry.put("VI", 1);
            countryToRegion.put(1, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(2);
            listWithRegionCode.add("RU");
            regionToCountry.put("RU", 7);
            listWithRegionCode.add("KZ");
            regionToCountry.put("KZ", 7);
            countryToRegion.put(7, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("EG");
            regionToCountry.put("EG", 20);
            countryToRegion.put(20, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("ZA");
            regionToCountry.put("ZA", 27);
            countryToRegion.put(27, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GR");
            regionToCountry.put("GR", 30);
            countryToRegion.put(30, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("NL");
            regionToCountry.put("NL", 31);
            countryToRegion.put(31, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BE");
            regionToCountry.put("BE", 32);
            countryToRegion.put(32, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("FR");
            regionToCountry.put("FR", 33);
            countryToRegion.put(33, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("ES");
            regionToCountry.put("ES", 34);
            countryToRegion.put(34, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("HU");
            regionToCountry.put("HU", 36);
            countryToRegion.put(36, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(2);
            listWithRegionCode.add("IT");
            regionToCountry.put("IT", 39);
            listWithRegionCode.add("VA");
            regionToCountry.put("VA", 39);
            countryToRegion.put(39, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("RO");
            regionToCountry.put("RO", 40);
            countryToRegion.put(40, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CH");
            regionToCountry.put("CH", 41);
            countryToRegion.put(41, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("AT");
            regionToCountry.put("AT", 43);
            countryToRegion.put(43, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(4);
            listWithRegionCode.add("GB");
            regionToCountry.put("GB", 44);
            listWithRegionCode.add("GG");
            regionToCountry.put("GG", 44);
            listWithRegionCode.add("IM");
            regionToCountry.put("IM", 44);
            listWithRegionCode.add("JE");
            regionToCountry.put("JE", 44);
            countryToRegion.put(44, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("DK");
            regionToCountry.put("DK", 45);
            countryToRegion.put(45, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SE");
            regionToCountry.put("SE", 46);
            countryToRegion.put(46, listWithRegionCode);
            
            listWithRegionCode = new ArrayList<>(2);
            listWithRegionCode.add("NO");
            regionToCountry.put("NO", 47);
            listWithRegionCode.add("SJ");
            regionToCountry.put("SJ", 47);
            countryToRegion.put(47, listWithRegionCode);
            
            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PL");
            regionToCountry.put("PL", 48);
            countryToRegion.put(48, listWithRegionCode);
            
            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("DE");
            regionToCountry.put("DE", 49);
            countryToRegion.put(49, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PE");
            regionToCountry.put("PE", 51);
            countryToRegion.put(51, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MX");
            regionToCountry.put("MX", 52);
            countryToRegion.put(52, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CU");
            regionToCountry.put("CU", 53);
            countryToRegion.put(53, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("AR");
            regionToCountry.put("AR", 54);
            countryToRegion.put(54, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BR");
            regionToCountry.put("BR", 55);
            countryToRegion.put(55, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CL");
            regionToCountry.put("CL", 56);
            countryToRegion.put(56, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CO");
            regionToCountry.put("CO", 57);
            countryToRegion.put(57, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("VE");
            regionToCountry.put("VE", 58);
            countryToRegion.put(58, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MY");
            regionToCountry.put("MY", 60);
            countryToRegion.put(60, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(3);
            listWithRegionCode.add("AU");
            regionToCountry.put("AU", 61);
            listWithRegionCode.add("CC");
            regionToCountry.put("CC", 61);
            listWithRegionCode.add("CX");
            regionToCountry.put("CX", 61);
            countryToRegion.put(61, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("ID");
            regionToCountry.put("ID", 62);
            countryToRegion.put(62, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PH");
            regionToCountry.put("PH", 63);
            countryToRegion.put(63, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("NZ");
            regionToCountry.put("NZ", 64);
            countryToRegion.put(64, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SG");
            regionToCountry.put("SG", 65);
            countryToRegion.put(65, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TH");
            regionToCountry.put("TH", 66);
            countryToRegion.put(66, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("JP");
            regionToCountry.put("JP", 81);
            countryToRegion.put(81, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("KR");
            regionToCountry.put("KR", 82);
            countryToRegion.put(82, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("VN");
            regionToCountry.put("VN", 84);
            countryToRegion.put(84, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CN");
            regionToCountry.put("CN", 86);
            countryToRegion.put(86, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TR");
            regionToCountry.put("TR", 90);
            countryToRegion.put(90, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("IN");
            regionToCountry.put("IN", 91);
            countryToRegion.put(91, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PK");
            regionToCountry.put("PK", 92);
            countryToRegion.put(92, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("AF");
            regionToCountry.put("AF", 93);
            countryToRegion.put(93, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("LK");
            regionToCountry.put("LK", 94);
            countryToRegion.put(94, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MM");
            regionToCountry.put("MM", 95);
            countryToRegion.put(95, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("IR");
            regionToCountry.put("IR", 98);
            countryToRegion.put(98, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SS");
            regionToCountry.put("SS", 211);
            countryToRegion.put(211, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(2);
            listWithRegionCode.add("MA");
            regionToCountry.put("MA", 212);
            listWithRegionCode.add("EH");
            regionToCountry.put("EH", 212);
            countryToRegion.put(212, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("DZ");
            regionToCountry.put("DZ", 213);
            countryToRegion.put(213, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TN");
            regionToCountry.put("TN", 216);
            countryToRegion.put(216, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("LY");
            regionToCountry.put("LY", 218);
            countryToRegion.put(218, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GM");
            regionToCountry.put("GM", 220);
            countryToRegion.put(220, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SN");
            regionToCountry.put("SN", 221);
            countryToRegion.put(221, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MR");
            regionToCountry.put("MR", 222);
            countryToRegion.put(222, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("ML");
            regionToCountry.put("ML", 223);
            countryToRegion.put(223, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GN");
            regionToCountry.put("GN", 224);
            countryToRegion.put(224, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CI");
            regionToCountry.put("CI", 225);
            countryToRegion.put(225, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BF");
            regionToCountry.put("BF", 226);
            countryToRegion.put(226, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("NE");
            regionToCountry.put("NE", 227);
            countryToRegion.put(227, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TG");
            regionToCountry.put("TG", 228);
            countryToRegion.put(228, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BJ");
            regionToCountry.put("BJ", 229);
            countryToRegion.put(229, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MU");
            regionToCountry.put("MU", 230);
            countryToRegion.put(230, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("LR");
            regionToCountry.put("LR", 231);
            countryToRegion.put(231, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SL");
            regionToCountry.put("SL", 232);
            countryToRegion.put(232, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GH");
            regionToCountry.put("GH", 233);
            countryToRegion.put(233, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("NG");
            regionToCountry.put("NG", 234);
            countryToRegion.put(234, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TD");
            regionToCountry.put("TD", 235);
            countryToRegion.put(235, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CF");
            regionToCountry.put("CF", 236);
            countryToRegion.put(236, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CM");
            regionToCountry.put("CM", 237);
            countryToRegion.put(237, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CV");
            regionToCountry.put("CV", 238);
            countryToRegion.put(238, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("ST");
            regionToCountry.put("ST", 239);
            countryToRegion.put(239, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GQ");
            regionToCountry.put("GQ", 240);
            countryToRegion.put(240, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GA");
            regionToCountry.put("GA", 241);
            countryToRegion.put(241, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CG");
            regionToCountry.put("CG", 242);
            countryToRegion.put(242, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CD");
            regionToCountry.put("CD", 243);
            countryToRegion.put(243, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("AO");
            regionToCountry.put("AO", 244);
            countryToRegion.put(244, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GW");
            regionToCountry.put("GW", 245);
            countryToRegion.put(245, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("IO");
            regionToCountry.put("IO", 246);
            countryToRegion.put(246, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("AC");
            regionToCountry.put("AC", 247);
            countryToRegion.put(247, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SC");
            regionToCountry.put("SC", 248);
            countryToRegion.put(248, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SD");
            regionToCountry.put("SD", 249);
            countryToRegion.put(249, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("RW");
            regionToCountry.put("RW", 250);
            countryToRegion.put(250, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("ET");
            regionToCountry.put("ET", 251);
            countryToRegion.put(251, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SO");
            regionToCountry.put("SO", 252);
            countryToRegion.put(252, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("DJ");
            regionToCountry.put("DJ", 253);
            countryToRegion.put(253, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("KE");
            regionToCountry.put("KE", 254);
            countryToRegion.put(254, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TZ");
            regionToCountry.put("TZ", 255);
            countryToRegion.put(255, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("UG");
            regionToCountry.put("UG", 256);
            countryToRegion.put(256, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BI");
            regionToCountry.put("BI", 257);
            countryToRegion.put(257, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MZ");
            regionToCountry.put("MZ", 258);
            countryToRegion.put(258, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("ZM");
            regionToCountry.put("ZM", 260);
            countryToRegion.put(260, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MG");
            regionToCountry.put("MG", 261);
            countryToRegion.put(261, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(2);
            listWithRegionCode.add("RE");
            regionToCountry.put("RE", 262);
            listWithRegionCode.add("YT");
            regionToCountry.put("YT", 262);
            countryToRegion.put(262, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("ZW");
            regionToCountry.put("ZW", 263);
            countryToRegion.put(263, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("NA");
            regionToCountry.put("NA", 264);
            countryToRegion.put(264, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MW");
            regionToCountry.put("MW", 265);
            countryToRegion.put(265, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("LS");
            regionToCountry.put("LS", 266);
            countryToRegion.put(266, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BW");
            regionToCountry.put("BW", 267);
            countryToRegion.put(267, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SZ");
            regionToCountry.put("SZ", 268);
            countryToRegion.put(268, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("KM");
            regionToCountry.put("KM", 269);
            countryToRegion.put(269, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(2);
            listWithRegionCode.add("SH");
            regionToCountry.put("SH", 290);
            listWithRegionCode.add("TA");
            regionToCountry.put("TA", 290);
            countryToRegion.put(290, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("ER");
            regionToCountry.put("ER", 291);
            countryToRegion.put(291, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("AW");
            regionToCountry.put("AW", 297);
            countryToRegion.put(297, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("FO");
            regionToCountry.put("FO", 298);
            countryToRegion.put(298, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GL");
            regionToCountry.put("GL", 299);
            countryToRegion.put(299, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GI");
            regionToCountry.put("GI", 350);
            countryToRegion.put(350, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PT");
            regionToCountry.put("PT", 351);
            countryToRegion.put(351, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("LU");
            regionToCountry.put("LU", 352);
            countryToRegion.put(352, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("IE");
            regionToCountry.put("IE", 353);
            countryToRegion.put(353, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("IS");
            regionToCountry.put("IS", 354);
            countryToRegion.put(354, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("AL");
            regionToCountry.put("AL", 355);
            countryToRegion.put(355, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MT");
            regionToCountry.put("MT", 356);
            countryToRegion.put(356, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CY");
            regionToCountry.put("CY", 357);
            countryToRegion.put(357, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(2);
            listWithRegionCode.add("FI");
            regionToCountry.put("FI", 358);
            listWithRegionCode.add("AX");
            regionToCountry.put("AX", 358);
            countryToRegion.put(358, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BG");
            regionToCountry.put("BG", 359);
            countryToRegion.put(359, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("LT");
            regionToCountry.put("LT", 370);
            countryToRegion.put(370, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("LV");
            regionToCountry.put("LV", 371);
            countryToRegion.put(371, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("EE");
            regionToCountry.put("EE", 372);
            countryToRegion.put(372, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MD");
            regionToCountry.put("MD", 373);
            countryToRegion.put(373, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("AM");
            regionToCountry.put("AM", 374);
            countryToRegion.put(374, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BY");
            regionToCountry.put("BY", 375);
            countryToRegion.put(375, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("AD");
            regionToCountry.put("AD", 376);
            countryToRegion.put(376, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MC");
            regionToCountry.put("MC", 377);
            countryToRegion.put(377, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SM");
            regionToCountry.put("SM", 378);
            countryToRegion.put(378, listWithRegionCode);

            //arrayList = new ArrayList<>(1);
            //arrayList.add("VA");
            //hashMap2.put("VA", Integer.valueOf(379));
            //hashMap.put(Integer.valueOf(379), arrayList);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("UA");
            regionToCountry.put("UA", 380);
            countryToRegion.put(380, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("RS");
            regionToCountry.put("RS", 381);
            countryToRegion.put(381, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("ME");
            regionToCountry.put("ME", 382);
            countryToRegion.put(382, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("HR");
            regionToCountry.put("HR", 385);
            countryToRegion.put(385, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SI");
            regionToCountry.put("SI", 386);
            countryToRegion.put(386, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BA");
            regionToCountry.put("BA", 387);
            countryToRegion.put(387, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MK");
            regionToCountry.put("MK", 389);
            countryToRegion.put(389, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CZ");
            regionToCountry.put("CZ", 420);
            countryToRegion.put(420, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SK");
            regionToCountry.put("SK", 421);
            countryToRegion.put(421, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("LI");
            regionToCountry.put("LI", 423);
            countryToRegion.put(423, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("FK");
            regionToCountry.put("FK", 500);
            countryToRegion.put(500, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BZ");
            regionToCountry.put("BZ", 501);
            countryToRegion.put(501, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GT");
            regionToCountry.put("GT", 502);
            countryToRegion.put(502, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SV");
            regionToCountry.put("SV", 503);
            countryToRegion.put(503, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("HN");
            regionToCountry.put("HN", 504);
            countryToRegion.put(504, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("NI");
            regionToCountry.put("NI", 505);
            countryToRegion.put(505, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CR");
            regionToCountry.put("CR", 506);
            countryToRegion.put(506, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PA");
            regionToCountry.put("PA", 507);
            countryToRegion.put(507, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PM");
            regionToCountry.put("PM", 508);
            countryToRegion.put(508, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("HT");
            regionToCountry.put("HT", 509);
            countryToRegion.put(509, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(3);
            listWithRegionCode.add("GP");
            regionToCountry.put("GP", 590);
            listWithRegionCode.add("BL");
            regionToCountry.put("BL", 590);
            listWithRegionCode.add("MF");
            regionToCountry.put("MF", 590);
            countryToRegion.put(590, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BO");
            regionToCountry.put("BO", 591);
            countryToRegion.put(591, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GY");
            regionToCountry.put("GY", 592);
            countryToRegion.put(592, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("EC");
            regionToCountry.put("EC", 593);
            countryToRegion.put(593, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GF");
            regionToCountry.put("GF", 594);
            countryToRegion.put(594, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PY");
            regionToCountry.put("PY", 595);
            countryToRegion.put(595, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MQ");
            regionToCountry.put("MQ", 596);
            countryToRegion.put(596, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SR");
            regionToCountry.put("SR", 597);
            countryToRegion.put(597, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("UY");
            regionToCountry.put("UY", 598);
            countryToRegion.put(598, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(2);
            listWithRegionCode.add("CW");
            regionToCountry.put("CW", 599);
            listWithRegionCode.add("BQ");
            regionToCountry.put("BQ", 599);
            countryToRegion.put(599, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TL");
            regionToCountry.put("TL", 670);
            countryToRegion.put(670, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("NF");
            regionToCountry.put("NF", 672);
            countryToRegion.put(672, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BN");
            regionToCountry.put("BN", 673);
            countryToRegion.put(673, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("NR");
            regionToCountry.put("NR", 674);
            countryToRegion.put(674, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PG");
            regionToCountry.put("PG", 675);
            countryToRegion.put(675, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TO");
            regionToCountry.put("TO", 676);
            countryToRegion.put(676, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SB");
            regionToCountry.put("SB", 677);
            countryToRegion.put(677, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("VU");
            regionToCountry.put("VU", 678);
            countryToRegion.put(678, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("FJ");
            regionToCountry.put("FJ", 679);
            countryToRegion.put(679, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PW");
            regionToCountry.put("PW", 680);
            countryToRegion.put(680, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("WF");
            regionToCountry.put("WF", 681);
            countryToRegion.put(681, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("CK");
            regionToCountry.put("CK", 682);
            countryToRegion.put(682, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("NU");
            regionToCountry.put("NU", 683);
            countryToRegion.put(683, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("WS");
            regionToCountry.put("WS", 685);
            countryToRegion.put(685, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("KI");
            regionToCountry.put("KI", 686);
            countryToRegion.put(686, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("NC");
            regionToCountry.put("NC", 687);
            countryToRegion.put(687, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TV");
            regionToCountry.put("TV", 688);
            countryToRegion.put(688, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PF");
            regionToCountry.put("PF", 689);
            countryToRegion.put(689, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TK");
            regionToCountry.put("TK", 690);
            countryToRegion.put(690, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("FM");
            regionToCountry.put("FM", 691);
            countryToRegion.put(691, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MH");
            regionToCountry.put("MH", 692);
            countryToRegion.put(692, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("001");
            countryToRegion.put(800, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("001");
            countryToRegion.put(808, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("KP");
            regionToCountry.put("KP", 850);
            countryToRegion.put(850, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("HK");
            regionToCountry.put("HK", 852);
            countryToRegion.put(852, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MO");
            regionToCountry.put("MO", 853);
            countryToRegion.put(853, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("KH");
            regionToCountry.put("KH", 855);
            countryToRegion.put(855, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("LA");
            regionToCountry.put("LA", 856);
            countryToRegion.put(856, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("001");
            countryToRegion.put(870, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("001");
            countryToRegion.put(878, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BD");
            regionToCountry.put("BD", 880);
            countryToRegion.put(880, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("001");
            countryToRegion.put(881, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("001");
            countryToRegion.put(882, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("001");
            countryToRegion.put(883, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TW");
            regionToCountry.put("TW", 886);
            countryToRegion.put(886, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("001");
            countryToRegion.put(888, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MV");
            regionToCountry.put("MV", 960);
            countryToRegion.put(960, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("LB");
            regionToCountry.put("LB", 961);
            countryToRegion.put(961, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("JO");
            regionToCountry.put("JO", 962);
            countryToRegion.put(962, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SY");
            regionToCountry.put("SY", 963);
            countryToRegion.put(963, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("IQ");
            regionToCountry.put("IQ", 964);
            countryToRegion.put(964, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("KW");
            regionToCountry.put("KW", 965);
            countryToRegion.put(965, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("SA");
            regionToCountry.put("SA", 966);
            countryToRegion.put(966, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("YE");
            regionToCountry.put("YE", 967);
            countryToRegion.put(967, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("OM");
            regionToCountry.put("OM", 968);
            countryToRegion.put(968, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("PS");
            regionToCountry.put("PS", 970);
            countryToRegion.put(970, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("AE");
            regionToCountry.put("AE", 971);
            countryToRegion.put(971, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("IL");
            regionToCountry.put("IL", 972);
            countryToRegion.put(972, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BH");
            regionToCountry.put("BH", 973);
            countryToRegion.put(973, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("QA");
            regionToCountry.put("QA", 974);
            countryToRegion.put(974, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("BT");
            regionToCountry.put("BT", 975);
            countryToRegion.put(975, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("MN");
            regionToCountry.put("MN", 976);
            countryToRegion.put(976, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("NP");
            regionToCountry.put("NP", 977);
            countryToRegion.put(977, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("001");
            countryToRegion.put(979, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TJ");
            regionToCountry.put("TJ", 992);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("TM");
            regionToCountry.put("TM", 993);
            countryToRegion.put(993, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("AZ");
            regionToCountry.put("AZ", 994);
            countryToRegion.put(994, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("GE");
            regionToCountry.put("GE", 995);
            countryToRegion.put(995, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("KG");
            regionToCountry.put("KG", 996);
            countryToRegion.put(996, listWithRegionCode);

            listWithRegionCode = new ArrayList<>(1);
            listWithRegionCode.add("UZ");
            regionToCountry.put("UZ", 998);
            countryToRegion.put(998, listWithRegionCode);

            countryCodeToRegionCodeMap = Collections.unmodifiableMap(countryToRegion);
            regionCodeToCountryCodeMap = Collections.unmodifiableMap(regionToCountry);
        }
    }
}
