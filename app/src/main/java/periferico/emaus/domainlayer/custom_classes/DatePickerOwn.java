package periferico.emaus.domainlayer.custom_classes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.DatePicker;

/**
 * Created by maubocanegra on 30/10/17.
 */

public class DatePickerOwn extends DatePicker {
    public DatePickerOwn(Context context) {
        super(context);
    }

    public DatePickerOwn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DatePickerOwn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DatePickerOwn(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }

        return false;
    }
}
