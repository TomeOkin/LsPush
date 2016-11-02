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
package com.tomeokin.lspush.biz.home;

import android.support.v7.util.SortedList;

import com.tomeokin.lspush.data.model.Collection;

public class CollectionSortCallback extends SortedList.Callback<Collection> {
    @Override
    public int compare(Collection o1, Collection o2) {
        // DESC
        return o2.getUpdateDate().compareTo(o1.getUpdateDate());
    }

    @Override
    public void onChanged(int position, int count) {

    }

    @Override
    public boolean areContentsTheSame(Collection oldItem, Collection newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areItemsTheSame(Collection item1, Collection item2) {
        return item1.getId() == item2.getId();
    }

    @Override
    public void onInserted(int position, int count) {

    }

    @Override
    public void onRemoved(int position, int count) {

    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {

    }
}
