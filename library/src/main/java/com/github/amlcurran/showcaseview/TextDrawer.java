/*
 * Copyright 2014 Alex Curran
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

package com.github.amlcurran.showcaseview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;

/**
 * Draws the text as required by the ShowcaseView
 */
class TextDrawer {
    public static final String TAG = TextDrawer.class.getSimpleName();

    private static final int INDEX_TEXT_START_X = 0;
    private static final int INDEX_TEXT_START_Y = 1;
    private static final int INDEX_TEXT_WIDTH = 2;
    private static final int INDEX_TEXT_HEIGHT = 3;

    private final TextPaint titlePaint;
    private final TextPaint textPaint;
    private final Context context;
    private final float padding;
    private final float actionBarOffset;

    private Layout.Alignment textAlignment = Layout.Alignment.ALIGN_NORMAL;
    private SpannableString textString;
    private DynamicLayout textLayout;
    private MetricAffectingSpan textSpan;

    private Layout.Alignment titleAlignment = Layout.Alignment.ALIGN_NORMAL;
    private SpannableString titleString;
    private DynamicLayout titleLayout;
    private MetricAffectingSpan titleSpan;

    private Drawable image;
    private float[] bestImagePosition = new float[4];

    private float[] mBestTextPosition = new float[4];
    private boolean hasRecalculated;
    @ShowcaseView.TextPosition
    private int forcedTextPosition = ShowcaseView.UNDEFINED;

    public TextDrawer(Resources resources, Context context) {
        padding = resources.getDimension(R.dimen.text_padding);
        actionBarOffset = resources.getDimension(R.dimen.action_bar_offset);

        this.context = context;

        titlePaint = new TextPaint();
        titlePaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
    }

    public void draw(Canvas canvas) {
        if (shouldDrawText()) {
            float[] textPosition = getBestTextPosition();
            int width = Math.max(0, (int) mBestTextPosition[INDEX_TEXT_WIDTH]);

            if (!TextUtils.isEmpty(titleString)) {
                canvas.save();
                if (hasRecalculated) {
                    titleLayout = new DynamicLayout(titleString, titlePaint,
                                                            width, titleAlignment, 1.0f, 1.0f, true);
                }
                if (titleLayout != null) {
                    canvas.translate(textPosition[INDEX_TEXT_START_X], textPosition[INDEX_TEXT_START_Y]);
                    titleLayout.draw(canvas);
                    canvas.restore();
                }
            }

            if (!TextUtils.isEmpty(textString)) {
                canvas.save();
                if (hasRecalculated) {
                    textLayout = new DynamicLayout(textString, textPaint,
                                                             width, textAlignment, 1.2f, 1.0f, true);
                }
                float offsetForTitle = titleLayout != null ? titleLayout.getHeight() : 0;
                if (textLayout != null) {
                    canvas.translate(textPosition[INDEX_TEXT_START_X], textPosition[INDEX_TEXT_START_Y] + offsetForTitle);
                    textLayout.draw(canvas);
                    canvas.restore();
                }

            }

            if (image != null) {
                canvas.save();
                float offsetForText = (titleLayout != null ? titleLayout.getHeight() : 0) +
                        (textLayout != null ? textLayout.getHeight() : 0);
                canvas.translate(textPosition[INDEX_TEXT_START_X], textPosition[INDEX_TEXT_START_Y] + offsetForText + padding);
                float widthRatio =  mBestTextPosition[INDEX_TEXT_WIDTH] / image.getIntrinsicWidth();
                float heightRatio = (mBestTextPosition[INDEX_TEXT_HEIGHT] - offsetForText + (2 * padding)) / image.getIntrinsicHeight();

                bestImagePosition[INDEX_TEXT_START_X] = 0;
                bestImagePosition[INDEX_TEXT_START_Y] = 0;

                if (widthRatio < heightRatio) {
                    bestImagePosition[INDEX_TEXT_WIDTH] = image.getIntrinsicWidth() * widthRatio;
                    bestImagePosition[INDEX_TEXT_HEIGHT] = image.getIntrinsicHeight() * widthRatio;
                } else {
                    bestImagePosition[INDEX_TEXT_WIDTH] = image.getIntrinsicWidth() * heightRatio;
                    bestImagePosition[INDEX_TEXT_HEIGHT] = image.getIntrinsicHeight() * heightRatio;
                }

                image.setBounds((int) bestImagePosition[INDEX_TEXT_START_X],
                        (int) bestImagePosition[INDEX_TEXT_START_Y],
                        (int) (bestImagePosition[INDEX_TEXT_WIDTH] + bestImagePosition[INDEX_TEXT_START_X] - (2 * padding)),
                        (int) (bestImagePosition[INDEX_TEXT_HEIGHT] + bestImagePosition[INDEX_TEXT_START_Y] - (2 * padding)));
                image.draw(canvas);
                canvas.restore();
            }
        }
        hasRecalculated = false;
    }

