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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionItemAnimator extends DefaultItemAnimator {
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private Map<RecyclerView.ViewHolder, AnimatorSet> mFavorAnimMap = new HashMap<>();

    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state,
        @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {

        if (changeFlags == FLAG_CHANGED) {
            for (Object payload : payloads) {
                if (payload instanceof CollectionListAdapter.Payload) {
                    return new CollectionItemHolderInfo((CollectionListAdapter.Payload) payload);
                }
            }
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder,
        @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {

        if (preInfo instanceof CollectionItemHolderInfo) {
            CollectionItemHolderInfo holderInfo = (CollectionItemHolderInfo) preInfo;
            CollectionListAdapter.CollectionViewHolder holder = (CollectionListAdapter.CollectionViewHolder) newHolder;

            resetItem(holder);
            if (CollectionListAdapter.ACTION_ADD_FAVOR.equals(holderInfo.updateAction)) {
                animateAddFavor(holder, holderInfo);
            } else if (CollectionListAdapter.ACTION_REMOVE_FAVOR.equals(holderInfo.updateAction)) {
                CollectionListAdapter.updateFavorIcon(holder.favorIcon, false);
                CollectionListAdapter.updateFavorText(holder.favorCount, holderInfo.favorCount);
            }
        }

        return false;
    }

    private void resetItem(CollectionListAdapter.CollectionViewHolder holder) {
        if (mFavorAnimMap.containsKey(holder)) {
            mFavorAnimMap.get(holder).cancel();
        }
        holder.favorIcon.setEnabled(true);
    }

    private void animateAddFavor(final CollectionListAdapter.CollectionViewHolder holder,
        final CollectionItemHolderInfo holderInfo) {
        AnimatorSet animatorSet = animateAddFavor(holder.favorIcon, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                holder.favorIcon.setEnabled(false);
                CollectionListAdapter.updateFavorIcon(holder.favorIcon, true);
                CollectionListAdapter.updateFavorText(holder.favorCount, holderInfo.favorCount);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFavorAnimMap.remove(holder);
                dispatchChangeFinishedIfAllAnimationsEnded(holder);
                holder.favorIcon.setEnabled(true);
            }
        });
        mFavorAnimMap.put(holder, animatorSet);
    }

    public static AnimatorSet animateAddFavor(View favorIcon, AnimatorListenerAdapter listenerAdapter) {
        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(favorIcon, "scaleX", 0.5f, 1f);
        bounceAnimX.setDuration(300);
        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(favorIcon, "scaleY", 0.5f, 1f);
        bounceAnimY.setDuration(300);
        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
        bounceAnimY.addListener(listenerAdapter);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(bounceAnimX).with(bounceAnimY);
        animatorSet.start();

        return animatorSet;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
        resetItem((CollectionListAdapter.CollectionViewHolder) item);
    }

    private void dispatchChangeFinishedIfAllAnimationsEnded(CollectionListAdapter.CollectionViewHolder holder) {
        if (mFavorAnimMap.containsKey(holder)) {
            return;
        }
        dispatchAddFinished(holder);
    }

    @Override
    public void endAnimations() {
        super.endAnimations();
        for (AnimatorSet animatorSet : mFavorAnimMap.values()) {
            animatorSet.cancel();
        }
    }

    public class CollectionItemHolderInfo extends ItemHolderInfo {
        public String updateAction;
        public long favorCount;

        public CollectionItemHolderInfo(CollectionListAdapter.Payload payload) {
            this.updateAction = payload.action;
            this.favorCount = payload.favorCount;
        }
    }
}
