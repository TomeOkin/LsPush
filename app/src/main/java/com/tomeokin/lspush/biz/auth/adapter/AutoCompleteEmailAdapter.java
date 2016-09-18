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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.tomeokin.lspush.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteEmailAdapter extends ArrayAdapter<String> {
    private final List<String> mEmptyList;
    private final List<String> mHintList;
    List<String> filteredList = new ArrayList<>();

    public AutoCompleteEmailAdapter(Context context, List<String> emptyList, List<String> hintList) {
        super(context, R.layout.row_autocomplete_email, emptyList);
        mEmptyList = emptyList;
        mHintList = hintList;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new EmailFilter(this, mEmptyList, mHintList);
    }

    @Override
    public String getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
