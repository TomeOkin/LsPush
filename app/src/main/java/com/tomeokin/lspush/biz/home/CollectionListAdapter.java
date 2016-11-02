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
import com.tomeokin.lspush.common.DateUtils;
import com.tomeokin.lspush.common.ImageUtils;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.data.model.Image;
import com.tomeokin.lspush.data.model.User;
import com.tomeokin.lspush.ui.glide.ImageLoader;
import com.tomeokin.lspush.ui.widget.tag.TagGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CollectionListAdapter extends RecyclerView.Adapter<CollectionListAdapter.ViewHolder>
    implements View.OnClickListener {
    private final float mMaxHeight;
    private final float mMaxWidth;
    private Callback mCallback = null;
    private final SortedList.BatchedCallback<Collection> mBatchCallback =
        new SortedList.BatchedCallback<>(new CollectionSortCallback());
    private SortedList<Collection> mColSortList = new SortedList<>(Collection.class, mBatchCallback);
    private int mClickIndex; // the index of the Collection Opened
    private View.OnClickListener mExplorerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int uid = (int) v.getTag(R.id.avatar_tag_uid);
            // TODO: 2016/10/20 user page
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

    public void updateColList(@NonNull Collection collection) {
        if (mColSortList.get(mClickIndex).getId() != collection.getId()) {
            Timber.w("Error matching on collection list");
        }
        mColSortList.updateItemAt(mClickIndex, collection);
        notifyItemChanged(mClickIndex);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.layout_item_collection, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(this);
        holder.explorersMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = (int) v.getTag();
                final Collection collection = mColSortList.get(position);
                if (mCallback != null) {
                    mCallback.onShowMoreExplorers(collection);
                }
            }
        });
        holder.favorIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = (int) v.getTag();
                final Collection collection = mColSortList.get(position);
                final boolean hasFavor = !collection.isHasFavor();
                collection.setHasFavor(hasFavor);
                collection.setFavorCount(collection.getFavorCount() + (hasFavor ? 1 : -1));
                updateFavorIcon(holder.favorIcon, hasFavor);
                holder.favorCount.setText(String.valueOf(collection.getFavorCount()));

                if (mCallback != null) {
                    mCallback.onFavorChange(collection);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Collection collection = mColSortList.get(position);
        final User user = collection.getUser();
        final Context context = holder.itemView.getContext();
        final Image image = collection.getImage();

        holder.itemView.setTag(position);
        holder.explorersMore.setTag(position);
        holder.favorIcon.setTag(position);

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

        updateFavorIcon(holder.favorIcon, collection.isHasFavor());
        holder.favorCount.setText(String.valueOf(collection.getFavorCount()));
    }

    private void updateTitleColor(Context context, TextView title, boolean hasRead) {
        title.setTextColor(
            ContextCompat.getColor(context, hasRead ? R.color.grey_40_transparent : R.color.black_87_transparent));
    }

    private void updateFavorIcon(ImageView favorIcon, boolean hasFavor) {
        favorIcon.setImageResource(hasFavor ? R.drawable.ic_action_heart_solid : R.drawable.ic_action_heart_hollow);
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
        final int targetCount = explorers == null ? 0 : explorers.size();
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
            mClickIndex = position;
            mCallback.onOpenCollectionUrl(collection);
        }
    }

    @Override
    public int getItemCount() {
        return mColSortList.size();
    }

    private float optimumRadio(int width, int height) {
        return ImageUtils.optimumRadio(mMaxWidth, mMaxHeight, width, height);
    }

    public final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar_iv) ImageView avatar;
        @BindView(R.id.nickname_tv) TextView nickname;
        @BindView(R.id.updateDate) TextView updateDate;

        @BindView(R.id.content_layout) LinearLayout mContentLayout;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.description) TextView description;
        @BindView(R.id.description_image) ImageView descriptionImage;

        @BindView(R.id.tagGroup) TagGroup tagGroup;
        @BindView(R.id.explorers_container) LinearLayout explorersContainer;
        @BindView(R.id.explorers_more_tv) TextView explorersMore;
        @BindView(R.id.favor_iv) ImageView favorIcon;
        @BindView(R.id.favor_count) TextView favorCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface Callback {
        void onFavorChange(Collection collection);

        void onShowMoreExplorers(Collection collection);

        void onOpenCollectionUrl(Collection collection);
    }
}
