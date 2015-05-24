package lu.schoolido.sukutomo.sukutomo;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by arukantara on 23/05/15.
 */
public class OnSlideListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    public OnSlideListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private final String TAG = GestureListener.class.getSimpleName();

        private static final int SLIDE_THRESHOLD = 200;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            try {
                float deltaY = e2.getY() - e1.getY();
                float deltaX = e2.getX() - e1.getX();

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > SLIDE_THRESHOLD) {
                        if (deltaX > 0) {
                            return onSlideRight(deltaX);
                        } else {
                            return onSlideLeft(deltaX);
                        }
                    }
                } else {
                    if (Math.abs(deltaY) > SLIDE_THRESHOLD) {
                        if (deltaY > 0) {
                            return onSlideDown(deltaY);
                        } else {
                            return onSlideUp(deltaY);
                        }
                    }
                }
            } catch (Exception exception) {
                Log.e(TAG, exception.getMessage());
            }

            return false;
        }
    }

    public boolean onSlideUp(float delta) {
        return false;
    }

    public boolean onSlideDown(float delta) {
        return false;
    }

    public boolean onSlideLeft(float delta) {
        return false;
    }

    public boolean onSlideRight(float delta) {
        return false;
    }
}
