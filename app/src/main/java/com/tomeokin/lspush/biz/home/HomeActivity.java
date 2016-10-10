package com.tomeokin.lspush.biz.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.auth.AuthActivity;
import com.tomeokin.lspush.biz.base.support.BaseActionCallback;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.usercase.user.LocalUserInfoAction;
import com.tomeokin.lspush.common.Navigator;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.injection.ProvideComponent;
import com.tomeokin.lspush.injection.component.DaggerHomeComponent;
import com.tomeokin.lspush.injection.component.HomeComponent;
import com.tomeokin.lspush.injection.module.HomeModule;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import javax.inject.Inject;

public class HomeActivity extends BaseActivity implements BaseActionCallback, ProvideComponent<HomeComponent> {
    private HomeComponent mComponent;
    @Inject LocalUserInfoAction mLocalUserInfoAction;

    @Override
    public HomeComponent component() {
        if (mComponent == null) {
            mComponent = DaggerHomeComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .homeModule(new HomeModule())
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
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_home);

        component().inject(this);
        mLocalUserInfoAction.attach(this);
        AccessResponse accessResponse = mLocalUserInfoAction.getAccessResponse();
        if (accessResponse == null) {
            Navigator.moveTo(this, SplashFragment.class, null, false);
            return;
        }



        // access response is not null, we move to home fragment
        //initView();
        Navigator.moveTo(this, HomeFragment.class, null, false);

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
            if (accessResponse != null) {
                //initView();
                Navigator.moveTo(this, HomeFragment.class, null, false);
            } else {
                Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    //private void initView() {
    //    ButterKnife.bind(this);
    //}
}
