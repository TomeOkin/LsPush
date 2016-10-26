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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import com.tomeokin.lspush.biz.base.BaseFragment;
import com.tomeokin.lspush.biz.base.support.BaseActionCallback;
import com.tomeokin.lspush.biz.usercase.collection.CollectionAction;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.data.model.Image;
import com.tomeokin.lspush.data.model.Link;
import com.tomeokin.lspush.data.model.User;
import com.tomeokin.lspush.data.model.WebPageInfo;
import com.tomeokin.lspush.injection.component.HomeComponent;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class HomeFragment extends BaseFragment
    implements BaseActionCallback, CollectionListAdapter.Callback, UriDialogFragment.OnUrlConfirmListener {
    private static final int REQUEST_OPEN_COLLECTION = 201;
    private static final int REQUEST_EDIT_COLLECTION = 202;
    private static final int REQUEST_GET_URL_INFO = 203;

    private Unbinder mUnBinder;
    private List<Collection> mColList;
    private CollectionListAdapter mColListAdapter;
    private UriDialogFragment.Builder mUriDialogBuilder;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.col_rv) RecyclerView mColRv;

    @Inject CollectionAction mCollectionAction;
    @Inject CollectionHolder mCollectionHolder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        component(HomeComponent.class).inject(this);

        User user = new User();
        user.setUid("one");
        user.setNickname("One");

        Link link = new Link();
        link.setTitle("Tencent/tinker");
        link.setUrl("https://github.com/Tencent/tinker");

        Collection collection = new Collection();
        collection.setUser(user);
        collection.setLink(link);
        collection.setDescription(
            "tinker - Tinker is a hot-fix solution library for Android, it supports dex, library and resources update without reinstall apk.");
        Image image = new Image();
        image.setWidth(660);
        image.setHeight(386);
        image.setColor(0);
        image.setUrl("https://github.com/Tencent/tinker/raw/dev/assets/tinker.png");
        collection.setImage(image);
        collection.setId(1);
        Instant now = Instant.now();
        Date create = DateTimeUtils.toDate(now);
        collection.setCreateDate(create);
        collection.setUpdateDate(create);
        collection.setExplorers(Collections.singletonList(user));
        collection.setTags(Arrays.asList("github", "热修复", "Tencent", "hot-fix"));
        collection.setFavorCount(101);
        collection.setHasFavor(true);
        collection.setHasRead(false);

        mColList = new ArrayList<>(1);
        mColList.add(collection);

        //Bundle bundle = CollectionTargetFragment.prepareArgument("http://www.jianshu.com/p/2a9fcf3c11e4");
        //Navigator.moveTo(this, CollectionTargetFragment.class, bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mUnBinder = ButterKnife.bind(this, view);
        setupToolbar();

        mColListAdapter = new CollectionListAdapter(getActivity(), mColList, this);
        mColRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mColRv.setAdapter(mColListAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCollectionAction.attach(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
        mCollectionAction.detach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnBinder = null;
        mCollectionAction = null;
        mCollectionHolder.setCollection(null);
        mCollectionHolder = null;
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
                showUriDialog();
                return true;
            case R.id.action_pin:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showUriDialog() {
        if (mUriDialogBuilder == null) {
            mUriDialogBuilder =
                new UriDialogFragment.Builder(getContext(), getFragmentManager()).setTargetFragment(this,
                    REQUEST_GET_URL_INFO);
        }
        mUriDialogBuilder.show();
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {

    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {

    }

    @Override
    public void onFavorChange(Collection collection) {

    }

    @Override
    public void onShowMoreExplorers(Collection collection) {

    }

    @Override
    public void onOpenCollectionUrl(Collection collection) {
        //CollectionWebViewActivity.start(this, collection, REQUEST_OPEN_COLLECTION);
        mCollectionHolder.setCollection(collection);
        CollectionWebViewActivity.start(this, null, REQUEST_OPEN_COLLECTION);
    }

    @Override
    public void onUrlConfirm(@Nullable WebPageInfo webPageInfo) {
        if (webPageInfo != null) {
            Timber.i("url info: %s", webPageInfo.toString());
            //CollectionEditorActivity.start(this, webPageInfo.toCollection(), REQUEST_EDIT_COLLECTION);
            mCollectionHolder.setCollection(webPageInfo.toCollection());
            CollectionEditorActivity.start(this, null, REQUEST_EDIT_COLLECTION);
        } else {
            Timber.i("WebPageInfo is null");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.v("Result %d, requestCode %d", resultCode, requestCode);
        if (resultCode != Activity.RESULT_OK) {
            Timber.w("Error result %d, requestCode %d", resultCode, requestCode);
            return;
        }

        if (requestCode == REQUEST_OPEN_COLLECTION) {
            Collection collection = mCollectionHolder.getCollection();
            if (collection != null) {
                mColListAdapter.updateColList(collection);
            }
            //if (data != null) {
            //    //Collection collection = data.getParcelableExtra(CollectionWebViewActivity.REQUEST_RESULT_COLLECTION);
            //
            //}
        } else if (requestCode == REQUEST_EDIT_COLLECTION) {
            // TODO: 2016/10/25 移动到最上方，刷新
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
