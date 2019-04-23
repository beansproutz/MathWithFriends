package com.example.utility;

import android.view.View;

// Simple modifier class used to set screen to immersive fullscreen mode
public final class FullScreenModifier {

    // Sets screen to immersive fullscreen mode - hiding the system bars.
    public static void setFullscreen(View view) {
        view.setSystemUiVisibility(
                  View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

}