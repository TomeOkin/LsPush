package com.tomeokin.lspush.biz.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.auth.SignOutActivity;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.injection.ProvideComponent;
import com.tomeokin.lspush.injection.component.DaggerMainComponent;
import com.tomeokin.lspush.injection.component.MainComponent;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements BaseActionCallback, ProvideComponent<MainComponent> {
    private MainComponent mComponent;
    @Inject MainPresenter mPresenter;

    @Override public MainComponent component() {
        if (mComponent == null) {
            mComponent = DaggerMainComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build();
        }
        return mComponent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        component().inject(this);
        mPresenter.attachView(this);
        boolean hasLogin = mPresenter.hasLogin();
        if (!hasLogin) {
            Intent intent = new Intent(this, SignOutActivity.class);
            startActivity(intent);
            finish();
        }

        //Bundle bundle = CollectionTargetFragment.prepareArgument("http://www.jianshu.com/p/2a9fcf3c11e4");
        //Navigator.moveTo(this, CollectionTargetFragment.class, bundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        mPresenter = null;
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {

    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {

    }
}
