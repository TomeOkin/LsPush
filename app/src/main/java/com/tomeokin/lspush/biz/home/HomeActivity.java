package com.tomeokin.lspush.biz.home;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.auth.AuthActivity;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.biz.base.support.BaseActionCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.job.sync.SyncService;
import com.tomeokin.lspush.biz.usercase.user.LocalUserInfoAction;
import com.tomeokin.lspush.biz.usercase.user.LsPushUserState;
import com.tomeokin.lspush.ui.navigator.Navigator;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.injection.ProvideComponent;
import com.tomeokin.lspush.injection.component.DaggerHomeComponent;
import com.tomeokin.lspush.injection.component.HomeComponent;
import com.tomeokin.lspush.injection.module.CollectionModule;

import javax.inject.Inject;

import timber.log.Timber;

public class HomeActivity extends BaseActivity implements BaseActionCallback, ProvideComponent<HomeComponent> {
    private HomeComponent mComponent;
    private ServiceConnection mServiceConnection;

    @Inject LocalUserInfoAction mLocalUserInfoAction;
    @Inject LsPushUserState mLsPushUserState;

    @Override
    public HomeComponent component() {
        if (mComponent == null) {
            mComponent = DaggerHomeComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .collectionModule(new CollectionModule())
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
        enter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
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
                //Navigator.moveTo(this, HomeFragment.class, null, false);
                enter();
            } else {
                AuthActivity.start(this);
                finish();
            }
        }
    }

    private void enter() {
        SyncService.start(this);
        mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                Timber.v("sync service connected");
                SyncService.SyncBinder binder = (SyncService.SyncBinder) service;
                binder.getService().sync(new SyncService.Callback() {
                    @Override
                    public void onSuccess() {
                        Timber.v("onSuccess");
                        if (mLsPushUserState.getAccessResponse() != null) {
                            Navigator.moveTo(HomeActivity.this, HomeFragment.class, null, false);
                        } else {
                            Timber.w("user is not login");
                            AuthActivity.start(HomeActivity.this);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Timber.w("sync service onFailure");
                        AuthActivity.start(HomeActivity.this);
                        finish();
                    }
                });
            }

            public void onServiceDisconnected(ComponentName className) {
                Timber.v("sync service disconnected");
            }
        };

        final Intent intent = new Intent(this, SyncService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    //private void initView() {
    //    ButterKnife.bind(this);
    //}
}
