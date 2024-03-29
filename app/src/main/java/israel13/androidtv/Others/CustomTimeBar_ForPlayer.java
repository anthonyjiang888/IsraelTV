package israel13.androidtv.Others;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by puspak on 30/8/17.
 */

public class CustomTimeBar_ForPlayer extends View implements TimeBar {

    /**
     * The threshold in dps above the bar at which touch events trigger fine scrub mode.
     */
    private static final int FINE_SCRUB_Y_THRESHOLD = -50;
    /**
     * The ratio by which times are reduced in fine scrub mode.
     */
    private static final int FINE_SCRUB_RATIO = 3;
    /**
     * The time after which the scrubbing listener is notified that scrubbing has stopped after
     * performing an incremental scrub using key input.
     */
    private static final long STOP_SCRUBBING_TIMEOUT_MS = 1000;
    private static final int DEFAULT_INCREMENT_COUNT = 30000;
    private static final int DEFAULT_BAR_HEIGHT = 80;
    private static final int DEFAULT_TOUCH_TARGET_HEIGHT = 26;
   // int greenColorValue = Color.parseColor("#0f9e60");
    private static final int DEFAULT_PLAYED_COLOR = 0x33FFFFFF;
    private static final int DEFAULT_BUFFERED_COLOR = 0xCCFFFFFF;
    private static final int DEFAULT_AD_MARKER_COLOR = 0xB2FFFF00;
    private static final int DEFAULT_AD_MARKER_WIDTH = 80;
    private static final int DEFAULT_SCRUBBER_ENABLED_SIZE = 0;
    private static final int DEFAULT_SCRUBBER_DISABLED_SIZE = 0;
    private static final int DEFAULT_SCRUBBER_DRAGGED_SIZE = 0;
    private static final int OPAQUE_COLOR = 0xFF000000;

    private final Rect seekBounds;
    private final Rect progressBar;
    private final Rect bufferedBar;
    private final Rect scrubberBar;
    private final Paint progressPaint;
    private final Paint bufferedPaint;
    private final Paint scrubberPaint;
    private final Paint adMarkerPaint;
    private final int barHeight;
    private final int touchTargetHeight;
    private final int adMarkerWidth;
    private final int scrubberEnabledSize;
    private final int scrubberDisabledSize;
    private final int scrubberDraggedSize;
    private final int scrubberPadding;
    private final int fineScrubYThreshold;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final Runnable stopScrubbingRunnable;

    private int scrubberSize;
    private OnScrubListener listener;
    private int keyCountIncrement;
    private long keyTimeIncrement;
    private int lastCoarseScrubXPosition;
    private int[] locationOnScreen;
    private Point touchPosition;

    private boolean scrubbing;
    private long scrubPosition;
    private long duration;
    private long position;
    private long bufferedPosition;
    private int adBreakCount;
    private long[] adBreakTimesMs;

    /**
     * Creates a new time bar.
     */
    public CustomTimeBar_ForPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        seekBounds = new Rect();
        progressBar = new Rect();
        bufferedBar = new Rect();
        scrubberBar = new Rect();
        progressPaint = new Paint();
        bufferedPaint = new Paint();
        scrubberPaint = new Paint();
        adMarkerPaint = new Paint();

