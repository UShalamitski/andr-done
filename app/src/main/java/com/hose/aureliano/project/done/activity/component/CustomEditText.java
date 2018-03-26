package com.hose.aureliano.project.done.activity.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by evere on 24.03.2018
 */
@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {

    private CustomEditTextListener listener;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListener(CustomEditTextListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                dispatchKeyEvent(event);
                if (listener != null) {
                    listener.handle();
                }
                return false;
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public interface CustomEditTextListener {
        void handle();
    }
}
