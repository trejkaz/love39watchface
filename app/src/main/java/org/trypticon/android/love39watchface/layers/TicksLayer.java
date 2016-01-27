package org.trypticon.android.love39watchface.layers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;

import com.ustwo.clockwise.WatchShape;

import org.trypticon.android.love39watchface.R;
import org.trypticon.android.love39watchface.framework.PaintHolder;
import org.trypticon.android.love39watchface.framework.WatchModeHelper;
import org.trypticon.android.love39watchface.framework.Workarounds;

/**
 * Layer which draws the ticks on the watch face.
 */
class TicksLayer extends Layer {
    private final int subdivisions;
    private final int count;

    private final boolean zeroAtTop;
    private final Drawable[] digits;
    private final Drawable[] ticks;
    private final PaintHolder primaryTickPaint;
    private final PaintHolder secondaryTickPaint;

    private boolean round;

    TicksLayer(final Context context, int subdivisions, boolean zeroAtTop, Drawable[] digits) {
        this.subdivisions = subdivisions;
        count = 12 * subdivisions;

        this.zeroAtTop = zeroAtTop;
        this.digits = digits;
        ticks = new Drawable[count];

        primaryTickPaint = new PaintHolder(false) {
            @Override
            protected void configure(Paint paint) {
                paint.setColor(Workarounds.getColor(context, R.color.analog_primary_tick_stroke));
                paint.setStrokeWidth(context.getResources().getDimension(R.dimen.analog_primary_tick_stroke));
                paint.setAntiAlias(true);
                paint.setStrokeCap(Paint.Cap.SQUARE);
                paint.setStyle(Paint.Style.STROKE);
            }
        };

        secondaryTickPaint = new PaintHolder(false) {
            @Override
            protected void configure(Paint paint) {
                paint.setColor(Workarounds.getColor(context, R.color.analog_secondary_tick_stroke));
                paint.setStrokeWidth(context.getResources().getDimension(R.dimen.analog_secondary_tick_stroke));
                paint.setAntiAlias(true);
                paint.setStrokeCap(Paint.Cap.SQUARE);
                paint.setStyle(Paint.Style.STROKE);
            }
        };
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateGeometry();
    }

    @Override
    public void updateShape(WatchShape shape) {
        this.round = shape == WatchShape.CIRCLE;
        updateGeometry();
    }

    @Override
    public void updateWatchMode(WatchModeHelper mode) {
        primaryTickPaint.updateWatchMode(mode);
        secondaryTickPaint.updateWatchMode(mode);
        updateGeometry();
    }

    /**
     * Updates all the geometry so that we don't have to do all the maths at drawing-time.
     */
    private void updateGeometry() {
        float width = getBounds().width();
        float height = getBounds().height();
        float centerX = getBounds().centerX();
        float centerY = getBounds().centerY();
        float baseTickDistance = getBounds().centerX() - 3;
        float scale = width / 400.0f;

        int angleShift = zeroAtTop ? 180 : 0;
        for (int i = 0, degrees = 0; i < count; i++, degrees += 360 / count) {
            float tickDistance = computeTickDistance(baseTickDistance, degrees);
            double radians = Math.toRadians(degrees + angleShift);
            float directionX = - (float) Math.sin(radians);
            float directionY = + (float) Math.cos(radians);

            if (i % subdivisions == 0) {
                // primary tick
                Path path = new Path();
                path.moveTo(
                        centerX + directionX * (tickDistance + 2),
                        centerY + directionY * (tickDistance + 2));
                path.lineTo(
                        centerX + directionX * (tickDistance * 2),
                        centerY + directionY * (tickDistance * 2));
                ShapeDrawable tick = new ShapeDrawable(new PathShape(path, width, height));
                tick.getPaint().set(primaryTickPaint.getPaint());
                tick.setBounds(getBounds());
                ticks[i] = tick;

                float digitDistance = tickDistance - digits[0].getIntrinsicHeight();
                float digitX = centerX + digitDistance * directionX;
                float digitY = centerY + digitDistance * directionY;

                Drawable digitDrawable = digits[i / subdivisions];
                float digitWidth = digitDrawable.getIntrinsicWidth() * scale;
                float digitHeight = digitDrawable.getIntrinsicHeight() * scale;

                digitDrawable.setBounds(
                        (int) (digitX - digitWidth / 2.0f),
                        (int) (digitY - digitHeight / 2.0f),
                        (int) (digitX + digitWidth / 2.0f),
                        (int) (digitY + digitHeight / 2.0f));

            } else {
                // secondary tick
                Path path = new Path();
                path.moveTo(
                        centerX + directionX * (tickDistance - 8),
                        centerY + directionY * (tickDistance - 8));
                path.lineTo(
                        centerX + directionX * (tickDistance * 2),
                        centerY + directionY * (tickDistance * 2));
                ShapeDrawable tick = new ShapeDrawable(new PathShape(path, width, height));
                tick.getPaint().set(secondaryTickPaint.getPaint());
                tick.setBounds(getBounds());
                ticks[i] = tick;
            }
        }
    }

    /**
     * Computes the distance of a tick from the centre.
     *
     * @param baseTickDistance the distance the tick would be if the watch face were circular.
     * @param degrees the number of degrees for the current rotation.
     * @return the tick distance.
     */
    private float computeTickDistance(float baseTickDistance, float degrees) {
        if (round) {
            return baseTickDistance;
        } else {
            double radians = Math.toRadians(degrees % 90);
            double factor = 2.5;
            return (float) (baseTickDistance / (
                    Math.pow((Math.pow(Math.cos(radians), factor) +
                                    Math.pow(Math.sin(radians), factor)),
                            1.0f/factor)));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        for (Drawable digit : digits) {
            digit.draw(canvas);
        }
        for (Drawable tick : ticks) {
            tick.draw(canvas);
        }
    }
}