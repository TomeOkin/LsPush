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
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.util.DateUtils;
import com.tomeokin.lspush.util.ImageUtils;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.data.model.Image;
import com.tomeokin.lspush.data.model.User;
import com.tomeokin.lspush.ui.glide.ImageLoader;
import com.tomeokin.lspush.ui.widget.tag.TagGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CollectionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements View.OnClickListener {
    public static final String ACTION_ADD_FAVOR = "ACTION_ADD_FAVOR";
    public static final String ACTION_REMOVE_FAVOR = "ACTION_REMOVE_FAVOR";

    private final int VIEW_TYPE_NORMAL = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private final float mMaxHeight;
    private final float mMaxWidth;

    private final SortedList.BatchedCallback<Collection> mBatchCallback =
        new SortedList.BatchedCallback<>(new CollectionSortCallback());
    private SortedList<Collection> mColSortList = new SortedList<>(Collection.class, mBatchCallback);

    private Callback mCallback = null;

    private boolean mIsLoading = false;
    private final View.OnClickListener mExplorerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int uid = (int) v.getTag(R.id.avatar_tag_uid);
            // TODO: 2016/10/20 user page
        }
    };
    private final View.OnClickListener mUserClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String uid = (String) v.getTag();
            if (mCallback != null) {
                mCallback.onOpenUserPage(uid);
            }
        }
    };
    private final View.OnClickListener mExplorersMoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag();
            final Collection collection = mColSortList.get(position);
            if (mCallback != null) {
                mCallback.onShowMoreExplorers(collection);
            }
        }
    };
    private final View.OnClickListener mFavorChangeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag();
            final Collection collection = mColSortList.get(position);
            final boolean hasFavor = !collection.isHasFavor();
            collection.setHasFavor(hasFavor);
            collection.setFavorCount(collection.getFavorCount() + (hasFavor ? 1 : -1));
            //updateFavorIcon(holder.favorIcon, hasFavor);
            //updateFavorText(holder.favorCount, collection.getFavorCount());
            notifyItemChanged(position,
                new Payload(hasFavor ? ACTION_ADD_FAVOR : ACTION_REMOVE_FAVOR, collection.getFavorCount()));

            if (mCallback != null) {
                mCallback.onFavorChange(v, position, collection);
            }
        }
    };

    public CollectionListAdapter(Activity activity, @Nullable List<Collection> colList, @Nullable Callback callback) {
        final Resources resources = activity.getResources();
        mMaxHeight = resources.getDimension(R.dimen.list_item_max_content) - resources.getDimension(
            R.dimen.row_vertical_padding);
        final View content = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        mMaxWidth = content.getWidth() - 2 * resources.getDimension(R.dimen.page_vertical_margin);

        if (colList != null) {
            setColList(colList);
        }
        setCallback(callback);
    }

    public void setColList(@NonNull List<Collection> colList) {
        mColSortList.beginBatchedUpdates();
        try {
            mColSortList.clear();
            mColSortList.addAll(colList);
        } finally {
            mColSortList.endBatchedUpdates();
        }
        notifyDataSetChanged();
    }

    public void insertColList(@NonNull List<Collection> colList) {
        mColSortList.beginBatchedUpdates();
        try {
            mColSortList.addAll(colList);
        } finally {
            mColSortList.endBatchedUpdates();
        }
        notifyDataSetChanged();
    }

    public void updateColList(int position, @NonNull Collection collection) {
        if (mColSortList.get(position).getId() != collection.getId()) {
            Timber.w("Error matching on collection list");
        }
        mColSortList.updateItemAt(position, collection);
        notifyItemChanged(position);
    }

    public void showLoadingProgress() {
        mIsLoading = true;
        notifyItemInserted(mColSortList.size());
    }

    public void hideLoadingProgress() {
        mIsLoading = false;
        notifyItemRemoved(mColSortList.size());
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public int getItemViewType(int position) {
        return position < mColSortList.size() ? VIEW_TYPE_NORMAL : VIEW_TYPE_LOADING;
    }

    @Override
    public int getItemCount() {
        return mColSortList.size() + (mIsLoading ? 1 : 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_NORMAL) {
            return createCollectionHolder(inflater, parent);
        } else if (viewType == VIEW_TYPE_LOADING) {
            return createLoadingViewHolder(inflater, parent);
        }
        return null;
    }

    private RecyclerView.ViewHolder createCollectionHolder(LayoutInflater inflater, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.list_item_collection, parent, false);
        final CollectionViewHolder holder = new CollectionViewHolder(view);
        holder.itemView.setOnClickListener(this);
        holder.userField.setOnClickListener(mUserClickListener);
        holder.explorersMore.setOnClickListener(mExplorersMoreListener);
        holder.favorIcon.setOnClickListener(mFavorChangeListener);
        return holder;
    }

    private LoadingViewHolder createLoadingViewHolder(LayoutInflater inflater, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.layout_loading, parent, false);
        return new LoadingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CollectionViewHolder) {
            onBindCollectionViewHolder((CollectionViewHolder) holder, position);
        } else {
            onBindLoadingViewHolder((LoadingViewHolder) holder, position);
        }
    }

    private void onBindCollectionViewHolder(CollectionViewHolder holder, int position) {
        final Collection collection = mColSortList.get(position);
        final User user = collection.getUser();
        final Context context = holder.itemView.getContext();
        final Image image = collection.getImage();

        holder.itemView.setTag(position);
        holder.explorersMore.setTag(position);
        holder.favorIcon.setTag(position);

        holder.userField.setTag(user.getUid());

        ImageLoader.loadAvatar(context, holder.avatar, user.getImage());
        holder.nickname.setText(user.getNickname());
        holder.updateDate.setText(DateUtils.toDurationFriendly(context, collection.getUpdateDate()));

        holder.title.setText(collection.getLink().getTitle());
        updateTitleColor(context, holder.title, collection.isHasRead());
        holder.description.setText(collection.getDescription());
        final float radio = optimumRadio(image.getWidth(), image.getHeight());
        ImageLoader.loadImage(context, holder.descriptionImage, image, radio);

        holder.tagGroup.setTags(collection.getTags());
        setExplorers(holder.explorersContainer, collection.getExplorers());
        boolean hasMore = collection.getExplorers() != null && collection.getExplorers().size() > 5;
        holder.explorersMore.setVisibility(hasMore ? View.VISIBLE : View.GONE);

        updateFavorIcon(holder.favorIcon, collection.isHasFavor());
        updateFavorText(holder.favorCount, collection.getFavorCount());
    }

    @SuppressWarnings("UnusedParameters")
    private void onBindLoadingViewHolder(LoadingViewHolder holder, int position) {
        holder.mProgressBar.show();
    }

    private void updateTitleColor(Context context, TextView title, boolean hasRead) {
        title.setTextColor(
            ContextCompat.getColor(context, hasRead ? R.color.grey_40_transparent : R.color.black_87_transparent));
    }

    public static void updateFavorIcon(ImageView favorIcon, boolean hasFavor) {
        favorIcon.setImageResource(hasFavor ? R.drawable.ic_action_heart_solid : R.drawable.ic_action_heart_hollow);
    }

    public static void updateFavorText(TextView favorText, long favorCount) {
        favorText.setText(String.valueOf(favorCount));
    }

    private void setExplorers(ViewGroup container, @Nullable List<User> explorers) {
        final Context context = container.getContext();
        // // TODO: 2016/10/8 performance improve
        //container.removeAllViews();
        //for (User explorer : explorers) {
        //    final ImageView avatar =
        //        (ImageView) LayoutInflater.from(context).inflate(R.layout.layout_item_explorer, container, false);
        //    ImageLoader.loadAvatar(context, avatar, explorer.getImage());
        //    container.addView(avatar);
        //}

        final int count = container.getChildCount();
        int targetCount = explorers == null ? 0 : explorers.size();
        targetCount = targetCount >= 5 ? 5 : targetCount;
        final LayoutInflater inflater = LayoutInflater.from(context);
        if (count > targetCount) {
            container.removeViews(targetCount, count - targetCount);
        }
        ImageView avatar;
        for (int i = 0; i < targetCount; i++) {
            if (i > count - 1) { // no cache
                avatar = (ImageView) inflater.inflate(R.layout.layout_item_explorer, container, false);
            } else {
                avatar = (ImageView) container.getChildAt(i);
            }
            ImageLoader.loadAvatar(context, avatar, explorers.get(i).getImage());
            if (avatar.getParent() == null) {
                container.addView(avatar);
            }
            avatar.setTag(R.id.avatar_tag_uid, explorers.get(i).getUid());
            avatar.setOnClickListener(mExplorerListener);
        }
    }

    @Override
    public void onClick(View v) {
        final int position = (int) v.getTag();
        final Collection collection = mColSortList.get(position);
        collection.setHasRead(true);
        TextView title = (TextView) v.findViewById(R.id.title);
        updateTitleColor(v.getContext(), title, collection.isHasRead());
        if (mCallback != null) {
            mCallback.onOpenCollectionUrl(position, collection);
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private float optimumRadio(int width, int height) {
        return ImageUtils.optimumRadio(mMaxWidth, mMaxHeight, width, height);
    }

    public final class CollectionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_field) View userField;
        @BindView(R.id.avatar_iv) ImageView avatar;
        @BindView(R.id.nickname_tv) TextView nickname;
        @BindView(R.id.updateDate) TextView updateDate;

        @BindView(R.id.title) TextView title;
        @BindView(R.id.description) TextView description;
        @BindView(R.id.description_image) ImageView descriptionImage;

        @BindView(R.id.tagGroup) TagGroup tagGroup;
        @BindView(R.id.explorers_container) LinearLayout explorersContainer;
        @BindView(R.id.explorers_more_tv) TextView explorersMore;
        @BindView(R.id.favor_iv) ImageView favorIcon;
        @BindView(R.id.favor_count) TextView favorCount;

        public CollectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public final class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.loading_progress_bar) ContentLoadingProgressBar mProgressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface Callback {
        void onFavorChange(View favorButton, int position, Collection collection);

        void onShowMoreExplorers(Collection collection);

        void onOpenCollectionUrl(int position, Collection collection);

        void onOpenUserPage(String uid);
    }

    public final class Payload {
        public String action;
        public long favorCount;

        public Payload(String action, long favorCount) {
            this.action = action;
            this.favorCount = favorCount;
        }
    }
}
