package org.trypticon.dozenalwatchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Encapsulates the drawing of the watch face itself.
 */
class DozenalWatchFace extends Drawable {
    private final Context context;

    private final Paint clearPaint;
    private final Paint realBackgroundPaint;
    private Paint backgroundPaint;

    private float centerX;
    private float centerY;
    private Hand hourHand;
    private Hand minuteHand;
    private Hand secondHand;
    private Hand thirdHand;

    private final Paint centreFill;
    private final float centreRadius;

    private final Ticks ticks;

    private final Paint datePaint;

    private boolean ambient;
    private boolean highQuality;
    private final DozenalTime dozenalTime = new DozenalTime();
    private final DozenalDateFormat dozenalDateFormat = new DozenalDateFormat();

    DozenalWatchFace(Context context) {
        this.context = context;

        clearPaint = new Paint();
        clearPaint.setColor(Color.BLACK);

        realBackgroundPaint = new Paint();
        realBackgroundPaint.setColor(Workarounds.getColor(context, R.color.analog_background));

        centreFill = new Paint();
        centreFill.setColor(Workarounds.getColor(context, R.color.analog_centre_fill));
        centreFill.setAntiAlias(true);
        centreFill.setStrokeWidth(context.getResources().getDimension(R.dimen.analog_centre_stroke));
        centreFill.setStrokeCap(Paint.Cap.ROUND);
        centreFill.setStyle(Paint.Style.FILL_AND_STROKE);
        centreFill.setShadowLayer(
                context.getResources().getDimension(R.dimen.analog_hand_shadow_width),
                0, 0, Workarounds.getColor(context, R.color.analog_centre_fill));

        centreRadius = context.getResources().getDimension(R.dimen.analog_centre_radius);

        ticks = new Ticks(context);

        Typeface roboto = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
        datePaint = new Paint();
        datePaint.setTypeface(roboto);
        datePaint.setColor(Workarounds.getColor(context, R.color.date_fill));
        datePaint.setTextAlign(Paint.Align.CENTER);
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
    }

    @Override
    public void draw(Canvas canvas) {
        dozenalTime.setToNow();

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);

        canvas.drawText(
                dozenalDateFormat.formatDate(dozenalTime),
                centerX, centerY * 1 / 2, datePaint);

        if (!ambient) {
            thirdHand.draw(canvas, dozenalTime.getThirdTurns() * 360);
            secondHand.draw(canvas, dozenalTime.getSecondTurns() * 360);
        }

        minuteHand.draw(canvas, dozenalTime.getMinuteTurns() * 360);
        hourHand.draw(canvas, dozenalTime.getHourTurns() * 360);

        canvas.drawCircle(centerX, centerY, centreRadius, centreFill);

        ticks.draw(canvas);
    }

    void updateShape(boolean round) {
        ticks.updateShape(round);
    }

    void updateLocale(Locale locale) {
        dozenalDateFormat.updateLocale(locale);
    }

    void updateTimeZone(TimeZone timeZone) {
        dozenalTime.updateTimeZone(timeZone);
        dozenalTime.setToNow();
    }

    void updateAmbient(boolean lowBitAmbient, boolean ambient) {
        boolean highQuality = !(ambient && lowBitAmbient);
        if (this.ambient != ambient || this.highQuality != highQuality) {
            this.ambient = ambient;
            this.highQuality = highQuality;
            updateRenderingProperties();
        }
    }

    private void updateRenderingProperties() {
        backgroundPaint = highQuality ? realBackgroundPaint : clearPaint;

        hourHand.updateHighQuality(highQuality);
        minuteHand.updateHighQuality(highQuality);
        secondHand.updateHighQuality(highQuality);
        thirdHand.updateHighQuality(highQuality);
        centreFill.setAntiAlias(highQuality);
        ticks.updateHighQuality(highQuality);
        datePaint.setAntiAlias(highQuality);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        ticks.setBounds(bounds);

        int width = bounds.width();
        int height = bounds.height();

        centerX = width / 2f;
        centerY = height / 2f;
        float thirdLength = centerX - 30;
        float secondLength = centerX - 45;
        float minuteLength = centerX - 60;
        float hourLength = centerX - 80;

        hourHand = new Hand(
                context,
                R.dimen.analog_hand_width,
                R.color.analog_hands_fill,
                R.dimen.analog_hand_stroke_width,
                R.dimen.analog_hand_shadow_width,
                centerX, centerY, hourLength);
        minuteHand = new Hand(
                context,
                R.dimen.analog_hand_width,
                R.color.analog_hands_fill,
                R.dimen.analog_hand_stroke_width,
                R.dimen.analog_hand_shadow_width,
                centerX, centerY, minuteLength);
        secondHand = new Hand(
                context,
                R.dimen.analog_second_hand_width,
                R.color.analog_second_hand_fill,
                R.dimen.analog_second_hand_stroke_width,
                R.dimen.analog_second_hand_shadow_width,
                centerX, centerY, secondLength);
        thirdHand = new Hand(
                context,
                R.dimen.analog_third_hand_width,
                R.color.analog_third_hand_fill,
                R.dimen.analog_third_hand_stroke_width,
                R.dimen.analog_third_hand_shadow_width,
                centerX, centerY, thirdLength);

        updateRenderingProperties();
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
        return PixelFormat.OPAQUE;
    }
}