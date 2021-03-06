/*
 * Copyright © 2016-2017 Trejkaz <trejkaz@trypticon.org>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING.WTFPL file for more details.
 *
 * This file additionally may contain code which programmatically renders
 * representations of elements of Hatsune Miku's design. Such art-as-code is
 * subject to the terms and conditions of the Creative Commons -
 * Attribution-NonCommercial, 3.0 Unported (CC BY-NC) licence.
 * See the Copying.CC_BY_NC file for more details.
 */

package org.trypticon.android.love39watchface.layers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import org.trypticon.android.love39watchface.R;
import org.trypticon.android.love39watchface.framework.PaintHolder;
import org.trypticon.android.love39watchface.framework.WatchModeHelper;
import org.trypticon.android.love39watchface.time.MultiTime;
import org.trypticon.android.love39watchface.time.TimeSystem;

/**
 * Layer which draws the date.
 */
class DateLayer extends Layer {
    private final Context context;
    private final TimeSystem timeSystem;
    private PaintHolder datePaint;

    private String formattedDate;

    DateLayer(final Context context, TimeSystem timeSystem) {
        this.context = context;
        this.timeSystem = timeSystem;

        updateDatePaint(400);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        updateDatePaint(bounds.width());
    }

    private void updateDatePaint(final float width) {
        datePaint = new PaintHolder(false) {
            @Override
            protected void configure(Paint paint) {
                Typeface roboto = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
                paint.setTypeface(roboto);
                paint.setColor(context.getColor(R.color.date_fill));
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(width * Proportions.DATE_SIZE);
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
            }
        };
    }

    @Override
    public void updateWatchMode(WatchModeHelper mode) {
        datePaint.updateWatchMode(mode);
    }

    @Override
    public void updateTime(MultiTime time) {
        formattedDate = time.getTime(timeSystem).getFormattedDate();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float width = getBounds().width();

        canvas.drawText(
                formattedDate, 0, formattedDate.length(),
                width * Proportions.DATE_X,
                width * Proportions.DATE_Y, datePaint.getPaint());
    }
}
