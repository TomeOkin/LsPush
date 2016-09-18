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
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.tomeokin.lspush.R;

public class SearchEditText extends AppCompatEditText implements TextView.OnEditorActionListener {
    private Drawable[] mCompoundDrawables;
    private Drawable mRightDrawable;
    private OnFilterTextListener mOnFilterTextListener;
    private InputMethodManager mInputMethodManager;
    private boolean mEnableQuickBackWhenEmpty;
    private boolean mSearchIconEnabled = true;
    private boolean mClearButtonEnabled = true;
    private boolean mTextFilled;
    private boolean mHasLayout = false;
    private boolean mShouldShowInput;

    public SearchEditText(Context context) {
        super(context);
        init(null);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray styles = getContext().obtainStyledAttributes(attributeSet, R.styleable.SearchEditText);
            mEnableQuickBackWhenEmpty = styles.getBoolean(R.styleable.SearchEditText_enableQuickBackWhenEmpty, false);
            styles.recycle();
        }
        setOnEditorActionListener(this);
        mCompoundDrawables = getCompoundDrawables();
        mRightDrawable = mCompoundDrawables[2];
        setCompoundDrawables(mCompoundDrawables[0], mCompoundDrawables[1], null, mCompoundDrawables[3]);

        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void clearFocus() {
        setFocusableInTouchMode(false);
        super.clearFocus();
        setFocusableInTouchMode(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTextFilled
            && mClearButtonEnabled
            && event.getAction() == MotionEvent.ACTION_UP
            && mRightDrawable != null
            && event.getX() > getWidth() - getPaddingRight() - mRightDrawable.getIntrinsicWidth()) {
            setText("");
            requestFocus();
            showInput();
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onPreDraw() {
        boolean isEmpty = TextUtils.isEmpty(getSearchString());
        if (mTextFilled != isEmpty) {
            return super.onPreDraw();
        }
        mTextFilled = !isEmpty;
        updateDrawable();
        return false;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (mOnFilterTextListener != null) {
            mOnFilterTextListener.onTextChanged(this, text, start, lengthBefore, lengthAfter);
        }
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (mEnableQuickBackWhenEmpty
            && event.getKeyCode() == KeyEvent.KEYCODE_BACK
            && event.getAction() == KeyEvent.ACTION_DOWN
            && TextUtils.isEmpty(getSearchString())) {
            hideInput();
        }
        return super.dispatchKeyEventPreIme(event);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId != EditorInfo.IME_ACTION_GO && actionId != EditorInfo.IME_ACTION_SEARCH) {
            return false;
        }
        hideInput();
        if (mOnFilterTextListener != null) {
            mOnFilterTextListener.onTextCompleted(this, getSearchString());
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!TextUtils.isEmpty(getText()) && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                hideInput();
                if (mOnFilterTextListener != null) {
                    mOnFilterTextListener.onTextCompleted(this, getSearchString());
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasLayout) {
            requestFocus();
            mHasLayout = true;
        }

        if (mShouldShowInput) {
            showInput();
            mShouldShowInput = false;
        }
    }

    private void updateDrawable() {
        Drawable left = mSearchIconEnabled ? mCompoundDrawables[0] : null;
        Drawable right = null;
        if (mTextFilled && mClearButtonEnabled) {
            right = mRightDrawable;
        }
        setCompoundDrawables(left, mCompoundDrawables[1], right, mCompoundDrawables[3]);
    }

    public final void hideInput() {
        mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        mShouldShowInput = false;
    }

    public final void showInput() {
        if (mHasLayout) {
            mInputMethodManager.showSoftInput(this, 0);
        } else {
            mShouldShowInput = true;
        }
    }

    public void setSearchIconEnabled(boolean enabled) {
        mSearchIconEnabled = enabled;
        updateDrawable();
    }

    public void setClearButtonEnabled(boolean enabled) {
        mClearButtonEnabled = enabled;
        updateDrawable();
    }

    public void setClearButtonAlpha(int alpha) {
        mRightDrawable.mutate().setAlpha(alpha);
    }

    public void setClearButtonColorFilter(ColorFilter colorFilter) {
        mRightDrawable.mutate().setColorFilter(colorFilter);
    }

    public void setOnFilterTextListener(OnFilterTextListener listener) {
        mOnFilterTextListener = listener;
    }

    public String getSearchString() {
        return getText().toString().trim();
    }

    public CharSequence getTextForSearch() {
        CharSequence text = getText();
        if (TextUtils.isEmpty(text) || text.length() != 1) {
            return text;
        }
        char charAt = text.charAt(0);
        if (charAt == '@' || charAt == '#') {
            return "";
        }
        return text;
    }

    public CharSequence getStrippedText() {
        CharSequence text = getText();
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        char charAt = text.charAt(0);
        if (charAt == '@' || charAt == '#') {
            return text.subSequence(1, text.length());
        }
        return text;
    }
}
