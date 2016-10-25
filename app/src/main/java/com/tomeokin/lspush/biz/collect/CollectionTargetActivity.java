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
package com.tomeokin.lspush.biz.collect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.ui.widget.BaseWebViewActivity;
import com.tomeokin.lspush.ui.widget.dialog.OnActionClickListener;

import timber.log.Timber;

public class CollectionTargetActivity extends BaseWebViewActivity
    implements View.OnTouchListener, OnActionClickListener {
    public static final String EXTRA_TARGET_URL = "extra.target.url";
    public static final int REQUEST_DIALOG = 201;
    public static final String REQUEST_RESULT_IMAGE_URL = "request.result.image.url";

    private String mUrl;
    private float mTouchX, mTouchY;
    private String mImageUrl;
    private boolean mSelect;

    public static void start(Activity activity, String url, int requestCode) {
        Intent starter = new Intent(activity, CollectionTargetActivity.class);
        starter.putExtra(EXTRA_TARGET_URL, url);
        activity.startActivityForResult(starter, requestCode);
        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.hold);
    }

    @Override
    protected boolean onPrepareActivity() {
        mUrl = getIntent().getStringExtra(EXTRA_TARGET_URL);
        return !TextUtils.isEmpty(mUrl);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onPrepareWebView(WebView webView) {
        super.onPrepareWebView(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        // https://labs.mwrinfosecurity.com/blog/webview-addjavascriptinterface-remote-code-execution/
        // FIXME: 2016/9/20 fix it later
        webView.addJavascriptInterface(new JsInterface(this), getString(R.string.web_image_click_target));
        // http://blog.csdn.net/u013107656/article/details/51729398
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        webView.removeJavascriptInterface("accessibilityTraversal");
        webView.removeJavascriptInterface("accessibility");
        webView.setOnTouchListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSubTitle(getString(R.string.select_description_image));
    }

    @Override
    protected String onPrepareUrl() {
        return mUrl;
    }

    @Override
    public void onDialogActionClick(DialogInterface dialog, int requestCode, int which) {
        if (requestCode == REQUEST_DIALOG) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                Timber.v("ok is click");
                mSelect = true;
                onBackPressed();
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                Timber.v("cancel is click");
            }
        }
    }

    private class JsInterface {
        private final Context mContext;

        public JsInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void click(String url, int width, int height) {
            mImageUrl = url;
            final float density = getResources().getDisplayMetrics().density;
            new ImageDialogFragment.Builder(mContext, getSupportFragmentManager()).setRequestCode(REQUEST_DIALOG)
                .show(url, (int) (width * density), (int) (height * density));
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //In response to the picture on the web click event by wenview touch
        final float density = getResources().getDisplayMetrics().density; //Screen density
        float touchX = event.getX() / density;  //Must be divided by the density of the screen
        float touchY = event.getY() / density;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchX = touchX;
            mTouchY = touchY;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            float dx = Math.abs(touchX - mTouchX);
            float dy = Math.abs(touchY - mTouchY);
            if (dx < 10.0 / density && dy < 10.0 / density) {
                clickImage(touchX, touchY);
            }
        }
        return false;
    }

    private void clickImage(float touchX, float touchY) {
        final String js = getString(R.string.web_image_click, touchX, touchY);
        getWebView().loadUrl(js);
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        if (mSelect) {
            data.putExtra(REQUEST_RESULT_IMAGE_URL, mImageUrl);
        }
        setResult(Activity.RESULT_OK, data);
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
    }
}