    public void setContentText(CharSequence details) {
        if (details != null) {
            SpannableString ssbDetail = new SpannableString(details);
            ssbDetail.setSpan(textSpan, 0, ssbDetail.length(), 0);
            textString = ssbDetail;
            hasRecalculated = true;
        }
    }

    public void setContentTitle(CharSequence title) {
        if (title != null) {
            SpannableString ssbTitle = new SpannableString(title);
            ssbTitle.setSpan(titleSpan, 0, ssbTitle.length(), 0);
            titleString = ssbTitle;
            hasRecalculated = true;
        }
    }

    public void setContentImage(@DrawableRes Integer drawableRes) {
        if (drawableRes != null) {
            image = ContextCompat.getDrawable(context, drawableRes);
            hasRecalculated = true;
        }
    }
    /**
     * Calculates the best place to position text
     *  @param canvasW width of the screen
     * @param canvasH height of the screen
     * @param shouldCentreText
     * @param showcase
     */
    public void calculateTextPosition(int canvasW, int canvasH, boolean shouldCentreText, Rect showcase, Rect button) {

        int[] areas = new int[4]; //left, top, right, bottom
    	areas[ShowcaseView.LEFT_OF_SHOWCASE] = showcase.left * canvasH;
    	areas[ShowcaseView.ABOVE_SHOWCASE] = showcase.top * canvasW;
    	areas[ShowcaseView.RIGHT_OF_SHOWCASE] = (canvasW - showcase.right) * canvasH;
    	areas[ShowcaseView.BELOW_SHOWCASE] = (canvasH - showcase.bottom) * canvasW;
    	
    	int largest = 0;
    	for(int i = 1; i < areas.length; i++) {
    		if(areas[i] > areas[largest])
    			largest = i;
    	}

        if (forcedTextPosition != ShowcaseView.UNDEFINED) {
            largest = forcedTextPosition;
        }

    	// Position text in largest area
    	switch(largest) {
    	case ShowcaseView.LEFT_OF_SHOWCASE:
    		mBestTextPosition[INDEX_TEXT_START_X] = padding;
    		mBestTextPosition[INDEX_TEXT_START_Y] = padding;
    		mBestTextPosition[INDEX_TEXT_WIDTH] = showcase.left - 2 * padding;
    		mBestTextPosition[INDEX_TEXT_HEIGHT] = canvasH - (2 * padding);
    		break;
    	case ShowcaseView.ABOVE_SHOWCASE:
    		mBestTextPosition[INDEX_TEXT_START_X] = padding;
    		mBestTextPosition[INDEX_TEXT_START_Y] = padding + actionBarOffset;
    		mBestTextPosition[INDEX_TEXT_WIDTH] = canvasW - 2 * padding;
            mBestTextPosition[INDEX_TEXT_HEIGHT] = canvasH - (showcase.height() + padding);
    		break;
    	case ShowcaseView.RIGHT_OF_SHOWCASE:
    		mBestTextPosition[INDEX_TEXT_START_X] = showcase.right + padding;
    		mBestTextPosition[INDEX_TEXT_START_Y] = padding;
    		mBestTextPosition[INDEX_TEXT_WIDTH] = (canvasW - showcase.right) - 2 * padding;
            mBestTextPosition[INDEX_TEXT_HEIGHT] = canvasH - (2 * padding);
    		break;
    	case ShowcaseView.BELOW_SHOWCASE:
    		mBestTextPosition[INDEX_TEXT_START_X] = padding;
    		mBestTextPosition[INDEX_TEXT_START_Y] = showcase.bottom + padding;
    		mBestTextPosition[INDEX_TEXT_WIDTH] = canvasW - 2 * padding;
            mBestTextPosition[INDEX_TEXT_HEIGHT] = canvasH - (showcase.height() + padding);
    		break;
    	}
    	if(shouldCentreText) {
	    	// Center text vertically or horizontally
	    	switch(largest) {
	    	case ShowcaseView.LEFT_OF_SHOWCASE:
	    	case ShowcaseView.RIGHT_OF_SHOWCASE:
	    		mBestTextPosition[INDEX_TEXT_START_Y] += canvasH / 4;
	    		break;
	    	case ShowcaseView.ABOVE_SHOWCASE:
	    	case ShowcaseView.BELOW_SHOWCASE:
	    		mBestTextPosition[INDEX_TEXT_WIDTH] /= 2;
	    		mBestTextPosition[INDEX_TEXT_START_X] += canvasW / 4;
	    		break;
	    	} 
    	} else {
    		// As text is not centered add actionbar padding if the text is left or right
	    	switch(largest) {
	    		case ShowcaseView.LEFT_OF_SHOWCASE:
	    		case ShowcaseView.RIGHT_OF_SHOWCASE:
	    			mBestTextPosition[INDEX_TEXT_START_Y] += actionBarOffset;
	    			break;
	    	}
    	}

    	Rect containingRect = new Rect((int) mBestTextPosition[INDEX_TEXT_START_X],
                (int) mBestTextPosition[INDEX_TEXT_START_Y],
                (int) (mBestTextPosition[INDEX_TEXT_START_X] + mBestTextPosition[INDEX_TEXT_WIDTH]),
                (int) (mBestTextPosition[INDEX_TEXT_START_Y] + mBestTextPosition[INDEX_TEXT_HEIGHT]));

    	if (containingRect.contains(button)) {

        }

        hasRecalculated = true;
    }

