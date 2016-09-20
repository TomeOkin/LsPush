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

import android.text.TextUtils;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmailFilter extends Filter {
    private final AutoCompleteEmailAdapter mAdapter;
    private final List<String> mEmptyList;
    private final List<String> mHintList;
    private final List<String> mFilteredList;

    public EmailFilter(AutoCompleteEmailAdapter adapter, List<String> emptyList, List<String> hintList) {
        mAdapter = adapter;
        mEmptyList = emptyList;
        mHintList = hintList;
        mFilteredList = new ArrayList<>(mEmptyList.size() + mHintList.size());
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        mFilteredList.clear();
        final FilterResults results = new FilterResults();

        if (TextUtils.isEmpty(constraint)) {
            mFilteredList.addAll(mEmptyList);
        } else {
            final String filterPattern = constraint.toString().toLowerCase(Locale.ENGLISH).trim();

            for (final String history : mEmptyList) {
                if (history.toLowerCase(Locale.ENGLISH).contains(filterPattern)) {
                    mFilteredList.add(history);
                }
            }

            final int index = filterPattern.lastIndexOf('@');
            if (index != -1) {
                CharSequence prefix = constraint.subSequence(0, index + 1);
                for (final String hint : mHintList) {
                    final String str = prefix + hint;
                    if (!mFilteredList.contains(str)) {
                        mFilteredList.add(str);
                    }
                }
            }
        }

        results.values = mFilteredList;
        results.count = mFilteredList.size();
        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mAdapter.filteredList.clear();
        mAdapter.filteredList.addAll((List<String>) results.values);
        mAdapter.notifyDataSetChanged();
    }
}
