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
package com.tomeokin.lspush.biz.auth;

import android.annotation.SuppressLint;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.auth.adapter.CountryCodeDataAdapter;
import com.tomeokin.lspush.biz.auth.listener.OnCountryCodeSelectedListener;
import com.tomeokin.lspush.common.CountryCodeUtils;
import com.tomeokin.lspush.common.StringUtils;
import com.tomeokin.lspush.data.model.CountryCodeData;
import com.tomeokin.lspush.ui.colorfilter.ColorFilterCache;
import com.tomeokin.lspush.ui.widget.listener.OnFilterTextListener;
import com.tomeokin.lspush.ui.widget.SearchEditText;
import com.tomeokin.lspush.ui.widget.dialog.BaseDialogFragment;

import java.util.List;
import java.util.Locale;

public class CountryCodePickerDialog extends BaseDialogFragment {
    private List<CountryCodeData> countryCodeDatas;
    public SearchEditText searchEditText;
    public CountryCodeDataAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countryCodeDatas = CountryCodeUtils.getCountryCodeDatas(getContext());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    protected Builder config(@NonNull Builder builder) {
        builder.setTitle(getString(R.string.select_your_country).toUpperCase(Locale.getDefault()));

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_country_codes, null);
        ListView listView = (ListView) view.findViewById(R.id.country_code_list);
        searchEditText = (SearchEditText) view.findViewById(R.id.search);
        searchEditText.setOnFilterTextListener(new OnFilterTextListener() {
            @Override
            public void onTextChanged(SearchEditText editText, CharSequence text, int start, int lengthBefore,
                int lengthAfter) {
                String str = StringUtils.removeBlank(text).toLowerCase(Locale.getDefault());
                adapter.clearList();
                if (TextUtils.isEmpty(str)) {
                    adapter.restoreList();
                    adapter.notifyDataSetChanged();
                } else {
                    for (CountryCodeData countryCodeData : adapter.getBackList()) {
                        if (StringUtils.isEqualWithNature(countryCodeData.formatWithDescription(), str, 0)
                            || StringUtils.isEqualWithNature(countryCodeData.countryCode, str, 0)
                            || StringUtils.isEqualWithNature(countryCodeData.formatCountryCode(), str, 0)) {
                            adapter.addCountryCodeData(countryCodeData);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onTextCompleted(SearchEditText editText, String text) {

            }
        });
        ColorFilter colorFilter =
            ColorFilterCache.getColorFilter(ContextCompat.getColor(getContext(), R.color.grey_light));
        searchEditText.getCompoundDrawables()[0].mutate().setColorFilter(colorFilter);
        searchEditText.setClearButtonColorFilter(colorFilter);
        adapter = new CountryCodeDataAdapter(getContext(), countryCodeDatas);
        listView.setAdapter(adapter);

        builder.addCustomMessageView(view);
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountryCodeData countryCodeData = (CountryCodeData) parent.getItemAtPosition(position);
                ((OnCountryCodeSelectedListener) getTargetFragment()).onCountryCodeSelected(countryCodeData);
                getDialog().dismiss();
            }
        });
        return builder;
    }
}