    public void setTitleStyling(int styleId) {
        titleSpan = new TextAppearanceSpan(this.context, styleId);
        setContentTitle(titleString);
    }

    public void setDetailStyling(int styleId) {
        textSpan = new TextAppearanceSpan(this.context, styleId);
        setContentText(textString);
    }

    public float[] getBestTextPosition() {
        return mBestTextPosition;
    }

    public boolean shouldDrawText() {
        return !TextUtils.isEmpty(titleString) || !TextUtils.isEmpty(textString);
    }

    public void setContentPaint(TextPaint contentPaint) {
        textPaint.set(contentPaint);
        if (textString != null) {
            textString.removeSpan(textSpan);
        }
        textSpan = new NoOpSpan();
        setContentText(textString);
    }

    public void setTitlePaint(TextPaint textPaint) {
        titlePaint.set(textPaint);
        if (titleString != null) {
            titleString.removeSpan(titleSpan);
        }
        titleSpan = new NoOpSpan();
        setContentTitle(titleString);
    }

    public void setDetailTextAlignment(Layout.Alignment textAlignment) {
        this.textAlignment = textAlignment;
    }

    public void setTitleTextAlignment(Layout.Alignment titleTextAlignment) {
        this.titleAlignment = titleTextAlignment;
    }

    public void forceTextPosition(@ShowcaseView.TextPosition int textPosition) {
        if (textPosition > ShowcaseView.BELOW_SHOWCASE || textPosition < ShowcaseView.UNDEFINED) {
            throw new IllegalArgumentException("ShowcaseView text was forced with an invalid position");
        }
        forcedTextPosition = textPosition;
    }

    private static class NoOpSpan extends MetricAffectingSpan {
        @Override
        public void updateDrawState(TextPaint tp) {
            // No-op
        }

        @Override
        public void updateMeasureState(TextPaint p) {
            // No-op
        }
    }
}
