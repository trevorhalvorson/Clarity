package com.trevorhalvorson.clarity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClarityView extends View {
    private static final String TAG = "ClarityView";
    private static final int DEFAULT_TEXT_COLOR = android.R.color.black;
    private static final float DEFAULT_TEXT_SIZE = 12;
    private static final String NUMBERS = "0123456789";
    private static final String LOWER_ALPHA = "abcdefghijklmopqrstuvwxyz";
    private static final String UPPER_ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private StaticLayout mLayout;
    private TextPaint mTextPaint;
    private SpannableStringBuilder mSpannableStringBuilder;

    private Pattern mSymbolPattern;
    private Pattern mNumberPattern;
    private Pattern mLowerAlphaPattern;
    private Pattern mUpperAlphaPattern;

    private float mTextSize;

    private int mLowerAlphaColor;
    private int mUpperAlphaColor;
    private int mNumberColor;
    private int mSymbolColor;

    public ClarityView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ClarityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ClarityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public ClarityView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setAntiAlias(true);
        int defTextColor = ContextCompat.getColor(context, DEFAULT_TEXT_COLOR);

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
                R.styleable.ClarityView, defStyleAttr, defStyleRes);

        try {
            setText(styledAttrs.getString(R.styleable.ClarityView_android_text));

            setTextSize(styledAttrs.getDimension(R.styleable.ClarityView_android_textSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            DEFAULT_TEXT_SIZE, getResources().getDisplayMetrics())));

            setSymbolColor(styledAttrs.getColor(
                    R.styleable.ClarityView_clarity_symbolColor, defTextColor));

            setNumberColor(styledAttrs.getColor(
                    R.styleable.ClarityView_clarity_numberColor, defTextColor));

            setUpperAlphabetColor(styledAttrs.getColor(
                    R.styleable.ClarityView_clarity_upperAlphabetColor, defTextColor));

            setLowerAlphabetColor(styledAttrs.getColor(
                    R.styleable.ClarityView_clarity_lowerAlphabetColor, defTextColor));
        } finally {
            styledAttrs.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = resolveSize(getDesiredWidth(), widthMeasureSpec);
        int measuredHeight = resolveSize(getDesiredHeight(), heightMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        mLayout.draw(canvas);
        canvas.restore();
    }

    private int getDesiredWidth() {
        final float maxWidth = mLayout.getWidth();
        return Math.round(maxWidth + getPaddingLeft() + getPaddingRight());
    }

    private int getDesiredHeight() {
        final float maxHeight = mLayout.getHeight();
        return Math.round(maxHeight + getPaddingTop() + getPaddingBottom());
    }

    private void setColors() {
        setSymbolColor(mSymbolColor);
        setUpperAlphabetColor(mUpperAlphaColor);
        setLowerAlphabetColor(mLowerAlphaColor);
        setNumberColor(mNumberColor);
    }

    //*** PUBLIC API ***//

    /**
     * @return the current text the {@link ClarityView} is displaying
     */
    public String getText() {
        return mSpannableStringBuilder.toString();
    }

    /**
     * Sets the text value to display
     *
     * @param text text to display
     */
    public void setText(String text) {
        if (text != null) {
            mSpannableStringBuilder = new SpannableStringBuilder(text);
            int width = (int) mTextPaint.measureText(text);
            mLayout = new StaticLayout(mSpannableStringBuilder, mTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
            setColors();
            invalidate();
            requestLayout();
        }
    }

    /**
     * @return the size of the text size in this {@link ClarityView}
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * Sets the text size
     *
     * @param textSize int specifying
     */
    public void setTextSize(float textSize) {
        if (mTextSize != textSize) {
            mTextSize = textSize;
            mTextPaint.setTextSize(Math.round(textSize *
                    getResources().getDisplayMetrics().scaledDensity));
            invalidate();
            requestLayout();
        }

    }

    /**
     * @return the color used for symbolic characters
     */
    public int getSymbolColor() {
        return mSymbolColor;
    }

    /**
     * Sets the text color for symbolic characters (ASCII 33 - 47 AND 58 - 64 AND 91 - 96 AND 123 - 126)
     *
     * @param color the color used for numeric characters
     */
    public void setSymbolColor(int color) {
        mSymbolColor = color;
        if (mSymbolPattern == null) {
            mSymbolPattern =
                    Pattern.compile("[^" + LOWER_ALPHA + UPPER_ALPHA + NUMBERS + "]",
                            Pattern.UNICODE_CASE);
        }
        if (mSpannableStringBuilder != null) {
            Matcher matcher = mSymbolPattern.matcher(mSpannableStringBuilder.toString());
            while (matcher.find()) {
                mSpannableStringBuilder.setSpan(new ForegroundColorSpan(color),
                        matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        invalidate();
    }

    /**
     * @return the color used for numeric characters
     */
    public int getNumberColor() {
        return mNumberColor;
    }

    /**
     * Sets the text color for numeric characters 0-9 (ASCII 48 - 57)
     *
     * @param color the color used for numeric characters
     */
    public void setNumberColor(int color) {
        mNumberColor = color;
        if (mNumberPattern == null) {
            mNumberPattern =
                    Pattern.compile("[" + NUMBERS + "]",
                            Pattern.UNICODE_CASE);
        }
        if (mSpannableStringBuilder != null) {
            Matcher matcher = mNumberPattern.matcher(mSpannableStringBuilder.toString());
            while (matcher.find()) {
                mSpannableStringBuilder.setSpan(new ForegroundColorSpan(color),
                        matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        invalidate();
    }

    /**
     * @return the color used for upper case alphabetic characters
     */
    public int getUpperAlphabetColor() {
        return mUpperAlphaColor;
    }

    /**
     * Sets the text color for upper case alphabet characters A-Z (ASCII 65 - 90)
     *
     * @param color color used for upper case alphabet characters
     */
    public void setUpperAlphabetColor(int color) {
        mUpperAlphaColor = color;
        if (mUpperAlphaPattern == null) {
            mUpperAlphaPattern =
                    Pattern.compile("[" + UPPER_ALPHA + "]",
                            Pattern.UNICODE_CASE);
        }
        if (mSpannableStringBuilder != null) {
            Matcher matcher = mUpperAlphaPattern.matcher(mSpannableStringBuilder.toString());
            while (matcher.find()) {
                mSpannableStringBuilder.setSpan(new ForegroundColorSpan(color),
                        matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        invalidate();
    }

    /**
     * @return the color used for lower case alphabetic characters
     */
    public int getLowerAlphabetColor() {
        return mLowerAlphaColor;
    }

    /**
     * Sets the text color for lower case alphabet characters a-z (ASCII 97 - 122)
     *
     * @param color color used for lower case alphabet characters
     */
    public void setLowerAlphabetColor(int color) {
        mLowerAlphaColor = color;
        if (mLowerAlphaPattern == null) {
            mLowerAlphaPattern =
                    Pattern.compile("[" + LOWER_ALPHA + "]",
                            Pattern.UNICODE_CASE);
        }
        if (mSpannableStringBuilder != null) {
            Matcher matcher = mLowerAlphaPattern.matcher(mSpannableStringBuilder.toString());
            while (matcher.find()) {
                mSpannableStringBuilder.setSpan(new ForegroundColorSpan(color),
                        matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        invalidate();
    }
}
