package com.tomeokin.lspush.biz.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.auth.SignOutActivity;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.usercase.LocalUserInfoAction;
import com.tomeokin.lspush.common.Navigator;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.injection.ProvideComponent;
import com.tomeokin.lspush.injection.component.DaggerMainComponent;
import com.tomeokin.lspush.injection.component.MainComponent;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements BaseActionCallback, ProvideComponent<MainComponent> {
    private MainComponent mComponent;
    @Inject LocalUserInfoAction mLocalUserInfoAction;

    @Override
    public MainComponent component() {
        if (mComponent == null) {
            mComponent = DaggerMainComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build();
        }
        return mComponent;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // when received a new Intent
        //setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        component().inject(this);
        mLocalUserInfoAction.attach(this);
        AccessResponse accessResponse = mLocalUserInfoAction.getAccessResponse();
        if (accessResponse == null) {
            //Navigator.moveTo(this, SplashFragment.class, null, false);
            return;
        }

        // access response is not null, we move to main fragment
        Navigator.moveTo(this, MainFragment.class, null);

        //Bundle bundle = CollectionTargetFragment.prepareArgument("http://www.jianshu.com/p/2a9fcf3c11e4");
        //Navigator.moveTo(this, CollectionTargetFragment.class, bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalUserInfoAction.detach();
        mLocalUserInfoAction = null;
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {
        if (action == UserScene.ACTION_GET_ACCESS_RESPONSE) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == UserScene.ACTION_GET_ACCESS_RESPONSE) {
            AccessResponse accessResponse = (AccessResponse) response;
            setTheme(R.style.AppTheme);
            if (accessResponse != null) {
                Navigator.moveTo(this, MainFragment.class, null);
            } else {
                Intent intent = new Intent(this, SignOutActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
