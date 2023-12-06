/******************************************************************************
* By accessing or copying this work, you agree to comply with the following   *
* terms:                                                                      *
*                                                                             *
* Copyright (c) 2019-2023 mesibo                                              *
* https://mesibo.com                                                          *
* All rights reserved.                                                        *
*                                                                             *
* Redistribution is not permitted. Use of this software is subject to the     *
* conditions specified at https://mesibo.com . When using the source code,    *
* maintain the copyright notice, conditions, disclaimer, and  links to mesibo * 
* website, documentation and the source code repository.                      *
*                                                                             *
* Do not use the name of mesibo or its contributors to endorse products from  *
* this software without prior written permission.                             *
*                                                                             *
* This software is provided "as is" without warranties. mesibo and its        *
* contributors are not liable for any damages arising from its use.           *
*                                                                             *
* Documentation: https://mesibo.com/documentation/                            *
*                                                                             *
* Source Code Repository: https://github.com/mesibo/                          *
*******************************************************************************/

package com.mesibo.uihelper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;

public class AutoResizeTextView extends AppCompatTextView {
    private static final int NO_LINE_LIMIT = -1;
    private final RectF _availableSpaceRect = new RectF();
    private final SizeTester _sizeTester;
    private float _maxTextSize, _spacingMult = 1.0f, _spacingAdd = 0.0f, _minTextSize;
    private int _widthLimit, _maxLines;
    private boolean _initialized = false;
    private TextPaint _paint;

    private interface SizeTester {
        /**
         * @param suggestedSize  Size of text to be tested
         * @param availableSpace available space in which text must fit
         * @return an integer < 0 if after applying {@code suggestedSize} to
         * text, it takes less space than {@code availableSpace}, > 0
         * otherwise
         */
        int onTestSize(int suggestedSize, RectF availableSpace);
    }

    public AutoResizeTextView(final Context context) {
        this(context, null, android.R.attr.textViewStyle);
    }

