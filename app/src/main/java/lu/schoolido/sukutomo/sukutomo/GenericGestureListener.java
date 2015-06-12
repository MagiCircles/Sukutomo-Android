package lu.schoolido.sukutomo.sukutomo;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by arukantara on 11/06/15.
 * GenericGestureListener provides a simple base for the different views where we can slide in four directions.
 */
public class GenericGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SLIDE_THRESHOLD = 100;
    private final String TAG = GenericGestureListener.class.getSimpleName();
    protected static Animation slideUpAnimation;
    protected static Animation slideDownAnimation;
    protected static Animation slideExitRightAnimation;
    protected static Animation slideExitLeftAnimation;
    protected static Animation slideEnterLeftAnimation;

    public GenericGestureListener(Context context) {
        slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_exit_up);
        slideDownAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_exit_down);
        slideExitRightAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_exit_right);
        slideExitLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_exit_left);
        slideExitRightAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_enter_right);
        slideEnterLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_enter_left);
    }

    /**
     * Overrides SimpleOnGestureListener onFling in order to detect slide gestures in four different
     * directions.
     * @param e1 Start MotionEvent
     * @param e2 End MotionEvent
     * @param distanceX
     * @param distanceY
     * @return true, if the event is consumed.
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        try {
            float deltaY = e2.getY() - e1.getY();
            float deltaX = e2.getX() - e1.getX();

            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (Math.abs(deltaX) > SLIDE_THRESHOLD) {
                    if (deltaX > 0) {
                        return onSlideRight();
                    } else {
                        return onSlideLeft();
                    }
                }
            } else {
                if (Math.abs(deltaY) > SLIDE_THRESHOLD) {
                    if (deltaY > 0) {
                        return onSlideDown();
                    } else {
                        return onSlideUp();
                    }
                }
            }
        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage());
        }

        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    public boolean onSlideLeft() {
        return false;
    }

    public boolean onSlideRight() {
        return false;
    }

    public boolean onSlideUp() {

        return false;
    }

    public boolean onSlideDown() {
        return false;
    }
}
