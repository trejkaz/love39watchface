package org.trypticon.android.love39watchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.ustwo.clockwise.WatchMode;

import org.trypticon.android.love39watchface.framework.PaintHolder;
import org.trypticon.android.love39watchface.framework.PaintUtils;
import org.trypticon.android.love39watchface.framework.WatchModeAware;
import org.trypticon.android.love39watchface.framework.WatchModeHelper;
import org.trypticon.android.love39watchface.framework.Workarounds;
import org.trypticon.android.love39watchface.time.Time;

/**
 * Drawable to draw the hands.
 */
public abstract class Hands extends Drawable implements WatchModeAware {

    private final Context context;

    private Hand hourHand;
    private Hand minuteHand;
    private Hand secondHand;
    private Hand thirdHand;

    private final PaintHolder centrePaint;
    private final float centreRadius;

    private final Path handGlowPath;
    private final Paint handGlowPaint;

    // Default to interactive for rendering in other places, like the config screen.
    private WatchModeHelper watchMode = new WatchModeHelper(WatchMode.INTERACTIVE);

    public Hands(final Context context) {
        this.context = context;

        centrePaint = new PaintHolder(true) {
            @Override
            protected void configure(Paint paint) {
                paint.setColor(Workarounds.getColor(context, R.color.analog_centre_fill));
                paint.setAntiAlias(true);
                paint.setStrokeWidth(context.getResources().getDimension(R.dimen.analog_centre_stroke));
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
            }
        };

        centreRadius = context.getResources().getDimension(R.dimen.analog_centre_radius);

        handGlowPaint = PaintUtils.createGlowPaint(context, R.color.analog_centre_fill);
        handGlowPath = new Path();

        createHands();
    }

    abstract boolean isZeroAtTop();

    abstract boolean hasThirds();

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        createHands();
    }

    private void createHands() {
        Rect bounds = getBounds();
        int centerX = bounds.centerX();
        int centerY = bounds.centerY();
        float scale = bounds.width() / 400.0f;

        //todo should change these to % at some point
        float thirdLength = centerX - 30 * scale;
        float secondLength = centerX - 35 * scale;
        float minuteLength = centerX - 40 * scale;
        float hourLength = centerX - 60 * scale;

        //todo scale hand widths too :(
        hourHand = new Hand(
                context,
                R.dimen.analog_hand_width,
                R.color.analog_hands_fill,
                R.dimen.analog_hand_stroke_width,
                centerX, centerY, hourLength, true, false);
        minuteHand = new Hand(
                context,
                R.dimen.analog_hand_width,
                R.color.analog_hands_fill,
                R.dimen.analog_hand_stroke_width,
                centerX, centerY, minuteLength, true, true);
        secondHand = new Hand(
                context,
                R.dimen.analog_second_hand_width,
                hasThirds() ? R.color.analog_second_hand_fill
                        : R.color.analog_third_hand_fill,
                R.dimen.analog_second_hand_stroke_width,
                centerX, centerY, secondLength, false, false);
        thirdHand = new Hand(
                context,
                R.dimen.analog_third_hand_width,
                R.color.analog_third_hand_fill,
                R.dimen.analog_third_hand_stroke_width,
                centerX, centerY, thirdLength, false, false);
    }

    public void updateTime(Time time) {
        int angleShift = isZeroAtTop() ? 180 : 0;
        hourHand.updateAngle(time.getHourTurns() * 360 + angleShift);
        minuteHand.updateAngle(time.getMinuteTurns() * 360 + angleShift);

        if (watchMode.isInteractive()) {
            secondHand.updateAngle(time.getSecondTurns() * 360 + angleShift);
            if (time.hasThirds()) {
                thirdHand.updateAngle(time.getThirdTurns() * 360 + angleShift);
            }
        }
    }

    @Override
    public void updateWatchMode(WatchModeHelper mode) {
        watchMode = mode;
        centrePaint.updateWatchMode(mode);
        hourHand.updateWatchMode(mode);
        minuteHand.updateWatchMode(mode);
        secondHand.updateWatchMode(mode);
        thirdHand.updateWatchMode(mode);
    }

    @Override
    public void draw(Canvas canvas) {
        //todo could probably precompute more in onBoundsChange
        Rect bounds = getBounds();
        int centerX = bounds.centerX();
        int centerY = bounds.centerY();

        if (watchMode.isInteractive()) {
            handGlowPath.reset();
            handGlowPath.addPath(hourHand.getPath());
            handGlowPath.addPath(minuteHand.getPath());
            handGlowPath.addCircle(centerX, centerY, centreRadius, Path.Direction.CCW);
            canvas.drawPath(handGlowPath, handGlowPaint);
        }

        hourHand.draw(canvas);
        minuteHand.draw(canvas);

        if (watchMode.isInteractive()) {
            secondHand.draw(canvas);
            if (hasThirds()) {
                thirdHand.draw(canvas);
            }
        }

        canvas.drawCircle(centerX, centerY, centreRadius, centrePaint.getPaint());
    }

    @Override
    public void setAlpha(int alpha) {
        // Ignored for now.
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // Ignored for now.
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
