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
package com.tomeokin.lspush.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.tomeokin.nativestackblur.NativeBlurUtil;

public class LandingRotatingBackgroundView extends View {
    private static final long startTime;
    private final int[] colors = new int[] {0xffa60ca6, 0xff00dddd, 0xffff9600, Color.CYAN, 0, 0, 0, 0};
    private final short[] indices = new short[] {0, 1, 2, 0, 2, 3};
    private final float[] verts = new float[] {0, 0, 0, 1, 1, 1, 1, 0};
    private final int[] location = new int[2];
    private final Matrix matrix = new Matrix();
    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private View alignView;
    private Bitmap tile;

    static {
        startTime = SystemClock.elapsedRealtime();
    }

    public LandingRotatingBackgroundView(Context context) {
        super(context);
        init();
    }

    public LandingRotatingBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LandingRotatingBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Bitmap bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(canvas.getWidth(), canvas.getHeight());
        canvas.drawVertices(Canvas.VertexMode.TRIANGLES, verts.length, verts, 0, null, 0, colors, 0, indices, 0, 6,
            paint);
        NativeBlurUtil.getInstance().blur(bitmap, 25);
        tile = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 相对于窗口左上角的偏移
        getLocationInWindow(location);
        int height = location[1];
        if (alignView != null) {
            alignView.getLocationInWindow(location);
            height = location[1] + alignView.getHeight() - height;
        } else {
            height = getHeight();
        }

        if (height > 0) {
            int width = getWidth() / 2;
            height = height / 2 * 3;
            float hypotenuse = (float) Math.sqrt(height * height + width * width);
            float scale = 2f * hypotenuse / tile.getWidth();
            // 产生一个渐变的角度，大概 10s 为一个周期
            float degrees = (float) (SystemClock.elapsedRealtime() - startTime) % 37000f * 360f / 37000f;
            float center = tile.getWidth() / 2;

            matrix.reset();
            matrix.preRotate(degrees, center, center);
            matrix.postScale(scale, scale); // 放大 sqrt(w+9h)
            matrix.postTranslate(width - hypotenuse, height - hypotenuse);
            canvas.drawBitmap(tile, matrix, paint);
            invalidate();
        }
    }

    public void setAlignBottomView(View view) {
        this.alignView = view;
    }
}
