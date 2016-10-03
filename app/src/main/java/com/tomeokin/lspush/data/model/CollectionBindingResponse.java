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
package com.tomeokin.lspush.data.model;

public class CollectionBindingResponse extends BaseResponse {
    private CollectionBinding colBinding;

    public CollectionBindingResponse(CollectionBinding favor) {
        this.colBinding = favor;
    }

    public CollectionBindingResponse(int resultCode, String result, CollectionBinding favor) {
        super(resultCode, result);
        this.colBinding = favor;
    }

    public CollectionBinding getCollectionBinding() {
        return colBinding;
    }

    public void setCollectionBinding(CollectionBinding favor) {
        this.colBinding = favor;
    }
}
