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
package com.tomeokin.lspush.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.tomeokin.lspush.R;

/**
 * @see <a href="http://blog-shaggywork.rhcloud.com/2016/04/08/handling-hint-text-margin-between-edittext-and-textinputlayout/">Handling
 * Hint Text Margin between EditText and TextInputLayout</a>, a little change
 * @see <a href="https://developer.android.com/reference/android/support/design/widget/TextInputLayout.html">Offical
 * TextInputLayout Page</a>
 */
public class MarginTextInputLayout extends TextInputLayout {
    private float mHintBottomMargin = 0;

    public MarginTextInputLayout(Context context) {
        super(context);
    }

    public MarginTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MarginTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.MarginTextInputLayout);
        mHintBottomMargin =
            attributes.getDimension(R.styleable.MarginTextInputLayout_hintBottomMargin, mHintBottomMargin);
        attributes.recycle();
    }

    @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (child instanceof EditText && params instanceof android.widget.LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) params;
            llp.topMargin += mHintBottomMargin;
        }
    }
}
