package com.example.abhishek.codefurything;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

public class KeyPressService extends AccessibilityService implements View.OnKeyListener {
    int count = 0;
    long prev_time = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(MainActivity.TAG, event.toString());
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.d(MainActivity.TAG, event.toString());

        int action = event.getAction();
        int keyCode = event.getKeyCode();
        long curr_time = event.getEventTime();

        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                ++count;
            }

            if (count == 1) {
                prev_time = curr_time;
            }

            if (count == 2) {
                long time_delta = curr_time - prev_time;
                if (time_delta <= 1000) {
                    Log.d(MainActivity.TAG, "Starting main activity!");
                    Intent intent = new Intent(KeyPressService.this, MainActivity.class);
                    intent.putExtra("SOS", true);
                    startActivity(intent);
                }

                count = 0;
            }
        }

        return super.onKeyEvent(event);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }
}
