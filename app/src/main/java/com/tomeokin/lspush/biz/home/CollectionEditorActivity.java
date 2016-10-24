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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.biz.base.support.BaseActionCallback;
import com.tomeokin.lspush.biz.collect.CollectionTargetActivity;
import com.tomeokin.lspush.biz.usercase.collection.CollectionAction;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.injection.ProvideComponent;
import com.tomeokin.lspush.injection.component.CollectionEditorComponent;
import com.tomeokin.lspush.injection.component.DaggerCollectionEditorComponent;
import com.tomeokin.lspush.injection.module.CollectionModule;
import com.tomeokin.lspush.ui.glide.ImageLoader;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CollectionEditorActivity extends BaseActivity
    implements BaseActionCallback, ProvideComponent<CollectionEditorComponent> {
    public static final String REQUEST_RESULT_COLLECTION = "request.result.collection";
    private static final String EXTRA_COLLECTION = "extra.collection";

    private static final int REQUEST_IMAGE_URL = 201;

    private CollectionEditorComponent mComponent;
    private Collection mCollection;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.title) EditText mTitleField;
    @BindView(R.id.description) EditText mDescriptionField;
    @BindView(R.id.description_image) ImageView mDescriptionImageField;

    @Inject CollectionAction mCollectionAction;

    public static void start(@NonNull Fragment source, @Nullable Collection collection, int requestCode) {
        Intent starter = new Intent(source.getContext(), CollectionEditorActivity.class);
        if (collection != null) {
            starter.putExtra(EXTRA_COLLECTION, collection);
        }
        source.startActivityForResult(starter, requestCode);
        source.getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.hold);
    }

    @Override
    public CollectionEditorComponent component() {
        if (mComponent == null) {
            mComponent = DaggerCollectionEditorComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .collectionModule(new CollectionModule())
                .build();
        }
        return mComponent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_editor);
        ButterKnife.bind(this);

        mCollection = getIntent().getParcelableExtra(EXTRA_COLLECTION);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.edit);
        }
        mToolbar.setNavigationIcon(R.drawable.ic_nav_arrow_left);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTitleField.setText(mCollection.getLink().getTitle());
        mDescriptionField.setText(mCollection.getDescription());
        mDescriptionImageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionTargetActivity.start(CollectionEditorActivity.this, mCollection.getLink().getUrl(),
                    REQUEST_IMAGE_URL);
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        component().inject(this);
        mCollectionAction.attach(this);
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(REQUEST_RESULT_COLLECTION, mCollection);
        setResult(Activity.RESULT_OK, data);
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {

    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.v("Result %d, requestCode %d", resultCode, requestCode);
        if (resultCode != Activity.RESULT_OK) {
            Timber.w("Error result %d, requestCode %d", resultCode, requestCode);
            return;
        }

        if (requestCode == REQUEST_IMAGE_URL) {
            String url = data.getStringExtra(CollectionTargetActivity.REQUEST_RESULT_IMAGE_URL);
            if (!TextUtils.isEmpty(url)) {
                Timber.i("image url %s", url);
                ImageLoader.loadImage(this, mDescriptionImageField, url);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
