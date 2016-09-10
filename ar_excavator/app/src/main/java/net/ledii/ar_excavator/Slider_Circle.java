package net.ledii.ar_excavator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.RelativeLayout;

public class Slider_Circle extends View {
    //Constants
    public static final float START_RIGHT = 0;
    public static final float START_BOTTOM = 90;
    public static final float START_LEFT = 180;
    public static final float START_TOP = 270;
    public static final float CIRCLE_FULL = 360;
    public static final float CIRCLE_SEMI = 180;

    //Variables
    private RectF rect;
    private float min = 0, max = 100, current = 0;
    private float interval = 1;
    private float startAngle = START_TOP;
    private float totalAngle = CIRCLE_FULL;
    private boolean fillClockwise = true;
    private float thickness = 20;
    private VelocityTracker mVelocityTracker = null;

    Slider_Circle(Context context) {
        super(context);

        //Initialize
        rect = new RectF();

        //Add view to layout
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
        RelativeLayout layout = (RelativeLayout) ((Activity)context).findViewById(R.id.layoutMain);
        layout.addView(this, params);

        //Calculate size
        float size = (int)Global.get(Global.SCREEN_WIDTH) * 0.8f;
        setSize(size);

        //Calculate top center
        float offset = (int)Global.get(Global.SCREEN_WIDTH) * 0.1f;
        setX(offset);
        setY(offset);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#FF0000"));
        paint.setStrokeWidth(thickness);

        //Calculate angles
        float drawMaxAngle = totalAngle;
        float drawAngle = totalAngle * ((current - min) / (max - min));
        if (!fillClockwise) {
            drawMaxAngle = -drawMaxAngle;
            drawAngle = -drawAngle;
        }

        //Draw background
        paint.setColor(Color.parseColor("#FFFFFF"));
        canvas.drawArc(rect, startAngle, drawMaxAngle, false, paint);

        //Draw progress
        paint.setColor(Color.parseColor("#00FF00"));
        canvas.drawArc(rect, startAngle, drawAngle, false, paint);

        if (totalAngle == CIRCLE_SEMI) {
            //Draw text
            float halfW = getWidth() / 2f;

            paint.setColor(Color.parseColor("#FFFFFF"));
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(60);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("" + current, halfW, halfW, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch(action) {
            case MotionEvent.ACTION_DOWN: {
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    mVelocityTracker.clear();
                }

                mVelocityTracker.addMovement(event);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //Get velocity
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1);
                float velX = VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId);

                //Calculate speed
                int inc = (int)velX;
                float threshhold = 0.2f;
                if (inc == 0 && velX >= threshhold) { inc = 1; }
                if (inc == 0 && velX <= -threshhold) { inc = -1; }

                //Set value
                setValue(current + (interval * inc));
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mVelocityTracker.recycle();
                break;
            }
            case MotionEvent.ACTION_UP: {
                //Set increase or decrease value by one step
                float touchX = event.getX();
                if (touchX >= getWidth() / 2f) {
                    setValue(current + interval);
                }
                else {
                    setValue(current - interval);
                }
                break;
            }
        }
        return true;
    }

    public void setSize(float size) {
        int diameter = (int)size;
        getLayoutParams().width = diameter;
        getLayoutParams().height = diameter;

        int off = (int)(thickness / 2f);
        rect.left = off;
        rect.top = off;
        rect.right = diameter - off;
        rect.bottom = diameter - off;
    }

    public void setTotalAngle(float angle) {
        totalAngle = angle;
    }

    public void setStartAngle(float angle) {
        startAngle = angle;
    }

    public void setRange(float min, float max) {
        this.min = min;
        this.max = max;
        if (current > max) { setValue(max); }
        if (current < min) { setValue(min); }
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public void setValue(float value) {
        current = value;
        if (current > max) { current = max; }
        if (current < min) { current = min; }
        invalidate();
    }

    public void setFillMode(boolean clockwise) {
        fillClockwise = clockwise;
        invalidate();
    }
}