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
package com.tomeokin.lspush.biz.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends BaseActivity {
    private static final String ARG_UID = "arg.uid";

    private String mUid;

    @BindView(R.id.appBar) AppBarLayout mAppBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    //@BindView(R.id.toolbar_user_layout) View mToolbarUserField;
    //@BindView(R.id.toolbar_user_avatar) ImageView mToolbarAvatar;
    //@BindView(R.id.toolbar_user_nickname) TextView mToolbarNickname;
    //
    //@BindView(R.id.user_header_avatar) ImageView mHeaderAvatar;
    //@BindView(R.id.user_header_nickname) TextView mHeaderNickname;
    //@BindView(R.id.user_header_uid) TextView mHeaderUid;

    public static void start(Context context, String uid) {
        Intent starter = new Intent(context, UserActivity.class);
        starter.putExtra(ARG_UID, uid);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        mUid = getIntent().getStringExtra(ARG_UID);
        //mAppBar.addOnOffsetChangedListener(mOnOffsetChangeListener);
    }

    public void setToolbarAlpha(int alpha) {
        //mToolbarAvatar.getDrawable().setAlpha(alpha);
        //mToolbarNickname.setTextColor(Color.argb(alpha, 255, 255, 255));
        //mToolbarUserField mZhangdan.getDrawable().setAlpha(alpha);
        //mZhangdan_txt.setTextColor(Color.argb(alpha, 255, 255, 255));
        //mTongxunlu.getDrawable().setAlpha(alpha);
        //mJiahao.getDrawable().setAlpha(alpha);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mAppBar.removeOnOffsetChangedListener(mOnOffsetChangeListener);
    }
}
