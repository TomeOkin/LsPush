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
    private final List<CountryCodeData> list;
    private final ArrayList<CountryCodeData> b;

    public CountryCodeDataAdapter(Context context, List<CountryCodeData> list) {
        super(context, R.layout.row_menu_item, list);
        this.list = list;
        this.b = new ArrayList<>();
        b.addAll(list);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu_item, parent, false);
            convertView.setPadding(0, 0, 0, 0);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.row_simple_text_textview);
        tv.setText(getItem(position).formatWithDescription());
        return convertView;
    }

    public void clearList() {
        list.clear();
    }

    public void restoreList() {
        list.addAll(b);
    }

    public ArrayList<CountryCodeData> getBackList() {
        return b;
    }

    public void addCountryCodeData(CountryCodeData countryCodeData) {
        list.add(countryCodeData);
    }
}
