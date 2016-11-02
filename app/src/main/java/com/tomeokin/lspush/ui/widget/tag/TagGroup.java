package com.tomeokin.lspush.ui.widget.tag;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ArrowKeyMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.TextView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.ui.widget.listener.TextWatcherAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A <code>TagGroup</code> is a special layout with a set of tags.
 * This group has two modes:
 * <p>
 * 1. APPEND mode
 * 2. DISPLAY mode
 * </p>
 * Default is DISPLAY mode. When in APPEND mode, the group is capable of input for append new tags
 * and delete tags.
 * <p>
 * When in DISPLAY mode, the group is only contain NORMAL state tags, and the tags in group
 * is not focusable.
 * </p>
 *
 * @author Jun Gu (http://2dxgujun.com)
 * @version 2.0
 * @since 2015-2-3 14:16:32
 */
public class TagGroup extends ViewGroup {
    /** Indicates whether this TagGroup is set up to APPEND mode or DISPLAY mode. Default is false. */
    private boolean isAppendMode;

    // tag normal
    private int textColor = Color.rgb(0x66, 0xbd, 0x2b);
    private float textSize = sp2px(14);
    private int borderColor = textColor;
    private float borderStrokeWidth = dp2px(1f);
    private int backgroundColor = Color.WHITE;

    // tag input
    private CharSequence inputHint;
    private int inputHintColor = Color.argb(0x80, 0x00, 0x00, 0x00);
    private int inputTextColor = Color.argb(0xde, 0x00, 0x00, 0x00);
    private int dashBorderColor = Color.rgb(0xaa, 0xaa, 0xaa);

    // tag check(delete)
    private int checkedTextColor = Color.WHITE;
    private int checkedMarkerColor = Color.WHITE;
    private int checkedBorderColor = Color.rgb(0x49, 0xc1, 0x20);
    private int checkedBackgroundColor = Color.rgb(0x49, 0xc1, 0x20);

    // tag press
    private int pressedBackgroundColor = Color.rgb(0xed, 0xed, 0xed);

    // tag margin, but ignore for around
    private int mHorizontalSpacing = (int) dp2px(8);
    private int mVerticalSpacing = (int) dp2px(4);

    // tag padding
    private int mHorizontalPadding = (int) dp2px(12);
    private int mVerticalPadding = (int) dp2px(4);

    /** Listener used to dispatch tag change event. */
    private OnTagChangeListener mOnTagChangeListener;

    /** Listener used to dispatch tag click event. */
    private OnTagClickListener mOnTagClickListener;

    /** Listener used to handle tag click event. */
    private InternalTagClickListener mInternalTagClickListener = new InternalTagClickListener();

    public TagGroup(Context context) {
        this(context, null);
    }

    public TagGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Load styled attributes.
        final TypedArray a =
            context.obtainStyledAttributes(attrs, R.styleable.TagGroup, defStyleAttr, R.style.TagGroup);
        try {
            textSize = a.getDimension(R.styleable.TagGroup_atg_textSize, textSize);
            textColor = a.getColor(R.styleable.TagGroup_atg_textColor, textColor);

            borderStrokeWidth = a.getDimension(R.styleable.TagGroup_atg_borderStrokeWidth, borderStrokeWidth);
            borderColor = a.getColor(R.styleable.TagGroup_atg_borderColor, borderColor);

            isAppendMode = a.getBoolean(R.styleable.TagGroup_atg_isAppendMode, false);
            inputHint = a.getText(R.styleable.TagGroup_atg_inputHint);
            inputHintColor = a.getColor(R.styleable.TagGroup_atg_inputHintColor, inputHintColor);
            inputTextColor = a.getColor(R.styleable.TagGroup_atg_inputTextColor, inputTextColor);

            backgroundColor = a.getColor(R.styleable.TagGroup_atg_backgroundColor, backgroundColor);
            dashBorderColor = a.getColor(R.styleable.TagGroup_atg_dashBorderColor, dashBorderColor);

            checkedBorderColor = a.getColor(R.styleable.TagGroup_atg_checkedBorderColor, checkedBorderColor);
            checkedTextColor = a.getColor(R.styleable.TagGroup_atg_checkedTextColor, checkedTextColor);
            checkedMarkerColor = a.getColor(R.styleable.TagGroup_atg_checkedMarkerColor, checkedMarkerColor);
            checkedBackgroundColor =
                a.getColor(R.styleable.TagGroup_atg_checkedBackgroundColor, checkedBackgroundColor);
            pressedBackgroundColor =
                a.getColor(R.styleable.TagGroup_atg_pressedBackgroundColor, pressedBackgroundColor);

            mHorizontalSpacing = (int) a.getDimension(R.styleable.TagGroup_atg_horizontalSpacing, mHorizontalSpacing);
            mVerticalSpacing = (int) a.getDimension(R.styleable.TagGroup_atg_verticalSpacing, mVerticalSpacing);
            mHorizontalPadding = (int) a.getDimension(R.styleable.TagGroup_atg_horizontalPadding, mHorizontalPadding);
            mVerticalPadding = (int) a.getDimension(R.styleable.TagGroup_atg_verticalPadding, mVerticalPadding);
        } finally {
            a.recycle();
        }

        if (isInEditMode()) {
            setTags("hello", "tag", "editMode");
        } else {
            if (isAppendMode) {
                // Append the initial INPUT tag.
                appendInputTag();

                // Set the click listener to detect the end-input event.
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitTag();
                    }
                });
            }
        }
    }

    /**
     * Call this to submit the INPUT tag.
     */
    public void submitTag() {
        final TagView inputTag = getInputTag();
        if (inputTag != null && !TextUtils.isEmpty(inputTag.getText())) {
            inputTag.endInput();

            if (mOnTagChangeListener != null) {
                mOnTagChangeListener.onAppend(TagGroup.this, inputTag.getText().toString());
            }
            appendInputTag();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width, height = 0;

        int row = 0; // The row counter.
        int rowWidth = 0; // Calc the current row width.
        int rowMaxHeight = 0; // Calc the max tag height, in current row.

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                rowWidth += childWidth;
                if (rowWidth > widthSize) { // Next line.
                    rowWidth = childWidth; // The next row width.
                    height += rowMaxHeight + mVerticalSpacing;
                    rowMaxHeight = childHeight; // The next row max height.
                    row++;
                } else { // This line.
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                }
                rowWidth += mHorizontalSpacing;
            }
        }
        // Account for the last row height.
        height += rowMaxHeight;

        // Account for the padding too.
        height += getPaddingTop() + getPaddingBottom();

        // If the tags grouped in one row, set the width to wrap the tags.
        if (row == 0) {
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        } else {// If the tags grouped exceed one line, set the width to match the parent.
            width = widthSize;
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
            heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentWidth = r - l - getPaddingRight();

        int childLeft = parentLeft;
        int childTop = parentTop;
        int rowMaxHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                if (childLeft + width > parentWidth) { // Next line
                    childLeft = parentLeft;
                    childTop += rowMaxHeight + mVerticalSpacing;
                    rowMaxHeight = height;
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, height);
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);

                childLeft += width + mHorizontalSpacing;
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.tags = getTagArray();
        ss.checkedPosition = getCheckedTagIndex();
        if (getInputTag() != null) {
            ss.input = getInputTag().getText().toString();
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setTags(ss.tags);
        TagView checkedTagView = getTagAt(ss.checkedPosition);
        if (checkedTagView != null) {
            checkedTagView.setChecked(true);
        }
        if (getInputTag() != null) {
            getInputTag().setText(ss.input);
        }
    }

    /**
     * Returns the INPUT tag view in this group.
     *
     * @return the INPUT state tag view or null if not exists
     */
    protected TagView getInputTag() {
        if (isAppendMode) {
            final int inputTagIndex = getChildCount() - 1;
            final TagView inputTag = getTagAt(inputTagIndex);
            if (inputTag != null && inputTag.isEditable()) {
                return inputTag;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the INPUT state tag in this group.
     *
     * @return the INPUT state tag view or null if not exists
     */
    public String getInputTagText() {
        final TagView inputTagView = getInputTag();
        if (inputTagView != null) {
            return inputTagView.getText().toString();
        }
        return null;
    }

    /**
     * Return the last NORMAL state tag view in this group.
     *
     * @return the last NORMAL state tag view or null if not exists
     */
    protected TagView getLastNormalTagView() {
        final int lastNormalTagIndex = isAppendMode ? getChildCount() - 2 : getChildCount() - 1;
        return getTagAt(lastNormalTagIndex);
    }

    /**
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    @NonNull
    public List<String> getTags() {
        final int count = getChildCount();
        final List<String> tagList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final TagView tagView = getTagAt(i);
            if (!tagView.isEditable()) {
                tagList.add(tagView.getText().toString());
            }
        }

        return tagList;
    }

    public String[] getTagArray() {
        List<String> tagList = getTags();
        return tagList.toArray(new String[tagList.size()]);
    }

    /**
     * @see #setTags(String...)
     */
    public void setTags(@Nullable List<String> tagList) {
        if (tagList != null) {
            setTags(tagList.toArray(new String[tagList.size()]));
        } else {
            setTags();
        }
    }

    /**
     * Set the tags. It will remove all previous tags first.
     *
     * @param tags the tag list to set.
     */
    public void setTags(String... tags) {
        removeAllViews();
        for (final String tag : tags) {
            appendTag(tag);
        }

        if (isAppendMode) {
            appendInputTag();
        }
    }

    /**
     * Returns the tag view at the specified position in the group.
     *
     * @param index the position at which to get the tag view from.
     * @return the tag view at the specified position or null if the position
     * does not exists within this group.
     */
    protected TagView getTagAt(int index) {
        return (TagView) getChildAt(index);
    }

    /**
     * Returns the checked tag view in the group.
     *
     * @return the checked tag view or null if not exists.
     */
    protected TagView getCheckedTag() {
        final int checkedTagIndex = getCheckedTagIndex();
        if (checkedTagIndex != -1) {
            return getTagAt(checkedTagIndex);
        }
        return null;
    }

    /**
     * Return the checked tag index.
     *
     * @return the checked tag index, or -1 if not exists.
     */
    protected int getCheckedTagIndex() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final TagView tag = getTagAt(i);
            if (tag.isChecked()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Register a callback to be invoked when this tag group is changed.
     *
     * @param listener the callback that will run
     */
    public void setOnTagChangeListener(OnTagChangeListener listener) {
        mOnTagChangeListener = listener;
    }

    /**
     * @see #appendInputTag(String)
     */
    protected void appendInputTag() {
        appendInputTag(null);
    }

    /**
     * Append a INPUT tag to this group. It will throw an exception if there has a previous INPUT tag.
     *
     * @param tag the tag text.
     */
    protected void appendInputTag(String tag) {
        final TagView previousInputTag = getInputTag();
        if (previousInputTag != null) {
            throw new IllegalStateException("Already has a INPUT tag in group.");
        }

        final TagView newInputTag = create(getContext(), true, tag);
        addView(newInputTag);
    }

    /**
     * Append tag to this group.
     *
     * @param tag the tag to append.
     */
    protected void appendTag(CharSequence tag) {
        final TagView newTag = create(getContext(), false, tag);
        addView(newTag);
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new TagGroup.LayoutParams(getContext(), attrs);
    }

    /**
     * Register a callback to be invoked when a tag is clicked.
     *
     * @param l the callback that will run.
     */
    public void setOnTagClickListener(OnTagClickListener l) {
        mOnTagClickListener = l;
    }

    protected void deleteTag(TagView tagView) {
        removeView(tagView);
        if (mOnTagChangeListener != null) {
            mOnTagChangeListener.onDelete(TagGroup.this, tagView.getText().toString());
        }
    }

    /**
     * Interface definition for a callback to be invoked when a tag group is changed.
     */
    public interface OnTagChangeListener {
        /**
         * Called when a tag has been appended to the group.
         *
         * @param tag the appended tag.
         */
        void onAppend(TagGroup tagGroup, CharSequence tag);

        /**
         * Called when a tag has been deleted from the the group.
         *
         * @param tag the deleted tag.
         */
        void onDelete(TagGroup tagGroup, CharSequence tag);
    }

    /**
     * Interface definition for a callback to be invoked when a tag is clicked.
     */
    public interface OnTagClickListener {
        /**
         * Called when a tag has been clicked.
         *
         * @param tag The tag text of the tag that was clicked.
         */
        void onTagClick(TagGroup tagGroup, CharSequence tag);
    }

    /**
     * Per-child layout information for layouts.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }

    /**
     * For {@link TagGroup} save and restore state.
     */
    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int tagCount;
        String[] tags;
        int checkedPosition;
        String input;

        public SavedState(Parcel source) {
            super(source);
            tagCount = source.readInt();
            tags = new String[tagCount];
            source.readStringArray(tags);
            checkedPosition = source.readInt();
            input = source.readString();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            tagCount = tags.length;
            dest.writeInt(tagCount);
            dest.writeStringArray(tags);
            dest.writeInt(checkedPosition);
            dest.writeString(input);
        }
    }

    /**
     * The tag view click listener for internal use.
     */
    class InternalTagClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            final TagView tag = (TagView) v;
            if (isAppendMode) {
                if (tag.isEditable()) {
                    // If the clicked tag is in INPUT state, uncheck the previous checked tag if exists.
                    final TagView checkedTag = getCheckedTag();
                    if (checkedTag != null) {
                        checkedTag.setChecked(false);
                    }
                } else {
                    // If the clicked tag is currently checked, delete the tag.
                    if (tag.isChecked()) {
                        if (tag.isInCheckedMarkerBound()) {
                            deleteTag(tag);
                        } else {
                            tag.setChecked(false);
                        }
                    } else {
                        // If the clicked tag is unchecked, uncheck the previous checked tag if exists,
                        // then check the clicked tag.
                        final TagView checkedTag = getCheckedTag();
                        if (checkedTag != null) {
                            checkedTag.setChecked(false);
                        }
                        tag.setChecked(true);
                    }
                }
            } else {
                if (mOnTagClickListener != null) {
                    mOnTagClickListener.onTagClick(TagGroup.this, tag.getText());
                }
            }
        }
    }

    private static boolean isActionEnter(int actionId, KeyEvent event) {
        return actionId == EditorInfo.IME_NULL
            && event != null
            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
            && event.getAction() == KeyEvent.ACTION_DOWN;
    }

    private final TextView.OnEditorActionListener mOnEditorEnterListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (isActionEnter(actionId, event)) {
                if (!TextUtils.isEmpty(v.getText())) {
                    // If the input content is available, end the input and dispatch
                    // the event, then append a new INPUT state tag.
                    ((TagView) v).endInput();
                    if (mOnTagChangeListener != null) {
                        mOnTagChangeListener.onAppend(TagGroup.this, v.getText());
                    }
                    appendInputTag();
                }
                return true;
            }
            return false;
        }
    };

    private final OnKeyListener mOnDeleteListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                // If the input content is empty, check or remove the last NORMAL state tag.
                TagView tagView = (TagView) v;
                if (TextUtils.isEmpty(tagView.getText())) {
                    TagView lastNormalTagView = getLastNormalTagView();
                    if (lastNormalTagView != null) {
                        if (lastNormalTagView.isChecked()) {
                            removeView(lastNormalTagView);
                            if (mOnTagChangeListener != null) {
                                mOnTagChangeListener.onDelete(TagGroup.this, lastNormalTagView.getText());
                            }
                        } else {
                            final TagView checkedTagView = getCheckedTag();
                            if (checkedTagView != null) {
                                checkedTagView.setChecked(false);
                            }
                            lastNormalTagView.setChecked(true);
                        }
                        return true;
                    }
                }
            }
            return false;
        }
    };

    private TextWatcher mBeforeTextChangeWatcher = new TextWatcherAdapter() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // When the INPUT state tag changed, uncheck the checked tag if exists.
            final TagView checkedTagView = getCheckedTag();
            if (checkedTagView != null) {
                checkedTagView.setChecked(false);
            }
        }
    };

    public TagView create(Context context, final boolean editable, CharSequence text) {
        TagView tagView = new TagView(context);
        tagView.setPadding(mHorizontalPadding, mVerticalPadding, mHorizontalPadding, mVerticalPadding);
        tagView.setLayoutParams(
            new TagGroup.LayoutParams(TagGroup.LayoutParams.WRAP_CONTENT, TagGroup.LayoutParams.WRAP_CONTENT));

        tagView.setGravity(Gravity.CENTER);
        tagView.setText(text);
        tagView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        tagView.setClickable(isAppendMode);
        tagView.setOnClickListener(mInternalTagClickListener);

        tagView.setEditable(editable, inputHint);
        if (editable) {
            tagView.requestFocus();
            tagView.setOnEditorActionListener(mOnEditorEnterListener);
            // Handle the BACKSPACE key down.
            tagView.setOnKeyListener(mOnDeleteListener);
            tagView.addTextChangedListener(mBeforeTextChangeWatcher);
        }

        // create one
        return tagView;
    }

    /**
     * The tag view which has two states can be either NORMAL or INPUT.
     */
    public class TagView extends AppCompatTextView {
        //private final OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
        //    @Override
        //    public boolean onLongClick(View v) {
        //        return ((TagView) v).isEditable();
        //    }
        //};

        /** The offset to the text. */
        private static final int CHECKED_MARKER_OFFSET = 3;

        /** The stroke width of the checked marker */
        private static final int CHECKED_MARKER_STROKE_WIDTH = 4;

        private boolean mEditable = false;

        /** Indicates the tag if checked. */
        private boolean isChecked = false;

        /** Indicates the tag if pressed. */
        private boolean isPressed = false;

        private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private Paint mCheckedMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        /** The rect for the tag's left corner drawing. */
        private RectF mLeftCornerRectF = new RectF();

        /** The rect for the tag's right corner drawing. */
        private RectF mRightCornerRectF = new RectF();

        /** The rect for the tag's horizontal blank fill area. */
        private RectF mHorizontalBlankFillRectF = new RectF();

        /** The rect for the tag's vertical blank fill area. */
        private RectF mVerticalBlankFillRectF = new RectF();

        /** The rect for the checked mark draw bound. */
        private RectF mCheckedMarkerBound = new RectF();

        /** Used to detect the touch event. */
        private Rect mOutRect = new Rect();

        /** The path for draw the tag's outline border. */
        private Path mBorderPath = new Path();

        /** The path effect provide draw the dash border. */
        private PathEffect mPathEffect = new DashPathEffect(new float[] { 10, 5 }, 0);

        private float mLastClickX, mLastClickY;

        public TagView(Context context) {
            super(context);
            init();
        }

        public TagView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            setWillNotDraw(false);
            setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            // Interrupted long click event to avoid PAUSE popup.
            //setOnLongClickListener(mOnLongClickListener);

            mEditable = true;
            setEditable(false, null);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(borderStrokeWidth);
            mBackgroundPaint.setStyle(Paint.Style.FILL);
            mCheckedMarkerPaint.setStyle(Paint.Style.FILL);
            mCheckedMarkerPaint.setStrokeWidth(CHECKED_MARKER_STROKE_WIDTH);
            mCheckedMarkerPaint.setColor(checkedMarkerColor);
        }

        private void resetPaint() {
            if (isAppendMode) {
                if (mEditable) {
                    mBorderPaint.setColor(dashBorderColor);
                    mBorderPaint.setPathEffect(mPathEffect);
                    mBackgroundPaint.setColor(backgroundColor);
                    setHintTextColor(inputHintColor);
                    setTextColor(inputTextColor);
                } else {
                    mBorderPaint.setPathEffect(null);
                    if (isChecked) {
                        mBorderPaint.setColor(checkedBorderColor);
                        mBackgroundPaint.setColor(checkedBackgroundColor);
                        setTextColor(checkedTextColor);
                    } else {
                        mBorderPaint.setColor(borderColor);
                        mBackgroundPaint.setColor(backgroundColor);
                        setTextColor(textColor);
                    }
                }
            } else {
                mBorderPaint.setColor(borderColor);
                mBackgroundPaint.setColor(backgroundColor);
                setTextColor(textColor);
            }

            if (isPressed) {
                mBackgroundPaint.setColor(pressedBackgroundColor);
            }
        }

        public void setEditable(boolean editable, @Nullable CharSequence hint) {
            if (mEditable == editable) {
                return;
            }

            mEditable = editable;
            setFocusable(editable);
            setFocusableInTouchMode(editable);
            setHint(hint);
            setMovementMethod(editable ? ArrowKeyMovementMethod.getInstance() : null);
            resetPaint();
        }

        /**
         * Call this method to end this tag's INPUT state.
         */
        public void endInput() {
            setEditable(false, null);
            requestLayout();
        }

        public boolean isEditable() {
            return mEditable;
        }

        /**
         * Set whether this tag view is in the checked state.
         *
         * @param checked true is checked, false otherwise
         */
        @SuppressWarnings("ResourceType")
        public void setChecked(boolean checked) {
            isChecked = checked;
            // Make the checked mark drawing region.
            int right = isChecked ? (int) (mHorizontalPadding + getHeight() / 2.5f + CHECKED_MARKER_OFFSET)
                : mHorizontalPadding;
            setPadding(mHorizontalPadding, mVerticalPadding, right, mVerticalPadding);
            resetPaint();
        }

        public boolean isChecked() {
            return isChecked;
        }

        @Override
        protected boolean getDefaultEditable() {
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawArc(mLeftCornerRectF, -180, 90, true, mBackgroundPaint);
            canvas.drawArc(mLeftCornerRectF, -270, 90, true, mBackgroundPaint);
            canvas.drawArc(mRightCornerRectF, -90, 90, true, mBackgroundPaint);
            canvas.drawArc(mRightCornerRectF, 0, 90, true, mBackgroundPaint);
            canvas.drawRect(mHorizontalBlankFillRectF, mBackgroundPaint);
            canvas.drawRect(mVerticalBlankFillRectF, mBackgroundPaint);

            if (isChecked) {
                canvas.save();
                canvas.rotate(45, mCheckedMarkerBound.centerX(), mCheckedMarkerBound.centerY());
                canvas.drawLine(mCheckedMarkerBound.left, mCheckedMarkerBound.centerY(), mCheckedMarkerBound.right,
                    mCheckedMarkerBound.centerY(), mCheckedMarkerPaint);
                canvas.drawLine(mCheckedMarkerBound.centerX(), mCheckedMarkerBound.top, mCheckedMarkerBound.centerX(),
                    mCheckedMarkerBound.bottom, mCheckedMarkerPaint);
                canvas.restore();
            }
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

            int m = (int) (h / 2.5f);
            h = bottom - top;
            mCheckedMarkerBound.set(right - m - mHorizontalPadding + CHECKED_MARKER_OFFSET, top + h / 2 - m / 2,
                right - mHorizontalPadding + CHECKED_MARKER_OFFSET, bottom - h / 2 + m / 2);

            // Ensure the checked mark drawing region is correct across screen orientation changes.
            if (isChecked) {
                setPadding(mHorizontalPadding, mVerticalPadding,
                    (int) (mHorizontalPadding + h / 2.5f + CHECKED_MARKER_OFFSET), mVerticalPadding);
            }
        }

        protected boolean isInCheckedMarkerBound() {
            return mCheckedMarkerBound.contains(mLastClickX, mLastClickY);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (mEditable) {
                // The INPUT tag doesn't change background color on the touch event.
                return super.onTouchEvent(event);
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mLastClickX = event.getX();
                    mLastClickY = event.getY();
                    getDrawingRect(mOutRect);
                    isPressed = true;
                    resetPaint();
                    invalidate();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!mOutRect.contains((int) event.getX(), (int) event.getY())) {
                        isPressed = false;
                        resetPaint();
                        invalidate();
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    isPressed = false;
                    resetPaint();
                    invalidate();
                    break;
                }
            }
            return super.onTouchEvent(event);
        }

        @Override
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
            return new ZanyInputConnection(super.onCreateInputConnection(outAttrs), true);
        }

        /**
         * Solve edit text delete(backspace) key detect, see<a href="http://stackoverflow.com/a/14561345/3790554">
         * Android: Backspace in WebView/BaseInputConnection</a>
         */
        private class ZanyInputConnection extends InputConnectionWrapper {
            public ZanyInputConnection(android.view.inputmethod.InputConnection target, boolean mutable) {
                super(target, mutable);
            }

            @Override
            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
                if (beforeLength == 1 && afterLength == 0) {
                    // backspace
                    return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) && sendKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                }
                return super.deleteSurroundingText(beforeLength, afterLength);
            }
        }
    }
}