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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.jorgecastillo.FillableLoader;
import com.github.jorgecastillo.State;
import com.github.jorgecastillo.listener.OnStateChangeListener;
import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.biz.base.support.BaseActionCallback;
import com.tomeokin.lspush.biz.collect.CollectionTargetActivity;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.usercase.collection.CollectionAction;
import com.tomeokin.lspush.common.ImageUtils;
import com.tomeokin.lspush.common.StringUtils;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.data.model.Image;
import com.tomeokin.lspush.injection.ProvideComponent;
import com.tomeokin.lspush.injection.component.CollectionEditorComponent;
import com.tomeokin.lspush.injection.component.DaggerCollectionEditorComponent;
import com.tomeokin.lspush.injection.module.CollectionModule;
import com.tomeokin.lspush.ui.widget.dialog.OnActionClickListener;
import com.tomeokin.lspush.ui.widget.dialog.SimpleDialogBuilder;
import com.tomeokin.lspush.ui.widget.tag.TagGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CollectionEditorActivity extends BaseActivity
    implements BaseActionCallback, ProvideComponent<CollectionEditorComponent>, OnActionClickListener {
    //public static final String REQUEST_RESULT_COLLECTION = "request.result.collection";
    //private static final String EXTRA_COLLECTION = "extra.collection";

    private static final int REQUEST_IMAGE_URL = 201;

    private CollectionEditorComponent mComponent;
    private Collection mCollection;
    private Image mImage = new Image();
    private boolean mIsPostingCollection = false;
    private boolean mHasChange;

    @BindDimen(R.dimen.list_item_max_content) float mMaxContentHeight;
    @BindInt(R.integer.svg_stroke_drawing_duration) int mStrokeDrawingDuration;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.toolbar_action_close) ImageButton mCloseButton;
    @BindView(R.id.toolbar_action_post) ImageButton mPostButton;
    @BindView(R.id.toolbar_post_waiting) FillableLoader mPostWaiting;

    @BindView(R.id.content_layout) LinearLayout mContentContainer;
    @BindView(R.id.title) EditText mTitleField;
    @BindView(R.id.description) EditText mDescriptionField;
    @BindView(R.id.description_image) ImageView mDescriptionImageField;
    @BindView(R.id.tagGroup) TagGroup mTagGroup;

    @Inject CollectionAction mCollectionAction;
    @Inject CollectionHolder mCollectionHolder;

    public static void start(@NonNull Fragment source, int requestCode) {
        Intent starter = new Intent(source.getContext(), CollectionEditorActivity.class);
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

        //mCollection = getIntent().getParcelableExtra(EXTRA_COLLECTION);
        component().inject(this);
        mCollection = mCollectionHolder.getCollection();
        if (mCollection == null || mCollection.getLink() == null) {
            finish();
        }

        setupToolbar();
        mPostWaiting.setSvgPath(getString(R.string.svg_up));
        mPostWaiting.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(int state) {
                if (state == State.FINISHED && mIsPostingCollection) {
                    mPostWaiting.setStrokeDrawingDuration(0);
                    mPostWaiting.start();
                }
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
        mTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(TagGroup tagGroup, CharSequence tag) {
                // TODO: 2016/10/31
            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.edit);
        }
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPostWaiting.setStrokeDrawingDuration(mStrokeDrawingDuration);
                postCollection();
            }
        });
    }

    private void postCollection() {
        if (mIsPostingCollection) {
            Toast.makeText(this, getString(R.string.waiting_post_collection), Toast.LENGTH_SHORT).show();
        } else {
            mIsPostingCollection = true;
            mPostButton.setVisibility(View.GONE);
            mPostWaiting.setVisibility(View.VISIBLE);
            mPostWaiting.start();

            mTagGroup.submitTag();
            mCollection.setDescription(mDescriptionField.getText().toString());
            mCollection.setImage(mImage);
            mCollection.setTags(mTagGroup.getTags());
            mCollectionAction.postCollection(mCollection);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //component().inject(this);
        mCollectionAction.attach(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTitleField.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCollectionAction != null) {
            mCollectionAction.detach();
            mCollectionAction = null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        String description = TextUtils.isEmpty(mCollection.getDescription()) ? "" : mCollection.getDescription();
        if (!mHasChange && description.equals(mDescriptionField.getText().toString())) {
            mCollectionHolder.setCollection(mCollection);
            setResult(Activity.RESULT_OK, data);
            super.onBackPressed();
        } else {
            new SimpleDialogBuilder(this).setTitle(R.string.ignore_change_of_collection)
                .setPositiveText(R.string.dialog_ok)
                .setNegativeText(R.string.dialog_cancel)
                .setActionClickListeningEnable(true)
                .show();
        }
    }

    @Override
    public void onDialogActionClick(DialogInterface dialog, int requestCode, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            mHasChange = false;
            onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == UserScene.ACTION_POST_COLLECTION) {
            mIsPostingCollection = false;
            mHasChange = false;
            mPostWaiting.setToFinishedFrame();
            mPostWaiting.setVisibility(View.GONE);
            mPostButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.post_collection_success), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {
        if (action == UserScene.ACTION_POST_COLLECTION) {
            mIsPostingCollection = false;
            mPostWaiting.setToFinishedFrame();
            mPostWaiting.setVisibility(View.GONE);
            mPostButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.post_collection_failure), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.v("Result %d, requestCode %d", resultCode, requestCode);
        if (resultCode != Activity.RESULT_OK) {
            Timber.w("Error result %d, requestCode %d", resultCode, requestCode);
            return;
        }

        if (requestCode == REQUEST_IMAGE_URL) {
            Image image = data.getParcelableExtra(CollectionTargetActivity.REQUEST_RESULT_IMAGE_URL);
            if (image != null && !TextUtils.isEmpty(image.getUrl())) {
                mImage = image;
                mHasChange = true;
                Timber.v("image %s", mImage.toString());
                mPostButton.setVisibility(View.INVISIBLE);
                mPostButton.setEnabled(false);

                float radio = optimumRadio();
                Glide.with(this)
                    .load(mImage.getUrl())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(R.drawable.ic_action_add_image)
                    .override((int) (image.getWidth() * radio), (int) (image.getHeight() * radio))
                    .into(new BitmapImageViewTarget(mDescriptionImageField) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            super.onResourceReady(resource, glideAnimation);
                            resolveImageColor(resource);
                        }
                    });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void resolveImageColor(@NonNull Bitmap resource) {
        Palette.from(resource).maximumColorCount(2).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                List<Palette.Swatch> swatches = palette.getSwatches();
                if (swatches.size() >= 1) {
                    Palette.Swatch swatch = swatches.get(0);
                    mImage.setColor(swatch.getRgb());
                    Timber.v("swatch color: %s", StringUtils.parseColor(swatch.getRgb()));
                } else {
                    resolveDefaultColor();
                }
                mPostButton.setVisibility(View.VISIBLE);
                mPostButton.setEnabled(true);
            }
        });
    }

    private void resolveDefaultColor() {
        int color = ContextCompat.getColor(CollectionEditorActivity.this, R.color.grey_3_whiteout);
        mImage.setColor(color);
        Timber.v("using color: %s", StringUtils.parseColor(color));
    }

    private float optimumRadio() {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mDescriptionImageField.getLayoutParams();
        float maxWidth = mContentContainer.getWidth()
            - mContentContainer.getPaddingLeft()
            - mContentContainer.getPaddingRight()
            - lp.leftMargin
            - lp.rightMargin
            - 50;
        float maxHeight = mMaxContentHeight - lp.topMargin - lp.topMargin;
        return ImageUtils.optimumRadio(maxWidth, maxHeight, mImage.getWidth(), mImage.getHeight());
    }
}
