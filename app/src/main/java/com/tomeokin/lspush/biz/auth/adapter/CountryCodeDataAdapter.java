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
package com.tomeokin.lspush.biz.auth.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.data.model.CountryCodeData;

import java.util.ArrayList;
import java.util.List;

public final class CountryCodeDataAdapter extends ArrayAdapter<CountryCodeData> {
    private final List<CountryCodeData> mList;
    private final ArrayList<CountryCodeData> mCountryCodeList;

    public CountryCodeDataAdapter(Context context, List<CountryCodeData> list) {
        super(context, R.layout.layout_row_item, list);
        mList = list;
        mCountryCodeList = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_item, parent, false);
            convertView.setPadding(0, 0, 0, 0);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.row_simple_text_textview);
        if (getItem(position) != null) {
            tv.setText(getItem(position).formatWithDescription());
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public void clearList() {
        mList.clear();
    }

    public void restoreList() {
        mList.addAll(mCountryCodeList);
    }

    public ArrayList<CountryCodeData> getBackList() {
        return mCountryCodeList;
    }

    public void addCountryCodeData(CountryCodeData countryCodeData) {
        mList.add(countryCodeData);
    }
}
