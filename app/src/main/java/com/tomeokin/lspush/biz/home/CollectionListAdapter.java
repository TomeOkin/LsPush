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

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.data.model.User;
import com.tomeokin.lspush.ui.glide.ImageLoader;
import com.tomeokin.lspush.ui.widget.tag.TagGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CollectionListAdapter extends RecyclerView.Adapter<CollectionListAdapter.ViewHolder>
    implements View.OnClickListener {
    private List<Collection> mColList = null;

    public CollectionListAdapter(List<Collection> colList) {
        setColList(colList);
    }

    public void setColList(List<Collection> colList) {
        mColList = colList;
        notifyDataSetChanged();
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
                // TODO: 2016/10/8
            }
        });
        holder.favorIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/10/8
                final int position = (int) v.getTag();
                final Collection collection = mColList.get(position);
                collection.setHasFavor(!collection.isHasFavor());
                updateFavorIcon(holder.favorIcon, collection.isHasFavor());
                // send to server
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Collection collection = mColList.get(position);
        final User user = collection.getUser();
        final Context context = holder.itemView.getContext();

        holder.itemView.setTag(position);

        ImageLoader.loadAvatar(context, holder.avatar, user.getImage());
        holder.nickname.setText(user.getNickname());

        holder.title.setText(collection.getLink().getTitle());
        updateTitleColor(context, holder.title, collection.isHasRead());
        holder.description.setText(collection.getDescription());
        ImageLoader.loadImage(context, holder.descriptionImage, collection.getImage());

        holder.tagGroup.setTags(collection.getTags());
        setExplorers(holder.explorersContainer, collection.getExplorers());
        // FIXME: 2016/10/8 change color
        holder.favorIcon.setTag(position);
        updateFavorIcon(holder.favorIcon, collection.isHasFavor());
        holder.favorCount.setText(String.valueOf(collection.getFavorCount()));
    }

    private void updateTitleColor(Context context, TextView title, boolean hasRead) {
        title.setTextColor(
            ContextCompat.getColor(context, hasRead ? R.color.black_50_transparent : R.color.black_87_transparent));
    }

    private void updateFavorIcon(ImageView favorIcon, boolean hasFavor) {
        favorIcon.setImageResource(hasFavor ? R.drawable.heart_solid : R.drawable.heart_hollow);
    }

    private void setExplorers(ViewGroup container, List<User> explorers) {
        final Context context = container.getContext();
        // TODO: 2016/10/8 performance improve
        container.removeAllViews();
        for (User explorer : explorers) {
            final ImageView avatar =
                (ImageView) LayoutInflater.from(context).inflate(R.layout.layout_item_explorer, container, false);
            ImageLoader.loadAvatar(context, avatar, explorer.getImage());
            container.addView(avatar);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO: 2016/10/8
        final int position = (int) v.getTag();
        final Collection collection = mColList.get(position);
        collection.setHasRead(true);
        TextView title = (TextView) v.findViewById(R.id.title_tv);
        updateTitleColor(v.getContext(), title, collection.isHasRead());
    }

    @Override
    public int getItemCount() {
        return mColList == null ? 0 : mColList.size();
    }

    public final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar_iv) ImageView avatar;
        @BindView(R.id.nickname_tv) TextView nickname;
        @BindView(R.id.title_tv) TextView title;
        @BindView(R.id.description_tv) TextView description;
        @BindView(R.id.description_iv) ImageView descriptionImage;
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
}
