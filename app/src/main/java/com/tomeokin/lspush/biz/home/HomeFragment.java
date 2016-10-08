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
package com.tomeokin.lspush.biz.home;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.BaseFragment;
import com.tomeokin.lspush.biz.usercase.collection.ObtainLatestCollectionsAction;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.injection.component.HomeComponent;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment implements BaseActionCallback {
    private Unbinder mUnBinder;
    @Nullable @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.col_rv) RecyclerView mColRv;

    @Inject ObtainLatestCollectionsAction mObtainLatestColAction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        component(HomeComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mUnBinder = ButterKnife.bind(this, view);
        setupToolbar();

        Toast.makeText(getContext(), "I'm HomeFragment", Toast.LENGTH_SHORT).show();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mObtainLatestColAction.attach(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
        mObtainLatestColAction.detach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnBinder = null;
        mObtainLatestColAction = null;
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(mToolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle(R.string.title_latest_collections);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_home_menu, menu);
        Drawable drawable = menu.findItem(R.id.action_search).getIcon();
        DrawableCompat.setTint(drawable, Color.WHITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(getContext(), "search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_add:
                return true;
            case R.id.action_pin:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {

    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {

    }
}
