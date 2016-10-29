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
import android.graphics.Rect;
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
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
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

import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
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

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.toolbar_action_close) ImageButton mCloseButton;
    @BindView(R.id.toolbar_action_post) ImageButton mPostButton;
    @BindView(R.id.toolbar_post_waiting) FillableLoader mPostWaiting;

    @BindView(R.id.content_layout) LinearLayout mContentContainer;
    @BindView(R.id.title) EditText mTitleField;
    @BindView(R.id.description) EditText mDescriptionField;
    @BindView(R.id.description_image) ImageView mDescriptionImageField;

    @Inject CollectionAction mCollectionAction;
    @Inject CollectionHolder mCollectionHolder;

    public static void start(@NonNull Fragment source, @Nullable Collection collection, int requestCode) {
        Intent starter = new Intent(source.getContext(), CollectionEditorActivity.class);
        //if (collection != null) {
        //    starter.putExtra(EXTRA_COLLECTION, collection);
        //}
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
        //// TODO: 2016/10/27 uncomment it later
        //mCollection = mCollectionHolder.getCollection();
        //if (mCollection == null) {
        //    finish();
        //}

        setupToolbar();
        mPostWaiting.setSvgPath("M25,32L31,32L31,26L35,26L28,19L21,26L25,26L25,32Z");
        mPostWaiting.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(int state) {
                if (state == State.FINISHED && mIsPostingCollection) {
                    mPostWaiting.start();
                }
            }
        });

        //// TODO: 2016/10/27 uncomment it later
        //mTitleField.setText(mCollection.getLink().getTitle());
        //mDescriptionField.setText(mCollection.getDescription());
        //mDescriptionImageField.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        CollectionTargetActivity.start(CollectionEditorActivity.this, mCollection.getLink().getUrl(),
        //            REQUEST_IMAGE_URL);
        //    }
        //});
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.edit);
        }
        //mToolbar.setNavigationIcon(R.drawable.ic_nav_arrow_left);
        //mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        onBackPressed();
        //    }
        //});
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/10/27

                //showDialog();
                //postCollection();
                mIsPostingCollection = true;
                mPostButton.setVisibility(View.GONE);
                mPostWaiting.setVisibility(View.VISIBLE);
                mPostWaiting.start();
            }
        });
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    getMenuInflater().inflate(R.menu.activity_collection_editor_menu, menu);
    //    return true;
    //}

    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
    //    switch (item.getItemId()) {
    //        case R.id.action_ok:
    //            // TODO: 2016/10/27
    //            showDialog();
    //            //postCollection();
    //            return true;
    //        default:
    //            return super.onOptionsItemSelected(item);
    //    }
    //}

    private Rect mPosRect = new Rect();

    private void showDialog() {
        final View content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        final int contentWidth = content.getWidth(), contentHeight = content.getHeight();
        mPostButton.getGlobalVisibleRect(mPosRect);

        float radio = 300 / mPosRect.width();

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(300);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        float xDelta = ((contentWidth - 300) / 2.0f - mPosRect.left) * (1 / radio);
        float yDelta = ((contentHeight - 300) / 2.0f - mPosRect.top) * (1 / radio);

        TranslateAnimation translateAnimation=new TranslateAnimation(0, xDelta, 0, yDelta);
        animationSet.addAnimation(translateAnimation);

        ScaleAnimation scaleAnimation=new ScaleAnimation(1.0f, radio, 1.0f, radio);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setFillAfter(true);

        //mPostButton.setVisibility(View.INVISIBLE);
        mPostButton.getBackground().setTint(ContextCompat.getColor(this, R.color.colorPrimary));
        mPostButton.startAnimation(animationSet);
    }

    private void postCollection() {
        if (mIsPostingCollection) {
            Toast.makeText(this, getString(R.string.waiting_post_collection), Toast.LENGTH_SHORT).show();
        } else {
            mIsPostingCollection = true;
            mPostButton.setVisibility(View.GONE);
            mPostWaiting.setVisibility(View.VISIBLE);
            mPostWaiting.start();
            mCollection.setDescription(mDescriptionField.getText().toString());
            mCollection.setImage(mImage);
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
        //data.putExtra(REQUEST_RESULT_COLLECTION, mCollection);
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
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
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