        // Calculate the dimensions and paints for drawn elements.
        Resources res = context.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        fineScrubYThreshold = dpToPx(displayMetrics, FINE_SCRUB_Y_THRESHOLD);
        int defaultBarHeight = dpToPx(displayMetrics, DEFAULT_BAR_HEIGHT);
        int defaultTouchTargetHeight = dpToPx(displayMetrics, DEFAULT_TOUCH_TARGET_HEIGHT);
        int defaultAdMarkerWidth = dpToPx(displayMetrics, DEFAULT_AD_MARKER_WIDTH);
        int defaultScrubberEnabledSize = dpToPx(displayMetrics, DEFAULT_SCRUBBER_ENABLED_SIZE);
        int defaultScrubberDisabledSize = dpToPx(displayMetrics, DEFAULT_SCRUBBER_DISABLED_SIZE);
        int defaultScrubberDraggedSize = dpToPx(displayMetrics, DEFAULT_SCRUBBER_DRAGGED_SIZE);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar, 0,
                    0);
            try {
                barHeight = a.getDimensionPixelSize(com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_bar_height,
                        defaultBarHeight);
                touchTargetHeight = a.getDimensionPixelSize(com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_touch_target_height,
                        defaultTouchTargetHeight);
                adMarkerWidth = a.getDimensionPixelSize(com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_ad_marker_width,
                        defaultAdMarkerWidth);
                scrubberEnabledSize = a.getDimensionPixelSize(
                        com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_scrubber_enabled_size, defaultScrubberEnabledSize);
                scrubberDisabledSize = a.getDimensionPixelSize(
                        com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_scrubber_disabled_size, defaultScrubberDisabledSize);
                scrubberDraggedSize = a.getDimensionPixelSize(
                        com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_scrubber_dragged_size, defaultScrubberDraggedSize);
                int playedColor = a.getInt(com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_played_color, DEFAULT_PLAYED_COLOR);
                int bufferedColor = a.getInt(com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_buffered_color,
                        DEFAULT_BUFFERED_COLOR);
                int adMarkerColor = a.getInt(com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_ad_marker_color,
                        DEFAULT_AD_MARKER_COLOR);
                progressPaint.setColor(playedColor);
                scrubberPaint.setColor(OPAQUE_COLOR | playedColor);
                bufferedPaint.setColor(bufferedColor);
                adMarkerPaint.setColor(adMarkerColor);
            } finally {
                a.recycle();
            }
        } else {
            barHeight = defaultBarHeight;
            touchTargetHeight = defaultTouchTargetHeight;
            adMarkerWidth = defaultAdMarkerWidth;
            scrubberEnabledSize = defaultScrubberEnabledSize;
            scrubberDisabledSize = defaultScrubberDisabledSize;
            scrubberDraggedSize = defaultScrubberDraggedSize;
            scrubberPaint.setColor(OPAQUE_COLOR | DEFAULT_PLAYED_COLOR);
            progressPaint.setColor(DEFAULT_PLAYED_COLOR);
            bufferedPaint.setColor(DEFAULT_BUFFERED_COLOR);
            adMarkerPaint.setColor(DEFAULT_AD_MARKER_COLOR);
        }
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        stopScrubbingRunnable = new Runnable() {
            @Override
            public void run() {
                stopScrubbing(false);
            }
        };
        scrubberSize = scrubberEnabledSize;
        scrubberPadding =
                (Math.max(scrubberDisabledSize, Math.max(scrubberEnabledSize, scrubberDraggedSize)) + 1)
                        / 2;
        duration = C.TIME_UNSET;
        keyTimeIncrement = C.TIME_UNSET;
        keyCountIncrement = DEFAULT_INCREMENT_COUNT;
        setFocusable(true);
        if (Util.SDK_INT >= 16) {
            maybeSetImportantForAccessibilityV16();
        }
    }

    @Override
    public void setKeyTimeIncrement(long time) {
        Assertions.checkArgument(time > 0);
        keyCountIncrement = C.INDEX_UNSET;
        keyTimeIncrement = time;
    }

    @Override
    public void setKeyCountIncrement(int count) {
        Assertions.checkArgument(count > 0);
        keyCountIncrement = count;
        keyTimeIncrement = C.TIME_UNSET;
    }

    @Override
    public void setPosition(long position) {
        this.position = position;
        setContentDescription(getProgressText());
    }

    @Override
    public void setBufferedPosition(long bufferedPosition) {
        this.bufferedPosition = bufferedPosition;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
        if (scrubbing && duration == C.TIME_UNSET) {
            stopScrubbing(true);
        } else {
            updateScrubberState();
        }
    }

    @Override
    public void setAdGroupTimesMs(@Nullable long[] adGroupTimesMs, @Nullable boolean[] playedAdGroups, int adGroupCount) {

    }


    @Override
    public void addListener(OnScrubListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeListener(OnScrubListener listener) {

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateScrubberState();
        if (scrubbing && !enabled) {
            stopScrubbing(true);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        drawTimeBar(canvas);
        drawPlayhead(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || duration <= 0) {
            return false;
        }
        Point touchPosition = resolveRelativeTouchPosition(event);
        int x = touchPosition.x;
        int y = touchPosition.y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInSeekBar(x, y)) {
                    startScrubbing();
                    positionScrubber(x);
                    scrubPosition = getScrubberPosition();
                    update();
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (scrubbing) {
                    if (y < fineScrubYThreshold) {
                        int relativeX = x - lastCoarseScrubXPosition;
                        positionScrubber(lastCoarseScrubXPosition + relativeX / FINE_SCRUB_RATIO);
                    } else {
                        lastCoarseScrubXPosition = x;
                        positionScrubber(x);
                    }
                    scrubPosition = getScrubberPosition();
                    if (listener != null) {
                        listener.onScrubMove(this, scrubPosition);
                    }
                    update();
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (scrubbing) {
                    stopScrubbing(event.getAction() == MotionEvent.ACTION_CANCEL);
                    return true;
                }
                break;
            default:
                // Do nothing.
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isEnabled()) {
            long positionIncrement = getPositionIncrement();

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    positionIncrement = -positionIncrement;

                    // Fall through.
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (scrubIncrementally(positionIncrement)) {
                        removeCallbacks(stopScrubbingRunnable);
                        postDelayed(stopScrubbingRunnable, STOP_SCRUBBING_TIMEOUT_MS);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if (scrubbing) {
                        removeCallbacks(stopScrubbingRunnable);
                        stopScrubbingRunnable.run();
                        return true;
                    }
                    break;
                default:
                    // Do nothing.
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        int barY = height - touchTargetHeight;
        int seekLeft = getPaddingLeft();
        int seekRight = width - getPaddingRight();
        int progressY = barY + (touchTargetHeight - barHeight) / 2;
        seekBounds.set(seekLeft, barY, seekRight, barY + touchTargetHeight);
        progressBar.set(seekBounds.left + scrubberPadding, progressY,
                seekBounds.right - scrubberPadding, progressY + barHeight);
        update();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
    }

    @TargetApi(14)
    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SELECTED) {
            event.getText().add(getProgressText());
        }
        event.setClassName(DefaultTimeBar.class.getName());
    }

    @TargetApi(21)
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(DefaultTimeBar.class.getCanonicalName());
        info.setContentDescription(getProgressText());
        if (duration <= 0) {
            return;
        }
        if (Util.SDK_INT >= 21) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
        } else if (Util.SDK_INT >= 16) {
            info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        }
    }

    @TargetApi(16)
    @Override
    public boolean performAccessibilityAction(int action, Bundle args) {
        if (super.performAccessibilityAction(action, args)) {
            return true;
        }
        if (duration <= 0) {
            return false;
        }
        if (action == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) {
            if (scrubIncrementally(-getPositionIncrement())) {
                stopScrubbing(false);
            }
        } else if (action == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) {
            if (scrubIncrementally(getPositionIncrement())) {
                stopScrubbing(false);
            }
        } else {
            return false;
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        return true;
    }

    // Internal methods.

    @TargetApi(16)
    private void maybeSetImportantForAccessibilityV16() {
        if (getImportantForAccessibility() == View.IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        }
    }

    private void startScrubbing() {
        scrubbing = true;
        updateScrubberState();
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        if (listener != null) {
            listener.onScrubStart(this, getScrubberPosition());
        }
    }

    private void stopScrubbing(boolean canceled) {
        scrubbing = false;
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(false);
        }
        updateScrubberState();
        invalidate();
        if (listener != null) {
            listener.onScrubStop(this, getScrubberPosition(), canceled);
        }
    }

    private void updateScrubberState() {
        scrubberSize = scrubbing ? scrubberDraggedSize
                : (isEnabled() && duration >= 0 ? scrubberEnabledSize : scrubberDisabledSize);
    }

    private void update() {
        bufferedBar.set(progressBar);
        scrubberBar.set(progressBar);
        long newScrubberTime = scrubbing ? scrubPosition : position;
        if (duration > 0) {
            int bufferedPixelWidth =
                    (int) ((progressBar.width() * bufferedPosition) / duration);
            bufferedBar.right = progressBar.left + bufferedPixelWidth;
            int scrubberPixelPosition =
                    (int) ((progressBar.width() * newScrubberTime) / duration);
            scrubberBar.right = progressBar.left + scrubberPixelPosition;
        } else {
            bufferedBar.right = progressBar.left;
            scrubberBar.right = progressBar.left;
        }
        invalidate(seekBounds);
    }

    private void positionScrubber(float xPosition) {
        scrubberBar.right = Util.constrainValue((int) xPosition, progressBar.left, progressBar.right);
    }

    private Point resolveRelativeTouchPosition(MotionEvent motionEvent) {
        if (locationOnScreen == null) {
            locationOnScreen = new int[2];
            touchPosition = new Point();
        }
        getLocationOnScreen(locationOnScreen);
        touchPosition.set(
                ((int) motionEvent.getRawX()) - locationOnScreen[0],
                ((int) motionEvent.getRawY()) - locationOnScreen[1]);
        return touchPosition;
    }

    private long getScrubberPosition() {
        if (progressBar.width() <= 0 || duration == C.TIME_UNSET) {
            return 0;
        }
        return (scrubberBar.width() * duration) / progressBar.width();
    }

    private boolean isInSeekBar(float x, float y) {
        return seekBounds.contains((int) x, (int) y);
    }

    private void drawTimeBar(Canvas canvas) {
        int progressBarHeight = progressBar.height();
        int barTop = progressBar.centerY() - progressBarHeight / 2;
        int barBottom = barTop + progressBarHeight;
        if (duration <= 0) {
            canvas.drawRect(progressBar.left, barTop, progressBar.right, barBottom, progressPaint);
            return;
        }
        int bufferedLeft = bufferedBar.left;
        int bufferedRight = bufferedBar.right;
        int progressLeft = Math.max(Math.max(progressBar.left, bufferedRight), scrubberBar.right);
        if (progressLeft < progressBar.right) {
            canvas.drawRect(progressLeft, barTop, progressBar.right, barBottom, progressPaint);
        }
        bufferedLeft = Math.max(bufferedLeft, scrubberBar.right);
        if (bufferedRight > bufferedLeft) {
            canvas.drawRect(bufferedLeft, barTop, bufferedRight, barBottom, bufferedPaint);
        }
        if (scrubberBar.width() > 0) {
            canvas.drawRect(scrubberBar.left, barTop, scrubberBar.right, barBottom, scrubberPaint);
        }
        int adMarkerOffset = adMarkerWidth / 2;
        for (int i = 0; i < adBreakCount; i++) {
            long adBreakTimeMs = Util.constrainValue(adBreakTimesMs[i], 0, duration);
            int markerPositionOffset =
                    (int) (progressBar.width() * adBreakTimeMs / duration) - adMarkerOffset;
            int markerLeft = progressBar.left + Math.min(progressBar.width() - adMarkerWidth,
                    Math.max(0, markerPositionOffset));
            canvas.drawRect(markerLeft, barTop, markerLeft + adMarkerWidth, barBottom, adMarkerPaint);
        }
    }

    private void drawPlayhead(Canvas canvas) {
        if (duration <= 0) {
            return;
        }
        int playheadRadius = scrubberSize / 2;
        int playheadCenter = Util.constrainValue(scrubberBar.right, scrubberBar.left,
                progressBar.right);
        canvas.drawCircle(playheadCenter, scrubberBar.centerY(), playheadRadius, scrubberPaint);
    }

    private String getProgressText() {
        return Util.getStringForTime(formatBuilder, formatter, position);
    }

    private long getPositionIncrement() {
        return keyTimeIncrement == C.TIME_UNSET
                ? (duration == C.TIME_UNSET ? 0 : ( keyCountIncrement)) : keyTimeIncrement;
    }

    /**
     * Incrementally scrubs the position by {@code positionChange}.
     *
     * @param positionChange The change in the scrubber position, in milliseconds. May be negative.
     * @return Returns whether the scrubber position changed.
     */
    private boolean scrubIncrementally(long positionChange) {
        if (duration <= 0) {
            return false;
        }
        long scrubberPosition = getScrubberPosition();
        scrubPosition = Util.constrainValue(scrubberPosition + positionChange, 0, duration);
        if (scrubPosition == scrubberPosition) {
            return false;
        }
        if (!scrubbing) {
            startScrubbing();
        }
        if (listener != null) {
            listener.onScrubMove(this, scrubPosition);
        }
        update();
        return true;
    }

    private static int dpToPx(DisplayMetrics displayMetrics, int dps) {
        return (int) (dps * displayMetrics.density + 0.5f);
    }

}