    public AutoResizeTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public AutoResizeTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        // using the minimal recommended font size
        _minTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        _maxTextSize = getTextSize();
        _paint = new TextPaint(getPaint());
        if (_maxLines == 0)
            // no value was assigned during construction
            _maxLines = NO_LINE_LIMIT;
        // prepare size tester:
        _sizeTester = new SizeTester() {
            final RectF textRect = new RectF();

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public int onTestSize(final int suggestedSize, final RectF availableSpace) {
                _paint.setTextSize(suggestedSize);
                final TransformationMethod transformationMethod = getTransformationMethod();
                final String text;
                if (transformationMethod != null)
                    text = transformationMethod.getTransformation(getText(), AutoResizeTextView.this).toString();
                else
                    text = getText().toString();
                final boolean singleLine = getMaxLines() == 1;
                if (singleLine) {
                    textRect.bottom = _paint.getFontSpacing();
                    textRect.right = _paint.measureText(text);
                } else {
                    final StaticLayout layout = new StaticLayout(text, _paint, _widthLimit, Alignment.ALIGN_NORMAL, _spacingMult, _spacingAdd, true);
                    // return early if we have more lines
                    if (getMaxLines() != NO_LINE_LIMIT && layout.getLineCount() > getMaxLines())
                        return 1;
                    textRect.bottom = layout.getHeight();
                    int maxWidth = -1;
                    int lineCount = layout.getLineCount();
                    for (int i = 0; i < lineCount; i++) {
                        int end = layout.getLineEnd(i);
                        if (i < lineCount - 1 && end > 0 && !isValidWordWrap(text.charAt(end - 1), text.charAt(end)))
                            return 1;
                        if (maxWidth < layout.getLineRight(i) - layout.getLineLeft(i))
                            maxWidth = (int) layout.getLineRight(i) - (int) layout.getLineLeft(i);
                    }
                    //for (int i = 0; i < layout.getLineCount(); i++)
                    //    if (maxWidth < layout.getLineRight(i) - layout.getLineLeft(i))
                    //        maxWidth = (int) layout.getLineRight(i) - (int) layout.getLineLeft(i);
                    textRect.right = maxWidth;
                }
                textRect.offsetTo(0, 0);
                if (availableSpace.contains(textRect))
                    // may be too small, don't worry we will find the best match
                    return -1;
                // else, too big
                return 1;
            }
        };
        _initialized = true;
    }

    public boolean isValidWordWrap(char before, char after) {
        return before == ' ' || before == '-';
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        super.setAllCaps(allCaps);
        adjustTextSize();
    }

    @Override
    public void setTypeface(final Typeface tf) {
        super.setTypeface(tf);
        adjustTextSize();
    }

    @Override
    public void setTextSize(final float size) {
        _maxTextSize = size;
        adjustTextSize();
    }

    @Override
    public void setMaxLines(final int maxLines) {
        super.setMaxLines(maxLines);
        _maxLines = maxLines;
        adjustTextSize();
    }

    @Override
    public int getMaxLines() {
        return _maxLines;
    }

    @Override
    public void setSingleLine() {
        super.setSingleLine();
        _maxLines = 1;
        adjustTextSize();
    }

    @Override
    public void setSingleLine(final boolean singleLine) {
        super.setSingleLine(singleLine);
        if (singleLine)
            _maxLines = 1;
        else _maxLines = NO_LINE_LIMIT;
        adjustTextSize();
    }

    @Override
    public void setLines(final int lines) {
        super.setLines(lines);
        _maxLines = lines;
        adjustTextSize();
    }

    @Override
    public void setTextSize(final int unit, final float size) {
        final Context c = getContext();
        Resources r;
        if (c == null)
            r = Resources.getSystem();
        else r = c.getResources();
        _maxTextSize = TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
        adjustTextSize();
    }

    @Override
    public void setLineSpacing(final float add, final float mult) {
        super.setLineSpacing(add, mult);
        _spacingMult = mult;
        _spacingAdd = add;
    }

    /**
     * Set the lower text size limit and invalidate the view
     *
     * @param minTextSize
     */
    public void setMinTextSize(final float minTextSize) {
        _minTextSize = minTextSize;
        adjustTextSize();
    }

    private void adjustTextSize() {
        // This is a workaround for truncated text issue on ListView, as shown here: https://github.com/AndroidDeveloperLB/AutoFitTextView/pull/14
        // TODO think of a nicer, elegant solution.
//    post(new Runnable()
//    {
//    @Override
//    public void run()
//      {
        if (!_initialized)
            return;
        final int startSize = (int) _minTextSize;
        final int heightLimit = getMeasuredHeight() - getCompoundPaddingBottom() - getCompoundPaddingTop();
        _widthLimit = getMeasuredWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
        if (_widthLimit <= 0)
            return;
        _paint = new TextPaint(getPaint());
        _availableSpaceRect.right = _widthLimit;
        _availableSpaceRect.bottom = heightLimit;
        superSetTextSize(startSize);
//      }
//    });
    }

    private void superSetTextSize(int startSize) {
        int textSize = binarySearch(startSize, (int) _maxTextSize, _sizeTester, _availableSpaceRect);
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    private int binarySearch(final int start, final int end, final SizeTester sizeTester, final RectF availableSpace) {
        int lastBest = start, lo = start, hi = end - 1, mid;
        while (lo <= hi) {
            mid = lo + hi >>> 1;
            final int midValCmp = sizeTester.onTestSize(mid, availableSpace);
            if (midValCmp < 0) {
                lastBest = lo;
                lo = mid + 1;
            } else if (midValCmp > 0) {
                hi = mid - 1;
                lastBest = hi;
            } else return mid;
        }
        // make sure to return last best
        // this is what should always be returned
        return lastBest;
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        super.onTextChanged(text, start, before, after);
        adjustTextSize();
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldwidth, final int oldheight) {
        super.onSizeChanged(width, height, oldwidth, oldheight);
        if (width != oldwidth || height != oldheight)
            adjustTextSize();
    }
}
