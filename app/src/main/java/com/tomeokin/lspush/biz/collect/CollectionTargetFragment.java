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
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseFragment;

public class CollectionTargetFragment extends BaseFragment implements View.OnTouchListener {
    public static final String EXTRA_TARGET_URL = "extra.target.url";

    private WebView mWebView;
    private String mUrl;
    private float mTouchX, mTouchY;

    public static Bundle prepareArgument(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TARGET_URL, url);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(EXTRA_TARGET_URL);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_target, container, false);
        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebView.loadUrl(mUrl);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsInterface(getContext()), getString(R.string.web_image_click_target));
        mWebView.setOnTouchListener(this);
        return view;
    }

    private class JsInterface {
        private Context mContext;
        private ImageDialogFragment dialog;

        public JsInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void click(String url) {
            dialog = new ImageDialogFragment.Builder(mContext, getFragmentManager()).show(url);
            //Intent intent = new Intent(mContext,ImgUrlActivity.class);
            //intent.putExtra("url", url);
            //startActivity(intent);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //In response to the picture on the web click event by wenview touch
        float density = getResources().getDisplayMetrics().density; //Screen density
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
        mWebView.loadUrl(js);
    }
}
