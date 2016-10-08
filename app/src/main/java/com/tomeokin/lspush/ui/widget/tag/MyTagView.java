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
package com.tomeokin.lspush.ui.widget.tag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

public class MyTagView extends AppCompatTextView {
    private static final int default_border_color = Color.rgb(0x49, 0xC1, 0x20);
    private static final int default_text_color = Color.rgb(0x49, 0xC1, 0x20);
    private static final int default_background_color = Color.WHITE;

    private final float default_border_stroke_width = dp2px(0.5f);
    private final float default_text_size = sp2px(13.0f);
    private final float default_horizontal_spacing = dp2px(8.0f);
    private final float default_vertical_spacing = dp2px(4.0f);
    private final int default_horizontal_padding = (int) dp2px(12.0f);
    private final int default_vertical_padding = (int) dp2px(3.0f);

    /** The tag outline border stroke width, default is 0.5dp. */
    private float borderStrokeWidth = default_border_stroke_width;

    private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /** The rect for the tag's left corner drawing. */
    private RectF mLeftCornerRectF = new RectF();

    /** The rect for the tag's right corner drawing. */
    private RectF mRightCornerRectF = new RectF();

    /** The rect for the tag's horizontal blank fill area. */
    private RectF mHorizontalBlankFillRectF = new RectF();

    /** The rect for the tag's vertical blank fill area. */
    private RectF mVerticalBlankFillRectF = new RectF();

    /** The path for draw the tag's outline border. */
    private Path mBorderPath = new Path();

    public MyTagView(Context context) {
        this(context, null);
    }

    public MyTagView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MyTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBorderPaint.setColor(default_border_color);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(borderStrokeWidth);

        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(default_background_color);

        //final TypedArray styles = context.obtainStyledAttributes(attrs, R.styleable.TagView, defStyleAttr, 0);
        //
        //
        //// FIXME: 2016/10/5 if not set, set them
        //setTextColor(default_text_color);
        ////setTextSize(default_text_size);
        //setPadding(default_horizontal_padding, default_vertical_padding, default_horizontal_padding, default_vertical_padding);
        //setGravity(Gravity.CENTER);
        //styles.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mLeftCornerRectF, -180, 90, true, mBackgroundPaint);
        canvas.drawArc(mLeftCornerRectF, -270, 90, true, mBackgroundPaint);
        canvas.drawArc(mRightCornerRectF, -90, 90, true, mBackgroundPaint);
        canvas.drawArc(mRightCornerRectF, 0, 90, true, mBackgroundPaint);
        canvas.drawRect(mHorizontalBlankFillRectF, mBackgroundPaint);
        canvas.drawRect(mVerticalBlankFillRectF, mBackgroundPaint);

        canvas.drawPath(mBorderPath, mBorderPaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int left = (int) borderStrokeWidth;
        int top = (int) borderStrokeWidth;
        int right = (int) (left + w - borderStrokeWidth * 2);
        int bottom = (int) (top + h - borderStrokeWidth * 2);

        int d = bottom - top;

        mLeftCornerRectF.set(left, top, left + d, top + d);
        mRightCornerRectF.set(right - d, top, right, top + d);

        mBorderPath.reset();
        mBorderPath.addArc(mLeftCornerRectF, -180, 90);
        mBorderPath.addArc(mLeftCornerRectF, -270, 90);
        mBorderPath.addArc(mRightCornerRectF, -90, 90);
        mBorderPath.addArc(mRightCornerRectF, 0, 90);

        int l = (int) (d / 2.0f);
        mBorderPath.moveTo(left + l, top);
        mBorderPath.lineTo(right - l, top);

        mBorderPath.moveTo(left + l, bottom);
        mBorderPath.lineTo(right - l, bottom);

        mBorderPath.moveTo(left, top + l);
        mBorderPath.lineTo(left, bottom - l);

        mBorderPath.moveTo(right, top + l);
        mBorderPath.lineTo(right, bottom - l);

        mHorizontalBlankFillRectF.set(left, top + l, right, bottom - l);
        mVerticalBlankFillRectF.set(left + l, top, right - l, bottom);
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
